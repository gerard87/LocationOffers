package com.android.udl.locationoffers.uploadToAPI;

import com.android.udl.locationoffers.domain.Message;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by ubuntu on 24/05/17.
 */

public interface APIService {

    @POST("/messages/")
    @FormUrlEncoded
    Call<Message> saveMessage(@Field("messageId") String messageId,
                              @Field("commerceId") String commerceId,
                              @Field("title") String title,
                              @Field("description") String description,
                              @Field("download") int download,
                              @Field("exchange") int exchange);
}
