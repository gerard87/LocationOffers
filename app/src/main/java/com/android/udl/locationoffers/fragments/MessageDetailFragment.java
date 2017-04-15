package com.android.udl.locationoffers.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.udl.locationoffers.R;
import com.android.udl.locationoffers.domain.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MessageDetailFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Message message;
    private boolean removed;
    private String mode;

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


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        mode = sharedPreferences.getString("mode", null);


        removed = message.isRemoved();

        if (mode.equals(getString(R.string.user)) && !removed) {
            fab.setVisibility(View.INVISIBLE);
        }

        if (removed) {
            fab.setImageResource(R.drawable.ic_restore_white_24dp);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onRemovedMessage(true);
                    moveFromXToY(false);
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
        void onRemovedMessage(boolean removed);
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
                mListener.onRemovedMessage(true);
                moveFromXToY(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void moveFromXToY (boolean messagesToRemoved) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        removeFirebaseItem(db, user, getMode(messagesToRemoved));
        addToY(db, user, getMode(!messagesToRemoved));

        getActivity().getSupportFragmentManager().popBackStack();
    }

    private String getMode (boolean messagesToRemoved) {
        return mode.equals(getString(R.string.commerce)) ?
                (messagesToRemoved ? getString(R.string.messages) : "Removed") :
                (messagesToRemoved ? "User messages" : "User removed");
    }

    private void removeFirebaseItem (FirebaseDatabase db, FirebaseUser user, String mode) {
        if (user != null) {
            DatabaseReference ref = db.getReference(mode)
                    .child(user.getUid())
                    .child(message.getMessage_uid());
            ref.removeValue();

        }
    }

    private void addToY (FirebaseDatabase db, FirebaseUser user, String mode) {
        if (user != null) {
            DatabaseReference ref = db.getReference(mode)
                    .child(user.getUid())
                    .child(message.getMessage_uid());

            message.setRemoved(
                    !mode.equals(this.mode.equals(getString(R.string.commerce)) ?
                            getString(R.string.messages) : "User messages")
            );


            message.setImage(null);
            ref.setValue(message);
        }
    }

}
