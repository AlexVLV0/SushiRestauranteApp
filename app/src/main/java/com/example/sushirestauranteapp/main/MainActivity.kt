package com.example.sushirestauranteapp.main

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sushirestauranteapp.R
import com.example.sushirestauranteapp.databinding.ActivityMainBinding
import com.example.sushirestauranteapp.modelo.DatabaseHelper
import com.example.sushirestauranteapp.adapter.MenuAdapter
import com.example.sushirestauranteapp.modelo.Plato
import com.example.sushirestauranteapp.repositorio.RestauranteRepositorio
import com.example.sushirestauranteapp.viewmodel.RestauranteViewModel
import com.example.sushirestauranteapp.viewmodel.RestauranteViewModelFactory
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var repository: RestauranteRepositorio
    private var categoriaSeleccionada: String? = null
    private var botonSeleccionado: Button? = null
    private var isAdminMode = false

    private val viewModel: RestauranteViewModel by viewModels {
        RestauranteViewModelFactory()
    }

    private val carritoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK || result.resultCode == Activity.RESULT_CANCELED) {
                val vaciarCarrito = result.data?.getBooleanExtra("vaciarCarrito", false) ?: false
                if (vaciarCarrito) {
                    viewModel.vaciarCarrito()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(getSavedLanguage())
        binding = ActivityMainBinding.inflate(layoutInflater)
        dbHelper = DatabaseHelper(this)
        repository = RestauranteRepositorio(dbHelper)
        setContentView(binding.root)
        val idiomaGuardado = getSavedLanguage()
        if (idiomaGuardado == "es") {
            binding.btnIdioma.setImageResource(R.drawable.espanaaa__1_)
        } else {
            binding.btnIdioma.setImageResource(R.drawable.ukflag__1_)
        }

        isAdminMode = getAdminMode()
        binding.fabCarrito.setOnClickListener { abrirCarrito() }
        binding.btnIdioma.setOnClickListener { mostrarSelectorDeIdioma(it) }
        binding.btnResumen.setOnLongClickListener {
            if (isAdminMode) {
                mostrarDialogoDesactivarAdmin()
            } else {
                mostrarDialogoAdministrador()
            }
            true
        }

        menuAdapter = MenuAdapter(
            emptyList(),
            onClickAgregar = { plato, cantidad -> agregarAlCarrito(plato, cantidad) },
            context = this
        )
        binding.rvMenu.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = menuAdapter
        }

        viewModel.cargarPlatos()
        viewModel.platos.observe(this) { listaPlatos ->
            menuAdapter.actualizarLista(listaPlatos)
        }

        configurarBotonesCategoria()
        seleccionarCategoria("Sushi", binding.btnSushi)
    }

    private fun abrirCarrito() {
        val intent = Intent(this, CarritoActivity::class.java)
        val carrito = viewModel.obtenerCarrito()
        val nombres = ArrayList<String>()
        val cantidades = ArrayList<Int>()
        val precios = ArrayList<Double>()

        carrito.forEach { (plato, cantidad) ->
            nombres.add(plato.nombre)
            cantidades.add(cantidad)
            precios.add(plato.precio)
        }

        intent.putStringArrayListExtra("nombres", nombres)
        intent.putIntegerArrayListExtra("cantidades", cantidades)
        intent.putExtra("precios", precios.toDoubleArray())

        carritoLauncher.launch(intent)
    }

    private fun mostrarDialogoAdministrador() {
        val input = EditText(this).apply {
            hint = "Contraseña"
        }

        AlertDialog.Builder(this)
            .setTitle("Modo Administrador")
            .setMessage("Ingrese la contraseña para acceder a la gestión de platos.")
            .setView(input)
            .setPositiveButton("Aceptar") { _, _ ->
                if (input.text.toString() == "admin123") {
                    isAdminMode = true
                    saveAdminMode(true) // Guardar en SharedPreferences
                    Toast.makeText(this, "Modo Administrador Activado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }


    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

    }

    private fun getSavedLanguage(): String {
        val prefs = getSharedPreferences("config", MODE_PRIVATE)
        return prefs.getString("idioma", "es") ?: "es"
    }

    private fun mostrarSelectorDeIdioma(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.menu_idioma, popup.menu)
        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.idioma_es -> cambiarIdioma("es")
                R.id.idioma_en -> cambiarIdioma("en")
            }
            true
        }
        popup.show()
    }

    private fun cambiarIdioma(idioma: String) {
        guardarIdioma(idioma)
        setLocale(idioma)
        recreate()
    }


    private fun guardarIdioma(idioma: String) {
        val prefs = getSharedPreferences("config", MODE_PRIVATE)
        prefs.edit().putString("idioma", idioma).apply()
    }

    private fun agregarAlCarrito(plato: Plato, cantidad: Int) {
        viewModel.agregarAlCarrito(plato, cantidad)
    }

    private fun configurarBotonesCategoria() {
        val categorias = listOf(
            binding.btnSushi to "Sushi",
            binding.btnWok to "Wok",
            binding.btnTemaki to "Temaki",
            binding.btnAperitivo to "Aperitivo",
            binding.btnVinos to "Vinos",
            binding.btnPostre to "Postre",
            binding.btnNigiris to "Nigiris",
            binding.btnBebida to "Bebida",
            binding.btnSopas to "Sopas"
        )

        categorias.forEach { (boton, categoria) ->
            boton.setOnClickListener {
                seleccionarCategoria(categoria, boton)
            }
            boton.setOnLongClickListener {
                abrirGestionPlatos(categoria)
            }
        }
    }

    private fun seleccionarCategoria(categoria: String, boton: Button) {
        if (categoria != categoriaSeleccionada) {
            viewModel.filtrarPorCategoria(categoria)
            categoriaSeleccionada = categoria
            botonSeleccionado?.setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
            boton.setBackgroundColor(ContextCompat.getColor(this, R.color.blue_light))
            botonSeleccionado = boton
        }
    }

    private fun abrirGestionPlatos(categoria: String): Boolean {
        return if (isAdminMode) {
            val intent = Intent(this, GestionPlatosActivity::class.java)
            intent.putExtra("categoria", categoria)
            startActivity(intent)
            true
        } else {
            Toast.makeText(this, "Modo administrador no activado", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun saveAdminMode(enabled: Boolean) {
        val prefs = getSharedPreferences("config", MODE_PRIVATE)
        prefs.edit().putBoolean("modo_admin", enabled).apply()
    }

    private fun getAdminMode(): Boolean {
        val prefs = getSharedPreferences("config", MODE_PRIVATE)
        return prefs.getBoolean("modo_admin", false)
    }

    private fun mostrarDialogoDesactivarAdmin() {
        AlertDialog.Builder(this)
            .setTitle("Salir del Modo Administrador")
            .setMessage("¿Deseas desactivar el modo administrador?")
            .setPositiveButton("Sí") { _, _ ->
                isAdminMode = false
                saveAdminMode(false)
                Toast.makeText(this, "Modo Administrador Desactivado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
    }

}