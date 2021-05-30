package com.fyp.vmsapp.ui.article;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArticleFragment extends Fragment implements RecyclerViewItemInterface, ResponseInterface {

    private List<ArticleModel> lists;
    private ArticleAdapter adapter;

    private NavController navController;

    private ProgressDialog loader;

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

        loader = Loader.show(requireContext());

        Map<String, Object> data = new HashMap<>();

        APIRequest.request("", Constants.MethodGET,
                Constants.EndpointArticles, data, null, null, this);
    }

    @Override
    public void itemClick(String id, int family_member_id) {
        Bundle args = new Bundle();
        args.putInt("id", family_member_id);
        args.putString("title", id);
        navController.navigate(R.id.nav_article_details, args);
    }

    @Override
    public void edit(String imagePath, int family_member_id, int blood_group_id, int relationship_id, String name, String desc, String dob) {

    }

    @Override
    public void delete(int id) {
    }

    @Override
    public void response(JSONObject response) throws JSONException {

        if (loader != null && loader.isShowing()) {
            loader.dismiss();
        }

        if (response.has("articles")){
            if (response.getJSONArray("articles").length() > 0){
                JSONArray jsonArray = response.getJSONArray("articles");
                for (int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ArticleModel item = new ArticleModel(
                            jsonObject.getInt("id"),
                            jsonObject.getString("title")
                    );

                    lists.add(item);
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