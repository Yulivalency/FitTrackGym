package com.fittrack.gym.ui.calendario

import android.content.Intent
import android.view.MenuItem
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fittrack.gym.databinding.ActivityCalendarioBinding
import com.fittrack.gym.data.AppDatabase
import com.fittrack.gym.domain.ActividadRepository
import com.fittrack.gym.domain.ReservaRepository
import com.fittrack.gym.ui.detalle.DetalleActividadActivity
import com.fittrack.gym.ui.misreservas.MisReservasActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class CalendarioActivity: AppCompatActivity() {

    private lateinit var binding: ActivityCalendarioBinding
    private lateinit var adapter: CalendarioAdapter
    private lateinit var viewModel: CalendarioViewModel
    private lateinit var diasAdapter: DiasCalendarioAdapter

    // NUEVO: último día seleccionado
    private var diaSeleccionadoActual: DiaCalendarioUi? = null

    private fun generarDiasSemana(): List<DiaCalendarioUi> {
        val cal = Calendar.getInstance()   // Hoy
        val formatterDia = SimpleDateFormat("EEE", Locale("es", "ES"))

        val dias = mutableListOf<DiaCalendarioUi>()

        for (i in 0 until 7) {
            val diaCortoRaw = formatterDia.format(cal.time) // "lun.", "mar.", etc.
            val diaCorto = diaCortoRaw
                .replace(".", "")
                .replaceFirstChar { it.uppercase() }

            val numero = cal.get(Calendar.DAY_OF_MONTH).toString()

            val diaSemanaCalendar = cal.get(Calendar.DAY_OF_WEEK)
            val diaSemanaInt = when (diaSemanaCalendar) {
                Calendar.MONDAY    -> 1
                Calendar.TUESDAY   -> 2
                Calendar.WEDNESDAY -> 3
                Calendar.THURSDAY  -> 4
                Calendar.FRIDAY    -> 5
                Calendar.SATURDAY  -> 6
                Calendar.SUNDAY    -> 7
                else               -> 1
            }

            dias += DiaCalendarioUi(
                diaSemanaInt = diaSemanaInt,
                diaSemanaCorto = diaCorto,
                numero = numero
            )

            // Pasamos al siguiente día
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }

        return dias
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvBackCalendario.setOnClickListener {
            finish()   // vuelve a MainActivity
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)   // flecha atrás en la barra
        supportActionBar?.title = ""

        // 1) DB y repositorios de dominio
        val db = AppDatabase.get(this)
        val actividadRepo = ActividadRepository(db)
        val reservaRepo = ReservaRepository(db)

        // Usuario de prueba (luego lo podrás sacar de login / prefs)
        val usuarioId = 2 // Cliente demo

        // 2) ViewModel + Factory
        val factory = CalendarioViewModelFactory(
            actividadRepository = actividadRepo,
            reservaRepository = reservaRepo,
            usuarioId = usuarioId
        )
        viewModel = ViewModelProvider(this, factory)[CalendarioViewModel::class.java]

        // 3) Botón de reservas
        binding.cardVerReservas.setOnClickListener {
            startActivity(Intent(this, MisReservasActivity::class.java))
        }

        // ----- Recycler horizontal de días -----
        binding.rvDias.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        diasAdapter = DiasCalendarioAdapter { diaSeleccionado ->
            // Guardamos el día tocado
            diaSeleccionadoActual = diaSeleccionado
            // Y recargamos el calendario para ese día
            viewModel.cargarCalendario(diaSeleccionado)
        }
        binding.rvDias.adapter = diasAdapter

            // Generamos la semana y seleccionamos el primer día por defecto
        val diasSemana = generarDiasSemana()
        diasAdapter.submit(diasSemana)

            // Día por defecto (por ejemplo el primero de la lista)
        diaSeleccionadoActual = diasSemana.firstOrNull()
        diaSeleccionadoActual?.let { viewModel.cargarCalendario(it) }

        // 4) LayoutManager para el RecyclerView
        binding.rvCalendario.layoutManager = LinearLayoutManager(this)

       //  Instancia del Adapter con los 2 callbacks (tap y reservar)
        adapter = CalendarioAdapter(
            onClickActividad = { item ->
                // Abrir detalle con la actividad
                val intent = Intent(this, DetalleActividadActivity::class.java)
                intent.putExtra("actividad", item.actividad)   // la Actividad es Serializable
                startActivity(intent)
            },
            onClickReserva = { actividadId ->
                viewModel.onToggleReserva(actividadId)
            }
        )

       // Conectar el adapter al Recycler
        binding.rvCalendario.adapter = adapter

       //  (Opcional) divisor entre filas
        binding.rvCalendario.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )
        // 4) Observar los items del ViewModel
        viewModel.items.observe(this) { lista ->
            adapter.submit(lista)
        }

        // 5) Observar mensajes (reservar/anular/salir de lista)
        viewModel.mensaje.observe(this) { msg ->
            if (msg != null) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                viewModel.onMensajeMostrado()
            }
        }
        // 6) Cargar datos iniciales
        //viewModel.cargarCalendario()
    }

    override fun onResume() {
        super.onResume()
        // Cuando volvemos desde "Mis reservas", recargamos para el día actual
        diaSeleccionadoActual?.let { dia ->
            viewModel.cargarCalendario(dia)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()   // vuelve a la pantalla anterior
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}