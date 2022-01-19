package com.dcurreli.spese.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dcurreli.spese.R;
import com.dcurreli.spese.objects.DareAvere;
import com.dcurreli.spese.objects.Spesa;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class DareAvereAdapter extends RecyclerView.Adapter<DareAvereAdapter.MyViewHolder> {

    private Context context;
    private final ArrayList<DareAvere> dareAvereList;

    public DareAvereAdapter(Context context, ArrayList<DareAvere> dareAvereList) {
        this.context = context;
        this.dareAvereList = dareAvereList;
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
        View v = LayoutInflater.from(context).inflate(R.layout.load_spese_tab_dare_avere_saldo_item, parent, false);
        return new MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DareAvere spesa = dareAvereList.get(position);

        holder.dare.setText(spesa.getDare());
        holder.avere.setText(spesa.getAvere());
        holder.saldoImporto.setText(spesa.getImportoAsEur());

    }

    @Override
    public int getItemCount() {
        return dareAvereList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView dare, avere, saldoImporto;

        public MyViewHolder(@NotNull View itemView) {
            super(itemView);

            dare = itemView.findViewById(R.id.saldo_dare);
            avere = itemView.findViewById(R.id.saldo_avere);
            saldoImporto = itemView.findViewById(R.id.saldo_importo);

        }
    }
}
