package com.example.scutapp.doctor.doctorchat;

public class User {
    private String email;
    private String fullName;
    private String phone;
    private String id;
    private String state;

    public User(String email, String fullName, String phone, String id, String state) {
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.id = id;
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
