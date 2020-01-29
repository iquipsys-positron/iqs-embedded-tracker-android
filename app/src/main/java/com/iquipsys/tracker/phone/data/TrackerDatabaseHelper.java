package com.iquipsys.tracker.phone.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.iquipsys.tracker.phone.data.DatabaseDescription.Organization;

public class TrackerDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "iqt_tracker.db";
    private static final int DATABASE_VERSION = 1;

    public TrackerDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_CONTACTS_TABLE =
                "CREATE TABLE " + Organization.TABLE_NAME + "("
                        + Organization._ID + " INTEGER PRIMARY KEY, "
                        + Organization.COLUMN_ORG_ID + " TEXT, "
                        + Organization.COLUMN_NAME + " TEXT, "
                        + Organization.COLUMN_DESCRIPTION + " TEXT, "
                        + Organization.COLUMN_VERSION + " INTEGER, "
                        + Organization.COLUMN_CENTER_LAT + " FLOAT, "
                        + Organization.COLUMN_CENTER_LNG + " FLOAT, "
                        + Organization.COLUMN_RADIUS + " FLOAT, "
                        + Organization.COLUMN_ACTIVE_INT + " INTEGER, "
                        + Organization.COLUMN_INACTIVE_INT + " INTEGER, "
                        + Organization.COLUMN_OFFSITE_INT + " INTEGER, "
                        + Organization.COLUMN_OFFLINE_TIMEOUT + " INTEGER);";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

}