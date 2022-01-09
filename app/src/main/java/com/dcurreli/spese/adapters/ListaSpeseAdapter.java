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
import com.dcurreli.spese.objects.ListaSpese;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

public class ListaSpeseAdapter extends RecyclerView.Adapter<ListaSpeseAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<ListaSpese> listaSpeseList;
    private ActivityMainBinding binding;
    private NavController navController;

    public ListaSpeseAdapter(Context context, ArrayList<ListaSpese> listaSpeseList, ActivityMainBinding binding, NavController navController) {
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
        View v = LayoutInflater.from(context).inflate(R.layout.activity_main_menu_laterale_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ListaSpese listaSpese = listaSpeseList.get(position);
        holder.nome.setText(listaSpese.getNome());

        //gestisco l'evento on click
        holder.nome.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("toolbarTitle", holder.nome.getText().toString());

                //Chiudo il menu
                binding.drawerMainActivity.closeDrawer(GravityCompat.START);

                //Navigo sul fragment successivo passandogli il bundle
                navController.navigate(R.id.settingsFragment, bundle);
            }
        });
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

