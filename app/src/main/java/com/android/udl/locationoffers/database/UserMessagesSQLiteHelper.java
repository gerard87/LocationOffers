package com.android.udl.locationoffers.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ubuntu on 14/03/17.
 */

public class UserMessagesSQLiteHelper extends SQLiteOpenHelper {

    StringBuilder sb = new StringBuilder("CREATE TABLE UserMessages")
            .append("(_id INTEGER PRIMARY KEY AUTOINCREMENT, ")
            .append("title TEXT, ")
            .append("description TEXT, ")
            .append("image BLOB, ")
            .append("commerce_id INTEGER, ")

            .append("shown INTEGER, ")
            .append("used INTEGER, ")
            .append("qrCode BLOB, ")
            .append("FOREIGN KEY (commerce_id) REFERENCES Commerces(_id) ")
            .append(");");


    public UserMessagesSQLiteHelper(Context context, String nombre,
                                    SQLiteDatabase.CursorFactory factory, int version){
        super(context, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sb.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
