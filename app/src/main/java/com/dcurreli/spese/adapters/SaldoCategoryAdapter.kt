package com.dcurreli.spese.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dcurreli.spese.R
import com.dcurreli.spese.data.dto.BalanceCategory
import com.dcurreli.spese.data.dto.BalanceSubItem

class BalanceCategoryAdapter(private val context: Context) : RecyclerView.Adapter<BalanceCategoryAdapter.ViewHolder>() {

    private val balanceCategoryList = ArrayList<BalanceCategory>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.load_spese_tab_saldo_category, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val balanceCategory = balanceCategoryList[position]
        holder.buyerTextView.text = "${balanceCategory.buyer} ha speso:"
        holder.paidAmountTextView.text =  balanceCategory.getPaidAmountAsEUR()
        setSublist(holder.subItemsRecyclerView, balanceCategoryList[position].amountsToReceive)

    }

    private fun setSublist(subListRecyclerView: RecyclerView, amountToReceiveList: ArrayList<BalanceSubItem>) {
        val subItemAdapter = SaldoSubItemAdapter(amountToReceiveList)
        subListRecyclerView.layoutManager = LinearLayoutManager(context)
        subListRecyclerView.adapter = subItemAdapter
    }

    override fun getItemCount(): Int {
        return balanceCategoryList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(newBalanceCategoryList : List<BalanceCategory>){
        balanceCategoryList.clear()
        balanceCategoryList.addAll(newBalanceCategoryList)
        notifyDataSetChanged()
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val buyerTextView: TextView = itemView.findViewById(R.id.load_spese_saldo_pagatore)
        val paidAmountTextView: TextView = itemView.findViewById(R.id.saldo_importo_totale_pagato)
        val subItemsRecyclerView : RecyclerView = itemView.findViewById(R.id.lista_saldo_subitems)
    }
}