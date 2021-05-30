package com.fyp.vmsapp.ui.article;

public class ArticleModel {

    private int id;
    private String name;

    public ArticleModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
