package com.example.scutapp.user.talkto.model;

public class Doctor {
    private String email;
    private String fullName;
    private String id;
    private String specialization;

    public Doctor(String email, String fullName, String id, String specialization) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.specialization = specialization;
    }

    public Doctor(String email, String fullName, String specialization) {
        this.email = email;
        this.fullName = fullName;
        this.specialization = specialization;
    }

    public Doctor() {
    }

    public Doctor(String fullName, String specialization) {
        this.fullName = fullName;
        this.specialization = specialization;
    }

    public Doctor(String fullName) {
        this.fullName = fullName;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}
