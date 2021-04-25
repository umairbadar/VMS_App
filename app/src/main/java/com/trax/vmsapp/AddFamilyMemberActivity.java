package com.trax.vmsapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.trax.vmsapp.utilities.APIRequest;
import com.trax.vmsapp.utilities.Constants;
import com.trax.vmsapp.utilities.Loader;
import com.trax.vmsapp.utilities.ResponseInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class AddFamilyMemberActivity extends AppCompatActivity implements View.OnClickListener, ResponseInterface {

    private CircleImageView circleImageView;
    private EditText et_family_member_name;
    private TextView tv_dob;

    private static final int PICK_IMAGE_REQUEST = 1;

    private ProgressDialog loader;
    private String authorization;

    private Spinner spn_blood_group;
    private List<String> blood_group;
    private List<Integer> blood_group_id;
    private int selectBloodGroupId;

    private Spinner spn_age_group;
    private List<String> age_group;
    private List<Integer> age_group_id;
    private int selectAgeGroupId;

    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener dateOfBirth;
    
    private Bitmap selectedBitmapImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_family_member);

        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        authorization = sharedPreferences.getString("api_token", "");

        circleImageView = findViewById(R.id.profile_image);
        et_family_member_name = findViewById(R.id.et_family_member_name);
        tv_dob = findViewById(R.id.tv_dob);
        tv_dob.setOnClickListener(this);

        spn_blood_group = findViewById(R.id.spn_blood_group);
        blood_group = new ArrayList<>();
        blood_group_id = new ArrayList<>();
        spn_blood_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectBloodGroupId = blood_group_id.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spn_age_group = findViewById(R.id.spn_age_group);
        age_group = new ArrayList<>();
        age_group_id = new ArrayList<>();
        spn_age_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectAgeGroupId = age_group_id.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getData();

        myCalendar = Calendar.getInstance();
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageChooser();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                InputStream imageStream = getContentResolver().openInputStream(data.getData());
                selectedBitmapImage = BitmapFactory.decodeStream(imageStream);
                circleImageView.setImageBitmap(selectedBitmapImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private void getData() {

        loader = Loader.show(this);

        Map<String, Object> data = new HashMap<>();

        APIRequest.request(authorization, Constants.MethodGET,
                Constants.EndpointGetData, data, null, null, this);
    }

    @Override
    public void response(JSONObject response) throws JSONException {

        if (loader != null && loader.isShowing()) {
            loader.dismiss();
        }

        if (response.has("blood_group")) {
            if (response.getJSONArray("blood_group").length() > 0) {
                blood_group_id.add(0);
                blood_group.add("Select Blood Group");
                JSONArray jsonArray = response.getJSONArray("blood_group");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    blood_group_id.add(jsonObject.getInt("id"));
                    blood_group.add(jsonObject.getString("name"));
                }

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                        R.layout.support_simple_spinner_dropdown_item, blood_group) {
                    @Override
                    public boolean isEnabled(int position) {
                        if (position == 0) {
                            return false;
                        } else {
                            return true;
                        }
                    }

                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
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
                spn_blood_group.setAdapter(dataAdapter);
            }
        }

        if (response.has("age_category")) {
            if (response.getJSONArray("age_category").length() > 0) {
                age_group_id.add(0);
                age_group.add("Select Age Group");
                JSONArray jsonArray = response.getJSONArray("age_category");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    age_group_id.add(jsonObject.getInt("id"));
                    age_group.add(jsonObject.getString("name"));
                }

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                        R.layout.support_simple_spinner_dropdown_item, age_group) {
                    @Override
                    public boolean isEnabled(int position) {
                        if (position == 0) {
                            return false;
                        } else {
                            return true;
                        }
                    }

                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
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
                spn_age_group.setAdapter(dataAdapter);
            }
        }

        if (response.has("data")) {
            Toast.makeText(getApplicationContext(), response.getString("message"),
                    Toast.LENGTH_LONG).show();

            et_family_member_name.setText("");
            tv_dob.setText("");
            spn_blood_group.setSelection(0);
            spn_age_group.setSelection(0);
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

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void showDateOfBirthPicker() {

        dateOfBirth = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "yyyy/MM/dd"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
            tv_dob.setText(sdf.format(myCalendar.getTime()));
        };
    }

    private void validations() {

        String name = et_family_member_name.getText().toString();
        String dob = tv_dob.getText().toString();

        if (name.isEmpty()) {
            et_family_member_name.setError("Enter Name");
            et_family_member_name.requestFocus();
        } else if (dob.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Select Date of Birth",
                    Toast.LENGTH_LONG).show();
        } else if (spn_blood_group.getSelectedItemPosition() == 0) {
            Toast.makeText(getApplicationContext(), "Select Blood Group",
                    Toast.LENGTH_LONG).show();
        } else if (spn_age_group.getSelectedItemPosition() == 0) {
            Toast.makeText(getApplicationContext(), "Select Age Group",
                    Toast.LENGTH_LONG).show();
        } else {
            loader = Loader.show(this);

            Map<String, Object> data = new HashMap<>();
            data.put("name", name);
            data.put("date_of_birth", dob);
            data.put("blood_group_id", selectBloodGroupId);
            data.put("age_category_id", selectAgeGroupId);

            if (selectedBitmapImage != null) {
                File file = new File(getFilesDir(), "image.png");

                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    OutputStream outputStream = new FileOutputStream(file);
                    outputStream.write(bitmapToByte(selectedBitmapImage));
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                RequestBody requestBody = RequestBody.create(file,
                        MediaType.parse("image/*"));
                MultipartBody.Part image = MultipartBody.Part.createFormData(
                        "image", file.getName(), requestBody);
                APIRequest.request(authorization, Constants.MethodPOSTSimple,
                        Constants.EndpointAddMember, data, image, null, this);
            } else {
                APIRequest.request(authorization, Constants.MethodPOSTSimple,
                        Constants.EndpointAddMember, data, null, null, this);
            }
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.profile_image) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                showImageChooser();
            } else {
                //When permission denied
                //Request Permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE_REQUEST);
            }
        } else if (id == R.id.btn_add) {
            validations();
        } else if (id == R.id.tv_dob) {
            if (tv_dob.getText().length() > 0) {
                tv_dob.setText("");
            }

            showDateOfBirthPicker();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateOfBirth,
                    myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        }
    }
}