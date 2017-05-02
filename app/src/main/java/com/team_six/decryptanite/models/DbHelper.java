package com.team_six.decryptanite.models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.team_six.decryptanite.models.DbContract.*;

import java.sql.Timestamp;

public class DbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Decryptanite.db";

    private static final String SQL_CREATE_USERS =
            "CREATE TABLE " + Users.TABLE_NAME + " (" +
                    Users.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    Users.COLUMN_USER + " TEXT NOT NULL," +
                    Users.COLUMN_PASSWORD + " TEXT NOT NULL)";

    private static final String SQL_DELETE_USERS =
            "DROP TABLE IF EXISTS " + Users.TABLE_NAME;

    private static final String SQL_CREATE_MESSAGES =
            "CREATE TABLE " + Messages.TABLE_NAME + " (" +
                    Messages.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    Messages.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL," +
                    Messages.COLUMN_USER_ID + " INTEGER NOT NULL," +
                    Messages.COLUMN_ORIGINAL + " TEXT NOT NULL," +
                    Messages.COLUMN_DECRYPTED + " TEXT NOT NULL," +
                    "FOREIGN KEY (" + Messages.COLUMN_USER_ID + ") REFERENCES " + Users.TABLE_NAME +
                            " (" + Users.COLUMN_ID + "))";

    private static final String SQL_DELETE_MESSAGES =
            "DROP TABLE IF EXISTS " + Messages.TABLE_NAME;

    private static final String SQL_CREATE_WORKFLOWS =
            "CREATE TABLE " + Workflows.TABLE_NAME + " (" +
                    Workflows.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    Workflows.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL," +
                    Workflows.COLUMN_USER_ID + " INTEGER NOT NULL," +
                    Workflows.COLUMN_EVENT + " TEXT NOT NULL," +
                    Workflows.COLUMN_STATUS + " INTEGER NOT NULL," +
                    "FOREIGN KEY (" + Workflows.COLUMN_USER_ID + ") REFERENCES " + Users.TABLE_NAME +
                            " (" + Users.COLUMN_ID + "))";

    private static final String SQL_DELETE_WORKFLOWS =
            "DROP TABLE IF EXISTS " + Workflows.TABLE_NAME;

    private static final String SQL_CREATE_ADMIN =
            "INSERT INTO " + Users.TABLE_NAME + " (" +
                    Users.COLUMN_USER + "," + Users.COLUMN_PASSWORD +
                    ") VALUES ('admin', 'admin')";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USERS);
        db.execSQL(SQL_CREATE_MESSAGES);
        db.execSQL(SQL_CREATE_WORKFLOWS);
        db.execSQL(SQL_CREATE_ADMIN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_USERS);
        db.execSQL(SQL_DELETE_MESSAGES);
        db.execSQL(SQL_DELETE_WORKFLOWS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean verifyCredentials(String user, String pass)  {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + DbContract.Users.COLUMN_USER + " FROM " + DbContract.Users.TABLE_NAME +
                " WHERE " + DbContract.Users.COLUMN_USER + " = '" + user + "' AND " +
                DbContract.Users.COLUMN_PASSWORD + " = '" + pass + "'";
        Cursor cursor = db.rawQuery(query, null);

        boolean isAuthenticated = false;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            isAuthenticated = cursor.getString(0).equals(user);
        }
        cursor.close();

        return isAuthenticated;
    }

    public void logAccessEvent(String user, Status status)  {
        String query = "INSERT INTO " + Workflows.TABLE_NAME + " (" +
                Workflows.COLUMN_TIMESTAMP + ", " + Workflows.COLUMN_USER_ID + ", " +
                Workflows.COLUMN_EVENT + ", " + Workflows.COLUMN_STATUS +
                ") VALUES ('" + new Timestamp(System.currentTimeMillis()).toString() + "', '" +
                user + "', 'ACCESS', '" + status.toString() + "')";
        execSQL(query);
    }

    public void logLoadPicEvent(String user, Status status)  {
        String query = "INSERT INTO " + Workflows.TABLE_NAME + " (" +
                Workflows.COLUMN_TIMESTAMP + ", " + Workflows.COLUMN_USER_ID + ", " +
                Workflows.COLUMN_EVENT + ", " + Workflows.COLUMN_STATUS +
                ") VALUES ('" + new Timestamp(System.currentTimeMillis()).toString() + "', '" +
                user + "', 'LOAD PICTURE', '" + status.toString() + "')";
        execSQL(query);
    }

    public void logNewPicEvent(String user, Status status)  {
        String query = "INSERT INTO " + Workflows.TABLE_NAME + " (" +
                Workflows.COLUMN_TIMESTAMP + ", " + Workflows.COLUMN_USER_ID + ", " +
                Workflows.COLUMN_EVENT + ", " + Workflows.COLUMN_STATUS +
                ") VALUES ('" + new Timestamp(System.currentTimeMillis()).toString() + "', '" +
                user + "', 'NEW PICTURE', '" + status.toString() + "')";
        execSQL(query);
    }

    public void logOcrEvent(String user, Status status)  {
        String query = "INSERT INTO " + Workflows.TABLE_NAME + " (" +
                Workflows.COLUMN_TIMESTAMP + ", " + Workflows.COLUMN_USER_ID + ", " +
                Workflows.COLUMN_EVENT + ", " + Workflows.COLUMN_STATUS +
                ") VALUES ('" + new Timestamp(System.currentTimeMillis()).toString() + "', '" +
                user + "', 'OCR', '" + status.toString() + "')";
        execSQL(query);
    }

    public void logTranslationEvent(String user, Status status)  {
        String query = "INSERT INTO " + Workflows.TABLE_NAME + " (" +
                Workflows.COLUMN_TIMESTAMP + ", " + Workflows.COLUMN_USER_ID + ", " +
                Workflows.COLUMN_EVENT + ", " + Workflows.COLUMN_STATUS +
                ") VALUES ('" + new Timestamp(System.currentTimeMillis()).toString() + "', '" +
                user + "', 'TRANSLATION', '" + status.toString() + "')";
        execSQL(query);
    }

    public void storeTranslation(String user, String original, String translation)  {
        String query = "INSERT INTO " + Messages.TABLE_NAME + " (" +
                Messages.COLUMN_TIMESTAMP + ", " + Messages.COLUMN_USER_ID + ", " +
                Messages.COLUMN_ORIGINAL + ", " + Messages.COLUMN_DECRYPTED +
                ") VALUES ('" + new Timestamp(System.currentTimeMillis()).toString() + "', '" +
                user + "', '" + original + "', '" + translation + "')";
        execSQL(query);
    }

    private void execSQL(String query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
    }
}
