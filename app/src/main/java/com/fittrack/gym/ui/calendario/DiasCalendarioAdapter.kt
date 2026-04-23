package com.fittrack.gym.ui.calendario

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fittrack.gym.R

class DiasCalendarioAdapter(
    private val onDiaSeleccionado: (DiaCalendarioUi) -> Unit
) : RecyclerView.Adapter<DiasCalendarioAdapter.DiaVH>() {

    private val dias = mutableListOf<DiaCalendarioUi>()
    private var selectedPosition = 0

    fun submit(list: List<DiaCalendarioUi>) {
        dias.clear()
        dias.addAll(list)
        selectedPosition = 0
        notifyDataSetChanged()

        // Al iniciar, seleccionamos el primer día (hoy)
        if (dias.isNotEmpty()) {
            onDiaSeleccionado(dias[0])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaVH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dia_calendario, parent, false)
        return DiaVH(v)
    }

    override fun getItemCount(): Int = dias.size

    override fun onBindViewHolder(holder: DiaVH, position: Int) {
        holder.bind(dias[position], position == selectedPosition)
    }

    inner class DiaVH(v: View) : RecyclerView.ViewHolder(v) {
        private val tvDiaSemana: TextView = v.findViewById(R.id.tvDiaSemana)
        private val tvNumeroDia: TextView = v.findViewById(R.id.tvNumeroDia)

        init {
            v.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    val dia = dias[pos]

                    val oldPos = selectedPosition
                    selectedPosition = pos
                    notifyItemChanged(oldPos)
                    notifyItemChanged(selectedPosition)

                    onDiaSeleccionado(dia)
                }
            }
        }

        fun bind(dia: DiaCalendarioUi, selected: Boolean) {
            tvDiaSemana.text = dia.diaSemanaCorto
            tvNumeroDia.text = dia.numero
            itemView.isSelected = selected   // activa bg_dia_calendario_selector
        }
    }
}