package com.android.udl.locationoffers.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.udl.locationoffers.MainActivity;
import com.android.udl.locationoffers.R;
import com.android.udl.locationoffers.domain.Message;
import com.android.udl.locationoffers.uploadToAPI.ApiUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class NewMessageFormFragment extends Fragment {

    private EditText ed_title, ed_desc;

    private boolean update = false;

    private Message message;


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

        Button btn_ok = (Button) view.findViewById(R.id.button_form_ok);
        ed_title = (EditText) view.findViewById(R.id.editText_form_title);
        ed_desc = (EditText) view.findViewById(R.id.editText_form_description);

        Bundle args = getArguments();
        if (args != null && args.containsKey("message")) {
            message = args.getParcelable("message");
            if (message != null) {
                ed_title.setText(message.getTitle());
                ed_desc.setText(message.getDescription());
            }
            update = true;
        }

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.hasConnection) {
                    saveOrUpdateFirebase();
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setMessage(getString(R.string.network_error))
                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .create().show();
                }
            }
        });
    }

    private void saveOrUpdateFirebase () {
        if (messageOk()) {

            FirebaseDatabase db = FirebaseDatabase.getInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                DatabaseReference ref;
                if (update) {
                    ref = db.getReference(getString(R.string.messages))
                            .child(user.getUid()).child(message.getMessage_uid());
                    updateFirebase(ref);
                } else {
                    ref = db.getReference(getString(R.string.messages))
                            .child(user.getUid());
                    saveToFirebase(ref, user);
                }

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        hideKeyboard();

                        Toast.makeText(getContext(), getString(R.string.message_db_ok),
                                Toast.LENGTH_SHORT).show();

                        getFragmentManager().popBackStack(
                                null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                        ListFragment listFragment =
                                ListFragment.newInstance("messages", message);
                        startFragment(listFragment);
                        mListener.onMessageAdded(getString(R.string.messages));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

        } else {
            Toast.makeText(getContext(), getString(R.string.field_error),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToFirebase (DatabaseReference ref, FirebaseUser user) {
        DatabaseReference msgref = ref.push();
        Message message = new Message(ed_title.getText().toString(),
                ed_desc.getText().toString(), user.getUid(),
                user.getDisplayName(), msgref.getKey());
        msgref.setValue(message);

        ApiUtils.saveMessage(message);
    }

    private void updateFirebase (DatabaseReference ref) {
        ref.child("title").setValue(ed_title.getText().toString());
        ref.child("description").setValue(ed_desc.getText().toString());
        message.setTitle(ed_title.getText().toString());
        message.setDescription(ed_desc.getText().toString());
    }

    private void hideKeyboard () {
        Activity activity = getActivity();
        if (activity != null) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            View view = getView();
            if (view != null){
                IBinder token = getView().getWindowToken();
                if (token != null) imm.hideSoftInputFromWindow(token, 0);
            }
        }
    }


    private boolean messageOk () {
        return ed_title != null && ed_desc != null
                && !ed_title.getText().toString().equals("")
                && !ed_desc.getText().toString().equals("");
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
