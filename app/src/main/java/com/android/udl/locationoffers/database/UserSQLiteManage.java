package com.android.udl.locationoffers.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;

import com.android.udl.locationoffers.Utils.BitmapUtils;
import com.android.udl.locationoffers.domain.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ubuntu on 18/03/17.
 */

public class UserSQLiteManage {

    private Context context;
    UserMessagesSQLiteHelper usdbh;
    SQLiteDatabase db;

    public UserSQLiteManage(Context context){
        this.context = context;
        initialization();
    }

    public void initialization(){
        usdbh = new UserMessagesSQLiteHelper(context, "DBUser", null, 1);
        db = usdbh.getWritableDatabase();
    }

    public boolean isDatabaseInitialized(){
        return db != null;
    }

    public boolean checkIfOfferExists(int id){
        String[] args = new String[] { String.valueOf(id) };

        Cursor c = db.rawQuery("SELECT _id FROM UserMessages WHERE _id = ?",args);
        return c.getCount() >0;
    }

    public List<Message> getUserMessagesToShow(){
        List<Message> messageList = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT * FROM UserMessages;",null);
        if(c.moveToFirst()){
            do{
                Message m = new Message(
                        c.getInt(0),
                        c.getString(1),
                        c.getString(2),
                        BitmapUtils.byteArrayToBitmap(c.getBlob(3)),
                        c.getInt(4),
                        false);
                messageList.add(m);

            }while(c.moveToNext());
        }
        return messageList;
    }


}
