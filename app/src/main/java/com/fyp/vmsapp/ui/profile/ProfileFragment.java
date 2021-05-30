package com.fyp.vmsapp.ui.profile;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.fyp.vmsapp.R;
import com.fyp.vmsapp.utilities.APIRequest;
import com.fyp.vmsapp.utilities.Constants;
import com.fyp.vmsapp.utilities.Loader;
import com.fyp.vmsapp.utilities.ResponseInterface;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.vicmikhailau.maskededittext.MaskedEditText;

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

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment implements ResponseInterface, View.OnClickListener {

    private SharedPreferences sharedPreferences;
    private String authorization;

    private ProgressDialog loader;

    private EditText etName, etEmailAddress, etPassword, etConfirmPassword;
    private MaskedEditText etPhoneNumber, etCnic;
    private TextView tv_dob;
    private CircleImageView circleImageView;

    private Spinner spn_blood_group;
    private List<String> blood_group;
    private List<Integer> blood_group_id;
    private int selectBloodGroupId;
    private int blood_group_ids = 0;

    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener dateOfBirth;

    private static final int PICK_IMAGE_REQUEST = 1;

    private Bitmap selectedBitmapImage = null;

    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = requireActivity().getSharedPreferences("data", MODE_PRIVATE);
        authorization = sharedPreferences.getString("api_token", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btn_update_profile = view.findViewById(R.id.btn_update_profile);
        btn_update_profile.setOnClickListener(this);

        navController = Navigation.findNavController(view);

        myCalendar = Calendar.getInstance();

        spn_blood_group = view.findViewById(R.id.spn_blood_group);
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

        spn_blood_group.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                try {
                    InputMethodManager inputManager = (InputMethodManager) requireActivity().getSystemService(INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        circleImageView = view.findViewById(R.id.profile_image);
        circleImageView.setOnClickListener(this);

        etName = view.findViewById(R.id.etName);
        etEmailAddress = view.findViewById(R.id.etEmailAddress);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber);
        etCnic = view.findViewById(R.id.etCnic);
        tv_dob = view.findViewById(R.id.tv_dob);
        tv_dob.setOnClickListener(this);

        getBloodGroups();

        if (getArguments() != null) {
            String filename = getArguments().getString("bitmap");
            try {
                FileInputStream bitmap_stream = requireActivity().openFileInput(filename);
                selectedBitmapImage = BitmapFactory.decodeStream(bitmap_stream);
                circleImageView.setImageBitmap(selectedBitmapImage);
                bitmap_stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getBloodGroups() {

        loader = Loader.show(requireContext());

        Map<String, Object> data = new HashMap<>();

        APIRequest.request("", Constants.MethodGET,
                Constants.EndpointGetData, data, null, null, this);
    }

    private void getProfileDetails() {

        //loader = Loader.show(requireContext());

        Map<String, Object> data = new HashMap<>();

        APIRequest.request(authorization, Constants.MethodGET,
                Constants.EndpointGetProfile, data, null, null, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageChooser();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                InputStream imageStream = requireActivity().getContentResolver().openInputStream(data.getData());
                selectedBitmapImage = BitmapFactory.decodeStream(imageStream);
                circleImageView.setImageBitmap(selectedBitmapImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (requestCode == Constants.RequestCode && resultCode == RESULT_OK) {
            try {
                selectedBitmapImage = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                assert selectedBitmapImage != null;
                String filename = "bitmap.png";
                FileOutputStream stream = requireActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                selectedBitmapImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                stream.close();
                selectedBitmapImage.recycle();

                Bundle args = new Bundle();
                args.putString("bitmap", filename);
                args.putString("name", etName.getText().toString());
                args.putString("dob", tv_dob.getText().toString());
                args.putString("email_address", etEmailAddress.getText().toString());
                args.putString("phone_number", etPhoneNumber.getText().toString());
                args.putString("cnic", etCnic.getText().toString());
                args.putString("password", etPassword.getText().toString());
                args.putString("c_password", etConfirmPassword.getText().toString());
                NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.nav_profile, true).build();
                navController.navigate(R.id.nav_profile, args, navOptions);
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
        final Dialog dialog = new Dialog(requireContext());
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
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    showImageChooser();
                } else {
                    //When permission denied
                    //Request Permission
                    ActivityCompat.requestPermissions(requireActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE_REQUEST);
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

    @Override
    public void response(JSONObject response) throws JSONException {

        if (response.has("data")) {

            if (loader != null && loader.isShowing()) {
                loader.dismiss();
            }

            JSONObject jsonObject = response.getJSONObject("data");
            etName.setText(jsonObject.getString("name"));
            tv_dob.setText(jsonObject.getString("date_of_birth"));
            etCnic.setText(jsonObject.isNull("cnic") ? "" : jsonObject.getString("cnic"));
            etPhoneNumber.setText(jsonObject.isNull("phone_number") ? "" : jsonObject.getString("phone_number"));
            etEmailAddress.setText(jsonObject.isNull("email") ? "" : jsonObject.getString("email"));
            blood_group_ids = jsonObject.getInt("blood_group_id");
            spn_blood_group.setSelection(blood_group_id.indexOf(blood_group_ids));
            String image_path = jsonObject.isNull("image") ? "" : jsonObject.getString("image");

            if (!image_path.equals("") && selectedBitmapImage == null) {
                String url = Constants.BaseURL + "/storage/app/public/" + image_path;
                Picasso.get()
                        .load(url)
                        .placeholder(R.drawable.no_image)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .fit()
                        .centerInside()
                        .into(circleImageView);
            }

            if (etCnic.getText().length() == 15) {
                etCnic.setEnabled(false);
            }
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

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(requireContext(),
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
                getProfileDetails();
            }
        }

        if (response.has("message")) {

            if (loader != null && loader.isShowing()) {
                loader.dismiss();
            }
            Toast.makeText(requireContext(), response.getString("message"),
                    Toast.LENGTH_LONG).show();
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

    private void validations() {

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        String name = etName.getText().toString();
        String dob = tv_dob.getText().toString();
        String emailAddress = etEmailAddress.getText().toString();
        String phoneNumber = etPhoneNumber.getText().toString();
        String cnic = etCnic.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (name.isEmpty()) {
            etName.setError("Enter Name");
            etName.requestFocus();
        } else if (dob.isEmpty()) {
            Toast.makeText(requireContext(), "Select Date of Birth",
                    Toast.LENGTH_LONG).show();
        } else if (selectBloodGroupId == -1 || selectBloodGroupId == 0) {
            Toast.makeText(requireContext(), "Select Blood Group",
                    Toast.LENGTH_LONG).show();
        } else if (emailAddress.isEmpty()) {
            etEmailAddress.setError("Enter Email Address");
            etEmailAddress.requestFocus();
        } else if (!emailAddress.matches(emailPattern)) {
            etEmailAddress.setError("Enter Valid Email Address");
            etEmailAddress.requestFocus();
        } else if (phoneNumber.isEmpty()) {
            etPhoneNumber.setError("Enter Phone Number");
            etPhoneNumber.requestFocus();
        } else if (phoneNumber.length() < 12) {
            etPhoneNumber.setError("Enter Valid Phone Number");
            etPhoneNumber.requestFocus();
        } else if (cnic.isEmpty()) {
            etCnic.setError("Enter CNIC");
            etCnic.requestFocus();
        } else if (cnic.length() < 15) {
            etCnic.setError("Enter Valid CNIC");
            etCnic.requestFocus();
        } else if (password.length() > 0 && password.length() < 6) {
            etPassword.setError("Password length must 6");
            etPassword.requestFocus();
        } else if (confirmPassword.length() > 0 && confirmPassword.length() < 6) {
            etConfirmPassword.setError("Confirm Password length must 6");
            etConfirmPassword.requestFocus();
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(requireContext(), "Password not match",
                    Toast.LENGTH_LONG).show();
        } else {
            updateProfile(
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

    private void updateProfile(String name, String phoneNumber, String emailAddress, String password, String cnic, String dob, int selectBloodGroupId) {

        loader = Loader.show(requireActivity());

        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("phone_number", phoneNumber);
        data.put("email", emailAddress);
        if (password.length() > 0)
            data.put("password", password);
        data.put("cnic", cnic);
        data.put("blood_group_id", selectBloodGroupId);
        data.put("date_of_birth", dob);

        if (selectedBitmapImage != null) {
            File file = new File(requireActivity().getFilesDir(), "image.png");

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
                    Constants.EndpointUpdateProfile, data, image, null, this);
        } else {
            APIRequest.request(authorization, Constants.MethodPOSTSimple,
                    Constants.EndpointUpdateProfile, data, null, null, this);
        }

    }

    private byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.tv_dob) {

            try {
                InputMethodManager inputManager = (InputMethodManager) requireActivity().getSystemService(INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (tv_dob.getText().length() > 0) {
                tv_dob.setText("");
            }

            showDateOfBirthPicker();
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), dateOfBirth,
                    myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        } else if (id == R.id.profile_image) {
            showChooserDialog();
        } else if (id == R.id.btn_update_profile) {
            if (isNetworkAvailable()) {
                validations();
            } else {
                Toast.makeText(requireContext(), "No Internet Connection!",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}