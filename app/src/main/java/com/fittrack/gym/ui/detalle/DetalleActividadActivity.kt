package com.fittrack.gym.ui.detalle

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.fittrack.gym.R
import com.fittrack.gym.data.entity.Actividad

class DetalleActividadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_actividad)

        // --- Flecha back arriba a la izquierda ---
        val tvBack = findViewById<ImageView>(R.id.tvBackDetalle)
        tvBack.setOnClickListener { finish() }

        // ---- Referencias a vistas ----
        val imgActividad: ImageView = findViewById(R.id.imgActividad)
        val tvNombre: TextView = findViewById(R.id.tvNombreDetalle)
        val tvHorario: TextView = findViewById(R.id.tvHorarioDetalle)
        val tvDescripcion: TextView = findViewById(R.id.tvDescripcionDetalle)

        // ---- Recuperar la actividad del Intent ----
        val actividad: Actividad? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("actividad", Actividad::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("actividad") as? Actividad
        }

        if (actividad == null) {
            // Si viene nulo, cerramos la pantalla
            finish()
            return
        }
        // Rellenar nombre y horario
        tvNombre.text = actividad.nombre
        tvHorario.text = "${actividad.horaInicio}–${actividad.horaFin} · Aforo: ${actividad.aforo}"

        // ---- Descripción + imagen según el nombre ----
        val nombre = actividad.nombre.lowercase()

        val (desc, imgRes) = when {
            nombre.contains("bodypump") ->
                "BodyPump es un entrenamiento de fuerza completo con barra y discos. " +
                        "Trabajas todos los grupos musculares al ritmo de la música." to
                        R.drawable.img_bodypump

            nombre.contains("bodycombat") ->
                "BodyCombat combina movimientos de artes marciales y boxeo " +
                        "en una clase de cardio de alta intensidad." to
                        R.drawable.img_bodycombat

            nombre.contains("zumba") ->
                "Clase de baile fitness con ritmos latinos. Divertida, energética " +
                        "y ¡perfecta para quemar calorías sin darte cuenta!." to
                        R.drawable.img_zumba

            nombre.contains("pilates") ->
                "Pilates se centra en la estabilidad, fuerza del core, la postura " +
                        "y la flexibilidad, con movimientos controlados." to
                        R.drawable.img_pilates

            nombre.contains("booty") || nombre.contains("abs") ->
                "Booty & Abs se enfoca en glúteos y abdomen con trabajo de fuerza " +
                        "y tonificación específica." to
                        R.drawable.img_booty_abs

            nombre.contains("spinning") ->
                "Entrenamiento cardiovascular en bicicleta indoor, con cambios de ritmo " +
                        "y de resistencia al ritmo de la música." to
                        R.drawable.img_spinning

            nombre.contains("yoga") ->
                "Sesión de yoga para mejorar flexibilidad, control de la respiración " +
                        "y reducir el estrés." to
                        R.drawable.img_yoga

            else ->
                "Clase colectiva ${actividad.nombre} para mejorar tu condición física y sentirte mejor. " +
                        "Consulta en recepción o con el monitor para más detalles." to
                        R.drawable.img_generic_clase
        }

        tvDescripcion.text = desc
        imgActividad.setImageResource(imgRes)
    }

    // Para que la flecha de la barra haga 'back'
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
