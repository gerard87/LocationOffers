package com.android.udl.locationoffers.listeners;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.view.View;

import com.android.udl.locationoffers.fragments.MessageDetailFragment;
import com.android.udl.locationoffers.R;
import com.android.udl.locationoffers.adapters.MyAdapter;
import com.android.udl.locationoffers.transitions.DetailsTransition;

/**
 * Created by gerard on 15/03/17.
 */

public class ItemClick implements OnItemClickListener {

    private FragmentActivity activity;
    private RecyclerView recyclerView;

    public ItemClick (FragmentActivity activity, RecyclerView recyclerView) {
        this.activity = activity;
        this.recyclerView = recyclerView;
    }

    @Override
    public void onItemClick(View view, int position){
        //showToast("Clicked element: "+Integer.toString(position));

        View image_cv = view.findViewById(R.id.image_cv);
        View title_cv = view.findViewById(R.id.title_cv);
        View description_cv = view.findViewById(R.id.description_cv);
        View name_cv = view.findViewById(R.id.name_cv);


        MyAdapter adapter = (MyAdapter) recyclerView.getAdapter();

        MessageDetailFragment fragment =
                MessageDetailFragment.newInstance(adapter.getItem(position));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            fragment.setSharedElementEnterTransition(new DetailsTransition());
            fragment.setEnterTransition(new Fade());
            fragment.setSharedElementReturnTransition(new DetailsTransition());
        }
        startFragmentWithSharedElement(fragment,
                image_cv, activity.getString(R.string.TRANS_NAME_MESSAGEIMAGE),
                title_cv, activity.getString(R.string.TRANS_NAME_MESSAGETITLE),
                description_cv, activity.getString(R.string.TRANS_NAME_MESSAGEDESCRIPTION),
                name_cv, activity.getString(R.string.TRANS_NAME_MESSAGENAME));
    }

    private void startFragmentWithSharedElement(Fragment fragment,
                                                View sharedElement1, String transitionName1,
                                                View sharedElement2, String transitionName2,
                                                View sharedElement3, String transitionName3,
                                                View sharedElement4, String transitionName4) {
        if (fragment != null){
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .addSharedElement(sharedElement1, transitionName1)
                    .addSharedElement(sharedElement2, transitionName2)
                    .addSharedElement(sharedElement3, transitionName3)
                    .addSharedElement(sharedElement4, transitionName4)
                    .replace(R.id.content_main, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}