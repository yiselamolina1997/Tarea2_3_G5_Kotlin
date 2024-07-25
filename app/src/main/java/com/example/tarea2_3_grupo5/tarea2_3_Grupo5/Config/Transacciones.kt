package com.example.tarea2_3_grupo5.tarea2_3_Grupo5.Config

object Transacciones {
    //Nombre de la base de datos
    const val namedb: String = "PM01Tarea2_3"

    //Tabla de Contactos
    const val tablaImagenes: String = "imagenes"

    //Campos de la tabla de Imagenes
    const val id: String = "id"
    const val descripcion: String = "descripcion"
    const val foto: String = "foto"

    //Consultas de tabla contactos
    const val CreateTableImagenes: String =
        "CREATE TABLE " + tablaImagenes + "( id INTEGER PRIMARY KEY AUTOINCREMENT," + descripcion + " TEXT," + foto + " BLOB)"
    const val DropTableImagenes: String = "DROP TABLE IF EXISTS" + tablaImagenes
    const val SelectTableImagenes: String = "SELECT * FROM " + tablaImagenes
}
