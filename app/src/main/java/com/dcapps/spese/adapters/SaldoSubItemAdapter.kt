package com.dcapps.spese.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dcapps.spese.R
import com.dcapps.spese.data.dto.BalanceSubItem

class SaldoSubItemAdapter(private val saldoSubItemList: ArrayList<BalanceSubItem>) : RecyclerView.Adapter<SaldoSubItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.load_spese_tab_saldo_subitem, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dareAvere = saldoSubItemList[position]

        holder.daPagareA.text = "deve a ${dareAvere.receiver}"
        holder.importoDaPagare.text = dareAvere.getAmountToPayAsEur()
        holder.importoDaPagare.setTextColor(if(dareAvere.getAmountToPayFixed() == 0.00) holder.itemView.resources.getColor(R.color.green) else holder.itemView.resources.getColor(R.color.red))

        //Per tutte le righe dispari
        if(position%2 != 0){
            holder.recView.setBackgroundColor(holder.itemView.resources.getColor(R.color.alternativeSecondary))
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return saldoSubItemList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val recView: RelativeLayout = itemView.findViewById(R.id.saldo_subitem_layout)
        val daPagareA: TextView = itemView.findViewById(R.id.load_spese_saldo_da_pagare_a)
        val importoDaPagare: TextView = itemView.findViewById(R.id.saldo_importo_da_pagare)
    }
}