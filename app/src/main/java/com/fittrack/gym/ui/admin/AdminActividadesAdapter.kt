package com.fittrack.gym.ui.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fittrack.gym.R
import com.fittrack.gym.data.entity.Actividad

class AdminActividadesAdapter(
    private val onEditar: (Actividad) -> Unit,
    private val onBorrar: (Actividad) -> Unit
) : RecyclerView.Adapter<AdminActividadesAdapter.VH>() {

    private val items = mutableListOf<Actividad>()

    fun submitList(lista: List<Actividad>) {
        items.clear()
        items.addAll(lista)
        notifyDataSetChanged()
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvNombre: TextView = v.findViewById(R.id.tvNombreAdmin)
        val tvHorario: TextView = v.findViewById(R.id.tvHorarioAdmin)
        val btnEditar: Button = v.findViewById(R.id.btnEditarAdmin)
        val btnBorrar: TextView = v.findViewById(R.id.btnBorrarAdmin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_actividad, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val a = items[position]

        holder.tvNombre.text = a.nombre
        holder.tvHorario.text = "${a.horaInicio}–${a.horaFin} · Aforo: ${a.aforo}"

        holder.btnEditar.setOnClickListener { onEditar(a) }
        holder.btnBorrar.setOnClickListener { onBorrar(a) }
    }
}