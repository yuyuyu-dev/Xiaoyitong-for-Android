package com.example.schooltrade.model.db;

import com.example.schooltrade.entity.Goods;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品DAO - 最新规范
 * 支持：发布、查询、我的发布、编辑、删除、搜索、状态管理
 */
public class GoodsDao {

    /**
     * 发布商品（绑定发布者UserID）
     */
    public static boolean publishGoods(Goods goods) {
        String sql = "INSERT INTO GoodsInfo(UserID,Title,Content,Price,PublishType,WantGoods,img_url) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, goods.getUserId());
            pstmt.setString(2, goods.getTitle());
            pstmt.setString(3, goods.getContent());
            pstmt.setDouble(4, goods.getPrice());
            pstmt.setInt(5, goods.getPublishType());
            pstmt.setString(6, goods.getWantGoods());
            pstmt.setString(7, goods.getImgUrl());
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取所有上架商品（首页）
     */
    public static List<Goods> getAllGoods() {
        List<Goods> list = new ArrayList<>();
        String sql = "SELECT * FROM GoodsInfo WHERE status=1 ORDER BY create_time DESC";
        try (Connection conn = DBUtil.getConnection();
             ResultSet rs = conn.prepareStatement(sql).executeQuery()) {

            while (rs.next()) {
                list.add(mapToGoods(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 查询我的发布（根据用户ID）
     */
    public static List<Goods> getMyGoods(int userId) {
        List<Goods> list = new ArrayList<>();
        String sql = "SELECT * FROM GoodsInfo WHERE UserID=? ORDER BY create_time DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapToGoods(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 根据ID查询商品
     */
    public static Goods getGoodsById(int goodsId) {
        String sql = "SELECT * FROM GoodsInfo WHERE GoodsID=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, goodsId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapToGoods(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 编辑商品
     */
    public static boolean updateGoods(Goods goods) {
        String sql = "UPDATE GoodsInfo SET Title=?,Content=?,Price=?,PublishType=?,WantGoods=?,update_time=GETDATE() WHERE GoodsID=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, goods.getTitle());
            pstmt.setString(2, goods.getContent());
            pstmt.setDouble(3, goods.getPrice());
            pstmt.setInt(4, goods.getPublishType());
            pstmt.setString(5, goods.getWantGoods());
            pstmt.setInt(6, goods.getGoodsId());
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除商品
     */
    public static boolean deleteGoods(int goodsId) {
        String sql = "DELETE FROM GoodsInfo WHERE GoodsID=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, goodsId);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 搜索商品（标题+描述）
     */
    public static List<Goods> searchGoods(String keyword) {
        List<Goods> list = new ArrayList<>();
        String sql = "SELECT * FROM GoodsInfo WHERE status=1 AND (Title LIKE ? OR Content LIKE ?) ORDER BY create_time DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapToGoods(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 结果集映射为实体（复用代码，最新规范）
     */
    static Goods mapToGoods(ResultSet rs) throws Exception {
        Goods goods = new Goods();
        goods.setGoodsId(rs.getInt("GoodsID"));
        goods.setUserId(rs.getInt("UserID"));
        goods.setTitle(rs.getString("Title"));
        goods.setContent(rs.getString("Content"));
        goods.setPrice(rs.getDouble("Price"));
        goods.setPublishType(rs.getInt("PublishType"));
        goods.setWantGoods(rs.getString("WantGoods"));
        goods.setImgUrl(rs.getString("img_url"));
        return goods;
    }
}