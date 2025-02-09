package com.example.sushirestauranteapp.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sushirestauranteapp.R
import com.example.sushirestauranteapp.modelo.Plato

class RestauranteViewModel : ViewModel() {
    private val _carrito = MutableLiveData<MutableMap<Plato, Int>>(mutableMapOf())

    private val _platos = MutableLiveData<List<Plato>>()
    val platos: LiveData<List<Plato>> get() = _platos

    val listaPlatos = listOf(
        Plato(
            1, "Sushi Especial",
            "Sushi variado de alta calidad",
            "Nuestro Sushi Especial incluye una selección de los mejores cortes de pescado fresco, preparados con arroz de grano corto y un toque de vinagre de arroz, acompañado de salsa de soja y wasabi.",
            12.5, "Sushi", R.drawable.sushi
        ),

        Plato(
            2, "Wok de Pollo",
            "Salteado de pollo y verduras",
            "Un delicioso wok de pollo con trozos de pechuga de pollo jugosa, mezclado con verduras frescas y salteado en una combinación de salsas orientales. Servido con arroz jazmín y un toque de semillas de sésamo.",
            10.0, "Wok", R.drawable.wokpollo
        ),

        Plato(
            3, "Temaki de Salmón",
            "Cono de alga relleno de salmón",
            "El temaki de salmón es un rollo en forma de cono envuelto en alga nori, relleno con arroz sazonado, salmón fresco, aguacate y pepino. Perfecto para disfrutar con las manos, con un toque de mayonesa japonesa.",
            8.5, "Temaki", R.drawable.temaki
        ),

        Plato(
            4, "Nigiri de Atún",
            "Bocado de arroz con atún",
            "El nigiri de atún es un bocado clásico de la cocina japonesa. Se compone de una base de arroz de sushi moldeado a mano y cubierto con una fina lámina de atún rojo fresco. Acompañado de un toque de wasabi y salsa de soja.",
            7.0, "Nigiris", R.drawable.nigiri
        ),

        Plato(
            5, "Ramen Japonés",
            "Sopa de fideos con caldo",
            "Nuestro ramen japonés se elabora con un caldo de huesos cocido a fuego lento durante horas, logrando un sabor profundo y reconfortante. Servido con fideos artesanales, huevo marinado, alga nori y cebollín fresco.",
            9.5, "Sopas", R.drawable.sopa
        ),

        Plato(
            6, "Tarta de Matcha",
            "Postre de té verde japonés",
            "Un postre tradicional japonés con un suave bizcocho de matcha, equilibrado con notas dulces y amargas. Su textura esponjosa y su sabor refinado lo convierten en la opción perfecta para los amantes del té verde.",
            6.0, "Postre", R.drawable.matchabueno
        ),

        Plato(
            7, "Sake",
            "Vino de arroz japonés",
            "El sake es una bebida alcohólica tradicional japonesa elaborada a partir de arroz fermentado. De sabor suave y aroma delicado, puede disfrutarse caliente o frío, acompañando perfectamente platos de sushi y sashimi.",
            7.5, "Vinos", R.drawable.sake
        )
    )


    fun cargarPlatos() {
        _platos.value = listaPlatos
    }

    fun filtrarPorCategoria(categoria: String) {
        _platos.value = listaPlatos.filter { it.categoria == categoria }
    }

    fun agregarAlCarrito(plato: Plato, cantidad: Int) {
        val carritoActual = _carrito.value ?: mutableMapOf()
        carritoActual[plato] = (carritoActual[plato] ?: 0) + cantidad
        _carrito.value = carritoActual
    }

    fun vaciarCarrito() {
        _carrito.value = mutableMapOf()
    }

    fun obtenerCarrito(): List<Pair<Plato, Int>> {
        return _carrito.value?.toList() ?: emptyList()
    }

}
