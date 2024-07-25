package com.example.tarea2_3_grupo5.tarea2_3_Grupo5

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.tarea2_3_grupo5.R

class MainActivity : AppCompatActivity() {
    var subirImagen: Button? = null
    var verImagenes: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        subirImagen = findViewById<View>(R.id.btnSubirImagen) as Button
        verImagenes = findViewById<View>(R.id.btnVerImagenes) as Button

        val buttonClick = View.OnClickListener { view ->
            var actividad: Class<*>? = null
            if (view.id == R.id.btnSubirImagen) {
                actividad = ActivitySubirImagen::class.java
            }
            if (view.id == R.id.btnVerImagenes) {
                actividad = ActivityVerImagenes::class.java
            }
            if (actividad != null) {
                moveActivity(actividad)
            }
        }

        subirImagen!!.setOnClickListener(buttonClick)
        verImagenes!!.setOnClickListener(buttonClick)
    }

    private fun moveActivity(actividad: Class<*>) {
        val intent = Intent(applicationContext, actividad)
        startActivity(intent)
    }
}