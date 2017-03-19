package com.android.udl.locationoffers.domain;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.udl.locationoffers.Utils.BitmapUtils;

/**
 * Created by gerard on 07/03/17.
 */

public class Message implements Parcelable{

    private int id;
    private String title;
    private String description;
    private Bitmap image;
    private int commerce_id;
    private boolean removed;
    private String commerce_name;


    public Message(int id, String title, String description, Bitmap image,
                   int commerce_id, boolean removed, String commerce_name) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.commerce_id = commerce_id;
        this.removed = removed;
        this.commerce_name = commerce_name;
    }

    protected Message(Parcel in) {
        id = in.readInt();
        title = in.readString();
        description = in.readString();
        image = in.readParcelable(Bitmap.class.getClassLoader());
        commerce_id = in.readInt();
        removed = in.readByte()!=0;
        commerce_name = in.readString();
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCommerce_id() {
        return commerce_id;
    }

    public void setCommerce_id(int commerce_id) {
        this.commerce_id = commerce_id;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public String getCommerce_name() {
        return commerce_name;
    }

    public void setCommerce_name(String commerce_name) {
        this.commerce_name = commerce_name;
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
        dest.writeInt(id);
        dest.writeByte((byte)(removed?1:0));
        dest.writeInt(commerce_id);
        dest.writeString(commerce_name);
    }
}
