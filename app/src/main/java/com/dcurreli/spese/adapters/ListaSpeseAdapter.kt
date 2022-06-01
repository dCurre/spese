package com.dcurreli.spese.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.dcurreli.spese.R
import com.dcurreli.spese.data.entity.ListaSpese
import com.dcurreli.spese.utils.GenericUtils

class ListaSpeseAdapter(private val navController: NavController) : RecyclerView.Adapter<ListaSpeseAdapter.ViewHolder>() {

    private var listaSpeseList = ArrayList<ListaSpese>()

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
                GenericUtils.createBundleForListaSpese(
                    listaSpese.id,
                    holder.nomeSpesa.text.toString()
                )
            )
        }

        holder.relativeLayout.setBackgroundResource(if (listaSpese.isSaldato) R.drawable.lista_liste_saldato else R.drawable.lista_liste_da_saldare)

    }

    override fun getItemCount(): Int {
        return listaSpeseList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(newListaSpeseList : List<ListaSpese>){
        listaSpeseList.clear()
        listaSpeseList.addAll(newListaSpeseList)
        notifyDataSetChanged()
    }


    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val nomeSpesa: TextView = itemView.findViewById(R.id.nome_spesa)
        val relativeLayout: RelativeLayout = itemView.findViewById(R.id.spesa_list_layout)
    }
}