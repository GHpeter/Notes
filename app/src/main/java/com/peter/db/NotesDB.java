package com.peter.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 回忆 on 2015/2/6 0006.
 */
public class NotesDB extends SQLiteOpenHelper {
    private static String DBNAME = "notes";
    private static int version = 1;
    //表名
    public static final String TABLE_NAME_NOTES = "notes";
    public static final String TABLE_NAME_MEDIA = "media";
    //列名
    public static final String COLUMN_NAME_ID = "_id";
    public static final String COLUMN_NAME_NOTES_NAME = "name";
    public static final String COLUMN_NAME_NOTES_CONTENT = "content";
    public static final String COLUMN_NAME_NOTES_DATE = "date";

    public static final String COLUMN_NAME_MEDIA_PATH = "path";
    public static final String COLUMN_NAME_MEDIA_OWNER_NOTES_ID = "note_id";


    public NotesDB(Context context) {
        super(context, DBNAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String notes_sql = "CREATE TABLE " + TABLE_NAME_NOTES + "(" +
                COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME_NOTES_NAME + " TEXT NOT NULL DEFAULT \"\"," +
                COLUMN_NAME_NOTES_CONTENT + " TEXT NOT NULL DEFAULT \"\"," +
                COLUMN_NAME_NOTES_DATE + " TEXT NOT NULL DEFAULT \"\"" +
                ")";
        String media_sql = "CREATE TABLE " + TABLE_NAME_MEDIA + "(" +
                COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME_MEDIA_PATH + " TEXT NOT NULL DEFAULT \"\"," +
                COLUMN_NAME_MEDIA_OWNER_NOTES_ID + " INTEGER NOT NULL DEFAULT 0" +
                ")";
        db.execSQL(notes_sql);
        db.execSQL(media_sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
