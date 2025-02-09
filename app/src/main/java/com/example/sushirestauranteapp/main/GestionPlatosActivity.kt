package com.example.sushirestauranteapp.main

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.sushirestauranteapp.R
import com.example.sushirestauranteapp.modelo.DatabaseHelper
import com.example.sushirestauranteapp.adapter.MenuAdapter
import com.example.sushirestauranteapp.modelo.Plato
import java.io.File
import java.io.FileOutputStream

private lateinit var recyclerView: RecyclerView
@SuppressLint("StaticFieldLeak")
private lateinit var menuAdapter: MenuAdapter

class GestionPlatosActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var categoria: String = "Desconocida"
    private var imagePath: String? = null
    private lateinit var ivPlato: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_platos)

        recyclerView = findViewById(R.id.recyclerViewPlatos)
        menuAdapter = MenuAdapter(emptyList(), { _, _ -> }, this)
        recyclerView.adapter = menuAdapter
        dbHelper = DatabaseHelper(this)
        categoria = intent.getStringExtra("categoria") ?: "Desconocida"

        val btnAgregar = findViewById<Button>(R.id.btnAgregarPlato)
        val btnEliminar = findViewById<Button>(R.id.btnEliminarPlato)

        btnAgregar.setOnClickListener {
            mostrarDialogoAgregarPlato()
        }

        btnEliminar.setOnClickListener {
            Toast.makeText(this, "Funcionalidad de eliminar pendiente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarDialogoAgregarPlato() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialogo_agregar_plato, null)
        val etNombre = dialogView.findViewById<EditText>(R.id.etNombrePlato)
        val etDescripcion = dialogView.findViewById<EditText>(R.id.etDescripcionPlato)
        val etDetalle = dialogView.findViewById<EditText>(R.id.etDetallePlato)
        val etPrecio = dialogView.findViewById<EditText>(R.id.etPrecioPlato)
        ivPlato = dialogView.findViewById(R.id.ivPlato)
        val btnSeleccionarImagen = dialogView.findViewById<Button>(R.id.btnSeleccionarImagen)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Agregar Plato")
            .setView(dialogView)
            .setPositiveButton("Agregar") { _, _ ->
                val nombre = etNombre.text.toString()
                val descripcion = etDescripcion.text.toString()
                val detalle = etDetalle.text.toString()
                val precio = etPrecio.text.toString().toDoubleOrNull()

                if (nombre.isNotEmpty() && precio != null && imagePath != null) {
                    val nuevoPlato = Plato(0, nombre, descripcion, detalle, precio, categoria, imagePath!!)

                    val exito = dbHelper.insertarPlato(nuevoPlato)

                    if (exito) {
                        Toast.makeText(this, "Plato agregado correctamente", Toast.LENGTH_SHORT).show()
                        actualizarListaPlatos() // Recargar lista
                    } else {
                        Toast.makeText(this, "Error al agregar el plato", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Complete todos los campos y seleccione una imagen", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        btnSeleccionarImagen.setOnClickListener {
            abrirGaleria()
        }

        dialog.show()
    }



    private fun abrirGaleria() {
        resultadoGaleria.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
    }

    private val resultadoGaleria = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let {
                ivPlato.setImageURI(it)
                imagePath = guardarImagenEnInterno(it)
            }
        }
    }

    private fun guardarImagenEnInterno(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val file = File(filesDir, "plato_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        return file.absolutePath
    }

    private fun obtenerImagenId(imagePath: String): Int {
        return try {
            imagePath.toInt()
        } catch (e: NumberFormatException) {
            R.drawable.sushi
        }
    }

    private fun actualizarListaPlatos() {
        val listaPlatos = dbHelper.obtenerPlatosPorCategoria(categoria)
        menuAdapter.actualizarLista(listaPlatos)
    }
}
