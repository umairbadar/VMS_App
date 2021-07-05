package com.fyp.vmsapp.ui.vaccination;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.vmsapp.R;
import com.fyp.vmsapp.utilities.APIRequest;
import com.fyp.vmsapp.utilities.Constants;
import com.fyp.vmsapp.utilities.RecyclerViewItemInterface;
import com.fyp.vmsapp.utilities.ResponseInterface;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> implements ResponseInterface {

    List<Model> list;
    Context context;
    RecyclerViewItemInterface itemListener;
    List<String> hospitals_list;
    String selectHospital;

    public Adapter(List<Model> list, Context context, RecyclerViewItemInterface itemListener) {
        this.list = list;
        this.context = context;
        this.itemListener = itemListener;
        hospitals_list = new ArrayList<>();
        getHospitals();
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

        holder.btn_upload_slip.setOnClickListener(view -> itemListener.itemClick("2", item.getId()));

        if (item.getStatus() == 0) {
            holder.spn_hospitals.setEnabled(true);
            holder.btn_inject.setEnabled(true);
            holder.btn_upload_slip.setEnabled(false);
        } else if (item.getStatus() == 1) {
            holder.spn_hospitals.setEnabled(false);
            holder.btn_inject.setEnabled(false);
            holder.btn_inject.setText("Vaccinated");
            holder.btn_upload_slip.setEnabled(true);
        } else {
            holder.spn_hospitals.setEnabled(false);
            holder.btn_inject.setEnabled(false);
            holder.btn_inject.setText("Vaccinated");
            holder.btn_upload_slip.setEnabled(false);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                R.layout.support_simple_spinner_dropdown_item, hospitals_list) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NotNull ViewGroup parent) {
                View view;
                if (position == 0) {
                    TextView textView = new TextView(getContext());
                    textView.setHeight(0);
                    textView.setVisibility(View.GONE);
                    view = textView;
                } else {
                    view = super.getDropDownView(position, null, parent);
                }
                return view;
            }
        };

        dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        holder.spn_hospitals.setAdapter(dataAdapter);

        holder.spn_hospitals.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectHospital = hospitals_list.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        holder.btn_inject.setOnClickListener(
                view -> {
                    if (holder.spn_hospitals.getSelectedItemPosition() == 0) {
                        Toast.makeText(context, "Please select the hospital from where you have injected this vaccination.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        itemListener.delete(item.getId(), selectHospital);
                    }
                });
    }

    private void getHospitals() {

        Map<String, Object> data = new HashMap<>();

        APIRequest.request("", Constants.MethodGET,
                Constants.EndpointNearbyHospitals, data, null, null, this);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void response(JSONObject response) throws JSONException {

        if (response.has("hospitals")) {
            if (response.getJSONArray("hospitals").length() > 0) {
                hospitals_list.add("Select Hospital");
                JSONArray jsonArray = response.getJSONArray("hospitals");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = jsonObject.getString("name");
                    hospitals_list.add(name);
                }
            }
        }
    }

    @Override
    public void failure(String message) {

        Toast.makeText(context, message,
                Toast.LENGTH_LONG).show();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;
        Button btn_upload_slip, btn_inject;
        Spinner spn_hospitals;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);

            btn_inject = itemView.findViewById(R.id.btn_inject);
            btn_upload_slip = itemView.findViewById(R.id.btn_upload_slip);

            spn_hospitals = itemView.findViewById(R.id.spn_hospitals);
        }
    }
}
