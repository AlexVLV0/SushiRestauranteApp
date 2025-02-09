package com.example.sushirestauranteapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sushirestauranteapp.databinding.ItemCarritoBinding
import com.example.sushirestauranteapp.modelo.Plato

class CarritoAdapter(private val carrito: Map<Plato, Int>) :
    RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

    class CarritoViewHolder(private val binding: ItemCarritoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(plato: Plato, cantidad: Int) {
            binding.tvNombrePlato.text = plato.nombre
            binding.tvCantidad.text = "Cantidad: $cantidad"
            val subtotal = cantidad * plato.precio
            binding.tvSubtotal.text = "Subtotal: $${"%.2f".format(subtotal)}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val binding = ItemCarritoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CarritoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        val item = carrito.entries.elementAt(position)
        holder.bind(item.key, item.value)
    }

    override fun getItemCount(): Int = carrito.size
}
