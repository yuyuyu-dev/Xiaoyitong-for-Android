# SchoolTrade - 校园交易平台

## 📱 项目简介
SchoolTrade 是一个基于 Android 的校园二手交易平台，帮助学生方便地发布和浏览二手商品信息。

## ✨ 主要功能
- 🔐 用户注册与登录
- 📦 商品发布（支持图片上传）
- 🔍 商品搜索
- ❤️ 商品收藏
- 📋 我的发布管理
- 👤 个人中心

## 🛠️ 技术栈
- **开发语言**: Java
- **数据库**: SQLite
- **UI框架**: Material Design
- **构建工具**: Gradle (Kotlin DSL)
- **最低支持**: Android 7.0 (API 24)

## 📸 功能截图
（待添加）

## 🚀 快速开始

### 环境要求
- Android Studio Hedgehog 或更高版本
- JDK 11 或更高版本
- Android SDK API 24+

### 安装步骤
1. 克隆仓库
```bash
git clone https://github.com/你的用户名/SchoolTrade.git
```

2. 使用 Android Studio 打开项目

3. 同步 Gradle 依赖

4. 运行应用到模拟器或真机

## 📁 项目结构
```
app/src/main/java/com/example/schooltrade/
├── adapter/          # RecyclerView适配器
├── base/            # BaseActivity和BaseFragment
├── config/          # 应用配置
├── entity/          # 数据实体类
├── model/db/        # 数据库操作
├── ui/              # UI界面
│   ├── goods/       # 商品相关页面
│   ├── login/       # 登录注册
│   ├── main/        # 主界面
│   └── publish/     # 发布商品
└── utils/           # 工具类
```

## 📝 数据库设计
- **UserInfo**: 用户信息表
- **GoodsInfo**: 商品信息表
- **Collect**: 收藏记录表
- **AdminInfo**: 管理员表

## 🤝 贡献指南
欢迎提交 Issue 和 Pull Request！


## 👨‍💻 作者
yuyuyu移动开发技术小组

---
Have a good coding life
