package com.android.udl.locationoffers.uploadToAPI;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIService {

    @POST("messages")
    Call<ResponseBody> saveMessage(@Body MessageToUpload message);

    @POST("commerces")
    Call<ResponseBody> saveCommerce(@Body CommerceToUpload commerce);

    @POST("received")
    Call<ResponseBody> saveReceived(@Body ReceivedToUpload received);

    @PUT("received/{messageId}/{userId}/exchangedate")
    Call<ResponseBody> saveExchangeDate(@Path(value = "messageId", encoded = true)String messageId,
                                        @Path(value = "userId", encoded = true)String userId);

    @PUT("messages/{messageId}/downloads")
    Call<ResponseBody> increaseDownloadCounter(@Path(value = "messageId", encoded = true)
                                                        String messageId);

    @PUT("messages/{messageId}/exchange")
    Call<ResponseBody> increaseExchangeCounter(@Path(value = "messageId", encoded = true)
                                                       String messageId);

    @PUT("messages/{messageId}/removed")
    Call<ResponseBody> setMessageAsRemoved(@Path(value = "messageId", encoded = true)
                                                       String messageId,
                                           @Body Map<String, Boolean> data);

    @GET("messages/{messageId}/numDownloads")
    Call<ResponseBody> getNumDownloads(@Path(value = "messageId", encoded = true)
                                                   String messageId);

    @GET("messages/{messageId}/numExchanges")
    Call<ResponseBody> getNumExchanges(@Path(value = "messageId", encoded = true)
                                               String messageId);

    @GET("messages/{messageId}/publishDate")
    Call<ResponseBody> getPublishDate(@Path(value = "messageId", encoded = true)
                                               String messageId);
}
