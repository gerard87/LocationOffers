package com.android.udl.locationoffers.uploadToAPI;

import android.util.Log;

import com.android.udl.locationoffers.domain.Message;
import com.google.android.gms.tasks.Task;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ubuntu on 24/05/17.
 */

public class ApiUtils {

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://locationoffers.herokuapp.com/")
            .client(getHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private static APIService service = retrofit.create(APIService.class);

    private static OkHttpClient getHttpClient(){
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        return httpClient.build();
    }

    public static APIService getService(){
        return service;
    }

}
