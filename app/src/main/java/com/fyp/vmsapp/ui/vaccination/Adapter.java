package com.fyp.vmsapp.ui.vaccination;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.vmsapp.R;
import com.fyp.vmsapp.utilities.RecyclerViewItemInterface;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    List<Model> list;
    Context context;
    RecyclerViewItemInterface itemListener;

    public Adapter(List<Model> list, Context context, RecyclerViewItemInterface itemListener) {
        this.list = list;
        this.context = context;
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vaccination, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Model item = list.get(position);

        holder.tv_name.setText(item.getName());

        holder.btn_inject.setOnClickListener(view -> itemListener.delete(item.getId()));
        holder.btn_upload_slip.setOnClickListener(view -> itemListener.itemClick("2", item.getId()));

        if (item.getStatus() == 0){
            holder.btn_inject.setEnabled(true);
            holder.btn_upload_slip.setEnabled(false);
        } else if (item.getStatus() == 1){
            holder.btn_inject.setEnabled(false);
            holder.btn_upload_slip.setEnabled(true);
        } else {
            holder.btn_inject.setEnabled(false);
            holder.btn_upload_slip.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_name;
        Button btn_upload_slip, btn_inject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);

            btn_inject = itemView.findViewById(R.id.btn_inject);
            btn_upload_slip = itemView.findViewById(R.id.btn_upload_slip);
        }
    }
}
