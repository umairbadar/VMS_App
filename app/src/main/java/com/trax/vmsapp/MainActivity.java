package com.trax.vmsapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trax.vmsapp.utilities.APIRequest;
import com.trax.vmsapp.utilities.ConfirmationDialog;
import com.trax.vmsapp.utilities.Constants;
import com.trax.vmsapp.utilities.DialogConfirmationInterface;
import com.trax.vmsapp.utilities.Loader;
import com.trax.vmsapp.utilities.RecyclerViewItemInterface;
import com.trax.vmsapp.utilities.ResponseInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        DialogConfirmationInterface, ResponseInterface, RecyclerViewItemInterface {

    private SharedPreferences sharedPreferences;
    private String authorization;

    private List<ModelMemberList> list;
    private AdapterMemberList adapter;

    private ProgressDialog loader;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        authorization = sharedPreferences.getString("api_token", "");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        ImageButton btnLogout = toolbar.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);

        TextView tv_title = toolbar.findViewById(R.id.tv_title);
        tv_title.setText("Family Member List");

        RecyclerView family_members_list = findViewById(R.id.family_members_list);
        family_members_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        list = new ArrayList<>();
        adapter = new AdapterMemberList(list, this);
        family_members_list.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        family_members_list.addItemDecoration(dividerItemDecoration);
        if (isNetworkAvailable()){
            getList();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection!",
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {

        ConfirmationDialog.show(this, "close app", this);
    }

    private void getList(){
        loader = Loader.show(this);

        Map<String, Object> data = new HashMap<>();

        APIRequest.request(authorization, Constants.MethodGET,
                Constants.EndpointList, data, null, null, this);
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

                    ModelMemberList item = new ModelMemberList(
                            jsonObject.getString("name"),
                            jsonObject.getString("date_of_birth"),
                            jsonObject.isNull("image_path") ? "" : jsonObject.getString("image_path"),
                            jsonObject.getString("blood_group"),
                            jsonObject.getString("age_group")
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

        Toast.makeText(getApplicationContext(), message,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void itemClick(String id) {
        Intent intent = new Intent(this, VaccinationActivity.class);
        startActivity(intent);
    }

    @Override
    public void action(String action, Boolean agree) {

        if (agree) {
            if (action.equals("logout")) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else if (action.equals("close app")) {
                finish();
            }
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.btnLogout) {
            ConfirmationDialog.show(this, "logout", this);
        } else if (id == R.id.btn_add_family_member){
            Intent intent = new Intent(getApplicationContext(), AddFamilyMemberActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}