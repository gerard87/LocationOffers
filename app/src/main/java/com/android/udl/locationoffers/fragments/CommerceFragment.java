package com.android.udl.locationoffers.fragments;

import android.content.Context;
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
import com.android.udl.locationoffers.listeners.FloatingButtonScrollListener;
import com.android.udl.locationoffers.listeners.ItemClick;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.List;

public class CommerceFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private FloatingActionMenu fab_menu;
    private FloatingActionButton fab_button;

    private MessagesSQLiteHelper msh;

    private OnFragmentInteractionListener mListener;

    public CommerceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_commerce, container, false);
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
        List<Message> messages = du.getMessageDataFromDB();
        MyAdapter adapter = new MyAdapter(messages, new ItemClick(getActivity(), mRecyclerView));
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
                mListener.onFABNewMessageCommerce(getString(R.string.new_message));
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
        adapter.addAll(new DatabaseUtilities("Messages", msh).getMessageDataFromDB());
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
    }

}
