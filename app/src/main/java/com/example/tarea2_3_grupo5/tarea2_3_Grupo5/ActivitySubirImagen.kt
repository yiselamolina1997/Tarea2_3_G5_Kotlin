package com.example.tarea2_3_grupo5.tarea2_3_Grupo5

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.tarea2_3_grupo5.R
import com.example.tarea2_3_grupo5.tarea2_3_Grupo5.Config.SQLiteConnection
import com.example.tarea2_3_grupo5.tarea2_3_Grupo5.Config.Transacciones
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

class ActivitySubirImagen() : AppCompatActivity() {
    var conexion: SQLiteConnection? = null
    var areaFoto: ImageView? = null
    var tomarImagen: Button? = null
    var seleccionarImagen: Button? = null
    var salvarImagen: Button? = null
    var regresarSubir: Button? = null
    var descripcion: EditText? = null
    var imagenByteArray: ByteArray? = null
    var imageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subir_imagen)

        conexion = SQLiteConnection(this, Transacciones.namedb, null, 1)
        areaFoto = findViewById<View>(R.id.imageviewFoto) as ImageView
        tomarImagen = findViewById<View>(R.id.btnTomarImagen) as Button
        seleccionarImagen = findViewById<View>(R.id.btnSeleccionarImagen) as Button
        salvarImagen = findViewById<View>(R.id.btnSalvar) as Button
        descripcion = findViewById<View>(R.id.txtDescripcion) as EditText
        regresarSubir = findViewById<View>(R.id.btnRegresarSubir) as Button

        tomarImagen!!.setOnClickListener(View.OnClickListener { permisos() })

        seleccionarImagen!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                if (ContextCompat.checkSelfPermission(
                        this@ActivitySubirImagen,
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Si el permiso no es otrogado, lo pide
                    ActivityCompat.requestPermissions(
                        this@ActivitySubirImagen,
                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                        REQUEST_CODE
                    )
                } else {
                    // Crea el intent para seleccionar la imagen
                    val intent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, PICK_IMAGE_REQUEST)
                }
            }
        })

        salvarImagen!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                if (descripcion!!.text.toString().trim { it <= ' ' }.isEmpty()) {
                    descripcion!!.error =
                        "Porfavor ingrese una descripción para la imagen, no se permiten campos vacios!!!"
                } else if (imagenByteArray == null) {
                    Toast.makeText(
                        applicationContext,
                        "Porfavor tome una foto o seleccione una foto a subir!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    addImagen()
                }
            }
        })

        val buttonClick: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(view: View) {
                var actividad: Class<*>? = null
                if (view.id == R.id.btnRegresarSubir) {
                    actividad = MainActivity::class.java
                }
                if (actividad != null) {
                    moveActivity(actividad)
                }
            }
        }

        regresarSubir!!.setOnClickListener(buttonClick)
    }

    private fun moveActivity(actividad: Class<*>) {
        val intent = Intent(applicationContext, actividad)
        startActivity(intent)
    }

    private fun permisos() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                peticion_acceso_camera
            )
        } else {
            tomarFoto()
        }
    }

    private fun tomarFoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, peticion_toma_fotografia)
        }
    }

    //override al metodo para procesar la seleccion de la imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((requestCode == PICK_IMAGE_REQUEST) && (resultCode == RESULT_OK) && (data != null)) {
            val selectedImageUri = data.data

            // Actualiza el imageview para colocar la imagen seleccionada
            areaFoto!!.setImageURI(selectedImageUri)

            try {
                val inputStream = contentResolver.openInputStream((selectedImageUri)!!)
                imagenByteArray = getBytes(inputStream)
                // La imagen ahora se encuentra en forma de byte array dentro de la variable byteArray para poder guardarse en la base de datos
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else if (requestCode == peticion_toma_fotografia && resultCode == RESULT_OK) {
            try {
                val extras = data!!.extras
                imageBitmap = extras!!["data"] as Bitmap?
                areaFoto!!.setImageBitmap(imageBitmap)

                val stream = ByteArrayOutputStream()
                imageBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
                imagenByteArray = stream.toByteArray()
            } catch (ex: Exception) {
                ex.toString()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Crea el intent para seleccionar la imagen
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, PICK_IMAGE_REQUEST)
            } else {
                showPermissionExplanation()
            }
        }
    }

    @Throws(IOException::class)
    private fun getBytes(inputStream: InputStream?): ByteArray {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len: Int
        while ((inputStream!!.read(buffer).also { len = it }) != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }

    private fun showPermissionExplanation() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permiso Requerido")
        builder.setMessage("Para acceder a tu galería y seleccionar una imagen, necesitamos el permiso de almacenamiento. Por favor, otorga el permiso en la configuración de la aplicación.")
        builder.setPositiveButton("Ir a Ajustes", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                // Abre los ajustes de la aplicacion
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", packageName, null)
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        })
        builder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
            }
        })
        builder.show()
    }

    private fun addImagen() {
        try {
            val db = conexion!!.writableDatabase

            val valores = ContentValues()
            valores.put(Transacciones.descripcion, descripcion!!.text.toString())
            valores.put(Transacciones.foto, imagenByteArray)

            val result = db.insert(Transacciones.tablaImagenes, Transacciones.id, valores)

            Toast.makeText(this, getString(R.string.respuesta), Toast.LENGTH_SHORT).show()
            db.close()
            areaFoto!!.setImageResource(R.drawable.uploadimage)
            descripcion!!.setText("")
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.errorIngreso), Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        val peticion_acceso_camera: Int = 101
        val peticion_toma_fotografia: Int = 102
        private val REQUEST_CODE = 123
        private val PICK_IMAGE_REQUEST = 1
    }
}