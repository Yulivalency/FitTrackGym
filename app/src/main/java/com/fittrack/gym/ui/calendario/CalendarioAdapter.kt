package com.fittrack.gym.ui.calendario

import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fittrack.gym.R

class CalendarioAdapter(
    private val onClickActividad: (ActividadCalendarioItem) -> Unit,
    private val onClickReserva: (Int) -> Unit
) : RecyclerView.Adapter<CalendarioAdapter.ItemVH>() {

    // Lista de elementos que viene del ViewModel
    private val items = mutableListOf<ActividadCalendarioItem>()

    fun submit(list: List<ActividadCalendarioItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemVH {
        val v = LayoutInflater.from(parent.context)
              .inflate(R.layout.item_actividad, parent, false)
        return ItemVH(v, onClickActividad, onClickReserva)
    }

    override fun onBindViewHolder(holder: ItemVH, position: Int) {
        holder.bind(items[position])
    }

    class ItemVH(
        v: View,
        private val onClickActividad: (ActividadCalendarioItem) -> Unit,
        private val onClickReserva: (Int) -> Unit
    ) : RecyclerView.ViewHolder(v) {

        private val tvNombre: TextView = v.findViewById(R.id.tvNombre)
        private val tvHorarioAforo: TextView = v.findViewById(R.id.tvHorarioAforo)
        private val tvBadgeLibres: TextView = v.findViewById(R.id.tvBadgeLibres)
        private val btnReservar: Button = v.findViewById(R.id.btnReservar)
        private val btnVerDetalle: Button = v.findViewById(R.id.btnVerDetalle)

        private var actual: ActividadCalendarioItem? = null

        init {
            // Tap en toda la tarjeta
            v.setOnClickListener { actual?.let(onClickActividad) }

            // Tap en el botón Ver → abre detalle
            btnVerDetalle.setOnClickListener {
                actual?.let(onClickActividad)
            }

            // Tap en el botón Reservar/Anular/Salir de lista
            btnReservar.setOnClickListener {
                actual?.let { item ->
                    onClickReserva(item.actividad.id)
                }
            }
        }

        fun bind(item: ActividadCalendarioItem) {
            actual = item
            val a = item.actividad

            // Nombre y horario
            tvNombre.text = a.nombre
            tvHorarioAforo.text = "${a.horaInicio}–${a.horaFin} · Aforo: ${a.aforo}"

            // Badge según estado de reserva
            when (item.estadoReservaUi.tipo) {
                TipoReservaUi.NINGUNA -> {
                    tvBadgeLibres.text = "Disponible"
                    tvBadgeLibres.setBackgroundResource(R.drawable.bg_chip_disponible)
                }
                TipoReservaUi.RESERVADA -> {
                    tvBadgeLibres.text = "Reservada"
                    tvBadgeLibres.setBackgroundResource(R.drawable.bg_chip_lleno) // o uno rojo
                }
                TipoReservaUi.EN_ESPERA -> {
                    tvBadgeLibres.text = "En espera"
                    tvBadgeLibres.setBackgroundResource(R.drawable.bg_chip_lleno)
                }
            }

            // Texto del botón viene del ViewModel
            btnReservar.text = item.textoBoton   // "Reservar" / "Anular" / "Salir de lista"
        }
    }
}