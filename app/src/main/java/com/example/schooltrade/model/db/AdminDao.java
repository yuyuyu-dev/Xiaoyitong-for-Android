package com.example.schooltrade.model.db;

import com.example.schooltrade.entity.Admin;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminDao {
    // 管理员登录
    public static Admin login(String account, String pwd) {
        String sql = "SELECT * FROM AdminInfo WHERE account=? AND password=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, account);
            pstmt.setString(2, pwd);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Admin admin = new Admin();
                admin.setAdminId(rs.getInt("admin_id"));
                admin.setAccount(rs.getString("account"));
                admin.setRealName(rs.getString("real_name"));
                return admin;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}