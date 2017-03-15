package com.android.udl.locationoffers.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.udl.locationoffers.R;
import com.android.udl.locationoffers.adapters.MyAdapter;
import com.android.udl.locationoffers.database.DatabaseUtilities;
import com.android.udl.locationoffers.database.MessagesSQLiteHelper;
import com.android.udl.locationoffers.domain.Message;
import com.android.udl.locationoffers.listeners.ItemClick;

import java.util.List;


public class UserFragment extends Fragment {

    private RecyclerView mRecyclerView;

    private MessagesSQLiteHelper msh;

    public UserFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView = (RecyclerView) getView().findViewById(R.id.rv);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(llm);

        msh = new MessagesSQLiteHelper(getActivity(), "DBMessages", null, 1);
        DatabaseUtilities du = new DatabaseUtilities("Messages", msh);
        List<Message> messages = du.getDataFromDB();
        MyAdapter adapter = new MyAdapter(messages, new ItemClick(getActivity(), mRecyclerView));
        mRecyclerView.setAdapter(adapter);

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
        adapter.addAll(new DatabaseUtilities("Messages", msh).getDataFromDB());
    }

}
