package com.example.schooltrade.model.db;

import com.example.schooltrade.config.AppConfig;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBUtil {
    public static Connection getConnection() {
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            return DriverManager.getConnection(
                    AppConfig.getDBUrl(),
                    AppConfig.DB_USER,
                    AppConfig.DB_PWD
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void close(Connection conn) {
        try {
            if (conn != null) conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}