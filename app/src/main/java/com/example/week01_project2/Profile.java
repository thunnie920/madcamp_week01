package com.example.week01_project2;

public class Profile {
    private int picture;
    private String name;
    private String telNum;

    // Constructor
    public Profile(int picture, String name, String telNum, String email, String birthDay) {
        this.picture = picture;
        this.name = name;
        this.telNum = telNum;
    }

    // Getters
    public int getPicture() {
        return picture;
    }
    public String getName() {
        return name;
    }
    public String getTelNum() {
        return telNum;
    }

    // Setters
    public void setPicture(int picture) {
        this.picture = picture;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setTelNum(String telNum) {
        this.telNum = telNum;
    }
}
