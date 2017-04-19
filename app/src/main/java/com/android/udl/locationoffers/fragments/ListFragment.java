package com.android.udl.locationoffers.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.udl.locationoffers.R;
import com.android.udl.locationoffers.Utils.BitmapUtils;
import com.android.udl.locationoffers.adapters.MyAdapter;
import com.android.udl.locationoffers.domain.Message;
import com.android.udl.locationoffers.listeners.FloatingButtonScrollListener;
import com.android.udl.locationoffers.listeners.ItemClick;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    private static final String STATE_LIST = "Adapter data";

    private RecyclerView mRecyclerView;
    private FloatingActionMenu fab_menu;
    private FloatingActionButton fab_button;

    private OnFragmentInteractionListener mListener;

    private List<Message> messages;

    private MyAdapter adapter;

    private String db_mode;
    private String mode;

    public static ListFragment newInstance(String string) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString("db", string);
        fragment.setArguments(args);
        return fragment;
    }


    public ListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fab_menu = (FloatingActionMenu) getActivity().findViewById(R.id.fab_menu);

        mRecyclerView = (RecyclerView) getView().findViewById(R.id.rv);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(llm);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.PREFERENCES_NAME), Context.MODE_PRIVATE);
        mode = sharedPreferences.getString("mode", null);

        db_mode = getArguments().getString("db");

        if (savedInstanceState == null) {
            if (messages == null) messages = new ArrayList<>();
        } else {
            messages = savedInstanceState.getParcelableArrayList(STATE_LIST);
        }
        MyAdapter adapter = new MyAdapter(messages, new ItemClick(getActivity(), mRecyclerView));
        mRecyclerView.setAdapter(adapter);
        if (messages.size() == 0 || mListener.onReturnFromRemoved()) read();


        /* Show/hide floating button*/
        fab_menu.setClosedOnTouchOutside(true);
        mRecyclerView.addOnScrollListener(new FloatingButtonScrollListener(fab_menu));
        /* /Show/hide floating button*/

        fab_button = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab_menu.close(false);
                startFragment(new NewMessageFormFragment());
                mListener.onFABNewMessageCommerce(getString(R.string.new_message));
            }
        });

        if (mode.equals(getString(R.string.user))) fab_menu.setVisibility(View.INVISIBLE);

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

        String database = mode.equals(getString(R.string.commerce)) ?
                (db_mode.equals("messages") ? getString(R.string.messages) : "Removed") :
                (db_mode.equals("messages") ? "User messages" : "User removed");

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
             DatabaseReference ref =
                     db.getReference(database).child(user.getUid());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    adapter = (MyAdapter) mRecyclerView.getAdapter();
                    adapter.removeAll();

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        Log.d("COMMERCE","snapshot: " +
                                postSnapshot.getValue(Message.class).getTitle());
                        Message message = postSnapshot.getValue(Message.class);

                        downloadImage(message);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

    }

    private void downloadImage (final Message message) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference =
                storage.getReferenceFromUrl(getString(R.string.STORAGE_URL));
        StorageReference imageReference =
                storageReference.child(getString(R.string.STORAGE_PATH)+message.getCommerce_uid()+getString(R.string.STORAGE_FORMAT));
        imageReference.getBytes(1024*1024).addOnSuccessListener(
                new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        message.setImage(BitmapUtils.byteArrayToBitmap(bytes));
                        adapter.add(message);
                    }
                });
    }

    private void startFragment(Fragment fragment) {
        if (fragment != null){
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_main, fragment)
                    .addToBackStack(null)
                    .commit();
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


    public interface OnFragmentInteractionListener {
        void onFABNewMessageCommerce(String title);
        boolean onReturnFromRemoved();
    }

    public boolean isFabOpened() {
        return fab_menu != null && fab_menu.isOpened();
    }
    public void closeFab () {
        fab_menu.close(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_LIST, (ArrayList<Message>)messages);
    }



}
