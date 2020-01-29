package com.iquipsys.tracker.phone.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;

import com.iquipsys.tracker.phone.data.DatabaseDescription.Organization;

public class OrganizationsContentProvider extends ContentProvider {
    public static final String MESSAGE_ORGANIZATIONS_RELOAD = "com.iquipsys.tracker.phone.service.action.ORGANIZATIONS_RELOAD";

    private TrackerDatabaseHelper dbHelper;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int ONE_ORGANIZATION = 1;
    private static final int ORGANIZATIONS = 2;

    static {
        uriMatcher.addURI(DatabaseDescription.AUTHORITY,Organization.TABLE_NAME + "/#", ONE_ORGANIZATION);
        uriMatcher.addURI(DatabaseDescription.AUTHORITY, Organization.TABLE_NAME, ORGANIZATIONS);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new TrackerDatabaseHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(Organization.TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case ONE_ORGANIZATION:
                queryBuilder.appendWhere(
                        Organization._ID + "=" + uri.getLastPathSegment()
                );
                break;
            case ORGANIZATIONS:
                break;
            default:
                throw new UnsupportedOperationException(
                        "Invalid query uri: " /*getContext().getString(R.string.invalid_query_uri)*/ + uri
                );
        }

        Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private void broadcastReload() {
        Intent intent = new Intent(MESSAGE_ORGANIZATIONS_RELOAD);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri newContactUri = null;

        switch (uriMatcher.match(uri)) {
            case ORGANIZATIONS:
                long rowId = dbHelper.getWritableDatabase().insert(
                        Organization.TABLE_NAME, null, values
                );

                if (rowId > 0) {
                    newContactUri = Organization.buildOrganizationUri(rowId);

                    getContext().getContentResolver().notifyChange(uri, null);
                } else {
                    throw new SQLException(
                            "Insert failed: " /*getContext().getString(R.string.insert_failed)*/ + uri
                    );
                }
                break;
            default:
                throw new UnsupportedOperationException(
                        "Invalid insert uri: " /*getContext().getString(R.string.invalid_insert_uri)*/ + uri
                );
        }

        broadcastReload();

        return newContactUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int numberOfRowsUpdated;

        switch (uriMatcher.match(uri)) {
            case ONE_ORGANIZATION:
                String id = uri.getLastPathSegment();

                numberOfRowsUpdated = dbHelper.getWritableDatabase().update(
                        Organization.TABLE_NAME, values, Organization._ID + "=" + id,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException(
                        "Invalid update uri: " /*getContext().getString(R.string.invalid_update_uri)*/ + uri
                );
        }

        if (numberOfRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        broadcastReload();

        return numberOfRowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int numberOfRowsDeleted;

        switch (uriMatcher.match(uri)) {
            case ONE_ORGANIZATION:
                String id = uri.getLastPathSegment();

                numberOfRowsDeleted = dbHelper.getWritableDatabase().delete(
                        Organization.TABLE_NAME, Organization._ID + "=" + id, selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException(
                        "Invalid delete uri: " /*getContext().getString(R.string.invalid_delete_uri)*/ + uri
                );
        }

        if (numberOfRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        broadcastReload();

        return numberOfRowsDeleted;
    }
}
