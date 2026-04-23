package com.fittrack.gym

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.content.Intent
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.fittrack.gym.data.AppDatabase
import com.fittrack.gym.domain.ActividadRepository
import com.fittrack.gym.domain.ReservaRepository
import com.fittrack.gym.ui.admin.AdminActividadesActivity
import com.fittrack.gym.ui.calendario.CalendarioActivity
import com.fittrack.gym.ui.misreservas.MisReservasActivity
import com.fittrack.gym.ui.ocupacion.OcupacionActivity
import com.fittrack.gym.util.SeedData
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var actividadRepo: ActividadRepository
    private lateinit var reservaRepo: ReservaRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // La barra la puedes ocultar para que se vea más limpio
        supportActionBar?.hide()

        // 1) Instancia de la base de datos
        db = AppDatabase.get(this)
        actividadRepo = ActividadRepository(db)
        reservaRepo = ReservaRepository(db)

        val btnCalendario: LinearLayout = findViewById(R.id.btnCalendario)
        val btnMisReservas: LinearLayout = findViewById(R.id.btnMisReservas)
        val btnOcupacion: LinearLayout = findViewById(R.id.btnOcupacion)
        val btnAdmin: LinearLayout = findViewById(R.id.btnAdminActividades)

        //  Auto-seed: si no hay actividades, cargamos datos demo
        lifecycleScope.launch {
            val hayActividades = actividadRepo.tieneActividades()
            if (!hayActividades) {
                SeedData.run(db)
            }
        }

        // Ir al calendario de clases
        btnCalendario.setOnClickListener {
            startActivity(Intent(this, CalendarioActivity::class.java))
        }

        // Ir directamente a "Mis reservas" (atajo)
        btnMisReservas.setOnClickListener {
            startActivity(Intent(this, MisReservasActivity::class.java))
        }

        // Ir directamente a "Ocupación"
        btnOcupacion.setOnClickListener {
            startActivity(Intent(this, OcupacionActivity::class.java))
        }

        // Ir directamente a las acciones del Admin
        btnAdmin.setOnClickListener {
            startActivity(Intent(this, AdminActividadesActivity::class.java))
        }

        // Cargar datos demo (solo para pruebas)
        findViewById<TextView>(R.id.tvSeedDemo).setOnClickListener {
            lifecycleScope.launch {
                SeedData.run(db)
                Toast.makeText(
                    this@MainActivity,
                    "Datos demo cargados",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}