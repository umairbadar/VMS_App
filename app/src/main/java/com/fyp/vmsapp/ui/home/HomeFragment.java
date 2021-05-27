package com.fyp.vmsapp.ui.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.vmsapp.AdapterMemberList;
import com.fyp.vmsapp.LoginActivity;
import com.fyp.vmsapp.ModelMemberList;
import com.fyp.vmsapp.R;
import com.fyp.vmsapp.utilities.APIRequest;
import com.fyp.vmsapp.utilities.ConfirmationDialog;
import com.fyp.vmsapp.utilities.Constants;
import com.fyp.vmsapp.utilities.DialogConfirmationInterface;
import com.fyp.vmsapp.utilities.Loader;
import com.fyp.vmsapp.utilities.RecyclerViewItemInterface;
import com.fyp.vmsapp.utilities.ResponseInterface;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment implements View.OnClickListener,
        DialogConfirmationInterface, ResponseInterface, RecyclerViewItemInterface {

    private SharedPreferences sharedPreferences;
    private String authorization;

    private List<ModelMemberList> list;
    private AdapterMemberList adapter;

    private ProgressDialog loader;

    private NavController navController;

    private RecyclerView family_members_list;

    private int id;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = requireActivity().getSharedPreferences("data", MODE_PRIVATE);
        authorization = sharedPreferences.getString("api_token", "");
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        family_members_list = view.findViewById(R.id.family_members_list);
        family_members_list.setLayoutManager(new LinearLayoutManager(requireContext()));
        list = new ArrayList<>();

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        family_members_list.addItemDecoration(dividerItemDecoration);
        if (isNetworkAvailable()) {
            getList(1);
        } else {
            Toast.makeText(requireContext(), "No Internet Connection!",
                    Toast.LENGTH_LONG).show();
        }

        FloatingActionButton btn_add_family_member = view.findViewById(R.id.btn_add_family_member);
        btn_add_family_member.setOnClickListener(this);

        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.replaceFirst("^0+(?!$)", "");
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void getList(int showLoader) {
        if (showLoader == 1)
            loader = Loader.show(requireContext());

        Map<String, Object> data = new HashMap<>();

        APIRequest.request(authorization, Constants.MethodGET,
                Constants.EndpointList, data, null, null, this);
    }

    @Override
    public void response(JSONObject response) throws JSONException {

        if (response.has("data")) {
            if (loader != null && loader.isShowing()) {
                loader.dismiss();
            }
            if (response.getJSONArray("data").length() > 0) {
                JSONArray jsonArray = response.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    ModelMemberList item = new ModelMemberList(
                            jsonObject.getString("name"),
                            jsonObject.getString("date_of_birth"),
                            jsonObject.isNull("image_path") ? "" : jsonObject.getString("image_path"),
                            jsonObject.getString("blood_group"),
                            jsonObject.getString("age_group"),
                            jsonObject.getString("relationship"),
                            jsonObject.isNull("description") ? "" : jsonObject.getString("description"),
                            jsonObject.getInt("family_member_id"),
                            jsonObject.getInt("blood_group_id"),
                            jsonObject.getInt("relationship_id"),
                            jsonObject.getInt("age_group_id")
                    );

                    list.add(item);
                    adapter = new AdapterMemberList(list, this);
                    family_members_list.setAdapter(adapter);
                }
            }
        }

        if (response.has("message")) {
            Toast.makeText(requireContext(), response.getString("message"),
                    Toast.LENGTH_LONG).show();

            if (list.size() > 0) {
                list.clear();
                adapter = null;
            }

            getList(0);
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

    @Override
    public void itemClick(String id) {

        Bundle args = new Bundle();
        args.putInt("id", Integer.parseInt(id));
        navController.navigate(R.id.nav_vaccination, args);
    }

    @Override
    public void edit(String imagePath, int family_member_id, int blood_group_id, int relationship_id, String name, String desc, String dob) {
        Bundle args = new Bundle();
        args.putInt("family_member_id", family_member_id);
        args.putInt("blood_group_id", blood_group_id);
        args.putInt("relationship_id", relationship_id);
        args.putString("name", name);
        args.putString("image_path", imagePath);
        args.putString("desc", desc);
        args.putString("dob", dob);
        navController.navigate(R.id.nav_add_family_member, args);
    }

    @Override
    public void delete(int id) {
        this.id = id;
        ConfirmationDialog.show(requireContext(), "delete this family member", this);
    }

    @Override
    public void action(String action, Boolean agree) {

        if (agree) {
            if (action.equals("logout")) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(requireContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else if (action.equals("close app")) {
                requireActivity().finish();
            } else if (action.equals("delete this family member")) {
                loader = Loader.show(requireContext());
                Map<String, Object> data = new HashMap<>();
                data.put("family_member_id", id);
                APIRequest.request(authorization, Constants.MethodPOSTSimple,
                        Constants.EndpointDeleteMember, data, null, null, this);
            }
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.btn_add_family_member) {
            navController.navigate(R.id.nav_add_family_member);
        }
    }
}