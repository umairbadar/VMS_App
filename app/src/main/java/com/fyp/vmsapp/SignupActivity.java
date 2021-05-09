package com.fyp.vmsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.fyp.vmsapp.databinding.ActivitySignupBinding;
import com.fyp.vmsapp.utilities.APIRequest;
import com.fyp.vmsapp.utilities.Constants;
import com.fyp.vmsapp.utilities.Loader;
import com.fyp.vmsapp.utilities.ResponseInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener, ResponseInterface {

    private ActivitySignupBinding activitySignupBinding;
    private ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activitySignupBinding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(activitySignupBinding.getRoot());

        activitySignupBinding.btnSignup.setOnClickListener(this);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void registerUser(String name, String phone_number, String email, String password){

        loader = Loader.show(this);

        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("phone_number", phone_number);
        data.put("email", email);
        data.put("password", password);

        APIRequest.request("", Constants.MethodPOSTSimple,
                Constants.EndpointSignup, data, null,null, this);
    }

    @Override
    public void response(JSONObject response) throws JSONException {

        if (loader != null && loader.isShowing()){
            loader.dismiss();
        }

        Toast.makeText(getApplicationContext(), "Account Created!",
                Toast.LENGTH_LONG).show();

        finish();
    }

    @Override
    public void failure(String message) {

        if (loader != null && loader.isShowing()){
            loader.dismiss();
        }

        Toast.makeText(getApplicationContext(), message,
                Toast.LENGTH_LONG).show();

    }

    private void validations(){

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        String name = activitySignupBinding.etName.getText().toString();
        String emailAddress = activitySignupBinding.etEmailAddress.getText().toString();
        String phoneNumber = activitySignupBinding.etPhoneNumber.getText().toString();
        String password = activitySignupBinding.etPassword.getText().toString();
        String confirmPassword = activitySignupBinding.etConfirmPassword.getText().toString();

        if (name.isEmpty()){
            activitySignupBinding.etName.setError("Enter Name");
            activitySignupBinding.etName.requestFocus();
        } else if (emailAddress.isEmpty()){
            activitySignupBinding.etEmailAddress.setError("Enter Email Address");
            activitySignupBinding.etEmailAddress.requestFocus();
        } else if (!emailAddress.matches(emailPattern)){
            activitySignupBinding.etEmailAddress.setError("Enter Valid Email Address");
            activitySignupBinding.etEmailAddress.requestFocus();
        } else if (phoneNumber.isEmpty()){
            activitySignupBinding.etPhoneNumber.setError("Enter Phone Number");
            activitySignupBinding.etPhoneNumber.requestFocus();
        } else if (phoneNumber.length() < 11){
            activitySignupBinding.etPhoneNumber.setError("Enter Valid Phone Number");
            activitySignupBinding.etPhoneNumber.requestFocus();
        } else if (password.isEmpty()){
            activitySignupBinding.etPassword.setError("Enter Password");
            activitySignupBinding.etPassword.requestFocus();
        } else if (confirmPassword.isEmpty()){
            activitySignupBinding.etConfirmPassword.setError("Enter Confirm Password");
            activitySignupBinding.etConfirmPassword.requestFocus();
        } else if (!password.equals(confirmPassword)){
            Toast.makeText(getApplicationContext(), "Password not match",
                    Toast.LENGTH_LONG).show();
        } else {
            registerUser(
                    name,
                    phoneNumber,
                    emailAddress,
                    password
            );
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.btnSignup){
            if (isNetworkAvailable()){
                validations();
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection!",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}