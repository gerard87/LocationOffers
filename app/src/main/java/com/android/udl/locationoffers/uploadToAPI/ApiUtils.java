package com.android.udl.locationoffers.uploadToAPI;

import android.util.Log;

import com.android.udl.locationoffers.domain.Message;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ubuntu on 24/05/17.
 */

public class ApiUtils {

    private ApiUtils() {}
    private String TAG = "APISERVICE";

    public static final String BASE_URL = "https://locationoffers.herokuapp.com/";

    public static APIService getAPIService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static void saveMessage(Message message){
        Log.i("APISERVICE","Intentant guardar missatge");
        APIService mAPIService = ApiUtils.getAPIService();

        mAPIService.saveMessage(message.getMessage_uid(),
                message.getCommerce_uid(),
                message.getTitle(),
                message.getDescription(),
                0,0).enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if(response.isSuccessful()){
                    Log.i("APISERVICE","Message Submitted");
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.i("APISERVICE","Message Submitted");
            }
        });

    }
}
