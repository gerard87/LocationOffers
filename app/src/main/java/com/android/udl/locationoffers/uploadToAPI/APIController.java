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

        MessageToUpload msgToUpload = new MessageToUpload(message.getMessage_uid(),
                message.getCommerce_uid(),
                message.getTitle(),
                message.getDescription());

        ApiUtils.getService().saveMessage(msgToUpload)
                .enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("APISERVER", "UPLOADED MESSAGE");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("APISERVER", "ERROR");
            }
        });

        return tcs.getTask();
    }


    public Task<Void> saveCommerce(CommerceToUpload commerce){
        final TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        ApiUtils.getService().saveCommerce(commerce)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.i("APISERVER", "UPLOADED COMMERCE");
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.i("APISERVER", "ERROR");
                    }
                });

        return tcs.getTask();
    }


    public Task<Void> saveReceived(ReceivedToUpload received){
        final TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        ApiUtils.getService().saveReceived(received)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.i("APISERVER", "UPLOADED RECEIVED");
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.i("APISERVER", "ERROR");
                    }
                });

        return tcs.getTask();
    }


    public Task<Void> saveExchangeDate(String messageId, String userId){
        final TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        ApiUtils.getService().saveExchangeDate(messageId, userId)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.i("APISERVER", "UPLOADED EXCHANGEDATE");
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.i("APISERVER", "ERROR");
                    }
                });

        return tcs.getTask();
    }

    public Task<Void> increaseDownloadCounter(String messageId){
        final TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        ApiUtils.getService().increaseDownloadCounter(messageId)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.i("APISERVER", "Download counter increased");
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.i("APISERVER", "ERROR");
                    }
                });

        return tcs.getTask();
    }

    public Task<Void> increaseExchangeCounter(String messageId){
        final TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        ApiUtils.getService().increaseExchangeCounter(messageId)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.i("APISERVER", "Exchange counter increased");
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.i("APISERVER", "ERROR");
                    }
                });

        return tcs.getTask();
    }
}
