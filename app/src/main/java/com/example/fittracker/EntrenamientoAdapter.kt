package com.example.fittracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class EntrenamientoAdapter(private val lista: List<Entrenamiento>) :
    RecyclerView.Adapter<EntrenamientoAdapter.ViewHolder>() {

    // aqui guardo las vistas de cada item
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tipo: TextView = view.findViewById(R.id.itemTipo)
        val minutos: TextView = view.findViewById(R.id.itemMinutos)
        val intensidad: TextView = view.findViewById(R.id.itemIntensidad)
        val estrellas: RatingBar = view.findViewById(R.id.itemEstrellas)
        val checks: TextView = view.findViewById(R.id.itemChecks)
    }

    // crea la vista del item usando item_entrenamiento.xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_entrenamiento, parent, false)
        return ViewHolder(vista)
    }

    // pone los datos de cada entrenamiento en su item
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entrenamiento = lista[position]

        holder.tipo.text = entrenamiento.tipo
        holder.minutos.text = "${entrenamiento.minutos} min"
        holder.intensidad.text = "Intensidad: ${entrenamiento.intensidad}"
        holder.estrellas.rating = entrenamiento.estrellas.toFloat()

        // color segun la intensidad
        val color = when (entrenamiento.intensidad) {
            "Baja" -> R.color.intensidad_baja
            "Media" -> R.color.intensidad_media
            else -> R.color.intensidad_alta
        }
        holder.intensidad.setTextColor(ContextCompat.getColor(holder.itemView.context, color))

        if (entrenamiento.checks.isEmpty()) {
            holder.checks.text = "No marcaste nada"
        } else {
            holder.checks.text = entrenamiento.checks.joinToString(" - ") { "✓ $it" }
        }
    }

    override fun getItemCount() = lista.size
}