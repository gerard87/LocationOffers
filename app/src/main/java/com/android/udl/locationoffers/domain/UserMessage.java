package com.android.udl.locationoffers.domain;

import android.graphics.Bitmap;

/**
 * Created by ubuntu on 19/03/17.
 */

public class UserMessage {

    private int id;
    private String title;
    private String description;
    private Bitmap image;
    private int commerce_id;
    private boolean shown;
    private boolean used;
    private Bitmap qrCode;

    public UserMessage(int id, String title, String description, Bitmap image, int commerce_id, boolean shown, boolean used, Bitmap qrCode){
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.commerce_id = commerce_id;
        this.shown = shown;
        this.used = used;
        this.qrCode = qrCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public int getCommerce_id() {
        return commerce_id;
    }

    public void setCommerce_id(int commerce_id) {
        this.commerce_id = commerce_id;
    }

    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public Bitmap getQrCode() {
        return qrCode;
    }

    public void setQrCode(Bitmap qrCode) {
        this.qrCode = qrCode;
    }
}
