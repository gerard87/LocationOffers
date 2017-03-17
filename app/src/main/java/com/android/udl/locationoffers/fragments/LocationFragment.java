package com.android.udl.locationoffers.fragments;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.udl.locationoffers.R;
import com.android.udl.locationoffers.domain.PlaceInterest;
import com.android.udl.locationoffers.domain.PlacesInterestEnum;
import com.android.udl.locationoffers.domain.PlacesInterestEnumTranslator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static android.content.Context.MODE_PRIVATE;

public class LocationFragment extends Fragment  implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static int UPDATE_INTERVAL = 2000;
    private static int FASTEST_INTERVAL = 500;
    private static int DISPLACEMENT = 1;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    ArrayList<Integer> interestList;

    private Button btn1;
    private TextView tv1;

    private boolean mRequestingLocationUpdates = false;

    public LocationFragment() {
        // Required empty public constructor
    }

    public static LocationFragment newInstance() {
        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkPlayServices()) {
            buildGoogleApiClient();
            createLocationRequest();
        }

        SharedPreferences pref = getContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        interestList = new ArrayList<>();

        for(PlacesInterestEnum interest : PlacesInterestEnum.values()){
            if(pref.getBoolean(interest.toString(),true)){
                interestList.add(PlacesInterestEnumTranslator.translate(interest.toString()));
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    private void callPlaceDetectionApi() throws SecurityException {
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                String allPlaces = "";
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    Log.i("CALLPLACEDETECTIONAPI", String.format("Place '%s' with " +
                                    "likelihood: %g, TYPE: '%s'",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood(),placeLikelihood.getPlace().getPlaceTypes().toString()));

                    if(!Collections.disjoint(placeLikelihood.getPlace().getPlaceTypes(),(interestList))){
                        allPlaces += "\n" + placeLikelihood.getPlace().getName() + " " + placeLikelihood.getLikelihood();
                    }
                }

                likelyPlaces.release();
                Log.i("CALLPLACEDETECTIONAPI:","text view modificat");
                tv1.setText(allPlaces);
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstance){
        tv1 = (TextView) getView().findViewById(R.id.tvPlacesList);

        btn1 = (Button) getView().findViewById(R.id.button2);
        btn1.setText("START LOCATION UPDATES");
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePeriodicLocationUpdates();
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("ON CONNECTION FAILED","Google Places API connection failed with error code:");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    protected void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    protected void createLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private boolean checkPlayServices(){
        GoogleApiAvailability googleAPIAvailability = GoogleApiAvailability.getInstance();
        int result = googleAPIAvailability.isGooglePlayServicesAvailable(this.getActivity());
        if(result != ConnectionResult.SUCCESS){
            if(googleAPIAvailability.isUserResolvableError(result)) {
                googleAPIAvailability.getErrorDialog(getActivity(), result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //cuan esta conectat amb la api, per tant es on s'ha de cridar lo que actualitzi
        callPlaceDetectionApi();

        if (mRequestingLocationUpdates)
            startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callPlaceDetectionApi();
                }
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location){
        Toast.makeText(getActivity(),"Location changed!!!!",Toast.LENGTH_SHORT).show();
        callPlaceDetectionApi();
    }

    private void togglePeriodicLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            // Changing the button text

            mRequestingLocationUpdates = true;
            btn1.setText("STOP LOCATION UPDATES");

            startLocationUpdates();

            Log.d("LO", "Periodic location updates started!");

        } else {
            // Changing the button text

            mRequestingLocationUpdates = false;
            btn1.setText("START LOCATION UPDATES");

            stopLocationUpdates();

            Log.d("LO", "Periodic location updates stopped!");
        }
    }


    protected void startLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);

    }

}
