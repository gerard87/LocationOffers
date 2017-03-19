package com.android.udl.locationoffers.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;

import com.android.udl.locationoffers.Utils.BitmapUtils;
import com.android.udl.locationoffers.domain.Message;
import com.android.udl.locationoffers.domain.UserMessage;

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

    public boolean checkIfMessageExistByID(int messageID){
        String[] args = new String[] { String.valueOf(messageID) };

        Cursor c = db.rawQuery("SELECT _id FROM UserMessages WHERE commerce_id = ?",args);
        return (c!=null && c.getCount()>0);
    }

    public void insertMessage(UserMessage message){
        ContentValues newRegister = new ContentValues();
        newRegister.put("title",message.getTitle());
        newRegister.put("description",message.getDescription());
        newRegister.put("image", BitmapUtils.bitmapToByteArray(message.getImage()));
        newRegister.put("commerce_id", message.getCommerce_id());
        newRegister.put("shown", 1);
        newRegister.put("used",1);
        newRegister.put("qrCode",BitmapUtils.bitmapToByteArray(message.getQrCode()));

        db.insert("UserMessages", null, newRegister);
    }


}
