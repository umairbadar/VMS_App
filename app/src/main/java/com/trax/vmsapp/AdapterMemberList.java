package com.trax.vmsapp;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.trax.vmsapp.utilities.Constants;
import com.trax.vmsapp.utilities.RecyclerViewItemInterface;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterMemberList extends RecyclerView.Adapter<AdapterMemberList.ViewHolder> {

    List<ModelMemberList> list;
    RecyclerViewItemInterface itemListener;

    public AdapterMemberList(List<ModelMemberList> list, RecyclerViewItemInterface itemListener) {
        this.list = list;
        this.itemListener = itemListener;
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

        String url = Constants.BaseURL + "/storage/app/public/" + item.getImage_path();

        if (!item.getImage_path().equals("")){
            Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.no_image)
                    .fit()
                    .centerInside()
                    .into(holder.circleImageView);
        }

        holder.mainLayout.setOnClickListener(view ->
                itemListener.itemClick(item.getAge_group()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_name, tv_dob, tv_blood_group, tv_age_group;
        CircleImageView circleImageView;
        ConstraintLayout mainLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_dob = itemView.findViewById(R.id.tv_dob);
            tv_blood_group = itemView.findViewById(R.id.tv_blood_group);
            tv_age_group = itemView.findViewById(R.id.tv_age_group);

            circleImageView = itemView.findViewById(R.id.profile_image);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
