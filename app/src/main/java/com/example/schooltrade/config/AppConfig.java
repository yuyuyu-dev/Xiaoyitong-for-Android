package com.example.schooltrade.config;

public class AppConfig {
    public static final String DB_IP = "192.168.110.148"; // 改成你电脑IP
    public static final String DB_PORT = "1433";// 数据库端口号
    public static final String DB_NAME = "SchoolTradeDB";// 数据库名
    public static final String DB_USER = "sa";// 你的数据库用户名
    public static final String DB_PWD = "060729"; // 你的数据库密码

    public static String getDBUrl() {
        //这段链接是jtds的链接方式，jtds是java的sql数据访问工具，jtds:sqlserver是jtds的sqlserver的驱动，
        return "jdbc:jtds:sqlserver://" + DB_IP + ":" + DB_PORT
                + "/" + DB_NAME
                + ";instance=SQLEXPRESS"; // 关键！加上实例名
    }
}