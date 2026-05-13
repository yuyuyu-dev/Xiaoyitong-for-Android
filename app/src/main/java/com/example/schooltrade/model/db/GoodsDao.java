package com.example.schooltrade.model.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.schooltrade.entity.Goods;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品DAO - SQLite版本
 * 支持：发布、查询、我的发布、编辑、删除、搜索、状态管理
 */
public class GoodsDao {

    /**
     * 发布商品（绑定发布者UserID）
     */
    public static boolean publishGoods(Goods goods) {
        SQLiteDatabase db = DBUtil.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("UserID", goods.getUserId());
        values.put("Title", goods.getTitle());
        values.put("Content", goods.getContent());
        values.put("Price", goods.getPrice());
        values.put("PublishType", goods.getPublishType());
        values.put("WantGoods", goods.getWantGoods());
        values.put("img_url", goods.getImgUrl());
        
        long result = db.insert("GoodsInfo", null, values);
        return result > 0;
    }

    /**
     * 获取所有上架商品（首页）
     */
    public static List<Goods> getAllGoods() {
        List<Goods> list = new ArrayList<>();
        SQLiteDatabase db = DBUtil.getReadableDatabase();
        String sql = "SELECT * FROM GoodsInfo WHERE status=1 ORDER BY create_time DESC";
        Cursor cursor = db.rawQuery(sql, null);
        
        try {
            while (cursor.moveToNext()) {
                list.add(mapToGoods(cursor));
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    /**
     * 查询我的发布（根据用户ID）
     */
    public static List<Goods> getMyGoods(int userId) {
        List<Goods> list = new ArrayList<>();
        SQLiteDatabase db = DBUtil.getReadableDatabase();
        String sql = "SELECT * FROM GoodsInfo WHERE UserID=? ORDER BY create_time DESC";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(userId)});
        
        try {
            while (cursor.moveToNext()) {
                list.add(mapToGoods(cursor));
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    /**
     * 根据ID查询商品
     */
    public static Goods getGoodsById(int goodsId) {
        SQLiteDatabase db = DBUtil.getReadableDatabase();
        String sql = "SELECT * FROM GoodsInfo WHERE GoodsID=?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(goodsId)});
        
        try {
            if (cursor.moveToFirst()) {
                return mapToGoods(cursor);
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    /**
     * 编辑商品
     */
    public static boolean updateGoods(Goods goods) {
        SQLiteDatabase db = DBUtil.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Title", goods.getTitle());
        values.put("Content", goods.getContent());
        values.put("Price", goods.getPrice());
        values.put("PublishType", goods.getPublishType());
        values.put("WantGoods", goods.getWantGoods());
        values.put("update_time", "datetime('now')");
        
        int result = db.update("GoodsInfo", values, "GoodsID=?", 
                new String[]{String.valueOf(goods.getGoodsId())});
        return result > 0;
    }

    /**
     * 删除商品
     */
    public static boolean deleteGoods(int goodsId) {
        SQLiteDatabase db = DBUtil.getWritableDatabase();
        int result = db.delete("GoodsInfo", "GoodsID=?", 
                new String[]{String.valueOf(goodsId)});
        return result > 0;
    }

    /**
     * 搜索商品（标题+描述）
     */
    public static List<Goods> searchGoods(String keyword) {
        List<Goods> list = new ArrayList<>();
        SQLiteDatabase db = DBUtil.getReadableDatabase();
        String sql = "SELECT * FROM GoodsInfo WHERE status=1 AND (Title LIKE ? OR Content LIKE ?) ORDER BY create_time DESC";
        String searchPattern = "%" + keyword + "%";
        Cursor cursor = db.rawQuery(sql, new String[]{searchPattern, searchPattern});
        
        try {
            while (cursor.moveToNext()) {
                list.add(mapToGoods(cursor));
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    /**
     * 结果集映射为实体（复用代码，最新规范）
     */
    static Goods mapToGoods(Cursor cursor) {
        Goods goods = new Goods();
        goods.setGoodsId(cursor.getInt(cursor.getColumnIndexOrThrow("GoodsID")));
        goods.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("UserID")));
        goods.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("Title")));
        goods.setContent(cursor.getString(cursor.getColumnIndexOrThrow("Content")));
        goods.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("Price")));
        goods.setPublishType(cursor.getInt(cursor.getColumnIndexOrThrow("PublishType")));
        goods.setWantGoods(cursor.getString(cursor.getColumnIndexOrThrow("WantGoods")));
        goods.setImgUrl(cursor.getString(cursor.getColumnIndexOrThrow("img_url")));
        return goods;
    }
}