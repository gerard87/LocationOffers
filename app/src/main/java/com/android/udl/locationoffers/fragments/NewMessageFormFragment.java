package com.android.udl.locationoffers.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.AsyncTaskCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.udl.locationoffers.R;
import com.android.udl.locationoffers.Utils.BitmapUtils;
import com.android.udl.locationoffers.database.CommerceSQLiteHelper;
import com.android.udl.locationoffers.database.DatabaseUtilities;
import com.android.udl.locationoffers.database.MessagesSQLiteHelper;
import com.android.udl.locationoffers.domain.Commerce;
import com.android.udl.locationoffers.domain.Message;


public class NewMessageFormFragment extends Fragment {

    private Button btn_ok;
    private EditText ed_title, ed_desc;
    private int id;

    private boolean update = false;

    private MessagesSQLiteHelper msh;
    private CommerceSQLiteHelper csh;

    private OnFragmentInteractionListener mListener;

    public NewMessageFormFragment() {
        // Required empty public constructor
    }

    public static NewMessageFormFragment newInstance(Message message) {
        NewMessageFormFragment fragment = new NewMessageFormFragment();
        Bundle args = new Bundle();
        args.putParcelable("message", message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_message_form, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        msh = new MessagesSQLiteHelper(view.getContext(), "DBMessages", null, 1);
        csh = new CommerceSQLiteHelper(view.getContext(), "DBCommerces", null, 1);

        btn_ok = (Button) view.findViewById(R.id.button_form_ok);
        ed_title = (EditText) view.findViewById(R.id.editText_form_title);
        ed_desc = (EditText) view.findViewById(R.id.editText_form_description);

        Bundle args = getArguments();
        if (args != null && args.containsKey("message")) {
            Message message = args.getParcelable("message");
            ed_title.setText(message.getTitle());
            ed_desc.setText(message.getDescription());
            id = message.getId();
            update = true;
        }




        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDatabase();
            }
        });
    }


    private void saveToDatabase () {

        if (ed_title != null && ed_desc != null
                && !ed_title.getText().toString().equals("")
                && !ed_desc.toString().equals("")) {
            ContentValues data = new ContentValues();
            data.put("title", ed_title.getText().toString());
            data.put("description", ed_desc.getText().toString());

            if (update) {
                update (data);
            } else {
                save(data);
            }


            Toast.makeText(getContext(), getString(R.string.message_db_ok), Toast.LENGTH_SHORT).show();


            startFragment(new CommerceFragment());
            mListener.onMessageAdded(getString(R.string.messages));

        } else {
            Toast.makeText(getContext(), getString(R.string.field_error), Toast.LENGTH_SHORT).show();
        }

    }

    private void startFragment(Fragment fragment) {
        //Toast.makeText(this,fragment.toString(),Toast.LENGTH_SHORT).show();
        if (fragment != null){
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_main, fragment)
                    .commit();
        }
    }

    private void save (final ContentValues data) {

        DatabaseUtilities du = new DatabaseUtilities("Commerces",csh);
        /* ARREGLAR */
        Commerce commerce = du.getCommerceDataFromDB().get(0);

        data.put("image", BitmapUtils.bitmapToByteArray(commerce.getImage()));
        data.put("commerce_id", commerce.getId());


        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                SQLiteDatabase db = msh.getWritableDatabase();
                db.insert("Messages", null, data);
                return null;
            }
        });

    }

    private void update (final ContentValues data) {

        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                SQLiteDatabase db = msh.getWritableDatabase();
                db.update("Messages", data, "_id = ?", new String[]{Integer.toString(id)});
                return null;
            }
        });

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
        void onMessageAdded(String title);
    }



}
