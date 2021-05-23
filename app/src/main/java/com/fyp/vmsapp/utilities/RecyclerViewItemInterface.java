package com.fyp.vmsapp.utilities;

public interface RecyclerViewItemInterface {
    void itemClick(String id);

    void edit(String imagePath, int family_member_id, int blood_group_id, int relationship_id, String name, String desc, String dob);

    void delete(int id);
}
