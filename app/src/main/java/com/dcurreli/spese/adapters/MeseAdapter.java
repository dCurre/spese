package com.dcurreli.spese.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dcurreli.spese.R;
import com.dcurreli.spese.objects.Mese;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MeseAdapter extends RecyclerView.Adapter<MeseAdapter.MyViewHolder> {

    Context context;
    ArrayList<Mese> meseList;
    MenuInflater menuInflater;
    Menu menu;

    public MeseAdapter(Context context, ArrayList<Mese> meseList, MenuInflater menuInflater, Menu menu) {
        this.context = context;
        this.meseList = meseList;
        this.menuInflater = menuInflater;
        this.menu = menu;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        menuInflater.inflate(R.menu.menu_laterale, menu);
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Mese mese = meseList.get(position);
        holder.nomeMese.setTitle(mese.getNome());
    }

    @Override
    public int getItemCount() {
        return meseList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        MenuItem nomeMese;

        public MyViewHolder(@NotNull View itemView){
            super(itemView);
            //nomeMese = itemView.findViewById(R.id.mese);
        }
    }
}
