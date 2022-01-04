package com.dcurreli.spese.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dcurreli.spese.R;
import com.dcurreli.spese.objects.Spesa;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SpesaAdapter extends RecyclerView.Adapter<SpesaAdapter.MyViewHolder> {

    Context context;
    ArrayList<Spesa> speseList;

    public SpesaAdapter(Context context, ArrayList<Spesa> speseList) {
        this.context = context;
        this.speseList = speseList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.spese_list, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Spesa spesa = speseList.get(position);
        //holder.id.setText(spesa.getIdAsText());
        holder.spesa.setText(spesa.getSpesa());
        holder.importo.setText(spesa.importoAsTextEuro());
        holder.data.setText(spesa.getData());
        holder.pagatore.setText(spesa.getPagatore());
    }

    @Override
    public int getItemCount() {
        return speseList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView id, spesa, importo, data, pagatore;

        public MyViewHolder(@NotNull View itemView){
            super(itemView);
            //id = itemView.findViewById(R.id.spesa_id);
            spesa = itemView.findViewById(R.id.spesa_spesa);
            importo = itemView.findViewById(R.id.spesa_importo);
            data = itemView.findViewById(R.id.spesa_data);
            pagatore = itemView.findViewById(R.id.spesa_pagatore);
        }
    }
}
