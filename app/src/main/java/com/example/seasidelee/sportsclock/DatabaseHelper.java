package com.example.seasidelee.sportsclock;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Seaside Lee on 2015/12/21.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    // 数据库名
    private static final String DATABASE_NAME = "ClockManager";
    //Contact表名
    private static final String TABLE_CONTACTS = "Clock";
    //Contact表的列名
    private static final String ID = "id";
    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";
    private static final String ZHOUYI = "yi";
    private static final String ZHOUER = "er";
    private static final String ZHOUSAN = "san";
    private static final String ZHOUSI = "si";
    private static final String ZHOUWU = "wu";
    private static final String ZHOULIU = "liu";
    private static final String ZHOURI = "ri";
    private static final String LABLE = "lable";
    private static final String SONG_TITLE = "songtitle";
    private static final String SONG_URI = "songuri";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + HOUR + " INTEGER," + MINUTE + " INTEGER," + ZHOUYI + " BOOLEAN,"
                + ZHOUER + " BOOLEAN," + ZHOUSAN + " BOOLEAN," + ZHOUSI + " BOOLEAN,"
            + ZHOUWU + " BOOLEAN," + ZHOULIU + " BOOLEAN," + ZHOURI + " BOOLEAN,"
            + LABLE + " TEXT," + SONG_TITLE + " TEXT," + SONG_URI + " INTEGER,"+ ID + " INTEGER" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);*/
    }

    public void addContact(Clock contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(HOUR, contact.hour);
        values.put(MINUTE, contact.minute);
        values.put(ZHOUYI, contact.day[0]);
        values.put(ZHOUER, contact.day[1]);
        values.put(ZHOUSAN, contact.day[2]);
        values.put(ZHOUSI, contact.day[3]);
        values.put(ZHOUWU, contact.day[4]);
        values.put(ZHOULIU, contact.day[5]);
        values.put(ZHOURI, contact.day[6]);
        values.put(LABLE, contact.lable);
        values.put(SONG_TITLE, contact.songtitle);
        values.put(SONG_URI, contact.Uri);
        values.put(ID, contact.id);
        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }

    public ArrayList<Clock> getAllClocks() {
        ArrayList<Clock> ClockList = new ArrayList<Clock>();
        String selectQuery = "SELECT * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Clock contact = new Clock();
                contact.hour = (Integer.parseInt(cursor.getString(0)));
                contact.minute = (Integer.parseInt(cursor.getString(1)));
                for (int i = 0; i < 7; i++) {
                    if (cursor.getInt(i + 2) == 0) contact.day[i] = false;
                    else contact.day[i] = true;
                }
                contact.lable = cursor.getString(9);
                contact.songtitle = cursor.getString(10);
                contact.Uri = cursor.getInt(11);
                contact.id = cursor.getInt(12);
                ClockList.add(contact);
            } while (cursor.moveToNext());
        }
        return ClockList;
    }

    public int updateContact(Clock contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(HOUR, contact.hour);
        values.put(MINUTE, contact.minute);
        values.put(ZHOUYI, contact.day[0]);
        values.put(ZHOUER, contact.day[1]);
        values.put(ZHOUSAN, contact.day[2]);
        values.put(ZHOUSI, contact.day[3]);
        values.put(ZHOUWU, contact.day[4]);
        values.put(ZHOULIU, contact.day[5]);
        values.put(ZHOURI, contact.day[6]);
        values.put(LABLE, contact.lable);
        values.put(SONG_TITLE, contact.songtitle);
        values.put(SONG_URI, contact.Uri);
        values.put(ID, contact.id);
        return db.update(TABLE_CONTACTS, values, ID + " = ?", new String[] { String.valueOf(contact.id) });
    }

    public int getnum(){
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        //cursor.close();
        return cursor.getCount();
    }

    public int nextID(){
        int nextid = 0;
        ArrayList<Clock> ClockList = new ArrayList<Clock>();
        String selectQuery = "SELECT * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                if(nextid < cursor.getInt(12))
                    nextid = cursor.getInt(12);
            } while (cursor.moveToNext());
        }
        return nextid + 1;
    }

    public void deleteclock(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, ID + " = ?", new String[] { String.valueOf(id) });
        db.close();
    }
}
