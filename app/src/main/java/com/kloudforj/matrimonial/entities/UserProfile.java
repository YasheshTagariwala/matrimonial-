package com.kloudforj.matrimonial.entities;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class UserProfile
{
    private int id;
    private String email;
    private String first_name, middle_name, last_name, date_of_birth, sex;
    private String address1, address2, address3, country, state, city, pincode, phone_number;
    private String caste, sub_caste1, sub_caste2;
    private boolean email_verified, phone_number_verified;

//    public static ArrayList<UserProfile> fromJson(JSONArray jsonArrayUsers) {
    public static void fromJson (JSONArray jsonArrayUsers) {

        JSONObject jsonObjectUserDetails;
        ArrayList<UserProfile> userProfiles = new ArrayList<UserProfile>(jsonArrayUsers.length());

        for(int i = 0; i < jsonArrayUsers.length(); i++) {

            try {

                jsonObjectUserDetails = jsonArrayUsers.getJSONObject(i).getJSONObject("user_profile");

            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

    }
}