package com.example.sushirestauranteapp.main

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.sushirestauranteapp.R
import com.example.sushirestauranteapp.modelo.DatabaseHelper

private const val CHANNEL_ID = "pedido_channel"

class RecepcionPedidoActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private var idPedido: Int = -1

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                enviarNotificacion("Pedido confirmado", "Oye, ya tienes tu pedido")
            } else {
                Toast.makeText(this, "Permiso denegado, no se pueden enviar notificaciones", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recepcion_pedido)

        databaseHelper = DatabaseHelper(this)

        val tvPedido = findViewById<TextView>(R.id.tvPedido)
        val tvMesa = findViewById<TextView>(R.id.tvMesa)
        val btnAceptar = findViewById<Button>(R.id.btnAceptar)
        val btnRechazar = findViewById<Button>(R.id.btnRechazar)


        val nombres = intent.getStringArrayListExtra("nombres") ?: arrayListOf()
        val cantidades = intent.getIntegerArrayListExtra("cantidades") ?: arrayListOf()
        idPedido = intent.getIntExtra("idPedido", -1)
        val numeroMesa = intent.getIntExtra("numeroMesa", -1)

        val detallePedido = nombres.zip(cantidades) { nombre, cantidad -> "$nombre x$cantidad" }
            .joinToString("\n")
        tvPedido.text = detallePedido
        tvMesa.text = getString(R.string.numero_mesa, numeroMesa)

        crearCanalNotificacion()

        btnAceptar.setOnClickListener {
            if (idPedido != -1) {
                databaseHelper.actualizarEstadoPedido(idPedido, "aceptado")
            }
            verificarYEnviarNotificacion("Pedido confirmado", "Tu pedido está aceptado en la mesa $numeroMesa")
            setResult(Activity.RESULT_OK, Intent().putExtra("resultado", "confirmado"))
            finish()
        }

        btnRechazar.setOnClickListener {
            verificarYEnviarNotificacion("Pedido rechazado", "El pedido de la mesa $numeroMesa ha sido rechazado")
            setResult(Activity.RESULT_OK, Intent().putExtra("resultado", "rechazado"))
            finish()
        }
    }

    private fun crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Pedidos", NotificationManager.IMPORTANCE_HIGH)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun verificarYEnviarNotificacion(titulo: String, mensaje: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                enviarNotificacion(titulo, mensaje)
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            enviarNotificacion(titulo, mensaje)
        }
    }

    private fun enviarNotificacion(titulo: String, mensaje: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager = NotificationManagerCompat.from(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(1, notification)
        }
    }
}
