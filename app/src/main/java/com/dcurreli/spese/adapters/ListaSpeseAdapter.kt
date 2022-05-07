package com.dcurreli.spese.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.dcurreli.spese.R
import com.dcurreli.spese.objects.ListaSpese
import com.dcurreli.spese.utils.GenericUtils.createBundleForListaSpese

class ListaSpeseAdapter(private val context: Context, private val listaSpeseList: ArrayList<ListaSpese>, private val navController: NavController) : RecyclerView.Adapter<ListaSpeseAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.home_fragment_lista_spese, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val listaSpese = listaSpeseList[position]
        holder.nomeSpesa.text = listaSpese.nome

        //Gestisco l'evento on click
        holder.relativeLayout.setOnClickListener {
            //Navigo sul fragment successivo passandogli il bundle con id lista e nome lista
            navController.navigate(
                R.id.action_HomeFragment_to_loadSpeseFragment,
                createBundleForListaSpese(
                    listaSpese.id,
                    holder.nomeSpesa.text.toString()
                )
            )
        }

        if (listaSpese.isSaldato) {
            holder.saldato.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
        } else {
            holder.saldato.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return listaSpeseList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val nomeSpesa: TextView = itemView.findViewById(R.id.nome_spesa)
        val saldato: ImageView = itemView.findViewById(R.id.saldato_bar)
        val relativeLayout: RelativeLayout = itemView.findViewById(R.id.spesa_list_first_layout)
    }
}