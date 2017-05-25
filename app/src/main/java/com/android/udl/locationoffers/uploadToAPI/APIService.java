package com.android.udl.locationoffers.uploadToAPI;

import com.android.udl.locationoffers.domain.Message;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Created by ubuntu on 24/05/17.
 */

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
}
