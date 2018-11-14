package com.ajimatech.android.ezpz;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContactDBHelper extends SQLiteOpenHelper {
    /**
     * Defines the database version. This variable must be incremented in order for onUpdate to
     * be called when necessary.
     */
    private static final int DATABASE_VERSION = 1;
    /**
     * The name of the database on the device.
     */
    private static final String DATABASE_NAME = "ezContactsList.db";

    /**
     * Default constructor.
     *
     * @param context The application context using this database.
     */
    public ContactDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is first created.
     *
     * @param sqLiteDatabase The database being created, which all SQL statements will be executed on.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE " + EzContactContract.ContactEntry.TABLE_NAME + " (" +
                EzContactContract.ContactEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EzContactContract.ContactEntry.COLUMN_FULLNAME + " TEXT NOT NULL, " +
                EzContactContract.ContactEntry.COLUMN_PHOTOURI + " TEXT, " +
                EzContactContract.ContactEntry.COLUMN_NUMBERMOBILE + " TEXT, " +
                EzContactContract.ContactEntry.COLUMN_FACEBOOKID + " TEXT, " +
                EzContactContract.ContactEntry.COLUMN_NUMBERGVOICE + " TEXT, " +
                EzContactContract.ContactEntry.COLUMN_NUMBERVIBER + " TEXT);";
        System.out.println("###sql " + sql);
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
