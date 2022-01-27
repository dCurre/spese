package com.dcurreli.spese.adapters;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.dcurreli.spese.R;
import com.dcurreli.spese.objects.Spesa;
import com.dcurreli.spese.utils.SpesaUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SpesaAdapter extends RecyclerView.Adapter<SpesaAdapter.MyViewHolder> {

    private Context context;
    private final ArrayList<Spesa> speseList;

    public SpesaAdapter(Context context, ArrayList<Spesa> speseList) {
        this.context = context;
        this.speseList = speseList;
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
        View v = LayoutInflater.from(context).inflate(R.layout.load_spese_tab_spese_item, parent, false);
        return new MyViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Spesa spesa = speseList.get(position);
        holder.spesa.setText(spesa.getSpesa());
        holder.importo.setText(spesa.importoAsTextEuro());
        holder.data.setText(spesa.getData());
        holder.pagatore.setText(spesa.getPagatore());
    }

    @Override
    public int getItemCount() {
        return speseList.size();
    }

    public Spesa getItem(int position){
        return speseList.get(position);
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView importo, data, spesaNotFound;
        AppCompatTextView spesa, pagatore;

        public MyViewHolder(@NotNull View itemView) {
            super(itemView);

            spesa = itemView.findViewById(R.id.spesa_spesa);
            importo = itemView.findViewById(R.id.spesa_importo);
            data = itemView.findViewById(R.id.spesa_data);
            pagatore = itemView.findViewById(R.id.spesa_pagatore);
            spesaNotFound = itemView.findViewById(R.id.spese_not_found);
        }
    }

}
