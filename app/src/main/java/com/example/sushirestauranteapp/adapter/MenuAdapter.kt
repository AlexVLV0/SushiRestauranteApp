package com.example.sushirestauranteapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.sushirestauranteapp.R
import com.example.sushirestauranteapp.main.DetallePlatoActivity
import com.example.sushirestauranteapp.modelo.Plato

class MenuAdapter(
    private var listaPlatos: List<Plato>,
    private val onClickAgregar: (Plato, Int) -> Unit,
    private val context: Context
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    private val cantidades = mutableMapOf<Int, Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_plato, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val plato = listaPlatos[position]
        val cantidadActual = cantidades[plato.id] ?: 1
        holder.bind(plato, cantidadActual, onClickAgregar, context)
    }

    override fun getItemCount(): Int = listaPlatos.size

    @SuppressLint("NotifyDataSetChanged")
    fun actualizarLista(nuevaLista: List<Plato>) {
        listaPlatos = nuevaLista
        cantidades.clear()
        notifyDataSetChanged()
    }

    class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDescripcion: TextView = itemView.findViewById(R.id.tvPlatoDescripcion)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvPlatoNombre)
        private val tvPrecio: TextView = itemView.findViewById(R.id.tvPlatoPrecio)
        private val imgPlato: ImageView = itemView.findViewById(R.id.imgPlatoButton)
        private val btnRestar: Button = itemView.findViewById(R.id.btnRestar)
        private val btnSumar: Button = itemView.findViewById(R.id.btnSumar)
        private val tvCantidad: TextView = itemView.findViewById(R.id.tvCantidad)
        private val btnAgregar: Button = itemView.findViewById(R.id.btnAgregar)

        @SuppressLint("SetTextI18n")
        fun bind(plato: Plato, cantidadInicial: Int, onClickAgregar: (Plato, Int) -> Unit, context: Context) {
            tvDescripcion.text = plato.descripcion
            tvNombre.text = plato.nombre
            tvPrecio.text = "${plato.precio} €"


            when (plato.imagenResId) {
                is Int -> imgPlato.setImageResource(plato.imagenResId)
                is String -> imgPlato.setImageURI(Uri.parse(plato.imagenResId))
                else -> imgPlato.setImageResource(R.drawable.sushi)
            }

            tvCantidad.text = cantidadInicial.toString()

            imgPlato.setOnClickListener {
                val intent = Intent(context, DetallePlatoActivity::class.java).apply {
                    putExtra("PLATO_NOMBRE", plato.nombre)
                    putExtra("PLATO_DETALLE", plato.detalle)
                }
                context.startActivity(intent)
            }

            var cantidad = cantidadInicial

            btnSumar.setOnClickListener {
                cantidad++
                tvCantidad.text = cantidad.toString()
            }

            btnRestar.setOnClickListener {
                if (cantidad > 1) {
                    cantidad--
                    tvCantidad.text = cantidad.toString()
                }
            }

            btnAgregar.setOnClickListener {
                onClickAgregar(plato, cantidad)
                Toast.makeText(context, "${cantidad}x ${plato.nombre} añadido al carrito", Toast.LENGTH_SHORT).show()
                cantidad = 1
                tvCantidad.text = cantidad.toString()
            }
        }
    }
}
