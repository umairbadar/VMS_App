package com.fyp.vmsapp.ui.article;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.vmsapp.R;
import com.fyp.vmsapp.utilities.RecyclerViewItemInterface;

import java.util.ArrayList;
import java.util.List;

public class ArticleFragment extends Fragment implements RecyclerViewItemInterface {

    private List<ArticleModel> lists;
    private ArticleAdapter adapter;

    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        RecyclerView article_list = view.findViewById(R.id.article_list);
        article_list.setLayoutManager(new LinearLayoutManager(requireContext()));
        lists = new ArrayList<>();
        adapter = new ArticleAdapter(lists, requireContext(), this);
        article_list.setAdapter(adapter);
        getArticles();

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        article_list.addItemDecoration(dividerItemDecoration);
    }

    private void getArticles() {

        for (int i = 1; i < 5; i++){

            ArticleModel item = new ArticleModel(
                    "Article " + i
            );
            lists.add(item);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void itemClick(String id) {

        navController.navigate(R.id.nav_article_details);

    }

    @Override
    public void edit(String imagePath, int family_member_id, int blood_group_id, int relationship_id, String name, String desc, String dob) {

    }

    @Override
    public void delete(int id) {

    }
}