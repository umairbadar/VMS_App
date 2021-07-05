package com.fyp.vmsapp.ui.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.vmsapp.R;
import com.fyp.vmsapp.utilities.Constants;
import com.fyp.vmsapp.utilities.RecyclerViewItemInterface;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    Context context;
    List<HistoryModel> list;
    RecyclerViewItemInterface itemInterface;

    public HistoryAdapter(Context context, List<HistoryModel> list, RecyclerViewItemInterface itemInterface) {
        this.context = context;
        this.list = list;
        this.itemInterface = itemInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryModel item = list.get(position);

        holder.tv_name.setText(item.getName());
        holder.tv_date.setText("Date Time: " + item.getCreated_at());

        if (item.getStatus() == 0) {
            holder.tv_status.setText("Not Injected");
            holder.btn_view_slip.setEnabled(false);
        } else if (item.getStatus() == 1) {
            holder.tv_status.setText("Vaccinated from " + item.getHospital_name() + " but slip not uploaded");
            holder.btn_view_slip.setEnabled(false);
        } else {
            holder.tv_status.setText("Vaccinated from " + item.getHospital_name() + " & Slip uploaded");
            holder.btn_view_slip.setEnabled(true);
        }

        holder.btn_view_slip.setOnClickListener(v -> {
            String url = Constants.BaseURL + "/public/storage/" + item.getSlip_img();
            itemInterface.itemClick(url, 1);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name, tv_status, tv_date;
        Button btn_view_slip;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_status = itemView.findViewById(R.id.tv_status);
            tv_date = itemView.findViewById(R.id.tv_date);

            btn_view_slip = itemView.findViewById(R.id.btn_view_slip);
        }
    }
}
