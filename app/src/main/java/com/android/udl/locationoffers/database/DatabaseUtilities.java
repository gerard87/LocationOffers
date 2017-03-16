package com.android.udl.locationoffers.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.udl.locationoffers.Utils.BitmapUtils;
import com.android.udl.locationoffers.domain.Commerce;
import com.android.udl.locationoffers.domain.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gerard on 14/03/17.
 */

public class DatabaseUtilities {

    private String db_name;
    private MessagesSQLiteHelper msh;
    private CommerceSQLiteHelper csh;

    public DatabaseUtilities (String db_name, MessagesSQLiteHelper msh) {
        this.db_name = db_name;
        this.msh = msh;
    }

    public DatabaseUtilities (String db_name, CommerceSQLiteHelper csh) {
        this.db_name = db_name;
        this.csh = csh;
    }

    public List<Message> getMessageDataFromDB(){
        List<Message> messages = new ArrayList<>();

        SQLiteDatabase db = msh.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * from "+db_name, null);
        if (cursor.moveToFirst()) {
            do {
                Message message = new Message(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        BitmapUtils.byteArrayToBitmap(cursor.getBlob(3)),
                        cursor.getInt(4));
                messages.add(message);
            } while (cursor.moveToNext());
        }

        return messages;
    }

    public List<Commerce> getCommerceDataFromDB(){
        List<Commerce> commerces = new ArrayList<>();

        SQLiteDatabase db = csh.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * from "+db_name, null);
        if (cursor.moveToFirst()) {
            do {
                Commerce commerce = new Commerce(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        BitmapUtils.byteArrayToBitmap(cursor.getBlob(3)));
                commerces.add(commerce);
            } while (cursor.moveToNext());
        }

        return commerces;
    }

}
