package com.fittrack.gym.ui.ocupacion

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fittrack.gym.R
import com.fittrack.gym.data.AppDatabase
import com.fittrack.gym.domain.OcupacionRepository

class OcupacionActivity : AppCompatActivity() {

    private lateinit var viewModel: OcupacionViewModel
    private lateinit var adapter: OcupacionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocupacion)

        // Flecha back
        val tvBack = findViewById<ImageView>(R.id.tvBackOcupacion)
        tvBack.setOnClickListener { finish() }

        // RecyclerView
        val rv = findViewById<RecyclerView>(R.id.rvOcupacion)
        rv.layoutManager = LinearLayoutManager(this)
        adapter = OcupacionAdapter()
        rv.adapter = adapter

        // Repos + ViewModel
        val db = AppDatabase.get(this)
        val ocupacionRepo = OcupacionRepository(db)

        val factory = OcupacionViewModelFactory(ocupacionRepo)
        viewModel = ViewModelProvider(this, factory)[OcupacionViewModel::class.java]

        // Observa lista
        viewModel.items.observe(this) { lista ->
            adapter.submit(lista)
        }

        // Carga datos
        viewModel.cargarOcupacion()
    }
}