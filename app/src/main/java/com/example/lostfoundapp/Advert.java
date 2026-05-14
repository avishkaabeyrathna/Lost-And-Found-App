package com.example.lostfoundapp;

public class Advert {
    int id;
    String postType;
    String category;
    String name;
    String phone;
    String description;
    String date;
    String location;
    String imageUri;
    String timestamp;

    double latitude;
    double longitude;

    public Advert(int id, String postType, String category, String name, String phone,
                  String description, String date, String location, String imageUri, String timestamp, double latitude, double longitude) {
        this.id = id;
        this.postType = postType;
        this.category = category;
        this.name = name;
        this.phone = phone;
        this.description = description;
        this.date = date;
        this.location = location;
        this.imageUri = imageUri;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}