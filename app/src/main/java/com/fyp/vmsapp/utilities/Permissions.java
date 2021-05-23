package com.fyp.vmsapp.utilities;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;

abstract public class Permissions extends AppCompatActivity {

    public static void verify(Activity activity) {
        ArrayList<String> permissions_to_ask_array_list = new ArrayList<>();

        for (String permission : Constants.requiredPermissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                permissions_to_ask_array_list.add(permission);
            }
        }

        if (!permissions_to_ask_array_list.isEmpty()) {
            int size = permissions_to_ask_array_list.size();
            Object[] permissions_to_ask_array = permissions_to_ask_array_list.toArray();

            String[] permissions_to_ask = Arrays.copyOf(permissions_to_ask_array, size,
                    String[].class);

            ActivityCompat.requestPermissions(activity, permissions_to_ask, Constants.RequestCode);
        }
    }
}
