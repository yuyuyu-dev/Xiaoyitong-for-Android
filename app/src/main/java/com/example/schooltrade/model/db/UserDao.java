package com.example.schooltrade.model.db;

import com.example.schooltrade.entity.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDao {
    // 用户登录
    public static User login(String account, String pwd) {
        String sql = "SELECT * FROM UserInfo WHERE Account=? AND Password=? AND status=1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, account);
            pstmt.setString(2, pwd);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("UserID"));
                user.setAccount(rs.getString("Account"));
                user.setRealName(rs.getString("RealName"));
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 用户注册
    public static boolean register(User user) {
        String sql = "INSERT INTO UserInfo(Account,Password,RealName) VALUES(?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getAccount());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRealName());
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}