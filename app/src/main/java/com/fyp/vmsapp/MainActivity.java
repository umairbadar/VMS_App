package com.fyp.vmsapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.fyp.vmsapp.utilities.APIRequest;
import com.fyp.vmsapp.utilities.ConfirmationDialog;
import com.fyp.vmsapp.utilities.Constants;
import com.fyp.vmsapp.utilities.DialogConfirmationInterface;
import com.fyp.vmsapp.utilities.Permissions;
import com.fyp.vmsapp.utilities.ResponseInterface;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements DialogConfirmationInterface, ResponseInterface {

    private AppBarConfiguration mAppBarConfiguration;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Permissions.verify(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        String authorization = sharedPreferences.getString("api_token", "");
        String token = sharedPreferences.getString("token", "");
        if (!token.equals("")) {
            sendTokenToServer(authorization, token);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_family_member_tree, R.id.nav_article, R.id.nav_slideshow, R.id.nav_add_family_member,
                R.id.nav_article_details, R.id.nav_live_consultation, R.id.nav_vaccination, R.id.nav_nearby_hospitals,
                R.id.nav_history, R.id.nav_profile, R.id.nav_upcoming_vaccination, R.id.nav_vaccination_schedule)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.nav_logout) {
                    ConfirmationDialog.show(MainActivity.this, "Logout", MainActivity.this);
                } else if (id == R.id.nav_live_consultation) {
                    ConfirmationDialog.show(MainActivity.this, "call live consultation", MainActivity.this);
                } else if (id == R.id.nav_vaccination_schedule) {
                    ConfirmationDialog.show(MainActivity.this, "view the vaccination schedule", MainActivity.this);
                } else {
                    NavigationUI.onNavDestinationSelected(item, navController);
                }

                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        View headerView = navigationView.getHeaderView(0);
        TextView tv_name = headerView.findViewById(R.id.tv_name);
        tv_name.setText(sharedPreferences.getString("name", ""));
    }

    private void sendTokenToServer(String auth, String token) {

        Map<String, Object> data = new HashMap<>();
        data.put("device_token", token);

        APIRequest.request(auth, Constants.MethodPOSTSimple,
                Constants.EndpointSendDeviceTokenToServer, data, null, null, this);

    }

    @Override
    public void response(JSONObject response) throws JSONException {
        Log.e("TAG", "response: " + response);
    }

    @Override
    public void failure(String message) {

        Log.e("TAG", "failure: " + message);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void contactSupport() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(Constants.CallPrefix + Constants.SupportContactNumber));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                Permissions.verify(this);
                return;
            }
        }

        startActivity(intent);
    }

    @Override
    public void action(String action, Boolean agree) {
        if (agree) {
            if (action.equals("Logout")) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("logged_in_status");
                editor.apply();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else if (action.equals("call live consultation")) {
                contactSupport();
            } else if (action.equals("view the vaccination schedule")) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.africau.edu/images/default/sample.pdf"));
                startActivity(browserIntent);
            }
        }
    }
}