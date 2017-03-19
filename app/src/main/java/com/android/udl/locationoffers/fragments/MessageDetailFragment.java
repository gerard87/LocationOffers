package com.android.udl.locationoffers.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.udl.locationoffers.R;
import com.android.udl.locationoffers.Utils.BitmapUtils;
import com.android.udl.locationoffers.database.MessagesSQLiteHelper;
import com.android.udl.locationoffers.database.RemovedCommerceSQLiteHelper;
import com.android.udl.locationoffers.domain.Message;

public class MessageDetailFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Message message;
    private MessagesSQLiteHelper msh;
    private RemovedCommerceSQLiteHelper rcsh;
    private boolean removed;

    public MessageDetailFragment() {
        // Required empty public constructor
    }

    public static MessageDetailFragment newInstance(Message message) {
        MessageDetailFragment fragment = new MessageDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("Message", message);
        fragment.setArguments(args);
        return fragment;
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
        return inflater.inflate(R.layout.fragment_message_detail, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        msh = new MessagesSQLiteHelper(view.getContext(), "DBMessages", null, 1);
        rcsh = new RemovedCommerceSQLiteHelper(view.getContext(), "DBRemovedMessagesCommerce", null, 1);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.edit_fab);
        ImageView imageView = (ImageView) view.findViewById(R.id.image_cv);
        TextView textView_title = (TextView) view.findViewById(R.id.title_detail);
        TextView textView_description = (TextView) view.findViewById(R.id.description_detail);
        TextView textView_name = (TextView) view.findViewById(R.id.name_detail);

        Bundle args = getArguments();

        message = args.getParcelable("Message");
        imageView.setImageBitmap(message.getImage());
        textView_title.setText(message.getTitle());
        textView_description.setText(message.getDescription());
        textView_name.setText(message.getCommerce_name());

        removed = message.isRemoved();
        if (removed) {
            fab.setImageResource(R.drawable.ic_restore_white_24dp);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeItem("MessagesCommerceRemoved");
                    addToDB("Messages");
                }
            });
        } else {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NewMessageFormFragment fragment = NewMessageFormFragment.newInstance(message);
                    startFragment(fragment);
                    mListener.onEditMessageDetail(getString(R.string.edit_message));
                }
            });
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
        void onEditMessageDetail(String title);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        if (!removed) inflater.inflate(R.menu.message_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_detail:
                removeItem("Messages");
                addToDB("MessagesCommerceRemoved");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void removeItem(final String dbName) {

        final SQLiteDatabase db;
        String msg = "";
        if (dbName.equals("MessagesCommerceRemoved")) {
            db = rcsh.getWritableDatabase();
        } else if (dbName.equals("Messages")){
            msg = "Message id: " + Integer.toString(message.getId()) + " deleted!";
            db = msh.getWritableDatabase();
        } else {
            db = null;
        }
        if (db != null) {
            AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    db.delete(dbName, "_id=?", new String[]{String.valueOf(message.getId())});
                    return null;
                }
            });
            if (!msg.equals(""))Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
        getActivity().getSupportFragmentManager().popBackStack();
    }

    private void addToDB (final String dbName) {
        final ContentValues data = new ContentValues();
        data.put("title", message.getTitle());
        data.put("description", message.getDescription());
        data.put("image", BitmapUtils.bitmapToByteArray(message.getImage()));
        data.put("commerce_id", message.getCommerce_id());

        final SQLiteDatabase db;
        String msg = "";
        if (dbName.equals("MessagesCommerceRemoved")) {
            db = rcsh.getWritableDatabase();
        } else if (dbName.equals("Messages")){
            msg = "Message id: " + Integer.toString(message.getId()) + " restored!";
            db = msh.getWritableDatabase();
        } else {
            db = null;
        }
        if (db != null) {
            AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    db.insert(dbName, null, data);
                    return null;
                }
            });
            if (!msg.equals(""))Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }
}
