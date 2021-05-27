package com.fyp.vmsapp.ui.vaccination;

import android.Manifest;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.vmsapp.R;
import com.fyp.vmsapp.utilities.APIRequest;
import com.fyp.vmsapp.utilities.Constants;
import com.fyp.vmsapp.utilities.Loader;
import com.fyp.vmsapp.utilities.RecyclerViewItemInterface;
import com.fyp.vmsapp.utilities.ResponseInterface;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class VaccinationFragment extends Fragment implements RecyclerViewItemInterface, ResponseInterface {

    private Adapter adapter;
    private List<Model> list;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Bitmap selectedBitmapImage = null;

    private String authorization;

    private ProgressDialog loader;
    private int age_group_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null){
            age_group_id = getArguments().getInt("id");
        }

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("data", MODE_PRIVATE);
        authorization = sharedPreferences.getString("api_token", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vaccination, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView vaccination_list = view.findViewById(R.id.vaccination_list);
        vaccination_list.setLayoutManager(new LinearLayoutManager(requireContext()));
        list = new ArrayList<>();
        adapter = new Adapter(list, requireContext(), this);
        vaccination_list.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        vaccination_list.addItemDecoration(dividerItemDecoration);

        getVaccinationList();
    }

    private void getVaccinationList() {

        loader = Loader.show(requireContext());

        Map<String, Object> data = new HashMap<>();
        data.put("age_group_id", age_group_id);

        APIRequest.request("", Constants.MethodPOSTSimple,
                Constants.EndpointGetVaccination, data, null, null, this);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                InputStream imageStream = requireActivity().getContentResolver().openInputStream(data.getData());
                selectedBitmapImage = BitmapFactory.decodeStream(imageStream);
                uploadSlip(selectedBitmapImage);
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
                uploadSlip(selectedBitmapImage);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadSlip(Bitmap bitmap){

        Map<String, Object> data = new HashMap<>();

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
            outputStream.write(bitmapToByte(bitmap));
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
    }

    private byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
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

    @Override
    public void itemClick(String id) {
        //upload slip
        showChooserDialog();
    }

    @Override
    public void edit(String imagePath, int family_member_id, int blood_group_id, int relationship_id, String name, String desc, String dob) {

    }

    @Override
    public void delete(int id) {
        //inject
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
                    Model item = new Model(
                            jsonObject.getInt("id"),
                            jsonObject.getString("vaccine_name")
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

        Toast.makeText(requireContext(), message,
                Toast.LENGTH_LONG).show();
    }
}