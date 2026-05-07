数据库建立
CREATE DATABASE SchoolTradeDB;
GO
USE SchoolTradeDB;
GO

-- 1. 用户表（UserInfo）- 和代码字段完全一致
CREATE TABLE UserInfo (
    UserID INT PRIMARY KEY IDENTITY(1,1),
    Account VARCHAR(20) NOT NULL UNIQUE,
    Password VARCHAR(50) NOT NULL,
    RealName VARCHAR(10) NOT NULL,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT GETDATE(),
    update_time DATETIME DEFAULT GETDATE()
);
GO

-- 2. 商品表（GoodsInfo）- 和代码字段完全一致
CREATE TABLE GoodsInfo (
    GoodsID INT PRIMARY KEY IDENTITY(1,1),
    UserID INT NOT NULL,
    Title VARCHAR(50) NOT NULL,
    Content TEXT NOT NULL,
    Price DECIMAL(10,2) DEFAULT 0.00,
    PublishType TINYINT DEFAULT 0, -- 0=出售 1=置换
    WantGoods VARCHAR(50),
    img_url VARCHAR(255),
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT GETDATE(),
    update_time DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (UserID) REFERENCES UserInfo(UserID),
    INDEX idx_UserID (UserID),
    INDEX idx_Title (Title)
);
GO

-- 3. 收藏表（Collect）- 和代码字段完全一致
CREATE TABLE Collect (
    collect_id INT PRIMARY KEY IDENTITY(1,1),
    UserID INT NOT NULL,
    GoodsID INT NOT NULL,
    create_time DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (UserID) REFERENCES UserInfo(UserID),
    FOREIGN KEY (GoodsID) REFERENCES GoodsInfo(GoodsID),
    UNIQUE (UserID, GoodsID)
);
GO

-- 4. 管理员表（AdminInfo）
CREATE TABLE AdminInfo (
    admin_id INT PRIMARY KEY IDENTITY(1,1),
    account VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    real_name VARCHAR(50),
    create_time DATETIME DEFAULT GETDATE()
);
GO

-- 插入测试数据（直接可用）
INSERT INTO UserInfo (Account, Password, RealName)
VALUES 
('1001', '123456', '张三'),
('1002', '123456', '李四'),
('1003', '123456', '王五');

INSERT INTO GoodsInfo (UserID, Title, Content, Price, PublishType, WantGoods)
VALUES
(1, '高数上册课本', '九成新，大一教材，无笔记', 15.00, 0, NULL),
(1, '20000毫安充电宝', '正常使用，续航强，便携', 30.00, 0, NULL),
(2, '标准7号篮球', '几乎全新，想换羽毛球拍', 0.00, 1, '羽毛球拍'),
(2, '校园折叠自行车', '适合代步，刹车灵敏，轻便', 120.00, 0, NULL),
(3, '考研英语全套资料', '真题+笔记，想换数学考研资料', 0.00, 1, '考研数学资料');

INSERT INTO Collect (UserID, GoodsID)
VALUES
(2, 1),
(3, 4);

INSERT INTO AdminInfo(account, password, real_name)
VALUES ('admin', '123456', '系统管理员');
GO
