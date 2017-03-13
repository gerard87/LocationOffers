package com.android.udl.locationoffers.domain;

import android.graphics.Bitmap;

/**
 * Created by gerard on 07/03/17.
 */

public class Message {
    private String title;
    private String description;
    private Bitmap image;

    public Message(String title, String description, Bitmap image) {
        this.title = title;
        this.description = description;
        this.image = image;
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

    @Override
    public String toString() {
        return "Message{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", image=" + image.toString() +
                '}';
    }
}
