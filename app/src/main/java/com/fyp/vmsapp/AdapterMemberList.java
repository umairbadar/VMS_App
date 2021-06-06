package com.fyp.vmsapp;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.fyp.vmsapp.utilities.Constants;
import com.fyp.vmsapp.utilities.RecyclerViewItemInterface;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterMemberList extends RecyclerView.Adapter<AdapterMemberList.ViewHolder> implements Filterable {

    List<ModelMemberList> list;
    List<ModelMemberList> allPickups;
    RecyclerViewItemInterface itemListener;

    public AdapterMemberList(List<ModelMemberList> list, RecyclerViewItemInterface itemListener) {
        this.list = list;
        this.itemListener = itemListener;
        allPickups = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member_list, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelMemberList item = list.get(position);

        holder.tv_name.setText(item.getName());
        holder.tv_dob.setText("Date of Birth: " + item.getDate_of_birth());
        holder.tv_blood_group.setText("Blood Group: " + item.getBlood_group());
        holder.tv_age_group.setText("Age Group: " + item.getAge_group());
        holder.tv_relationship.setText(item.getRelationship());
        holder.tv_desc.setText("Description: " + item.getDesc());

        String url = Constants.BaseURL + "/storage/app/public/" + item.getImage_path();

        if (!item.getImage_path().equals("")){
            Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.no_image)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .fit()
                    .centerInside()
                    .into(holder.circleImageView);
        }

        holder.mainLayout.setOnClickListener(view ->
                itemListener.itemClick(String.valueOf(item.getAge_group_id()), item.getFamily_member_id()));


        holder.circleImageView.setOnClickListener(view ->
                itemListener.itemClick(String.valueOf(item.getAge_group_id()), item.getFamily_member_id()));

        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemListener.edit(
                        item.getImage_path(),
                        item.getFamily_member_id(),
                        item.getBlood_group_id(),
                        item.getRelationship_id(),
                        item.getName(),
                        item.getDesc(),
                        item.getDate_of_birth()
                );
            }
        });

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemListener.delete(item.getFamily_member_id());
            }
        });

        if (item.getRelationship_id() == 10){
            holder.btn_delete.setVisibility(View.GONE);
            holder.btn_edit.setVisibility(View.GONE);
            holder.tv_desc.setVisibility(View.GONE);

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_name, tv_dob, tv_blood_group, tv_age_group, tv_relationship, tv_desc;
        CircleImageView circleImageView;
        ConstraintLayout mainLayout;
        ImageButton btn_delete, btn_edit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            btn_edit = itemView.findViewById(R.id.btn_edit);
            btn_delete = itemView.findViewById(R.id.btn_delete);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_dob = itemView.findViewById(R.id.tv_dob);
            tv_blood_group = itemView.findViewById(R.id.tv_blood_group);
            tv_age_group = itemView.findViewById(R.id.tv_age_group);
            tv_relationship = itemView.findViewById(R.id.tv_relationship);
            tv_desc = itemView.findViewById(R.id.tv_desc);

            circleImageView = itemView.findViewById(R.id.profile_image);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }

    @Override
    public Filter getFilter() {
        return pickupsFilter;
    }

    private final Filter pickupsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ModelMemberList> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(allPickups);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (ModelMemberList item : allPickups) {
                    if (
                            item.getName().toLowerCase().contains(filterPattern) ||
                                    item.getRelationship().toLowerCase().contains(filterPattern) ||
                                    String.valueOf(item.getBlood_group()).toLowerCase().contains(filterPattern) ||
                                    String.valueOf(item.getDate_of_birth()).toLowerCase().contains(filterPattern) ||
                                    item.getAge_group().toLowerCase().contains(filterPattern)

                    ) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
