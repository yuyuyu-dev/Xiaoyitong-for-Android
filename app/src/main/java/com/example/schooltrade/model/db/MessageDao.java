package com.example.schooltrade.model.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.schooltrade.entity.Conversation;
import com.example.schooltrade.entity.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageDao {

    public static boolean sendMessage(Message message) {
        SQLiteDatabase db = DBUtil.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sender_id", message.getSenderId());
        values.put("receiver_id", message.getReceiverId());
        values.put("goods_id", message.getGoodsId());
        values.put("content", message.getContent());
        values.put("message_type", message.getMessageType());
        values.put("is_read", 0);

        long result = db.insert("Message", null, values);
        return result > 0;
    }

    public static List<Message> getConversationMessages(int userId, int otherUserId, int goodsId) {
        List<Message> list = new ArrayList<>();
        SQLiteDatabase db = DBUtil.getReadableDatabase();
        String sql = "SELECT * FROM Message WHERE " +
                "((sender_id=? AND receiver_id=?) OR (sender_id=? AND receiver_id=?)) " +
                "AND goods_id=? ORDER BY create_time ASC";
        Cursor cursor = db.rawQuery(sql, new String[]{
                String.valueOf(userId), String.valueOf(otherUserId),
                String.valueOf(otherUserId), String.valueOf(userId),
                String.valueOf(goodsId)
        });

        try {
            while (cursor.moveToNext()) {
                list.add(mapToMessage(cursor));
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    public static List<Conversation> getConversations(int userId) {
        List<Conversation> list = new ArrayList<>();
        SQLiteDatabase db = DBUtil.getReadableDatabase();
        String sql = "SELECT m.goods_id, u.UserID as other_user_id, u.RealName as other_user_name, " +
                "g.Title as goods_title, m.content as last_message, m.create_time as last_time, " +
                "SUM(CASE WHEN m.receiver_id=? AND m.is_read=0 THEN 1 ELSE 0 END) as unread_count " +
                "FROM Message m " +
                "LEFT JOIN UserInfo u ON (CASE WHEN m.sender_id=? THEN m.receiver_id ELSE m.sender_id END) = u.UserID " +
                "LEFT JOIN GoodsInfo g ON m.goods_id = g.GoodsID " +
                "WHERE m.sender_id=? OR m.receiver_id=? " +
                "GROUP BY m.goods_id, other_user_id " +
                "ORDER BY last_time DESC";

        Cursor cursor = db.rawQuery(sql, new String[]{
                String.valueOf(userId), String.valueOf(userId),
                String.valueOf(userId), String.valueOf(userId)
        });

        try {
            while (cursor.moveToNext()) {
                Conversation conv = new Conversation();
                conv.setGoodsId(cursor.getInt(cursor.getColumnIndexOrThrow("goods_id")));
                conv.setOtherUserId(cursor.getInt(cursor.getColumnIndexOrThrow("other_user_id")));
                conv.setOtherUserName(cursor.getString(cursor.getColumnIndexOrThrow("other_user_name")));
                conv.setGoodsTitle(cursor.getString(cursor.getColumnIndexOrThrow("goods_title")));
                conv.setLastMessage(cursor.getString(cursor.getColumnIndexOrThrow("last_message")));
                conv.setLastMessageTime(cursor.getString(cursor.getColumnIndexOrThrow("last_time")));
                conv.setUnreadCount(cursor.getInt(cursor.getColumnIndexOrThrow("unread_count")));
                list.add(conv);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    public static void markAsRead(int userId, int otherUserId, int goodsId) {
        SQLiteDatabase db = DBUtil.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_read", 1);
        db.update("Message", values,
                "receiver_id=? AND sender_id=? AND goods_id=?",
                new String[]{String.valueOf(userId),
                        String.valueOf(otherUserId),
                        String.valueOf(goodsId)});
    }

    public static int getUnreadCount(int userId) {
        SQLiteDatabase db = DBUtil.getReadableDatabase();
        String sql = "SELECT COUNT(*) FROM Message WHERE receiver_id=? AND is_read=0";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(userId)});

        try {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } finally {
            cursor.close();
        }
        return 0;
    }

    private static Message mapToMessage(Cursor cursor) {
        Message message = new Message();
        message.setMessageId(cursor.getInt(cursor.getColumnIndexOrThrow("message_id")));
        message.setSenderId(cursor.getInt(cursor.getColumnIndexOrThrow("sender_id")));
        message.setReceiverId(cursor.getInt(cursor.getColumnIndexOrThrow("receiver_id")));
        message.setGoodsId(cursor.getInt(cursor.getColumnIndexOrThrow("goods_id")));
        message.setContent(cursor.getString(cursor.getColumnIndexOrThrow("content")));
        message.setMessageType(cursor.getString(cursor.getColumnIndexOrThrow("message_type")));
        message.setIsRead(cursor.getInt(cursor.getColumnIndexOrThrow("is_read")));
        message.setCreateTime(cursor.getString(cursor.getColumnIndexOrThrow("create_time")));
        return message;
    }
}
