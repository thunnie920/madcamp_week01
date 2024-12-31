package com.example.week01_project3;

public class Profile {
    private int picture;
    private String name;
    private String telNum;
    private String email;
    private String birthDay;

    // Constructor
    public Profile(int picture, String name, String telNum, String email, String birthDay) {
        this.picture = picture;
        this.name = name;
        this.telNum = telNum;
        this.email = email;
        this.birthDay = birthDay;
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
    public String getEmail() {
        return email;
    }
    public String getBirthDay() {
        return birthDay;
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
    public void setEmail(String email) {
        this.email = email;
    }
    public void setBirthday(String birthDay) {
        this.birthDay = birthDay;
    }

}
