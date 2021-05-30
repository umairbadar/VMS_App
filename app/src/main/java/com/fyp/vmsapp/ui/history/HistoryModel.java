package com.fyp.vmsapp.ui.history;

public class HistoryModel {

    private int id;
    private String name;
    private int status;
    private String slip_img;

    public HistoryModel(int id, String name, int status, String slip_img) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.slip_img = slip_img;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getStatus() {
        return status;
    }

    public String getSlip_img() {
        return slip_img;
    }
}
