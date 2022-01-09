package com.dcurreli.spese.adapters;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.dcurreli.spese.R;
import com.dcurreli.spese.databinding.ActivityMainBinding;
import com.dcurreli.spese.objects.Mese;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MeseAdapter extends RecyclerView.Adapter<MeseAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Mese> meseList;
    private ActivityMainBinding binding;
    private NavController navController;

    public MeseAdapter(Context context, ArrayList<Mese> meseList, ActivityMainBinding binding, NavController navController) {
        this.context = context;
        this.meseList = meseList;
        this.binding = binding;
        this.navController = navController;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.menu_laterale_item_mese, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Mese mese = meseList.get(position);
        holder.nomeMese.setText(mese.getNome());

        if (mese.isSaldato())
            holder.saldatoImage.setBackgroundResource(R.drawable.ic_saldato_true);

        //gestisco l'evento on click
        holder.nomeMese.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("toolbarTitle", holder.nomeMese.getText().toString());

                //Chiudo il menu
                binding.drawerMainActivity.closeDrawer(GravityCompat.START);

                //Navigo sul fragment successivo passandogli il bundle
                navController.navigate(R.id.loadSpeseFragment, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return meseList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nomeMese;
        View saldatoImage;

        public MyViewHolder(@NotNull View itemView) {
            super(itemView);
            nomeMese = itemView.findViewById(R.id.mese_nome);
            saldatoImage = itemView.findViewById(R.id.mese_saldato);
        }
    }
}
