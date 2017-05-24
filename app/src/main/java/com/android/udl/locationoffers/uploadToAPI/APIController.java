package com.android.udl.locationoffers.uploadToAPI;

import android.util.Log;

import com.android.udl.locationoffers.domain.Message;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ubuntu on 24/05/17.
 */

public class APIController {

    private static APIController instance;

    private APIController() {}

    public static synchronized APIController getInstance(){
        if(instance == null){
            instance = new APIController();
        }
        return instance;
    }

    public Task<Void> saveMessage(Message message){
        final TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();


        ApiUtils.getService().saveMessage(message.getMessage_uid(),
                message.getCommerce_uid(),
                message.getTitle(),
                message.getDescription(),
                0,0)
                .enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("APISERVER", "SUBIDO");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("APISERVER", "ERROR");
            }
        });

        return tcs.getTask();
    }
}
