package com.dcurreli.spese.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dcurreli.spese.R
import com.dcurreli.spese.objects.SaldoSubItem

class SaldoSubItemAdapter(private val saldoSubItemList: ArrayList<SaldoSubItem>) : RecyclerView.Adapter<SaldoSubItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.load_spese_tab_saldo_subitem, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dareAvere = saldoSubItemList[position]

        holder.daPagareA.text = dareAvere.daPagareA
        holder.importoDaPagare.text = dareAvere.importoDaPagareFixedAsEur
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return saldoSubItemList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val daPagareA: TextView = itemView.findViewById(R.id.load_spese_saldo_da_pagare_a)
        val importoDaPagare: TextView = itemView.findViewById(R.id.saldo_importo_da_pagare)
    }
}