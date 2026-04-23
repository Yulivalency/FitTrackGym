package com.fittrack.gym


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Ocultamos la ActionBar para que se vea limpio
        supportActionBar?.hide()

        // Tras 2 segundos pasamos a la pantalla principal
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish() // cerramos el splash para que no se vuelva atrás a él
        }, 3000)
    }
}