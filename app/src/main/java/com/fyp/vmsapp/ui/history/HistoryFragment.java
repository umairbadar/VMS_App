package com.fyp.vmsapp.ui.history;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.vmsapp.R;
import com.fyp.vmsapp.utilities.APIRequest;
import com.fyp.vmsapp.utilities.Constants;
import com.fyp.vmsapp.utilities.Loader;
import com.fyp.vmsapp.utilities.RecyclerViewItemInterface;
import com.fyp.vmsapp.utilities.ResponseInterface;
import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class HistoryFragment extends Fragment implements ResponseInterface, View.OnClickListener, RecyclerViewItemInterface {

    private Spinner spn_family_member;
    private List<String> family_member_name;
    private List<Integer> family_member_id;
    private List<Integer> age_group_id;
    private int selectFamilyMemberId = -1;
    private int selectAgeGroupId = -1;
    private String selectFamilyMemberName = "";

    private SharedPreferences sharedPreferences;
    private String authorization;

    private ProgressDialog loader;

    private List<HistoryModel> list;
    private HistoryAdapter adapter;

    private boolean callStatus = false;

    private Button btn_create_pdf;

    private List<String> pdf;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = requireActivity().getSharedPreferences("data", MODE_PRIVATE);
        authorization = sharedPreferences.getString("api_token", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pdf = new ArrayList<>();

        RecyclerView history_list = view.findViewById(R.id.history_list);
        history_list.setLayoutManager(new LinearLayoutManager(requireContext()));
        list = new ArrayList<>();
        adapter = new HistoryAdapter(requireContext(), list, this);
        history_list.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        history_list.addItemDecoration(dividerItemDecoration);

        Button btn_search = view.findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);

        btn_create_pdf = view.findViewById(R.id.btn_create_pdf);
        btn_create_pdf.setOnClickListener(this);

        spn_family_member = view.findViewById(R.id.spn_family_member);
        family_member_name = new ArrayList<>();
        family_member_id = new ArrayList<>();
        age_group_id = new ArrayList<>();
        spn_family_member.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectFamilyMemberId = family_member_id.get(position);
                selectFamilyMemberName = family_member_name.get(position);
                selectAgeGroupId = age_group_id.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getFamilyMembers();
    }

    private void getFamilyMembers() {

        loader = Loader.show(requireContext());

        Map<String, Object> data = new HashMap<>();

        APIRequest.request(authorization, Constants.MethodGET,
                Constants.EndpointList, data, null, null, this);
    }

    private void getFamilyMemberHistory() {

        loader = Loader.show(requireContext());

        Map<String, Object> data = new HashMap<>();
        data.put("age_group_id", selectAgeGroupId);
        data.put("family_member_id", selectFamilyMemberId);

        APIRequest.request("", Constants.MethodPOSTSimple,
                Constants.EndpointGetVaccination, data, null, null, this);
    }

    private void showImageDialog(Context context, String imageUrl) {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.image_dialog);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        ZoomageView imageView = dialog.findViewById(R.id.opendImage);

        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.no_image)
                .fit()
                .centerInside()
                .into(imageView);

        dialog.show();
    }

    private void generatePDF() {

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint paint1 = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        //paint1.setTextAlign(Paint.Align.CENTER);
        paint1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint1.setTextSize(70);

        //paint.setTextAlign(Paint.Align.CENTER);
        //paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextSize(50);


        canvas.drawText("Family Member Name: " + selectFamilyMemberName, 10, 270, paint1);

        int abc = 350;
        for (int i = 0; i < pdf.size(); i++) {
            canvas.drawText(pdf.get(i), 30, abc, paint);
            abc += 70;
        }

        pdfDocument.finishPage(page);

        File file = new File(Environment.getExternalStorageDirectory(), "/" + selectFamilyMemberName + " vaccination history.pdf");

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(requireContext(), "PDF saved",
                    Toast.LENGTH_LONG).show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        pdfDocument.close();
    }

    @Override
    public void response(JSONObject response) throws JSONException {

        if (loader != null && loader.isShowing()) {
            loader.dismiss();
        }

        if (!callStatus) {
            if (response.has("data")) {
                if (response.getJSONArray("data").length() > 0) {
                    family_member_id.add(0);
                    age_group_id.add(0);
                    family_member_name.add("Select Family Member");
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        family_member_id.add(jsonObject.getInt("family_member_id"));
                        age_group_id.add(jsonObject.getInt("age_group_id"));
                        family_member_name.add(jsonObject.getString("name"));
                    }

                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(requireContext(),
                            R.layout.support_simple_spinner_dropdown_item, family_member_name) {
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
                    spn_family_member.setAdapter(dataAdapter);
                }
            }
        } else {
            if (response.has("data")) {

                if (list.size() > 0) {
                    list.clear();
                    adapter.notifyDataSetChanged();
                }

                if (response.getJSONArray("data").length() > 0) {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int status = jsonObject.getInt("status");
                        String hospital_name = jsonObject.isNull("hospital_name") ? "" : jsonObject.getString("hospital_name");
                        HistoryModel item = new HistoryModel(
                                jsonObject.getInt("id"),
                                jsonObject.getString("name"),
                                hospital_name,
                                status,
                                jsonObject.isNull("slip_img") ? "" : jsonObject.getString("slip_img")
                        );
                        if (status == 0) {
                            pdf.add(jsonObject.getString("name") + "     Not injected");
                        } else if (status == 1) {
                            pdf.add(jsonObject.getString("name") + "     Injected from " + hospital_name + " but slip not uploaded");
                        } else if (status == 2) {
                            pdf.add(jsonObject.getString("name") + "     Injected from " + hospital_name + "  & slip uploaded");
                        }
                        list.add(item);
                        adapter.notifyDataSetChanged();

                        if (btn_create_pdf.getVisibility() == View.INVISIBLE)
                            btn_create_pdf.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    @Override
    public void failure(String message) {

        if (loader != null && loader.isShowing()) {
            loader.dismiss();
        }

        if (list.size() > 0) {
            list.clear();
            adapter.notifyDataSetChanged();
        }

        if (btn_create_pdf.getVisibility() == View.VISIBLE)
            btn_create_pdf.setVisibility(View.INVISIBLE);

        Toast.makeText(requireContext(), message,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void itemClick(String id, int family_member_id) {
        //View Slip
        showImageDialog(requireContext(), id);
    }

    @Override
    public void edit(String imagePath, int family_member_id, int blood_group_id, int relationship_id, String name, String desc, String dob) {

    }

    @Override
    public void delete(int id, String hospital_name) {

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.btn_search) {
            if (spn_family_member.getSelectedItemPosition() != 0) {
                if (list.size() > 0) {
                    list.clear();
                    adapter.notifyDataSetChanged();
                }

                if (btn_create_pdf.getVisibility() == View.VISIBLE)
                    btn_create_pdf.setVisibility(View.INVISIBLE);

                if (pdf.size() > 0) {
                    pdf.clear();
                }

                callStatus = true;
                getFamilyMemberHistory();
            } else {
                if (list.size() > 0) {
                    list.clear();
                    adapter.notifyDataSetChanged();
                }
                Toast.makeText(requireContext(), "Select Family Member",
                        Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.btn_create_pdf) {
            generatePDF();
        }
    }
}