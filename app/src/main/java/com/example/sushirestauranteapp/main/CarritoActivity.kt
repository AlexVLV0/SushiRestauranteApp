package com.example.sushirestauranteapp.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.sushirestauranteapp.R
import java.text.DecimalFormat

class CarritoActivity : AppCompatActivity() {

    private val pedidoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultado = result.data?.getStringExtra("resultado")
                if (resultado == "confirmado" || resultado == "rechazado") {
                    devolverResultado(true)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        val tvResumenPedido = findViewById<TextView>(R.id.tvTotal)
        val tvMesa = findViewById<TextView>(R.id.tvMesa)
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmar)
        val btnRechazar = findViewById<Button>(R.id.btnRechazar)


        val nombres = intent.getStringArrayListExtra("nombres") ?: arrayListOf()
        val cantidades = intent.getIntegerArrayListExtra("cantidades") ?: arrayListOf()
        val precios = intent.getDoubleArrayExtra("precios") ?: doubleArrayOf()
        val numeroMesa = intent.getIntExtra("numeroMesa", -1)


        if (nombres.size != cantidades.size || nombres.size != precios.size) {
            mostrarMensaje("Error al cargar el carrito")
            devolverResultado(false)
            return
        }


        tvMesa.text = getString(R.string.numero_mesa, numeroMesa)

        val decimalFormat = DecimalFormat("#,##0.00")
        val detallePedido = StringBuilder()
        var total = 0.0

        for (i in nombres.indices) {
            val subtotal = precios[i] * cantidades[i]
            total += subtotal
            detallePedido.append("${nombres[i]} x${cantidades[i]} - $${decimalFormat.format(subtotal)}\n")
        }
        detallePedido.append("\nTotal: $${decimalFormat.format(total)}")

        tvResumenPedido.text = detallePedido.toString()

        btnConfirmar.setOnClickListener {
            mostrarMensaje(getString(R.string.pedidoaceptado))
            lanzarRecepcionPedido(nombres, cantidades, numeroMesa)
        }

        btnRechazar.setOnClickListener {
            mostrarMensaje(getString(R.string.pedidorechazado))
            devolverResultado(true)
        }
    }

    private fun lanzarRecepcionPedido(nombres: ArrayList<String>, cantidades: ArrayList<Int>, numeroMesa: Int) {
        val intent = Intent(this, RecepcionPedidoActivity::class.java).apply {
            putStringArrayListExtra("nombres", nombres)
            putIntegerArrayListExtra("cantidades", cantidades)
            putExtra("numeroMesa", numeroMesa)
        }
        pedidoLauncher.launch(intent)
    }

    private fun devolverResultado(vaciarCarrito: Boolean) {
        val intent = Intent().apply {
            putExtra("vaciarCarrito", vaciarCarrito)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun mostrarMensaje(mensaje: String) {
        val toast = android.widget.Toast.makeText(this, mensaje, android.widget.Toast.LENGTH_SHORT)
        toast.show()
    }
}
