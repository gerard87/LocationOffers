package com.android.udl.locationoffers.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ubuntu on 14/03/17.
 */

public class UserSQLiteHelper extends SQLiteOpenHelper {

    StringBuilder sb = new StringBuilder("CREATE TABLE UserMessages")
            .append("(_id INTEGER PRIMARY KEY, ")
            .append("shown INTEGER, ")
            .append("used INTEGER, ")
            .append("placeName TEXT, ")
            .append("offerName TEXT, ")
            .append("validUntil DATETIME, ")
            .append("qrCode BLOB ")
            .append(");");

    public UserSQLiteHelper(Context context, String nombre,
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
