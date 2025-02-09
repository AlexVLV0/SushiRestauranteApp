package com.example.sushirestauranteapp.modelo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Crear tablas
        db.execSQL(
            "CREATE TABLE Mesa (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "numero INTEGER NOT NULL)"
        )

        db.execSQL(
            "CREATE TABLE Cliente (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombre TEXT, " +
                    "mesaId INTEGER, " +
                    "FOREIGN KEY(mesaId) REFERENCES Mesa(id))"
        )

        db.execSQL(
            "CREATE TABLE Pedido (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "mesaId INTEGER, " +
                    "fechaHora TEXT, " +
                    "estado TEXT, " +
                    "FOREIGN KEY(mesaId) REFERENCES Mesa(id))"
        )

        db.execSQL(
            "CREATE TABLE Plato (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombre TEXT, " +
                    "descripcion TEXT, " +
                    "detalle TEXT, " +  // Se agregó 'detalle' en la tabla
                    "precio REAL, " +
                    "categoria TEXT, " +  // Se agregó 'categoria'
                    "imagenResId TEXT)" // Se guardará como texto (ruta de imagen o ID)
        )

        db.execSQL(
            "CREATE TABLE PedidoPlato (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "pedidoId INTEGER, " +
                    "platoId INTEGER, " +
                    "cantidad INTEGER, " +
                    "FOREIGN KEY(pedidoId) REFERENCES Pedido(id), " +
                    "FOREIGN KEY(platoId) REFERENCES Plato(id))"
        )

        db.execSQL(
            "CREATE TABLE Factura (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "mesaId INTEGER, " +
                    "total REAL, " +
                    "fechaHora TEXT, " +
                    "FOREIGN KEY(mesaId) REFERENCES Mesa(id))"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Factura")
        db.execSQL("DROP TABLE IF EXISTS PedidoPlato")
        db.execSQL("DROP TABLE IF EXISTS Plato")
        db.execSQL("DROP TABLE IF EXISTS Pedido")
        db.execSQL("DROP TABLE IF EXISTS Cliente")
        db.execSQL("DROP TABLE IF EXISTS Mesa")
        onCreate(db)
    }

    fun insertarPlato(plato: Plato): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", plato.nombre)
            put("descripcion", plato.descripcion)
            put("detalle", plato.detalle)
            put("precio", plato.precio)
            put("categoria", plato.categoria)

            // Convertir imagenResId a String antes de insertarlo
            val imagenStr = when (plato.imagenResId) {
                is Int -> plato.imagenResId.toString() // Guardar ID de drawable como String
                is String -> plato.imagenResId // Guardar ruta de archivo directamente
                else -> "" // Si es nulo u otro tipo, guardar cadena vacía
            }
            put("imagenResId", imagenStr)
        }

        val resultado = db.insert("Plato", null, values)
        db.close()

        return resultado != -1L
    }

    fun actualizarEstadoPedido(pedidoId: Int, nuevoEstado: String): Int {
        val db = this.writableDatabase
        val valores = ContentValues().apply {
            put("estado", nuevoEstado)
        }
        val filasActualizadas = db.update("Pedido", valores, "id = ?", arrayOf(pedidoId.toString()))
        db.close()
        return filasActualizadas
    }

    fun obtenerPlatosPorCategoria(categoria: String): List<Plato> {
        val listaPlatos = mutableListOf<Plato>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Plato WHERE categoria = ?", arrayOf(categoria))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"))
                val detalle = cursor.getString(cursor.getColumnIndexOrThrow("detalle"))
                val precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))

                // Recuperar imagenResId como String y convertirlo al tipo correcto
                val imagenStr = cursor.getString(cursor.getColumnIndexOrThrow("imagenResId"))
                val imagenResId: Any = if (imagenStr.all { it.isDigit() }) {
                    imagenStr.toInt() // Convertir a Int si es un ID de drawable
                } else {
                    imagenStr // Mantener como String si es una ruta de archivo
                }

                listaPlatos.add(Plato(id, nombre, descripcion, detalle, precio, categoria, imagenResId))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return listaPlatos
    }



    companion object {
        private const val DATABASE_NAME = "restaurante.db"
        private const val DATABASE_VERSION = 2
    }
}
