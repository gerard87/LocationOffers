package com.android.udl.locationoffers.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gerard on 16/03/17.
 */

public class CommerceSQLiteHelper extends SQLiteOpenHelper {

    String sqlCreate = "CREATE TABLE Commerces " +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT, " +
            "password TEXT, " +
            "image BLOB)";

    public CommerceSQLiteHelper (Context context, String name,
                                 SQLiteDatabase.CursorFactory factory,
                                 int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Commerces");
        db.execSQL(sqlCreate);
    }
}
