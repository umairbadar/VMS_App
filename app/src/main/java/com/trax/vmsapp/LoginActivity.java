package com.trax.vmsapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.trax.vmsapp.databinding.ActivityLoginBinding;
import com.trax.vmsapp.utilities.APIRequest;
import com.trax.vmsapp.utilities.Constants;
import com.trax.vmsapp.utilities.Loader;
import com.trax.vmsapp.utilities.ResponseInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, ResponseInterface {

    private ActivityLoginBinding activityLoginBinding;

    private ProgressDialog loader;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);

        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(activityLoginBinding.getRoot());

        activityLoginBinding.btnLogin.setOnClickListener(this);
        activityLoginBinding.btnSignup.setOnClickListener(this);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void loginUser(String phone_number, String password) {

        loader = Loader.show(this);

        Map<String, Object> data = new HashMap<>();
        data.put("phone_number", phone_number);
        data.put("password", password);

        APIRequest.request("", Constants.MethodPOSTSimple,
                Constants.EndpointLogin, data, null, null, this);
    }

    @Override
    public void response(JSONObject response) throws JSONException {

        if (loader != null && loader.isShowing()) {
            loader.dismiss();
        }

        if (response.has("information")) {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("name", response.getJSONObject("information").getString("name"));
            editor.putString("api_token", response.getJSONObject("information").getString("api_token"));
            editor.putBoolean("logged_in_status", true);
            editor.apply();
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();

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
    public void onClick(View v) {

        int id = v.getId();

        String phoneNumber = activityLoginBinding.etPhoneNumber.getText().toString();
        String password = activityLoginBinding.etPassword.getText().toString();

        if (id == R.id.btn_login) {
            if (isNetworkAvailable()){
                try {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (phoneNumber.isEmpty()) {
                    activityLoginBinding.etPhoneNumber.setError("Enter Phone Number");
                    activityLoginBinding.etPhoneNumber.requestFocus();
                } else if (password.isEmpty()) {
                    activityLoginBinding.etPassword.setError("Enter Password");
                    activityLoginBinding.etPassword.requestFocus();
                } else {
                    loginUser(
                            phoneNumber,
                            password
                    );
                }
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection!",
                        Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.btnSignup) {

            Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
            startActivity(intent);
        }
    }
}