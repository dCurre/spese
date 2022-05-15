package com.dcurreli.spese.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dcurreli.spese.R
import com.dcurreli.spese.objects.SaldoCategory
import com.dcurreli.spese.objects.SaldoSubItem

class SaldoCategoryAdapter(private val context: Context, private val dareAvereList: ArrayList<SaldoCategory>) : RecyclerView.Adapter<SaldoCategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.load_spese_tab_saldo_category, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val dareAvere = dareAvereList[position]

        holder.pagatore.text = dareAvere.pagatore
        holder.importoTotalePagato.text = dareAvere.importoPagatoAsEur
        setSublist(holder.subListRecyclerView, dareAvereList[position].pagatoreImportoDaAvere)

    }

    private fun setSublist(subListRecyclerView: RecyclerView, pagatoreImportoDaAvere: ArrayList<SaldoSubItem>?) {
        val subItemAdapter = SaldoSubItemAdapter(pagatoreImportoDaAvere!!)
        subListRecyclerView.layoutManager = LinearLayoutManager(context)
        subListRecyclerView.adapter = subItemAdapter
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return dareAvereList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val pagatore: TextView = itemView.findViewById(R.id.load_spese_saldo_pagatore)
        val importoTotalePagato: TextView = itemView.findViewById(R.id.saldo_importo_totale_pagato)
        val subListRecyclerView : RecyclerView = itemView.findViewById(R.id.lista_saldo_subitems)
    }
}