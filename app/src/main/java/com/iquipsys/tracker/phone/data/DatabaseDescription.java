package com.iquipsys.tracker.phone.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseDescription {
    public static final String AUTHORITY = "com.iquipsys.tracker.phone.data";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class Organization implements BaseColumns {
        public static final String TABLE_NAME = "organizations";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static final String COLUMN_ORG_ID = "org_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_CENTER_LAT = "center_lat";
        public static final String COLUMN_CENTER_LNG = "center_lng";
        public static final String COLUMN_RADIUS = "radius";
        public static final String COLUMN_VERSION = "version";
        public static final String COLUMN_ACTIVE_INT = "active_int";
        public static final String COLUMN_INACTIVE_INT = "inactive_interval";
        public static final String COLUMN_OFFSITE_INT = "offsite_int";
        public static final String COLUMN_OFFLINE_TIMEOUT = "offline_timeout";

        public static Uri buildOrganizationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}

