package com.fyp.vmsapp.ui.home;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showCloseDialog();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
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

    public void showCloseDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext()).create();
        alertDialog.setMessage("Are you sure you want to close app?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        requireActivity().finish();
                        dialog.dismiss();
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {
            case R.id.cv_upcoming_vaccinations:
                navController.navigate(R.id.nav_upcoming_vaccination);
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