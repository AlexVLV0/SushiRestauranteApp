package com.example.sushirestauranteapp.modelo

data class Plato(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val detalle: String,
    val precio: Double,
    val categoria: String,
    val imagenResId: Any
)

