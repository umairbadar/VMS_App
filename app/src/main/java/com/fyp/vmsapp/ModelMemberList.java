package com.fyp.vmsapp;

public class ModelMemberList {

    private String name;
    private String date_of_birth;
    private String image_path;
    private String blood_group;
    private String age_group;
    private String relationship;
    private String desc;
    private int family_member_id;
    private int blood_group_id;
    private int relationship_id;
    private int age_group_id;

    public ModelMemberList(String name, String date_of_birth, String image_path, String blood_group, String age_group, String relationship, String desc, int family_member_id, int blood_group_id, int relationship_id, int age_group_id) {
        this.name = name;
        this.date_of_birth = date_of_birth;
        this.image_path = image_path;
        this.blood_group = blood_group;
        this.age_group = age_group;
        this.relationship = relationship;
        this.desc = desc;
        this.family_member_id = family_member_id;
        this.blood_group_id = blood_group_id;
        this.relationship_id = relationship_id;
        this.age_group_id = age_group_id;
    }

    public int getFamily_member_id() {
        return family_member_id;
    }

    public int getBlood_group_id() {
        return blood_group_id;
    }

    public int getRelationship_id() {
        return relationship_id;
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

    public String getRelationship() {
        return relationship;
    }

    public String getDesc() {
        return desc;
    }

    public int getAge_group_id() {
        return age_group_id;
    }
}
