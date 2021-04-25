package com.trax.vmsapp;

public class ModelMemberList {

    private String name;
    private String date_of_birth;
    private String image_path;
    private String blood_group;
    private String age_group;


    public ModelMemberList(String name, String date_of_birth, String image_path, String blood_group, String age_group) {
        this.name = name;
        this.date_of_birth = date_of_birth;
        this.image_path = image_path;
        this.blood_group = blood_group;
        this.age_group = age_group;
    }

    public String getName() {
        return name;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public String getImage_path() {
        return image_path;
    }

    public String getBlood_group() {
        return blood_group;
    }

    public String getAge_group() {
        return age_group;
    }
}
