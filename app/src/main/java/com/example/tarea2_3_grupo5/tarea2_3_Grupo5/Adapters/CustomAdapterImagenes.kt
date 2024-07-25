package com.example.tarea2_3_grupo5.tarea2_3_Grupo5.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tarea2_3_grupo5.R
import com.example.tarea2_3_grupo5.tarea2_3_Grupo5.Adapters.CustomAdapterImagenes.CustomViewHolder
import com.example.tarea2_3_grupo5.tarea2_3_Grupo5.Models.imageItem

class CustomAdapterImagenes(private val context: Context, private val dataList: List<imageItem>) :
    RecyclerView.Adapter<CustomViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.custom_list_item_imagenes, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val data = dataList[position]

        // Bind data to views
        holder.descripcion.text = data.itemDescription
        holder.image.setImageBitmap(data.imageBitmap) // Set the Bitmap to the ImageView
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var descripcion: TextView = itemView.findViewById(R.id.txtListItemDescription)
        var image: ImageView = itemView.findViewById(R.id.imageviewListItemImagenes)
    }
}
