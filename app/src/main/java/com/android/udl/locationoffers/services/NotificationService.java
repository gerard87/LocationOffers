package com.android.udl.locationoffers.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.udl.locationoffers.R;
import com.android.udl.locationoffers.domain.Message;
import com.android.udl.locationoffers.domain.PlacesInterestEnum;
import com.android.udl.locationoffers.domain.PlacesInterestEnumTranslator;
import com.android.udl.locationoffers.uploadToAPI.APIController;
import com.android.udl.locationoffers.uploadToAPI.ReceivedToUpload;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ubuntu on 18/03/17.
 */

public class NotificationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //Service status variables
    private static NotificationService instance = null;
    public static final String TAG = "NotificationServiceTAG";


    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    FirebaseDatabase db;
    FirebaseUser user;

    ArrayList<Integer> interestList;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 20000; // 10 sec
    private static int FASTEST_INTERVAL = 20000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    private LocationRequest mLocationRequest;

    @Override
    public void onCreate(){
        showToast(getString(R.string.service_started));
        instance = this;

        db = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences pref = getApplicationContext().getSharedPreferences(getString(R.string.PREFERENCES_NAME), MODE_PRIVATE);
        interestList = new ArrayList<>();

        for(PlacesInterestEnum interest : PlacesInterestEnum.values()){
            if(pref.getBoolean(interest.toString(),true)){
                interestList.add(PlacesInterestEnumTranslator.translate(interest.toString()));
            }
        }

        // Building the GoogleApi client
        if (checkPlayServices()) {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }


            // Building the GoogleApi client
            buildGoogleApiClient();

            createLocationRequest();

            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
        }
    }



    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices(){
        GoogleApiAvailability googleAPIAvailability = GoogleApiAvailability.getInstance();
        int result = googleAPIAvailability.isGooglePlayServicesAvailable(getApplicationContext());
        return result == ConnectionResult.SUCCESS;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    /**
     * Creating google api client object
     * */
    protected void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(getApplicationContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    /**
     * Creating location request object
     * */
    protected void createLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy()
    {
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
        instance = null;
        showToast(getString(R.string.service_stopped));
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        callPlaceDetectionApi();

        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("ON CONNECTION FAILED","Google Places API connection failed with error code:");
    }

    @Override
    public void onLocationChanged(Location location) {
        callPlaceDetectionApi();
    }



    private void callPlaceDetectionApi() throws SecurityException {

        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces) {
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    if(!Collections.disjoint(placeLikelihood.getPlace().getPlaceTypes(),(interestList))){
                        Log.i("CALLPLACEDETECTIONAPI", String.format("Place '%s' with " +
                                        "likelihood: %g, TYPE: '%s'",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood(),placeLikelihood.getPlace().getPlaceTypes().toString()));

                        getMessagesByPlacesID(placeLikelihood.getPlace().getId());
                    }
                }
                likelyPlaces.release();
            }
        });
    }

    public void insertMessageIfNotReceived (final Message message) {
        DatabaseReference umsgRef = db.getReference("User messages")
                .child(user.getUid()).child(message.getMessage_uid());
        umsgRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    dataSnapshot.getRef().setValue(message);
                    //showNotification(message);
                    APIController.getInstance().saveReceived(
                            new ReceivedToUpload(message.getMessage_uid(),user.getUid())
                    );
                    APIController.getInstance().increaseDownloadCounter(message.getMessage_uid());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void insertMessageIfNotRemoved (final Message message) {
        DatabaseReference umsgRef = db.getReference("User removed")
                .child(user.getUid()).child(message.getMessage_uid());
        umsgRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    insertMessageIfNotReceived(message);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getMessagesByCommerceId (final String id) {
        DatabaseReference msgRef = db.getReference("Messages").child(id);
        msgRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Message message = postSnapshot.getValue(Message.class);
                    message.setUsed(false);
                    insertMessageIfNotRemoved(message);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getMessagesByPlacesID (final String id) {
        final DatabaseReference usersRef = db.getReference("Users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> commerce_id = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    if (postSnapshot.child("place").exists() &&
                            postSnapshot.child("place").getValue().equals(id)) {
                        commerce_id.add(postSnapshot.getKey());
                    }
                }
                for (String cid: commerce_id) {
                    Log.d("Messages By Place Id", cid);
                    getMessagesByCommerceId(cid);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void showToast(final String message){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),message,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //call this method to know if service is running and should restart after some changes in types of places
    public static boolean isServiceRunning(){
        return instance != null;
    }
}
