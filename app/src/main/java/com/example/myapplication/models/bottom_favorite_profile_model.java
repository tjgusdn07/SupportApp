package com.example.myapplication.models;

import com.google.firebase.database.DatabaseReference;

public class bottom_favorite_profile_model {

    public String birth;
    public String phone;
    public String debut;
    public String icon;
    public String intro;
    public String name;
    public String sns;
    public String team;
    public String mCategory;
    public String email;

    public bottom_favorite_profile_model() {
    }

    public String getmCategory() {
        return mCategory;
    }

    public void setmCategory(String mCategory) {
        this.mCategory = mCategory;
    }

    public String getemail() {
        return email;
    }

    public void setemail(String email) {
        this.email = email;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getDebut() {
        return debut;
    }

    public void setDebut(String debut) {
        this.debut = debut;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSns() {
        return sns;
    }

    public void setSns(String sns) {
        this.sns = sns;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getphone() {
        return phone;
    }

    public void setphone(String phone) {
        this.phone = phone;
    }

    public void profile(String icon, String name, String sns, String team, String phone, String birth, String debut) {
        this.birth = birth;
        this.debut = debut;
        this.icon = icon;
        this.team = team;
        this.name = name;
        this.sns = sns;
        this.phone = phone;

    }
}