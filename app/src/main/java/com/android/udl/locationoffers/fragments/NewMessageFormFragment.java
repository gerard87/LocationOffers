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
import android.widget.ImageView;
import android.widget.Toast;

import com.android.udl.locationoffers.R;
import com.android.udl.locationoffers.database.MessagesSQLiteHelper;

import java.io.ByteArrayOutputStream;


public class NewMessageFormFragment extends Fragment {

    private static final int PICK_IMAGE = 1;
    private static final int PERMISSION_EXTERNAL_STORAGE = 1;

    private Button btn_img, btn_ok;
    private EditText ed_title, ed_desc;
    private ImageView imageView;
    private Bitmap bitmap;

    private MessagesSQLiteHelper msh;

    private OnFragmentInteractionListener mListener;

    public NewMessageFormFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static NewMessageFormFragment newInstance() {
        NewMessageFormFragment fragment = new NewMessageFormFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_message_form, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        msh = new MessagesSQLiteHelper(getContext(), "DBMessages", null, 1);

        btn_img = (Button) getView().findViewById(R.id.button_form_image);
        btn_ok = (Button) getView().findViewById(R.id.button_form_ok);
        ed_title = (EditText) getView().findViewById(R.id.editText_form_title);
        ed_desc = (EditText) getView().findViewById(R.id.editText_form_description);
        imageView = (ImageView) getView().findViewById(R.id.image_form);


        btn_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED) {
                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_EXTERNAL_STORAGE);
                } else {
                    Intent pickIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickIntent.setType("image/*");
                    startActivityForResult(pickIntent, PICK_IMAGE);
                }

            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDatabase();
            }
        });
    }

    private void saveToDatabase () {
        byte[] image = bitmapToByteArray(bitmap);
        if (ed_title != null && ed_desc != null && image != null
                && !ed_title.getText().toString().equals("")) {
            ContentValues data = new ContentValues();
            data.put("title", ed_title.getText().toString());
            data.put("description", ed_desc.getText().toString());
            data.put("image", image);

            save (data);

            Toast.makeText(getContext(), getString(R.string.message_db_ok), Toast.LENGTH_SHORT).show();

            startFragment(new ComerceFragment());
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

        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                SQLiteDatabase db = msh.getWritableDatabase();
                db.insert("Messages", null, data);
                return null;
            }
        });

    }

    private byte[] bitmapToByteArray (Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case PICK_IMAGE:
                if(resultCode == Activity.RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();


                    bitmap = BitmapFactory.decodeFile(filePath);

                    imageView.setImageBitmap(bitmap);

                }
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
        // TODO: Update argument type and name
        void onMessageAdded(String title);
    }



}
