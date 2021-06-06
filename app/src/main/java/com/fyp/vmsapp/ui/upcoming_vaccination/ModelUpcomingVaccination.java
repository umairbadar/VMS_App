package com.fyp.vmsapp.ui.upcoming_vaccination;

public class ModelUpcomingVaccination {

    private String family_member_name;
    private String vaccination_name;

    public ModelUpcomingVaccination(String family_member_name, String vaccination_name) {
        this.family_member_name = family_member_name;
        this.vaccination_name = vaccination_name;
    }

    public String getFamily_member_name() {
        return family_member_name;
    }

    public String getVaccination_name() {
        return vaccination_name;
    }
}
