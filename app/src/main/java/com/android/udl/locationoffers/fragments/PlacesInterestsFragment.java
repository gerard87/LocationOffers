package com.android.udl.locationoffers.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import com.android.udl.locationoffers.R;
import com.android.udl.locationoffers.domain.PlaceInterest;
import com.android.udl.locationoffers.domain.PlacesInterestEnum;
import com.android.udl.locationoffers.services.NotificationService;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class PlacesInterestsFragment extends Fragment {

    Context myContext;
    PlaceInterestAdapter dataAdapter = null;
    ListView lv;

    public PlacesInterestsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_places_interests, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstance){
        if (getView() != null ) {
            lv = (ListView) getView().findViewById(R.id.listView1);
        }
        if (lv != null) {
            displayListView();
        }
    }


    @Override
    public void onPause(){
        if(NotificationService.isServiceRunning()){
            Intent serviceIntent = new Intent(getActivity(), NotificationService.class);
            serviceIntent.addCategory(NotificationService.TAG);
            getActivity().stopService(serviceIntent);

            serviceIntent = new Intent(getActivity(), NotificationService.class);
            serviceIntent.addCategory(NotificationService.TAG);
            getActivity().startService(serviceIntent);
        }
        super.onPause();
    }


    private class PlaceInterestAdapter extends ArrayAdapter<PlaceInterest> {

        private ArrayList<PlaceInterest> interestList;

        PlaceInterestAdapter(Context context, int textViewResourceId,
                             ArrayList<PlaceInterest> interestList) {
            super(context, textViewResourceId, interestList);
            this.interestList = new ArrayList<>();
            this.interestList.addAll(interestList);
        }

        private class ViewHolder {
            CheckBox name;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            ViewHolder holder;
            Log.i("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) myContext.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.interest_place_layout, parent, false);

                holder = new ViewHolder();
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        PlaceInterest interest = (PlaceInterest) cb.getTag();
                        interest.setSelected(cb.isChecked());

                        SharedPreferences pref = myContext.getSharedPreferences(getString(R.string.PREFERENCES_NAME), MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean(((PlaceInterest)cb.getTag()).getName(),cb.isChecked());
                        editor.apply();
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            PlaceInterest interest = interestList.get(position);
            holder.name.setText(getResources().getIdentifier(interest.getName(),"string","com.android.udl.locationoffers"));
            holder.name.setChecked(interest.isSelected());
            holder.name.setTag(interest);

            return convertView;

        }

    }


    private void displayListView(){
        SharedPreferences pref = myContext.getSharedPreferences(getString(R.string.PREFERENCES_NAME), MODE_PRIVATE);
        ArrayList<PlaceInterest> interestList = new ArrayList<>();

        PlaceInterest pi;
        for(PlacesInterestEnum interest : PlacesInterestEnum.values()){
            pi = new PlaceInterest(interest.toString(), pref.getBoolean(interest.toString(),true));
            interestList.add(pi);
        }

        dataAdapter = new PlaceInterestAdapter(myContext,R.layout.interest_place_layout,interestList);
        lv.setAdapter(dataAdapter);
    }
}
