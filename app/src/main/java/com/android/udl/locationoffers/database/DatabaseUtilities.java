package com.android.udl.locationoffers.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.udl.locationoffers.Utils.BitmapUtils;
import com.android.udl.locationoffers.domain.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gerard on 14/03/17.
 */

public class DatabaseUtilities {

    private String db_name;
    private MessagesSQLiteHelper msh;

    public DatabaseUtilities (String db_name, MessagesSQLiteHelper msh) {
        this.db_name = db_name;
        this.msh = msh;
    }

    public List<Message> getDataFromDB(){
        List<Message> messages = new ArrayList<>();

        SQLiteDatabase db = msh.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * from "+db_name, null);
        if (cursor.moveToFirst()) {
            do {
                messages.add(new Message(cursor.getString(1),
                        cursor.getString(2),
                        BitmapUtils.byteArrayToBitmap(cursor.getBlob(3))));
            } while (cursor.moveToNext());
        }

        return messages;
    }

}
