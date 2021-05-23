package com.fyp.vmsapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.fyp.vmsapp.databinding.ActivitySignupBinding;
import com.fyp.vmsapp.utilities.APIRequest;
import com.fyp.vmsapp.utilities.Constants;
import com.fyp.vmsapp.utilities.Loader;
import com.fyp.vmsapp.utilities.ResponseInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener, ResponseInterface {

    private ActivitySignupBinding activitySignupBinding;
    private ProgressDialog loader;

    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener dateOfBirth;

    private Spinner spn_blood_group;
    private List<String> blood_group;
    private List<Integer> blood_group_id;
    private int selectBloodGroupId = -1;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Bitmap selectedBitmapImage = null;
    byte[] bytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activitySignupBinding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(activitySignupBinding.getRoot());

        if (getIntent().hasExtra("bitmap")) {
            String filename = getIntent().getStringExtra("bitmap");

            try {
                FileInputStream bitmap_stream = this.openFileInput(filename);
                selectedBitmapImage = BitmapFactory.decodeStream(bitmap_stream);
                activitySignupBinding.profileImage.setImageBitmap(selectedBitmapImage);
                bitmap_stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        activitySignupBinding.btnSignup.setOnClickListener(this);
        activitySignupBinding.tvDob.setOnClickListener(this);
        activitySignupBinding.profileImage.setOnClickListener(this);

        myCalendar = Calendar.getInstance();
        getData();

        blood_group = new ArrayList<>();
        blood_group_id = new ArrayList<>();
        activitySignupBinding.spnBloodGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectBloodGroupId = blood_group_id.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        activitySignupBinding.spnBloodGroup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                try {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void getData() {

        loader = Loader.show(this);

        Map<String, Object> data = new HashMap<>();

        APIRequest.request("", Constants.MethodGET,
                Constants.EndpointGetData, data, null, null, this);
    }

    private byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private void registerUser(String name, String phone_number, String email, String password, String cnic, String dob, int selectBloodGroupId) {

        loader = Loader.show(this);

        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("phone_number", phone_number);
        data.put("email", email);
        data.put("password", password);
        data.put("cnic", cnic);
        data.put("blood_group_id", selectBloodGroupId);
        data.put("date_of_birth", dob);

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
            APIRequest.request("", Constants.MethodPOSTSimple,
                    Constants.EndpointSignup, data, image, null, this);
        } else {
            APIRequest.request("", Constants.MethodPOSTSimple,
                    Constants.EndpointSignup, data, null, null, this);
        }
    }

    @Override
    public void response(JSONObject response) throws JSONException {

        if (loader != null && loader.isShowing()) {
            loader.dismiss();
        }

        if (response.has("message")) {
            Toast.makeText(getApplicationContext(), response.getString("message"),
                    Toast.LENGTH_LONG).show();
            finish();
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
                activitySignupBinding.spnBloodGroup.setAdapter(dataAdapter);
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

    private void showDateOfBirthPicker() {

        dateOfBirth = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "yyyy/MM/dd"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
            activitySignupBinding.tvDob.setText(sdf.format(myCalendar.getTime()));
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                InputStream imageStream = getContentResolver().openInputStream(data.getData());
                selectedBitmapImage = BitmapFactory.decodeStream(imageStream);
                activitySignupBinding.profileImage.setImageBitmap(selectedBitmapImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (requestCode == Constants.RequestCode && resultCode == RESULT_OK) {
            try {
                selectedBitmapImage = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                assert selectedBitmapImage != null;
                String filename = "bitmap.png";
                FileOutputStream stream = openFileOutput(filename, Context.MODE_PRIVATE);
                selectedBitmapImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                stream.close();
                selectedBitmapImage.recycle();
                Intent intent = new Intent(this, SignupActivity.class);
                intent.putExtra("bitmap", filename);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void showChooserDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.chooser_dialog);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tv_open_camera = dialog.findViewById(R.id.tv_open_camera);
        tv_open_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, Constants.RequestCode);
                dialog.dismiss();
            }
        });

        TextView tv_open_gallery = dialog.findViewById(R.id.tv_open_gallery);
        tv_open_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    showImageChooser();
                } else {
                    //When permission denied
                    //Request Permission
                    ActivityCompat.requestPermissions(SignupActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE_REQUEST);
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void validations() {

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        String name = activitySignupBinding.etName.getText().toString();
        String dob = activitySignupBinding.tvDob.getText().toString();
        String emailAddress = activitySignupBinding.etEmailAddress.getText().toString();
        String phoneNumber = activitySignupBinding.etPhoneNumber.getText().toString();
        String cnic = activitySignupBinding.etCnic.getText().toString();
        String password = activitySignupBinding.etPassword.getText().toString();
        String confirmPassword = activitySignupBinding.etConfirmPassword.getText().toString();

        if (name.isEmpty()) {
            activitySignupBinding.etName.setError("Enter Name");
            activitySignupBinding.etName.requestFocus();
        } else if (dob.isEmpty()) {
            Toast.makeText(this, "Select Date of Birth",
                    Toast.LENGTH_LONG).show();
        } else if (selectBloodGroupId == -1 || selectBloodGroupId == 0) {
            Toast.makeText(this, "Select Blood Group",
                    Toast.LENGTH_LONG).show();
        } else if (emailAddress.isEmpty()) {
            activitySignupBinding.etEmailAddress.setError("Enter Email Address");
            activitySignupBinding.etEmailAddress.requestFocus();
        } else if (!emailAddress.matches(emailPattern)) {
            activitySignupBinding.etEmailAddress.setError("Enter Valid Email Address");
            activitySignupBinding.etEmailAddress.requestFocus();
        } else if (phoneNumber.isEmpty()) {
            activitySignupBinding.etPhoneNumber.setError("Enter Phone Number");
            activitySignupBinding.etPhoneNumber.requestFocus();
        } else if (phoneNumber.length() < 11) {
            activitySignupBinding.etPhoneNumber.setError("Enter Valid Phone Number");
            activitySignupBinding.etPhoneNumber.requestFocus();
        } else if (cnic.isEmpty()) {
            activitySignupBinding.etCnic.setError("Enter CNIC");
            activitySignupBinding.etCnic.requestFocus();
        } else if (cnic.length() < 15) {
            activitySignupBinding.etCnic.setError("Enter Valid CNIC");
            activitySignupBinding.etCnic.requestFocus();
        } else if (password.isEmpty()) {
            activitySignupBinding.etPassword.setError("Enter Password");
            activitySignupBinding.etPassword.requestFocus();
        } else if (confirmPassword.isEmpty()) {
            activitySignupBinding.etConfirmPassword.setError("Enter Confirm Password");
            activitySignupBinding.etConfirmPassword.requestFocus();
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(getApplicationContext(), "Password not match",
                    Toast.LENGTH_LONG).show();
        } else {
            registerUser(
                    name,
                    phoneNumber,
                    emailAddress,
                    password,
                    cnic,
                    dob,
                    selectBloodGroupId
            );
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.btnSignup) {
            if (isNetworkAvailable()) {
                validations();
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection!",
                        Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.tv_dob) {

            try {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (activitySignupBinding.tvDob.getText().length() > 0) {
                activitySignupBinding.tvDob.setText("");
            }

            showDateOfBirthPicker();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateOfBirth,
                    myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        } else if (id == R.id.profile_image) {
            showChooserDialog();
        }
    }
}