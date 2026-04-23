package com.fittrack.gym.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fittrack.gym.R
import com.fittrack.gym.data.AppDatabase
import com.fittrack.gym.domain.ActividadRepository
import com.fittrack.gym.data.entity.Actividad


class AdminActividadesActivity : AppCompatActivity() {

    private lateinit var viewModel: AdminActividadesViewModel
    private lateinit var adapter: AdminActividadesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_actividades)

        // --- Flecha back "<-" ---
        val tvBack = findViewById<ImageView>(R.id.tvBackAdmin)
        tvBack.setOnClickListener { finish() }

        // --- RecyclerView ---
        val rv = findViewById<RecyclerView>(R.id.rvAdminActividades)
        rv.layoutManager = LinearLayoutManager(this)

        adapter = AdminActividadesAdapter(
            onEditar = { actividad ->
                mostrarDialogoActividad(actividad)
            },
            onBorrar = { actividad ->
                viewModel.onBorrarActividad(actividad)
            }
        )
        rv.adapter = adapter

        // --- Botón "Nueva actividad" (CardView cuadrado lila) ---
        val fab = findViewById<FloatingActionButton>(R.id.fabNuevaActividad)
        fab.setOnClickListener {
            mostrarDialogoActividad(null)   // null = crear nueva
        }

        // --- Repos + ViewModel ---
        val db = AppDatabase.get(this)
        val actividadRepo = ActividadRepository(db)
        val factory = AdminActividadesViewModelFactory(actividadRepo)
        viewModel = ViewModelProvider(this, factory)[AdminActividadesViewModel::class.java]

        // Observar lista
        viewModel.actividades.observe(this) { lista ->
            adapter.submitList(lista)
        }

        // Observar mensajitos
        viewModel.mensaje.observe(this) { msg ->
            if (msg != null) {
                android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show()
                viewModel.onMensajeMostrado()
            }
        }

        // Cargar datos al entrar
        viewModel.cargarActividades()
    }
    private fun mostrarDialogoActividad(actividadInicial: Actividad?) {
        // 1) Inflar la vista del diálogo
        val view = LayoutInflater.from(this)
            .inflate(R.layout.dialog_editar_actividad, null, false)

        // 2) Referencias a los EditText
        val etNombre = view.findViewById<EditText>(R.id.etNombre)
        val etDia = view.findViewById<EditText>(R.id.etDiaSemana)
        val etInicio = view.findViewById<EditText>(R.id.etHoraInicio)
        val etFin = view.findViewById<EditText>(R.id.etHoraFin)
        val etAforo = view.findViewById<EditText>(R.id.etAforo)

        // 3) Referencias a los botones personalizados del layout
        val btnCancelar = view.findViewById<Button>(R.id.btnCancelar)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardar)

        // 4) Rellenar datos si es edición
        if (actividadInicial != null) {
            etNombre.setText(actividadInicial.nombre)
            etDia.setText(actividadInicial.diaSemana.toString())
            etInicio.setText(actividadInicial.horaInicio)
            etFin.setText(actividadInicial.horaFin)
            etAforo.setText(actividadInicial.aforo.toString())
        }

        // 5) Crear el diálogo SIN setPositive/NegativeButton
        val dialog = AlertDialog.Builder(this)
            .setTitle(if (actividadInicial == null) "Nueva actividad" else "Editar actividad")
            .setView(view)
            .create()

        // 6) Lógica de los botones del layout

        // Cancelar
        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        // Guardar
        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val dia = etDia.text.toString().toIntOrNull() ?: 1
            val inicio = etInicio.text.toString().trim()
            val fin = etFin.text.toString().trim()
            val aforo = etAforo.text.toString().toIntOrNull() ?: 0

            // (Opcional) validaciones básicas
            if (nombre.isEmpty()) {
                etNombre.error = "Introduce un nombre"
                return@setOnClickListener
            }

            val act = Actividad(
                id = actividadInicial?.id ?: 0,
                nombre = nombre,
                diaSemana = dia,
                horaInicio = inicio,
                horaFin = fin,
                aforo = aforo
            )

            viewModel.guardar(act)
            dialog.dismiss()
        }

        // 7) Mostrar el diálogo
        dialog.show()
    }
}