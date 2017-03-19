package com.android.udl.locationoffers.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.text.TextUtilsCompat;
import android.text.TextUtils;
import android.util.Log;

import com.android.udl.locationoffers.Utils.BitmapUtils;
import com.android.udl.locationoffers.domain.Commerce;
import com.android.udl.locationoffers.domain.Message;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gerard on 14/03/17.
 */

public class DatabaseQueries {

    private String db_name;
    private MessagesSQLiteHelper msh;
    private CommercesSQLiteHelper csh;
    private RemovedCommerceSQLiteHelper rcsh;

    public DatabaseQueries(String db_name, MessagesSQLiteHelper msh) {
        this.db_name = db_name;
        this.msh = msh;
    }

    public DatabaseQueries(String db_name, CommercesSQLiteHelper csh) {
        this.db_name = db_name;
        this.csh = csh;
    }

    public DatabaseQueries(String db_name, RemovedCommerceSQLiteHelper rcsh) {
        this.db_name = db_name;
        this.rcsh = rcsh;
    }

    public List<Message> getMessageDataFromDB(){
        List<Message> messages = new ArrayList<>();

        SQLiteDatabase db = msh.getReadableDatabase();

        String query = "SELECT * from "+db_name;

        Log.i("DATABASE: ", query);

        boolean removed = false;

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Message message = new Message(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        BitmapUtils.byteArrayToBitmap(cursor.getBlob(3)),
                        cursor.getInt(4),
                        removed);
                messages.add(message);
            } while (cursor.moveToNext());
        }

        return messages;
    }

    public List<Message> getMessagesDataByFieldsFromDB(List<String> fields, List<String> values) {

        SQLiteDatabase db = msh.getReadableDatabase();

        return getMessageDataByFieldsFromDB(fields, values, db, false);
    }

    public List<Message> getCommerceRemovedMessagesDataByFieldsFromDB(List<String> fields, List<String> values) {

        SQLiteDatabase db = rcsh.getReadableDatabase();

        return getMessageDataByFieldsFromDB(fields, values, db, true);
    }


    private List<Message> getMessageDataByFieldsFromDB(List<String> fields,
                                                       List<String> values,
                                                       SQLiteDatabase db,
                                                       boolean removed){
        List<Message> messages = new ArrayList<>();

        if (fields.size() != values.size()) return null;

        String query = "SELECT * from "+db_name+" WHERE ";
        for (int i=0; i<fields.size(); i++) {
            boolean number = false;
            if(TextUtils.isDigitsOnly(values.get(i))) {
                number = true;
            }

            query += fields.get(i) + " = ";
            if (!number) query += "'";
            query += values.get(i);
            if (!number) query += "'";
            if (i != fields.size()-1) query += " AND ";
        }

        Cursor cursor = db.rawQuery(query , null);
        if (cursor.moveToFirst()) {
            do {
                Message message = new Message(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        BitmapUtils.byteArrayToBitmap(cursor.getBlob(3)),
                        cursor.getInt(4),
                        removed);
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
                        cursor.getString(3),
                        BitmapUtils.byteArrayToBitmap(cursor.getBlob(4)));
                commerces.add(commerce);
            } while (cursor.moveToNext());
        }

        return commerces;
    }

    public List<Commerce> getCommerceDataByFieldsFromDB(List<String> fields, List<String> values){
        List<Commerce> commerces = new ArrayList<>();

        SQLiteDatabase db = csh.getReadableDatabase();

        if (fields.size() != values.size()) return null;

        String query = "SELECT * from "+db_name+" WHERE ";
        for (int i=0; i<fields.size(); i++) {
            boolean number = false;
            if(TextUtils.isDigitsOnly(values.get(i))) {
                number = true;
            }

            query += fields.get(i) + " = ";
            if (!number) query += "'";
            query += values.get(i);
            if (!number) query += "'";
            if (i != fields.size()-1) query += " AND ";
        }

        Log.i("DATABASE: ", query);

        Cursor cursor = db.rawQuery(query , null);
        if (cursor.moveToFirst()) {
            do {
                Commerce commerce = new Commerce(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        BitmapUtils.byteArrayToBitmap(cursor.getBlob(4)));
                commerces.add(commerce);
            } while (cursor.moveToNext());
        }

        return commerces;
    }

}
