package com.example.sushirestauranteapp.main

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sushirestauranteapp.R

class DetallePlatoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_plato)

        val tituloPlato = intent.getStringExtra("PLATO_NOMBRE")
        val descripcionPlato = intent.getStringExtra("PLATO_DETALLE")

        val tvTitulo: TextView = findViewById(R.id.tvTituloPlato)
        val tvDescripcion: TextView = findViewById(R.id.tvDescripcionPlato)

        tvTitulo.text = tituloPlato
        tvDescripcion.text = descripcionPlato
    }
}
