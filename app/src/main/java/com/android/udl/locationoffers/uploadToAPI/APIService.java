package com.android.udl.locationoffers.uploadToAPI;

import com.android.udl.locationoffers.domain.Message;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

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
}
