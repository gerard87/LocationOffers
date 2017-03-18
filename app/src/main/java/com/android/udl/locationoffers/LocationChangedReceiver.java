package com.android.udl.locationoffers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;

/**
 * Created by ubuntu on 18/03/17.
 */

public class LocationChangedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if(LocationResult.hasResult(intent)){
            LocationResult locationResult = LocationResult.extractResult(intent);
            Location location = locationResult.getLastLocation();

            String text = "Broadcast receiver\n"+
                    "Latitude: "+location.getLatitude()+
                    " - Longitude: "+location.getLongitude();

            Toast.makeText(context, text, Toast.LENGTH_LONG).show();


        }
    }

}
