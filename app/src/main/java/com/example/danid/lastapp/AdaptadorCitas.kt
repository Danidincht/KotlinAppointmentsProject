package com.example.danid.lastapp


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.example.danid.lastapp.OnRecyclerClick
import com.example.danid.lastapp.R

import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.zip.Inflater

/**
 * Created by danid on 19/01/2018.
 */

class AdaptadorCitas(lista: ArrayList<Cita>) : RecyclerView.Adapter<AdaptadorCitas.CitasViewHolder>() {
    internal var lista: ArrayList<Cita>

    init {
        this.lista = lista
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitasViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cita_layout, null)

        return CitasViewHolder(view)
    }

    override fun onBindViewHolder(holder: CitasViewHolder, position: Int) {
        holder.empresa.text = lista[holder.adapterPosition].empresa
        holder.cliente.text = lista[holder.adapterPosition].cliente
        holder.fecha.text = SimpleDateFormat("dd/MM/yyyy").format(lista[holder.adapterPosition].fecha)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

     inner class CitasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val empresa: TextView
         val cliente: TextView
         val fecha: TextView

        init {
            empresa = itemView.findViewById(R.id.companyNameTxt)
            cliente = itemView.findViewById(R.id.clientNameTxt)
            fecha = itemView.findViewById(R.id.dateTimeTxt)
        }
    }
}
