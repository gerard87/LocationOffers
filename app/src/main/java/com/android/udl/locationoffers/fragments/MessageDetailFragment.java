package com.android.udl.locationoffers.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.udl.locationoffers.R;
import com.android.udl.locationoffers.domain.Message;

public class MessageDetailFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

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

        Bundle args = getArguments();

        final Message message = args.getParcelable("Message");
        imageView.setImageBitmap(message.getImage());
        textView_title.setText(message.getTitle());
        textView_description.setText(message.getDescription());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewMessageFormFragment fragment = NewMessageFormFragment.newInstance(message);
                startFragment(fragment);
                mListener.onEditMessageDetail(getString(R.string.edit_message));
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
        void onEditMessageDetail(String title);
    }
}
