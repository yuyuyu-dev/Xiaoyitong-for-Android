package com.example.schooltrade.model.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.schooltrade.entity.Goods;

import java.util.ArrayList;
import java.util.List;

public class CollectDao {

    // 添加收藏
    public static boolean addCollect(int userId, int goodsId) {
        SQLiteDatabase db = DBUtil.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("goods_id", goodsId);
        
        long result = db.insert("Collect", null, values);
        return result > 0;
    }

    // 取消收藏
    public static boolean cancelCollect(int userId, int goodsId) {
        SQLiteDatabase db = DBUtil.getWritableDatabase();
        int result = db.delete("Collect", "user_id=? AND goods_id=?", 
                new String[]{String.valueOf(userId), String.valueOf(goodsId)});
        return result > 0;
    }

    // 查询我的收藏
    public static List<Goods> getMyCollect(int userId) {
        List<Goods> list = new ArrayList<>();
        SQLiteDatabase db = DBUtil.getReadableDatabase();
        String sql = "SELECT g.* FROM GoodsInfo g INNER JOIN Collect c ON g.GoodsID=c.goods_id WHERE c.user_id=?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(userId)});
        
        try {
            while (cursor.moveToNext()) {
                list.add(GoodsDao.mapToGoods(cursor));
            }
        } finally {
            cursor.close();
        }
        return list;
    }
    
    // 检查是否已收藏
    public static boolean isCollect(int userId, int goodsId) {
        SQLiteDatabase db = DBUtil.getReadableDatabase();
        String sql = "SELECT * FROM Collect WHERE user_id=? AND goods_id=?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(userId), String.valueOf(goodsId)});
        
        try {
            return cursor.moveToFirst();
        } finally {
            cursor.close();
        }
    }
}