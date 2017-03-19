package com.android.udl.locationoffers.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.android.udl.locationoffers.Utils.BitmapUtils;
import com.android.udl.locationoffers.domain.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ubuntu on 18/03/17.
 */

public class MessageSQLiteManage {

    private Context context;
    MessagesSQLiteHelper usdbh;
    SQLiteDatabase db;

    public MessageSQLiteManage(Context context){
        this.context = context;
        initialization();
    }

    public void initialization(){
        usdbh = new MessagesSQLiteHelper(context, "DBMessages", null, 1);
        db = usdbh.getWritableDatabase();
    }

    public boolean isDatabaseInitialized(){
        return db != null;
    }

    public List<Message> getMessagesByPlacesID(String placesID){
        List<Message> messageList = new ArrayList<>();
        Message message;

        String[] args = new String[] { String.valueOf(placesID) };

        String query =  "SELECT m._id, m.title, m.description, m.image, m.commerce_id "+
                "FROM Messages m INNER JOIN Commerces c ON m.commerce_id = c._id "+
                "WHERE c.placesID = ?;";

        Cursor c = db.rawQuery(query,args);

        if(c.moveToFirst()){
            do{
                message = new Message(
                        c.getInt(0),
                        c.getString(1),
                        c.getString(2),
                        BitmapUtils.byteArrayToBitmap(c.getBlob(3)),
                        c.getInt(4),
                        false
                );

                messageList.add(message);
            }while(c.moveToNext());
        }
        return messageList;
    }


}
