package com.example.tarea2_3_grupo5.tarea2_3_Grupo5

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tarea2_3_grupo5.R
import com.example.tarea2_3_grupo5.tarea2_3_Grupo5.Adapters.CustomAdapterImagenes
import com.example.tarea2_3_grupo5.tarea2_3_Grupo5.Config.SQLiteConnection
import com.example.tarea2_3_grupo5.tarea2_3_Grupo5.Config.Transacciones
import com.example.tarea2_3_grupo5.tarea2_3_Grupo5.Models.imageItem

class ActivityVerImagenes : AppCompatActivity() {
    var conexion: SQLiteConnection? = null
    var lista: RecyclerView? = null
    var regresar: Button? = null
    var dataList: MutableList<imageItem>? = null
    var adapter: CustomAdapterImagenes? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_imagenes)

        conexion = SQLiteConnection(this, Transacciones.namedb, null, 1)
        lista = findViewById<View>(R.id.recyclerViewListaImagenes) as RecyclerView
        regresar = findViewById<View>(R.id.btnRegresar) as Button
        dataList = ArrayList()

        imagenes

        // Configuración del administrador de diseño y adaptador para el RecyclerView
        val layoutManager = LinearLayoutManager(this)
        lista!!.layoutManager = layoutManager
        adapter = CustomAdapterImagenes(this, dataList as ArrayList<imageItem>)
        lista!!.adapter = adapter

        val buttonClick = View.OnClickListener { view ->
            var actividad: Class<*>? = null
            if (view.id == R.id.btnRegresar) {
                actividad = MainActivity::class.java
            }
            if (actividad != null) {
                moveActivity(actividad)
            }
        }

        regresar!!.setOnClickListener(buttonClick)
    }

    private val imagenes: Unit
        @SuppressLint("Range")
        get() {
            try {
                val db = conexion!!.readableDatabase
                dataList!!.clear() // Clear the list to avoid duplicates

                val cursor = db.rawQuery(Transacciones.SelectTableImagenes, null)

                while (cursor.moveToNext()) {
                    val description =
                        cursor.getString(cursor.getColumnIndex(Transacciones.descripcion))
                    val imageBytes = cursor.getBlob(cursor.getColumnIndex(Transacciones.foto))
                    val imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                    dataList!!.add(imageItem(imageBitmap, description))
                }

                cursor.close()

                adapter!!.notifyDataSetChanged()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

    private fun moveActivity(actividad: Class<*>) {
        val intent = Intent(applicationContext, actividad)
        startActivity(intent)
    }
}