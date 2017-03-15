package com.android.udl.locationoffers.adapters;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.udl.locationoffers.R;
import com.android.udl.locationoffers.domain.Message;
import com.android.udl.locationoffers.listeners.OnItemClickListener;

import java.util.Iterator;
import java.util.List;

/**
 * Created by gerard on 07/03/17.
 */

public class MyAdapter  extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<Message> mDataset;
    private OnItemClickListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView title;
        TextView description;
        ImageView image;
        ViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.card_view);
            title = (TextView) itemView.findViewById(R.id.title_cv);
            description = (TextView) itemView.findViewById(R.id.description_cv);
            image = (ImageView) itemView.findViewById(R.id.image_cv);
        }
    }

    public MyAdapter(List<Message> myDataset, OnItemClickListener listener) {
        mDataset = myDataset;
        this.listener = listener;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_card_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.title.setText(mDataset.get(position).getTitle());
        holder.description.setText(mDataset.get(position).getDescription());
        holder.image.setImageBitmap(mDataset.get(position).getImage());

        ViewCompat.setTransitionName(holder.image, String.valueOf(position)+"_image");
        ViewCompat.setTransitionName(holder.title, String.valueOf(position)+"_title");
        ViewCompat.setTransitionName(holder.description, String.valueOf(position)+"_desc");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, position);
            }
        });



    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public Message getItem (int position) {
        return mDataset.get(position);
    }

    public void removeAll(){
        Iterator<Message> iter = mDataset.iterator();
        while(iter.hasNext()){
            Message message = iter.next();
            int position = mDataset.indexOf(message);
            iter.remove();
            notifyItemRemoved(position);
        }
    }

    public void addAll(List<Message> list){
        for (Message message: list){
            add(message);
        }
    }

    public void add(Message message){
        mDataset.add(message);
        notifyItemInserted(mDataset.size()-1);
    }
}


