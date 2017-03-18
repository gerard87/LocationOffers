package com.android.udl.locationoffers.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by ubuntu on 18/03/17.
 */

public class UserSQLiteManage {

    private Context context;
    UserSQLiteHelper usdbh;
    SQLiteDatabase db;

    public void UserSQLiteManage(Context context){
        this.context = context;
        initialization();
    }

    public void initialization(){
        usdbh = new UserSQLiteHelper(context, "DBUser", null, 1);
        db = usdbh.getWritableDatabase();
    }

    public boolean isDatabaseInitialized(){
        return db != null;
    }

    /*public boolean checkIfOfferExists(int id){
            Cursor c = db.rawQuery("SELECT _id FROM Offer WHERE _id = {}",id);
            //Log.i("BBDD: ","offer count: "+String.valueOf(c.getCount()));
        return c.getCount() >0;

    }*/
}
