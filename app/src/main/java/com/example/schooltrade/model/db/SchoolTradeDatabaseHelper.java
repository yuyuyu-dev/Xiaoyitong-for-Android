package com.example.schooltrade.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SchoolTradeDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "schooltrade.db";
    private static final int DB_VERSION = 1;

    // 用户表
    private static final String CREATE_USER_TABLE = 
        "CREATE TABLE IF NOT EXISTS UserInfo (" +
        "UserID INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "Account TEXT NOT NULL UNIQUE, " +
        "Password TEXT NOT NULL, " +
        "RealName TEXT, " +
        "Phone TEXT, " +
        "Dormitory TEXT, " +
        "IsRealAuth INTEGER DEFAULT 0, " +
        "status INTEGER DEFAULT 1" +
        ")";

    // 商品表
    private static final String CREATE_GOODS_TABLE = 
        "CREATE TABLE IF NOT EXISTS GoodsInfo (" +
        "GoodsID INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "UserID INTEGER NOT NULL, " +
        "Title TEXT NOT NULL, " +
        "Content TEXT, " +
        "Price REAL NOT NULL, " +
        "PublishType INTEGER DEFAULT 0, " +
        "WantGoods TEXT, " +
        "img_url TEXT, " +
        "status INTEGER DEFAULT 1, " +
        "create_time TEXT DEFAULT (datetime('now')), " +
        "update_time TEXT DEFAULT (datetime('now'))" +
        ")";

    // 管理员表
    private static final String CREATE_ADMIN_TABLE = 
        "CREATE TABLE IF NOT EXISTS AdminInfo (" +
        "admin_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "account TEXT NOT NULL UNIQUE, " +
        "password TEXT NOT NULL, " +
        "real_name TEXT" +
        ")";

    // 收藏表
    private static final String CREATE_COLLECT_TABLE = 
        "CREATE TABLE IF NOT EXISTS Collect (" +
        "collect_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "user_id INTEGER NOT NULL, " +
        "goods_id INTEGER NOT NULL, " +
        "create_time TEXT DEFAULT (datetime('now'))" +
        ")";

    public SchoolTradeDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_GOODS_TABLE);
        db.execSQL(CREATE_ADMIN_TABLE);
        db.execSQL(CREATE_COLLECT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 如果后续版本需要修改表结构，在这里处理
        db.execSQL("DROP TABLE IF EXISTS Collect");
        db.execSQL("DROP TABLE IF EXISTS AdminInfo");
        db.execSQL("DROP TABLE IF EXISTS GoodsInfo");
        db.execSQL("DROP TABLE IF EXISTS UserInfo");
        onCreate(db);
    }
}
