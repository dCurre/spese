package com.dcurreli.spese.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dcurreli.spese.R
import com.dcurreli.spese.data.entity.Spesa

class SaldoAdapter : RecyclerView.Adapter<SaldoAdapter.ViewHolder>() {

    private val speseList = ArrayList<Spesa>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.load_spese_tab_saldo_totali_item, parent, false))
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

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(newSpesaList : List<Spesa>){
        speseList.clear()
        speseList.addAll(newSpesaList)
        notifyDataSetChanged()
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val pagatore: TextView = itemView.findViewById(R.id.saldo_pagatore)
        val importo: TextView = itemView.findViewById(R.id.saldo_importo)
    }
}