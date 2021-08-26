package com.example.myapplication.models;

public class Target {
    private String name;
    private String phone;
    private String birth;
    private String team;
    private String debut;
    private String sns;
    private String intro;
    private String icon;

    public Target(String name, String phone, String team, String debut, String sns, String intro, String birth, String icon) {
        this.name = name;
        this.phone = phone;
        this.birth = birth;
        this.team = team;
        this.debut = debut;
        this.sns = sns;
        this.intro = intro;
        this.icon = icon;
    }

    public Target(String s) {
        this.name = name;
        this.phone = phone;
        this.birth = birth;
        this.team = team;
        this.debut = debut;
        this.sns = sns;
        this.intro = intro;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getDebut() {
        return debut;
    }

    public void setDebut(String debut) {
        this.debut = debut;
    }

    public String getSns() {
        return sns;
    }

    public void setSns(String sns) {
        this.sns = sns;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
