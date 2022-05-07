package com.dcurreli.spese.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dcurreli.spese.R
import com.dcurreli.spese.objects.DareAvere

class DareAvereAdapter(private val dareAvereList: ArrayList<DareAvere>) : RecyclerView.Adapter<DareAvereAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.load_spese_tab_dare_avere_saldo_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val spesa = dareAvereList[position]
        holder.dare.text = spesa.dare
        holder.avere.text = spesa.avere
        holder.saldoImporto.text = spesa.importoAsEur
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return dareAvereList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val dare: TextView = itemView.findViewById(R.id.saldo_dare)
        val avere: TextView = itemView.findViewById(R.id.saldo_avere)
        val saldoImporto: TextView = itemView.findViewById(R.id.saldo_importo)
    }
}