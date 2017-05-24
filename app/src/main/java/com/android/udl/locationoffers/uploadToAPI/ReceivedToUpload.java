package com.android.udl.locationoffers.uploadToAPI;

/**
 * Created by ubuntu on 24/05/17.
 */

public class ReceivedToUpload {
    String messageId;
    String userId;

    public ReceivedToUpload(String messageId, String userId){
        this.messageId = messageId;
        this.userId = userId;
    }
}
