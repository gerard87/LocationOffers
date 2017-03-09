package com.android.udl.locationoffers.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.udl.locationoffers.R;
import com.android.udl.locationoffers.domain.Message;

import java.util.Iterator;
import java.util.List;

/**
 * Created by gerard on 07/03/17.
 */

public class MyAdapter  extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<Message> mDataset;

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

    public MyAdapter(List<Message> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_card_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(mDataset.get(position).getTitle());
        holder.description.setText(mDataset.get(position).getDescription());
        holder.image.setImageResource(mDataset.get(position).getImageId());

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
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


