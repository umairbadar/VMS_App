package com.fyp.vmsapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.fyp.vmsapp.R;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        CardView cv_upcoming_vaccinations = view.findViewById(R.id.cv_upcoming_vaccinations);
        cv_upcoming_vaccinations.setOnClickListener(this);

        CardView cv_family_member_tree = view.findViewById(R.id.cv_family_member_tree);
        cv_family_member_tree.setOnClickListener(this);

        CardView cv_articles = view.findViewById(R.id.cv_articles);
        cv_articles.setOnClickListener(this);

        CardView cv_history = view.findViewById(R.id.cv_history);
        cv_history.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id){
            case R.id.cv_upcoming_vaccinations:
                break;
            case R.id.cv_family_member_tree:
                navController.navigate(R.id.nav_family_member_tree);
                break;
            case R.id.cv_articles:
                navController.navigate(R.id.nav_article);
                break;
            case R.id.cv_history:
                navController.navigate(R.id.nav_history);
                break;
        }
    }
}