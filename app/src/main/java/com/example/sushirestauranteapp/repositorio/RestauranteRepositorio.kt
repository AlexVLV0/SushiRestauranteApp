package com.example.sushirestauranteapp.repositorio

import android.content.ContentValues
import com.example.sushirestauranteapp.modelo.DatabaseHelper

class RestauranteRepositorio(private val dbHelper: DatabaseHelper) {

    fun agregarMesa(numero: Int): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("numero", numero)
        }
        val resultado = db.insert("Mesa", null, values)
        db.close()
        return resultado
    }

    fun agregarCliente(nombre: String, mesaId: Int): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("mesaId", mesaId)
        }
        val resultado = db.insert("Cliente", null, values)
        db.close()
        return resultado
    }

    fun agregarPedido(mesaId: Int, fechaHora: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("mesaId", mesaId)
            put("fechaHora", fechaHora)
        }
        val resultado = db.insert("Pedido", null, values)
        db.close()
        return resultado
    }

    fun agregarPlato(nombre: String, descripcion: String, precio: Double): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("descripcion", descripcion)
            put("precio", precio)
        }
        val resultado = db.insert("Plato", null, values)
        db.close()
        return resultado
    }

    fun agregarPedidoPlato(pedidoId: Int, platoId: Int, cantidad: Int): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("pedidoId", pedidoId)
            put("platoId", platoId)
            put("cantidad", cantidad)
        }
        val resultado =db.insert("PedidoPlato", null, values)
        db.close()
        return resultado
    }

    fun agregarFactura(mesaId: Int, total: Double, fechaHora: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("mesaId", mesaId)
            put("total", total)
            put("fechaHora", fechaHora)
        }
        val resultado = db.insert("Factura", null, values)
        db.close()
        return resultado
    }
}
