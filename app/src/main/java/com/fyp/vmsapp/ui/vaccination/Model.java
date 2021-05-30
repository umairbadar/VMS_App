package com.fyp.vmsapp.ui.vaccination;

public class Model {

    private int id;
    private String name;
    private int status;

    public Model(int id, String name, int status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getStatus() {
        return status;
    }
}
