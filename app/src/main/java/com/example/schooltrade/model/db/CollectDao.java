package com.example.schooltrade.model.db;

import com.example.schooltrade.entity.Goods;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CollectDao {

    // 添加收藏
    public static boolean addCollect(int userId, int goodsId) {
        String sql = "INSERT INTO Collect(user_id, goods_id) VALUES(?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, goodsId);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 取消收藏
    public static boolean cancelCollect(int userId, int goodsId) {
        String sql = "DELETE FROM Collect WHERE user_id=? AND goods_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, goodsId);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 查询我的收藏
    public static List<Goods> getMyCollect(int userId) {
        List<Goods> list = new ArrayList<>();
        String sql = "SELECT g.* FROM GoodsInfo g INNER JOIN Collect c ON g.GoodsID=c.goods_id WHERE c.user_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(GoodsDao.mapToGoods(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}