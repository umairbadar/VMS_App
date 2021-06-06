package com.fyp.vmsapp.ui.upcoming_vaccination;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.vmsapp.R;
import com.fyp.vmsapp.utilities.APIRequest;
import com.fyp.vmsapp.utilities.Constants;
import com.fyp.vmsapp.utilities.Loader;
import com.fyp.vmsapp.utilities.ResponseInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class UpcomingVaccinationFragment extends Fragment implements ResponseInterface {

    private List<ModelUpcomingVaccination> list;
    private AdapterUpcomingVaccinations adapter;

    private ProgressDialog loader;
    private String authorization;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("data", MODE_PRIVATE);
        authorization = sharedPreferences.getString("api_token", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upcoming_vaccination, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView upcoming_vaccination_list = view.findViewById(R.id.upcoming_vaccination_list);
        upcoming_vaccination_list.setLayoutManager(new LinearLayoutManager(requireContext()));
        list = new ArrayList<>();
        adapter = new AdapterUpcomingVaccinations(requireContext(), list);
        upcoming_vaccination_list.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        upcoming_vaccination_list.addItemDecoration(dividerItemDecoration);

        if (isNetworkAvailable()) {
            getUpcomingVaccinations();
        } else {
            Toast.makeText(requireContext(), "No Internet Connection!",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void getUpcomingVaccinations() {

        loader = Loader.show(requireContext());

        Map<String, Object> data = new HashMap<>();

        APIRequest.request(authorization, Constants.MethodGET,
                Constants.EndpointUpcomingVaccinations, data, null, null, this);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void response(JSONObject response) throws JSONException {

        if (loader != null && loader.isShowing()) {
            loader.dismiss();
        }

        if (response.has("data")){
            if (response.getJSONArray("data").length() > 0){
                JSONArray jsonArray = response.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ModelUpcomingVaccination item = new ModelUpcomingVaccination(
                            jsonObject.getString("family_member"),
                            jsonObject.getString("vaccine")
                    );
                    list.add(item);
                    adapter.notifyDataSetChanged();
                }
            }
        }

    }

    @Override
    public void failure(String message) {

        if (loader != null && loader.isShowing()) {
            loader.dismiss();
        }

        Toast.makeText(requireContext(), message,
                Toast.LENGTH_LONG).show();

    }
}