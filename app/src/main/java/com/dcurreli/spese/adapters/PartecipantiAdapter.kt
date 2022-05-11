package com.dcurreli.spese.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dcurreli.spese.R
import com.dcurreli.spese.objects.Utente


class PartecipantiAdapter(private val utenteList: ArrayList<Utente>, private val listOwnerId: String) : RecyclerView.Adapter<PartecipantiAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.lista_settings_fragment_partecipanti_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val utente = utenteList[position]
        holder.partecipante.text = utente.nominativo

        //Log.i("prova link $position", ">>${utente.image}")
        //Glide.with(holder.itemView.context).load("https://lh3.googleusercontent.com/a-/AOh14Gj8PnDprCN5m7qsP50cfrLDXaXTB_dV6cZ9O5oTfg").diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.userImage)

        if(holder.itemView.context != null){
            Log.i("prova link $position", ">>${utente.image}")
            Glide.with(holder.userImage.context)
                .load("https://lh3.googleusercontent.com/a-/AOh14Gj8PnDprCN5m7qsP50cfrLDXaXTB_dV6cZ9O5oTfg=s96-c")
                .into(holder.userImage)
        }




        if (!utente.user_id.equals(listOwnerId, ignoreCase = true)) {
            holder.ownerIcon.visibility = View.GONE
        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return utenteList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val partecipante: TextView = itemView.findViewById(R.id.tw_partecipante)
        val userImage: ImageView = itemView.findViewById(R.id.partecipante_icon)
        val ownerIcon: ImageView = itemView.findViewById(R.id.owner_icon)
    }
}