package com.careerdevs.gateway.gorest.models;

import org.springframework.web.bind.annotation.RequestParam;

public class GoRestUser {
    private String name;
    private String email;
    private String gender;
    private String status;
    private String id;


    public GoRestUser(String name, String email, String gender, String status) {
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.status = status;
    }

    public GoRestUser(String name, String email, String gender, String status, String id) {
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.status = status;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
