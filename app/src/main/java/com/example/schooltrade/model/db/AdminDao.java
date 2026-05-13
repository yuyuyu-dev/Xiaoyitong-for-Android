package com.example.schooltrade.model.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.schooltrade.entity.Admin;

public class AdminDao {
    // 管理员登录
    public static Admin login(String account, String pwd) {
        SQLiteDatabase db = DBUtil.getReadableDatabase();
        String sql = "SELECT * FROM AdminInfo WHERE account=? AND password=?";
        Cursor cursor = db.rawQuery(sql, new String[]{account, pwd});
        
        try {
            if (cursor.moveToFirst()) {
                Admin admin = new Admin();
                admin.setAdminId(cursor.getInt(cursor.getColumnIndexOrThrow("admin_id")));
                admin.setAccount(cursor.getString(cursor.getColumnIndexOrThrow("account")));
                admin.setRealName(cursor.getString(cursor.getColumnIndexOrThrow("real_name")));
                return admin;
            }
        } finally {
            cursor.close();
        }
        return null;
    }
}