package com.android.udl.locationoffers.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.udl.locationoffers.R;
import com.android.udl.locationoffers.adapters.MyAdapter;
import com.android.udl.locationoffers.database.DatabaseQueries;
import com.android.udl.locationoffers.database.MessagesSQLiteHelper;
import com.android.udl.locationoffers.database.UserSQLiteManage;
import com.android.udl.locationoffers.domain.Message;
import com.android.udl.locationoffers.listeners.ItemClick;
import com.android.udl.locationoffers.services.NotificationService;

import java.util.List;


public class UserFragment extends Fragment {

    private RecyclerView mRecyclerView;

    private static final int MENU_START_SERVICE = 10;
    private static final int MENU_STOP_SERVICE = 20;

    private MessagesSQLiteHelper msh;

    public UserFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

        //msh = new MessagesSQLiteHelper(getActivity(), "DBMessages", null, 1);
        //DatabaseQueries du = new DatabaseQueries("Messages", msh);
        //List<Message> messages = du.getMessageDataFromDB();
        UserSQLiteManage userManage = new UserSQLiteManage(getContext());
        List<Message> messages = userManage.getUserMessagesToShow();
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
        adapter.addAll(new UserSQLiteManage(getContext()).getUserMessagesToShow());
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here

        menu.add(Menu.NONE,MENU_START_SERVICE,Menu.NONE, "Start Message Detection");
        menu.add(Menu.NONE,MENU_STOP_SERVICE,Menu.NONE, "Stop Message Detection");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serviceIntent;

        switch (item.getItemId()) {
            case MENU_START_SERVICE:
                serviceIntent = new Intent(getActivity(), NotificationService.class);
                serviceIntent.addCategory(NotificationService.TAG);
                getActivity().startService(serviceIntent);
                break;

            case MENU_STOP_SERVICE:
                serviceIntent = new Intent(getActivity(), NotificationService.class);
                serviceIntent.addCategory(NotificationService.TAG);
                getActivity().stopService(serviceIntent);
                break;

        }
        return true;
    }
}
