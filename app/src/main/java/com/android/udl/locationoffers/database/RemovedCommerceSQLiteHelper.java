package com.android.udl.locationoffers.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gerard on 18/03/17.
 */

public class RemovedCommerceSQLiteHelper extends SQLiteOpenHelper {

    String sqlCreate = "CREATE TABLE MessagesCommerceRemoved " +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "title TEXT, " +
            "description TEXT, " +
            "image BLOB, " +
            "commerce_id INTEGER, " +
            "FOREIGN KEY (commerce_id) REFERENCES Commerces(_id));";

    public RemovedCommerceSQLiteHelper (Context context, String name,
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
        db.execSQL("DROP TABLE IF EXISTS MessagesCommerceRemoved");
        db.execSQL(sqlCreate);
    }
}
