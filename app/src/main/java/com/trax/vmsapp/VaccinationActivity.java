package com.trax.vmsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class VaccinationActivity extends AppCompatActivity {

    private String cat;

    private List<Model> lists;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccination);

        Toolbar toolbar = findViewById(R.id.toolbar_vaccination);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("Vaccination List");
        ImageButton btnLogout = toolbar.findViewById(R.id.btnLogout);
        btnLogout.setVisibility(View.GONE);

        TextView tv_title = toolbar.findViewById(R.id.tv_title);
        tv_title.setText("Vaccination List");

        cat = getIntent().getStringExtra("cat");

        RecyclerView list = findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        lists = new ArrayList<>();
        adapter = new Adapter(lists, getApplicationContext());
        list.setAdapter(adapter);
        getVaccinationList();

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        list.addItemDecoration(dividerItemDecoration);
    }

    private void getVaccinationList() {

        for (int i = 0; i < 12; i++){

            Model item = new Model(
                    "Vaccination " + i
            );

            lists.add(item);
            adapter.notifyDataSetChanged();
        }
    }
}