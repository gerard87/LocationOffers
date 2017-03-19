package com.android.udl.locationoffers.domain;

import android.graphics.Bitmap;

/**
 * Created by gerard on 16/03/17.
 */

public class Commerce {

    private int id;
    private String name;
    private String password;
    private Bitmap image;
    private String placesID;

    public Commerce(int id, String name, String password, Bitmap image, String placesID) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.image = image;
        this.placesID = placesID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
