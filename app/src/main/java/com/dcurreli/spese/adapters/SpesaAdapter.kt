package com.dcurreli.spese.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.dcurreli.spese.R
import com.dcurreli.spese.data.entity.Expense

class SpesaAdapter : RecyclerView.Adapter<SpesaAdapter.ViewHolder>() {

    private var speseList = ArrayList<Expense>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.load_spese_tab_spese_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val spesa = speseList[position]
        holder.spesa.text = spesa.expense
        holder.importo.text = spesa.amountAsEur()
        holder.data.text = spesa.expenseDate
        holder.pagatore.text = spesa.buyer

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return speseList.size
    }

    fun getItem(position: Int): Expense {
        return speseList[position]
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(newSpesaList : List<Expense>){
        speseList.clear()
        speseList.addAll(newSpesaList)
        notifyDataSetChanged()
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val importo: TextView = itemView.findViewById(R.id.spesa_importo)
        val data: TextView = itemView.findViewById(R.id.spesa_data)
        val spesa: AppCompatTextView = itemView.findViewById(R.id.spesa_spesa)
        val pagatore: AppCompatTextView = itemView.findViewById(R.id.spesa_pagatore)
    }
}