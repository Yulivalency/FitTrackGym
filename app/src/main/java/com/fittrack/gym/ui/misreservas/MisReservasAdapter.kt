package com.fittrack.gym.ui.misreservas

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.fittrack.gym.R
import com.fittrack.gym.data.entity.Actividad

class MisReservasAdapter(

    private val onClickItem: (Actividad) -> Unit,
    private val onClickAccion: (ReservaUiItem) -> Unit
) : RecyclerView.Adapter<MisReservasAdapter.VH>() {

    private val items = mutableListOf<ReservaUiItem>()

    fun submit(list: List<ReservaUiItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvNombre: TextView = v.findViewById(R.id.tvNombre)
        val tvHorario: TextView = v.findViewById(R.id.tvHorario)
        val tvBadge: TextView = v.findViewById(R.id.tvBadge)
        val btnAccion: Button = v.findViewById(R.id.btnAccion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reserva, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val a = item.actividad

        holder.tvNombre.text = a.nombre
        holder.tvHorario.text = "${a.horaInicio}–${a.horaFin} · Aforo: ${a.aforo}"
        holder.btnAccion.text = item.textoBoton

        // Tap en la tarjeta → abre DetalleActividadActivity
        holder.itemView.setOnClickListener {
            onClickItem(a)
        }

        // Tap en el botón → Anular / Salir de lista
        holder.btnAccion.setOnClickListener {
            onClickAccion(item)
        }
        // --- CHIP (badge) ---
        holder.tvBadge.text = item.badgeTexto

        when (item.badgeTexto.lowercase()) {
            "reservada" -> {
                // rojo / aforo lleno
                holder.tvBadge.setBackgroundResource(R.drawable.bg_chip_lleno)
                holder.tvBadge.setTextColor(Color.WHITE)
            }
            "en espera" -> {
                // verde / disponible, reusamos tu chip verde
                holder.tvBadge.setBackgroundResource(R.drawable.bg_chip_disponible)
                holder.tvBadge.setTextColor(Color.WHITE)
            }
            else -> {
                // por si acaso, usa el badge normal
                holder.tvBadge.setBackgroundResource(R.drawable.bg_badge)
                holder.tvBadge.setTextColor(holder.tvBadge.context.getColor(R.color.fit_orange_primary))
            }
        }

        // --- BOTÓN (Anular / Salir de lista) ---
        holder.btnAccion.text = item.textoBoton

        // Quitamos tint por si el tema de Material lo está sobreescribiendo
        holder.btnAccion.backgroundTintList = null
        ViewCompat.setBackgroundTintList(holder.btnAccion, null)

        when (item.textoBoton.lowercase()) {
            "anular" -> {
                // igual que botón "Ver" (morado degradado)
                holder.btnAccion.setBackgroundResource(R.drawable.bg_boton_morado)
                holder.btnAccion.setTextColor(Color.WHITE)
            }
            "salir de lista" -> {
                // si quieres que sea naranja como "Reservar"
                holder.btnAccion.setBackgroundResource(R.drawable.bg_boton_naranja)
                holder.btnAccion.setTextColor(Color.WHITE)
            }
            else -> {
                // fallback: outline naranja
                holder.btnAccion.setBackgroundResource(R.drawable.bg_boton_outline_orange)
                holder.btnAccion.setTextColor(
                    holder.btnAccion.context.getColor(R.color.fit_orange_primary)
                )
            }
        }

        holder.btnAccion.setOnClickListener {
            onClickAccion(item)
        }
    }
}