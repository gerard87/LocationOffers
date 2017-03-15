package com.android.udl.locationoffers.domain;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.udl.locationoffers.Utils.BitmapUtils;

/**
 * Created by gerard on 07/03/17.
 */

public class Message implements Parcelable{
    private String title;
    private String description;
    private Bitmap image;

    public Message(String title, String description, Bitmap image) {
        this.title = title;
        this.description = description;
        this.image = image;
    }

    protected Message(Parcel in) {
        title = in.readString();
        description = in.readString();
        image = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeByteArray(BitmapUtils.bitmapToByteArray(image));
    }
}
