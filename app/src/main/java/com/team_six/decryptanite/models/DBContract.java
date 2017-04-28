package com.team_six.decryptanite.models;

import android.provider.BaseColumns;

public final class DBContract {
    private DBContract() {}

    public static class Users implements BaseColumns {
        public static final String TABLE_NAME = "Users";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_USER = "user";
        public static final String COLUMN_PASSWORD = "password";
    }

    public static class Messages implements BaseColumns {
        public static final String TABLE_NAME = "Messages";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_ORIGINAL = "original";
        public static final String COLUMN_DECRYPTED = "decrypted";
    }

    public static class Workflows implements BaseColumns {
        public static final String TABLE_NAME = "Workflows";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_EVENT = "event";
        public static final String COLUMN_STATUS = "status";
    }
}
