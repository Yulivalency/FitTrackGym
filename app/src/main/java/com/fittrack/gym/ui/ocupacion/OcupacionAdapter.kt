package com.fittrack.gym.ui.ocupacion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fittrack.gym.R

class OcupacionAdapter : RecyclerView.Adapter<OcupacionAdapter.VH>() {

    private val items = mutableListOf<OcupacionUiItem>()

    fun submit(lista: List<OcupacionUiItem>) {
        items.clear()
        items.addAll(lista)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ocupacion, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        private val tvNombre: TextView = v.findViewById(R.id.tvNombre)
        private val tvDetalle: TextView = v.findViewById(R.id.tvDetalle)
        private val pbOcupacion: ProgressBar = v.findViewById(R.id.pbOcupacion)
        private val tvPorcentaje: TextView = v.findViewById(R.id.tvPorcentaje)
        private val tvBadgeOcup: TextView = v.findViewById(R.id.tvBadgeOcup)

        fun bind(item: OcupacionUiItem) {
            val a = item.actividad

            tvNombre.text = a.nombre
            tvDetalle.text = "${a.horaInicio}–${a.horaFin} · Aforo: ${a.aforo}"

            val aforo = a.aforo
            val ocupadas = item.plazasOcupadas
            val porcentaje = item.porcentaje   // esto ya viene calculado

            pbOcupacion.max = 100
            pbOcupacion.progress = porcentaje
            tvPorcentaje.text = "$porcentaje% · $ocupadas/$aforo"

            // LÓGICA VISUAL: lleno vs disponible
            if (ocupadas >= aforo && aforo > 0) {
                // AFORO LLENO
                tvBadgeOcup.text = "Aforo lleno"
                tvBadgeOcup.setBackgroundResource(R.drawable.bg_chip_lleno)

                pbOcupacion.progressDrawable = ContextCompat.getDrawable(
                    itemView.context,
                    R.drawable.bg_progress_ocupacion
                )
            } else {
                // DISPONIBLE
                tvBadgeOcup.text = "Disponible"
                tvBadgeOcup.setBackgroundResource(R.drawable.bg_chip_disponible)

                pbOcupacion.progressDrawable = ContextCompat.getDrawable(
                    itemView.context,
                    R.drawable.bg_progress_ocup_disponible
                )
            }
        }
    }
}