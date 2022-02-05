package com.dcurreli.spese.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.dcurreli.spese.R;
import com.dcurreli.spese.databinding.ActivityMainBinding;
import com.dcurreli.spese.databinding.HomeFragmentBinding;
import com.dcurreli.spese.objects.ListaSpese;
import com.dcurreli.spese.utils.GenericUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ListaSpeseAdapter extends RecyclerView.Adapter<ListaSpeseAdapter.MyViewHolder> {

    private Context context;
    private final ArrayList<ListaSpese> listaSpeseList;
    private final HomeFragmentBinding binding;
    private final NavController navController;

    public ListaSpeseAdapter(Context context, ArrayList<ListaSpese> listaSpeseList, HomeFragmentBinding binding, NavController navController) {
        this.context = context;
        this.listaSpeseList = listaSpeseList;
        this.binding = binding;
        this.navController = navController;
    }
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.home_fragment_lista_spese, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ListaSpese listaSpese = listaSpeseList.get(position);
        holder.nomeSpesa.setText(listaSpese.getNome());

        //gestisco l'evento on click
        holder.arrow.setOnClickListener(view -> {
            //Navigo sul fragment successivo passandogli il bundle con id lista e nome lista
            navController.navigate(R.id.loadSpeseFragment, GenericUtils.INSTANCE.createBundleForListaSpese(listaSpese.getId(), holder.nomeSpesa.getText().toString()));
        });

        if(listaSpese.isSaldato()){
            holder.saldato.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
        }else{
            holder.saldato.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        }
    }

    @Override
    public int getItemCount() {
        return listaSpeseList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nomeSpesa;
        ImageView arrow, saldato;

        public MyViewHolder(@NotNull View itemView) {
            super(itemView);
            nomeSpesa = itemView.findViewById(R.id.nome_spesa);
            arrow = itemView.findViewById(R.id.navigate_to_list_arrow);
            saldato = itemView.findViewById(R.id.saldato_bar);
        }
    }

}