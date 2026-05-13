package com.example.schooltrade.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DBUtil {
    private static SchoolTradeDatabaseHelper dbHelper;

    public static void init(Context context) {
        if (dbHelper == null) {
            dbHelper = new SchoolTradeDatabaseHelper(context.getApplicationContext());
        }
    }

    public static SQLiteDatabase getWritableDatabase() {
        if (dbHelper == null) {
            throw new IllegalStateException("DBUtil not initialized. Call init() first.");
        }
        return dbHelper.getWritableDatabase();
    }

    public static SQLiteDatabase getReadableDatabase() {
        if (dbHelper == null) {
            throw new IllegalStateException("DBUtil not initialized. Call init() first.");
        }
        return dbHelper.getReadableDatabase();
    }

    public static void closeDatabase() {
        if (dbHelper != null) {
            dbHelper.close();
            dbHelper = null;
        }
    }
}