package com.fyp.vmsapp.ui.article;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fyp.vmsapp.R;
import com.fyp.vmsapp.utilities.APIRequest;
import com.fyp.vmsapp.utilities.Constants;
import com.fyp.vmsapp.utilities.Loader;
import com.fyp.vmsapp.utilities.ResponseInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ArticleDetailFragment extends Fragment implements ResponseInterface {

    private int id;
    private String title;
    private TextView tv_article_content;
    private TextView tv_article_title;

    private ProgressDialog loader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            id = getArguments().getInt("id");
            title = getArguments().getString("title");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_article_content = view.findViewById(R.id.tv_article_content);
        tv_article_title = view.findViewById(R.id.tv_article_title);
        getArticleContent();
    }

    private void getArticleContent() {
        loader = Loader.show(requireContext());

        Map<String, Object> data = new HashMap<>();
        data.put("article_id", id);

        APIRequest.request("", Constants.MethodPOSTSimple,
                Constants.EndpointArticleContent, data, null, null, this);
    }

    @Override
    public void response(JSONObject response) throws JSONException {
        if (loader != null && loader.isShowing()) {
            loader.dismiss();
        }

        if (response.has("article_content")) {
            tv_article_title.setText(title);
            tv_article_content.setText(response.getString("article_content"));
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