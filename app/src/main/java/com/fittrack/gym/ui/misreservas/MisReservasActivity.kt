package com.fittrack.gym.ui.misreservas

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fittrack.gym.R
import com.fittrack.gym.data.AppDatabase
import com.fittrack.gym.domain.ActividadRepository
import com.fittrack.gym.domain.ReservaRepository
import com.fittrack.gym.ui.detalle.DetalleActividadActivity

class MisReservasActivity : AppCompatActivity() {

    private lateinit var viewModel: MisReservasViewModel
    private lateinit var adapter: MisReservasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_reservas)

        // Flecha back (texto "<-")
        val tvBack = findViewById<ImageView>(R.id.tvBackMisReservas)
        tvBack.setOnClickListener { finish() }

        // RecyclerView
        val rv = findViewById<RecyclerView>(R.id.rvMisReservas)
        rv.layoutManager = LinearLayoutManager(this)

        adapter = MisReservasAdapter(
            onClickItem = { actividad ->
                // Abrir detalle al tocar cualquier tarjeta
                val intent = Intent(this, DetalleActividadActivity::class.java)
                intent.putExtra("actividad", actividad)
                startActivity(intent)
            },
            onClickAccion = { item ->
                // Pulsan "Anular" o "Salir de lista"
                viewModel.onBotonReserva(item.reserva.id)
            }
        )
        rv.adapter = adapter

        // ---- Repos + ViewModel ----
        val db = AppDatabase.get(this)   // usa el mismo método que en MainActivity
        val actividadRepo = ActividadRepository(db)
        val reservaRepo = ReservaRepository(db)

        val factory = MisReservasViewModelFactory(
            actividadRepo,
            reservaRepo,
            usuarioId = 2  // cliente demo
        )

        viewModel = ViewModelProvider(this, factory)[MisReservasViewModel::class.java]

        // Observers list and messages
        viewModel.items.observe(this) { lista ->
            adapter.submit(lista)
        }

        viewModel.mensaje.observe(this) { msg ->
            if (!msg.isNullOrEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                viewModel.onMensajeMostrado()
            }
        }

        // Cargamos al entrar
        viewModel.cargarReservas()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { finish(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }
}