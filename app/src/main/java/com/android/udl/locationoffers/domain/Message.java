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
    private boolean removed;
    private String commerce_name;
    private String commerce_uid;
    private String message_uid;
    private Boolean used;//USER

    public Message () {
    }

    public Message(String title, String description, String commerce_name,
                   String commerce_uid, Boolean used, Boolean removed){
        this.title = title;
        this.description = description;
        this.commerce_name = commerce_name;
        this.commerce_uid = commerce_uid;
        this.used = used;
        this.removed = removed;
    }

    public Message(String title, String description, String commerce_uid,
                   String commerce_name, String message_uid) {
        this.title = title;
        this.description = description;
        this.removed = false;
        this.commerce_uid = commerce_uid;
        this.commerce_name = commerce_name;
        this.message_uid = message_uid;
    }

    protected Message(Parcel in) {
        title = in.readString();
        description = in.readString();
        image = in.readParcelable(Bitmap.class.getClassLoader());
        removed = in.readByte()!=0;
        commerce_name = in.readString();
        commerce_uid = in.readString();
        message_uid = in.readString();
        used = in.readByte()!=0;
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

    public String getCommerce_uid() {
        return commerce_uid;
    }

    public void setCommerce_uid(String commerce_uid) {
        this.commerce_uid = commerce_uid;
    }

    public String getMessage_uid() {
        return message_uid;
    }

    public void setMessage_uid(String message_uid) {
        this.message_uid = message_uid;
    }

    public Boolean isUsed() { return used;  }

    public void setUsed(boolean used) { this.used = used; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeByteArray(BitmapUtils.bitmapToByteArray(image));
        dest.writeByte((byte)(removed?1:0));
        dest.writeString(commerce_name);
        dest.writeString(commerce_uid);
        dest.writeString(message_uid);
        try{
            dest.writeByte((byte)(used ?1:0));
        }catch(NullPointerException ignored){
        }

    }

    @Override
    public String toString() {
        return "Message{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", image=" + image +
                ", removed=" + removed +
                ", commerce_name='" + commerce_name + '\'' +
                ", commerce_uid='" + commerce_uid + '\'' +
                ", message_uid='" + message_uid + '\'' +
                ", used=" + used +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (title != null ? !title.equals(message.title) : message.title != null) return false;
        if (description != null ? !description.equals(message.description) : message.description != null)
            return false;
        if (commerce_uid != null ? !commerce_uid.equals(message.commerce_uid) : message.commerce_uid != null)
            return false;
        return message_uid != null ? message_uid.equals(message.message_uid) : message.message_uid == null;

    }
}
