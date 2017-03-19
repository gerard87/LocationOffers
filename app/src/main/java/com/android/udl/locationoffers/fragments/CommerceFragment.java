package com.android.udl.locationoffers.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.android.udl.locationoffers.database.CommercesSQLiteHelper;
import com.android.udl.locationoffers.database.DatabaseQueries;
import com.android.udl.locationoffers.database.MessagesSQLiteHelper;
import com.android.udl.locationoffers.database.RemovedCommerceSQLiteHelper;
import com.android.udl.locationoffers.domain.Commerce;
import com.android.udl.locationoffers.domain.Message;
import com.android.udl.locationoffers.listeners.FloatingButtonScrollListener;
import com.android.udl.locationoffers.listeners.ItemClick;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.Arrays;
import java.util.List;

public class CommerceFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private FloatingActionMenu fab_menu;
    private FloatingActionButton fab_button;

    private MessagesSQLiteHelper msh;
    private RemovedCommerceSQLiteHelper rcsh;
    private CommercesSQLiteHelper csh;

    private String db_mode;

    private OnFragmentInteractionListener mListener;

    private SharedPreferences sharedPreferences;

    private DatabaseQueries dq;
    private List<Message> messages;

    public static CommerceFragment newInstance(String string) {
        CommerceFragment fragment = new CommerceFragment();
        Bundle args = new Bundle();
        args.putString("db", string);
        fragment.setArguments(args);
        return fragment;
    }

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences("my_preferences", Context.MODE_PRIVATE);

        fab_menu = (FloatingActionMenu) getActivity().findViewById(R.id.fab_menu);

        mRecyclerView = (RecyclerView) getView().findViewById(R.id.rv);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(llm);

        db_mode = getArguments().getString("db");
        csh = new CommercesSQLiteHelper(getActivity(), "DBCommerces", null, 1);
        if (db_mode != null && db_mode.equals("messages")) {
            msh = new MessagesSQLiteHelper(getActivity(), "DBMessages", null, 1);
            dq = new DatabaseQueries("Messages", msh, csh);
        } else {
            rcsh = new RemovedCommerceSQLiteHelper(getActivity(), "DBRemovedMessagesCommerce", null, 1);
            dq = new DatabaseQueries("MessagesCommerceRemoved", rcsh, csh);
            fab_menu.hideMenu(true);
        }

        selectMode();

        MyAdapter adapter = new MyAdapter(messages, new ItemClick(getActivity(), mRecyclerView));
        mRecyclerView.setAdapter(adapter);

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
        selectMode();
        adapter.addAll(messages);
    }

    private void selectMode () {
        if (sharedPreferences.getString("mode", null).equals(getString(R.string.user))){
            messages = dq.getMessageDataFromDB();
        } else {
            List<String> fields = Arrays.asList("commerce_id");
            List<String> values = Arrays.asList(
                    Integer.toString(sharedPreferences.getInt("id", -1)));
            if (db_mode.equals("messages")) {
                messages = dq.getMessagesDataByFieldsFromDB(fields, values);
            } else {
                messages = dq.getCommerceRemovedMessagesDataByFieldsFromDB(fields, values);
            }
        }
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

    public boolean isFabOpened() {
        return fab_menu.isOpened();
    }
    public void closeFab () {
        fab_menu.close(true);
    }

}
