package com.example.sns_project.dictionary;

import java.util.HashMap;
import java.util.Map;

public class Crop_info {

    public String picture;
    public String crop_name;
    public String category;
    public String schedule;
    public String description1;
    public String description2;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public Crop_info(){}
    public Crop_info(String picture, String crop_name, String category, String schedule, String description1, String description2){
        this.picture = picture;
        this.crop_name=crop_name;
        this.category = category;
        this.schedule = schedule;
        this.description1=description1;
        this.description2=description2;

    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getCrop_name() {
        return crop_name;
    }

    public void setCrop_name(String crop_name) {
        this.crop_name = crop_name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }


    public String getDescription1() {
        return description1;
    }

    public void setDescription1(String description1) {
        this.description1 = description1;
    }

    public String getDescription2() {
        return description2;
    }

    public void setDescription2(String description2) {
        this.description2 = description2;
    }



}
