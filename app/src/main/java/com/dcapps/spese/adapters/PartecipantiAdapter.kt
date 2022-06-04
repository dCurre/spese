package com.dcapps.spese.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dcapps.spese.R
import com.dcapps.spese.data.entity.User


class PartecipantiAdapter : RecyclerView.Adapter<PartecipantiAdapter.ViewHolder>() {

    private val userList = ArrayList<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.lista_settings_fragment_partecipanti_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val utente = userList[position]
        holder.partecipante.text = utente.fullname
        if (position > 0) {
            holder.ownerIcon.visibility = View.INVISIBLE
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return userList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(newUserList : List<User>){
        userList.clear()
        userList.addAll(newUserList)
        notifyDataSetChanged()
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val partecipante: TextView = itemView.findViewById(R.id.tw_partecipante)
        val ownerIcon: ImageView = itemView.findViewById(R.id.owner_icon)
    }
}