package com.android.udl.locationoffers.listeners;

/**
 * Created by ubuntu on 15/03/17.
 */

public class PlaceInterest {
    String name;
    boolean selected;

    public PlaceInterest(){

    }

    public PlaceInterest(String name, boolean selected){
        this.name = name;
        this.selected = selected;
    }

    public boolean isSelected(){
        return this.selected;
    }

    public void setSelected(boolean selected){
        this.selected = selected;
    }

    public String getName(){
        return this.name;
    }
}
