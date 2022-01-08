package com.dcurreli.spese.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dcurreli.spese.R;
import com.dcurreli.spese.objects.ListaSpese;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

public class ListaSpeseAdapter extends RecyclerView.Adapter<ListaSpeseAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<ListaSpese> listaSpeseList;

    public ListaSpeseAdapter(Context context, ArrayList<ListaSpese> listaSpeseList) {
        this.context = context;
        this.listaSpeseList = listaSpeseList;
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
        View v = LayoutInflater.from(context).inflate(R.layout.menu_laterale_item_lista, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ListaSpese listaSpese = listaSpeseList.get(position);
        holder.nome.setText(listaSpese.getNome());
    }

    @Override
    public int getItemCount() {
        return listaSpeseList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nome;

        public MyViewHolder(@NotNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.lista_nome);
        }
    }

}

