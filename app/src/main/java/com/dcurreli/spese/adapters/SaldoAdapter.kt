package com.dcurreli.spese.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dcurreli.spese.R
import com.dcurreli.spese.objects.Spesa

class SaldoAdapter(private val speseList: ArrayList<Spesa>) : RecyclerView.Adapter<SaldoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.load_spese_tab_totali_saldo_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val spesa = speseList[position]
        holder.pagatore.text = spesa.pagatore
        holder.importo.text = spesa.importoAsTextEuro()

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return speseList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val pagatore: TextView = itemView.findViewById(R.id.saldo_pagatore)
        val importo: TextView = itemView.findViewById(R.id.saldo_importo)
    }
}