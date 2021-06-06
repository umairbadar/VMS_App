package com.fyp.vmsapp.ui.upcoming_vaccination;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.vmsapp.R;

import java.util.List;

public class AdapterUpcomingVaccinations extends RecyclerView.Adapter<AdapterUpcomingVaccinations.ViewHolder> {

    Context context;
    List<ModelUpcomingVaccination> list;

    public AdapterUpcomingVaccinations(Context context, List<ModelUpcomingVaccination> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_upcoming_vaccination, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelUpcomingVaccination item = list.get(position);

        holder.tv_family_member_name.setText("Family Member Name: " + item.getFamily_member_name());
        holder.tv_vaccination_name.setText("Upcoming Vaccination: " + item.getVaccination_name());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_family_member_name, tv_vaccination_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_family_member_name = itemView.findViewById(R.id.tv_family_member_name);
            tv_vaccination_name = itemView.findViewById(R.id.tv_vaccination_name);
        }
    }
}
