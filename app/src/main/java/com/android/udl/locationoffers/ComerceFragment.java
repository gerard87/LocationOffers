package com.android.udl.locationoffers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.udl.locationoffers.adapters.MyAdapter;
import com.android.udl.locationoffers.database.MessagesSQLiteHelper;
import com.android.udl.locationoffers.domain.Message;
import com.android.udl.locationoffers.listeners.FloatingButtonScrollListener;
import com.android.udl.locationoffers.listeners.OnItemClickListener;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;

public class ComerceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private List<Message> messages;
    private RecyclerView mRecyclerView;
    private FloatingActionMenu fab_menu;
    private FloatingActionButton fab_button;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ComerceFragment() {
    }


    public static ComerceFragment newInstance(String param1, String param2) {
        ComerceFragment fragment = new ComerceFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comerce, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView = (RecyclerView) getView().findViewById(R.id.rv);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(llm);
        initalizeData();
        MyAdapter adapter = new MyAdapter(messages, new ItemClick());
        mRecyclerView.setAdapter(adapter);

        /* Show/hide floating button*/
        fab_menu = (FloatingActionMenu) getActivity().findViewById(R.id.fab_menu);
        fab_menu.setClosedOnTouchOutside(true);
        mRecyclerView.addOnScrollListener(new FloatingButtonScrollListener(fab_menu));
        /* /Show/hide floating button*/

        fab_button = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new NewMessageFormFragment());
                mListener.onFragmentInteraction(getString(R.string.new_message));
            }
        });

        /* Swipe down to refresh */
        final SwipeRefreshLayout sr = (SwipeRefreshLayout) getView().findViewById(R.id.swiperefresh);
        sr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                read();
                sr.setRefreshing(false);
            }
        });
        sr.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_red_dark);
        /* /Swipe down to refresh */


    }

    private void read () {
        MyAdapter adapter = (MyAdapter) mRecyclerView.getAdapter();
        adapter.removeAll();
        initalizeData();
        adapter.addAll(messages);
    }

    private void startFragment(Fragment fragment) {
        if (fragment != null){
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_main, fragment)
                    .commit();
        }
    }

    private void showToast (String text){
        Toast.makeText(getContext(), text,Toast.LENGTH_SHORT).show();
    }

    private class ItemClick implements OnItemClickListener {
        @Override
        public void onItemClick(View view, int position){
                showToast("Clicked element: "+Integer.toString(position));
        }
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String title) {
        if (mListener != null) {
            mListener.onFragmentInteraction(title);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        void onFragmentInteraction(String title);
    }

    private void initalizeData(){
        messages = new ArrayList<>();
        Bitmap image = BitmapFactory
                .decodeResource(getContext().getResources(), R.drawable.ic_person_black_24dp);
        messages.add(new Message("Title", "Description", image));

        MessagesSQLiteHelper msh = new MessagesSQLiteHelper(getActivity(), "DBMessages", null, 1);
        SQLiteDatabase db = msh.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * from Messages", null);
        if (cursor.moveToFirst()) {
            do {
                messages.add(new Message(cursor.getString(1),
                        cursor.getString(2),
                        byteArrayToBitmap(cursor.getBlob(3))));
            } while (cursor.moveToNext());
        }

    }

    private Bitmap byteArrayToBitmap (byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
