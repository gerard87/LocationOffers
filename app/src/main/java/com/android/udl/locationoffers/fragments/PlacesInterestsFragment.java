package com.android.udl.locationoffers.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.udl.locationoffers.R;
import com.android.udl.locationoffers.domain.PlaceInterest;
import com.android.udl.locationoffers.domain.PlacesInterestEnum;
import com.android.udl.locationoffers.services.NotificationService;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlacesInterestsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlacesInterestsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlacesInterestsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Context myContext;
    PlaceInterestAdapter dataAdapter = null;
    ListView lv;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PlacesInterestsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlacesInterestsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlacesInterestsFragment newInstance(String param1, String param2) {
        PlacesInterestsFragment fragment = new PlacesInterestsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

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
        lv = (ListView) getView().findViewById(R.id.listView1);
        displayListView();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }




    private class PlaceInterestAdapter extends ArrayAdapter<PlaceInterest> {

        private ArrayList<PlaceInterest> interestList;

        public PlaceInterestAdapter(Context context, int textViewResourceId,
                               ArrayList<PlaceInterest> interestList) {
            super(context, textViewResourceId, interestList);
            this.interestList = new ArrayList<>();
            this.interestList.addAll(interestList);
        }

        private class ViewHolder {
            CheckBox name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            Log.i("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) myContext.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.interest_place_layout, null);

                holder = new ViewHolder();
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        PlaceInterest interest = (PlaceInterest) cb.getTag();
                        Toast.makeText(myContext,
                                "Clicked on Checkbox: " + cb.getText() +
                                        " is " + cb.isChecked(),
                                Toast.LENGTH_LONG).show();
                        interest.setSelected(cb.isChecked());

                        SharedPreferences pref = myContext.getSharedPreferences("MyPref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean(((PlaceInterest)cb.getTag()).getName(),cb.isChecked());
                        editor.commit();
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
        SharedPreferences pref = myContext.getSharedPreferences("MyPref", MODE_PRIVATE);
        ArrayList<PlaceInterest> interestList = new ArrayList<>();

        PlaceInterest pi;
        for(PlacesInterestEnum interest : PlacesInterestEnum.values()){
            pi = new PlaceInterest(interest.toString(), pref.getBoolean(interest.toString(),true));
            interestList.add(pi);
        }

        dataAdapter = new PlaceInterestAdapter(myContext,R.layout.interest_place_layout,interestList);
        lv.setAdapter(dataAdapter);
    }



    /*private void checkButtonClick() {


        Button myButton = (Button) findViewById(R.id.findSelected);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");

                ArrayList<PlaceInterest> countryList = dataAdapter.countryList;
                for(int i=0;i<countryList.size();i++){
                    PlaceInterest country = countryList.get(i);
                    if(country.isSelected()){
                        responseText.append("\n" + country.getName());
                    }
                }

                Toast.makeText(getApplicationContext(),
                        responseText, Toast.LENGTH_LONG).show();

            }
        });

    }*/

//}
}
