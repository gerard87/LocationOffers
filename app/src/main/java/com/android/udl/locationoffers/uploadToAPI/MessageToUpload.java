package com.android.udl.locationoffers.uploadToAPI;

/**
 * Created by ubuntu on 24/05/17.
 */

public class MessageToUpload {
    private String messageId;
    private String commerceId;
    private String title;
    private String description;
    private int download;
    private int exchange;

    public MessageToUpload(String messageId, String commerceId, String title, String description){
        this.messageId = messageId;
        this.commerceId = commerceId;
        this.title = title;
        this.description = description;
        this.download = 0;
        this.exchange = 0;
    }
}
