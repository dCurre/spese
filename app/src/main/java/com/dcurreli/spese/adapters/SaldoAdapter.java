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

public class SaldoAdapter extends RecyclerView.Adapter<SaldoAdapter.MyViewHolder> {

    private Context context;
    private final ArrayList<Spesa> speseList;

    public SaldoAdapter(Context context, ArrayList<Spesa> speseList) {
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
        View v = LayoutInflater.from(context).inflate(R.layout.load_spese_tab_saldo_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Spesa spesa = speseList.get(position);

        holder.pagatore.setText(spesa.getPagatore());
        holder.importo.setText(spesa.importoAsTextEuro());

    }

    @Override
    public int getItemCount() {
        return speseList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView pagatore, importo;

        public MyViewHolder(@NotNull View itemView) {
            super(itemView);

            pagatore = itemView.findViewById(R.id.saldo_pagatore);
            importo = itemView.findViewById(R.id.saldo_importo);

        }
    }
}
