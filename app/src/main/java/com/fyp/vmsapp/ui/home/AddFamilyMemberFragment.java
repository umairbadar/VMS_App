package com.fyp.vmsapp.ui.home;

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
import android.os.Bundle;
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
import com.squareup.picasso.Picasso;

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

public class AddFamilyMemberFragment extends Fragment implements View.OnClickListener, ResponseInterface {

    private CircleImageView circleImageView;
    private EditText et_family_member_name;
    private EditText et_desc;
    private TextView tv_dob;

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

    private static final int PICK_IMAGE_REQUEST = 1;

    private NavController navController;

    private int family_member_id = 0;
    private int relationship_id = 0;
    private int blood_group_ids = 0;
    private String name = "";
    private String image_path = "";
    private String desc = "";
    private String dob = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("data", MODE_PRIVATE);
        authorization = sharedPreferences.getString("api_token", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_family_member, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        circleImageView = view.findViewById(R.id.profile_image);
        circleImageView.setOnClickListener(this);
        et_family_member_name = view.findViewById(R.id.et_family_member_name);
        et_desc = view.findViewById(R.id.et_desc);
        tv_dob = view.findViewById(R.id.tv_dob);
        tv_dob.setOnClickListener(this);

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

        spn_age_group = view.findViewById(R.id.spn_age_group);
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

        spn_age_group.setOnTouchListener(new View.OnTouchListener() {
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

        getData();

        myCalendar = Calendar.getInstance();

        Button btn_add = view.findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);

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

            family_member_id = getArguments().getInt("family_member_id");
            blood_group_ids = getArguments().getInt("blood_group_id");
            relationship_id = getArguments().getInt("relationship_id");
            image_path = getArguments().getString("image_path");
            name = getArguments().getString("name");
            et_family_member_name.setText(name);
            desc = getArguments().getString("desc");
            et_desc.setText(desc);
            dob = getArguments().getString("dob");
            tv_dob.setText(dob);

            if (family_member_id != 0)
                btn_add.setText("Update");

            if (!image_path.equals("")) {
                String url = Constants.BaseURL + "/storage/app/public/" + image_path;
                Picasso.get()
                        .load(url)
                        .placeholder(R.drawable.no_image)
                        .fit()
                        .centerInside()
                        .into(circleImageView);
            }
        }
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
                args.putInt("family_member_id", family_member_id);
                args.putInt("blood_group_id", blood_group_ids);
                args.putInt("relationship_id", relationship_id);
                args.putString("name", name);
                args.putString("image_path", "");
                args.putString("desc", desc);
                args.putString("dob", dob);
                NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.nav_add_family_member, true).build();
                navController.navigate(R.id.nav_add_family_member, args, navOptions);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private void getData() {

        loader = Loader.show(requireContext());

        Map<String, Object> data = new HashMap<>();

        APIRequest.request("", Constants.MethodGET,
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
                spn_blood_group.setSelection(blood_group_id.indexOf(blood_group_ids));
            }
        }

        if (response.has("relationship")) {
            if (response.getJSONArray("relationship").length() > 0) {
                age_group_id.add(0);
                age_group.add("Select Relationship");
                JSONArray jsonArray = response.getJSONArray("relationship");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    age_group_id.add(jsonObject.getInt("id"));
                    age_group.add(jsonObject.getString("name"));
                }

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(requireContext(),
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
                spn_age_group.setSelection(age_group_id.indexOf(relationship_id));
            }
        }

        if (response.has("data")) {
            Toast.makeText(requireContext(), response.getString("message"),
                    Toast.LENGTH_LONG).show();

            et_family_member_name.setText("");
            tv_dob.setText("");
            spn_blood_group.setSelection(0);
            spn_age_group.setSelection(0);
            et_desc.setText("");
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

    private void validations() {

        String name = et_family_member_name.getText().toString();
        String dob = tv_dob.getText().toString();
        String desc = et_desc.getText().toString();

        if (name.isEmpty()) {
            et_family_member_name.setError("Enter Name");
            et_family_member_name.requestFocus();
        } else if (dob.isEmpty()) {
            Toast.makeText(requireContext(), "Select Date of Birth",
                    Toast.LENGTH_LONG).show();
        } else if (spn_blood_group.getSelectedItemPosition() == 0) {
            Toast.makeText(requireContext(), "Select Blood Group",
                    Toast.LENGTH_LONG).show();
        } else if (spn_age_group.getSelectedItemPosition() == 0) {
            Toast.makeText(requireContext(), "Select Relationship",
                    Toast.LENGTH_LONG).show();
        } else {
            loader = Loader.show(requireContext());

            Map<String, Object> data = new HashMap<>();
            if (family_member_id == 0) {
                data.put("name", name);
                data.put("date_of_birth", dob);
                data.put("blood_group_id", selectBloodGroupId);
                data.put("relationship_id", selectAgeGroupId);
                data.put("description", desc);
            } else {
                data.put("family_member_id", family_member_id);
                data.put("name", name);
                data.put("date_of_birth", dob);
                data.put("blood_group_id", selectBloodGroupId);
                data.put("relationship_id", selectAgeGroupId);
                data.put("description", desc);
            }

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
            showChooserDialog();

        } else if (id == R.id.btn_add) {
            validations();
        } else if (id == R.id.tv_dob) {

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
        }
    }
}