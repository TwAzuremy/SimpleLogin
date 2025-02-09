# Simple Login

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://chat.deepseek.com/a/chat/s/LICENSE)

## 📖 项目简介

一个开箱即用的用户管理系统模板，基于 SpringBoot 实现标准化的用户认证与基本信息管理功能。
提供快速集成能力，包含完整的注册 / 登录流程、 JWT 认证、Redis 会话管理和基础用户操作 API，可直接作为模块集成到新项目中。

## ✨ 核心功能

- ✅ 基于 Spring Security 安全认证
- ✅ JWT 无状态认证
- ✅ 接口签名认证
- ✅ Redis 缓存会话管理
- 🧩 模块化设计，即插即用
- 📦 包含以下基础功能：
  - 用户注册（ 邮箱 ）
  - 用户登录
  - 信息修改
- 🚧 正在开发的功能：
  - ⌛ 第三方登录
  - 📝 权限管理

## 🛠️ 技术栈

| 组件            | 用途                |
| :-------------- | :------------------ |
| Spring Boot 3   | 核心框架            |
| Spring Security | 安全控制            |
| JWT             | 无状态认证          |
| Redis           | 会话缓存 / 限流控制 |
| MySQL 8         | 数据持久化          |
| Lombok          | 代码简化            |

## 🚀 快速开始

### 前置要求

- JDK 17+
- MySQL 8.0+
- Redis 6+

### 启动步骤

1. 克隆仓库

```bash
git clone https://github.com/TwAzuremy/SimpleLogin.git
```

2. 创建数据库：执行 [init.sql](src/main/resources/sql) 进行初始化

3. 更换配置：
   1. 填写各应用的账号密码：[application-prod.yml](src/main/resources/application-prod.yml)
   2. 将 [application.yml](src/main/resources/application.yml) 的 `spring.profiles.active: dev` 改为 `prod`

## ⚙️ 配置说明

1. [application-prod.yml](src/main/resources/application-prod.yml)

| 环境变量                     | 默认值         | 说明                                                                                  |
| :--------------------------- | :------------- | :------------------------------------------------------------------------------------ |
| `spring.datasource.username` | /              | MySQL 账号，如是个人电脑本地运行，一般账户为 root                                     |
| `spring.datasource.password` | /              | MySQL 密码                                                                            |
| `spring.mail.host`           | smtp.gmail.com | smtp 链接，不同邮箱可自行上网查询，或是在该邮箱设置中查看                             |
| `spring.mail.port`           | 587            | smtp 端口，不同邮箱可自行上网查询，或是在该邮箱设置中查看                             |
| `spring.mail.username`       | /              | 发送人的邮箱账号                                                                      |
| `spring.mail.password`       | /              | 开启 smtp 后的专属密码，例如谷歌需要在账号中心的添加应用专用密码, 使用该密码登录 smtp |
| `spring.data.redis.password` | /              | Redis 的密码，如果有；如果没有，可将该配置移除                                        |

## 📂 项目结构

```
src/main/java
│  └─com
│      └─framework
│          └─simpleLogin
│              │
│              ├─advice			# 全局处理 (异常捕获、添加响应头...)
│              │
│              ├─annotation		# 注解类
│              │
│              ├─aspect			# 切面类
│              │
│              ├─config			# 配置类
│              │
│              ├─controller		# 控制层 ( API 接口 )
│              │
│              ├─domain			#
│              │
│              ├─dto			# 数据传输
│              │
│              ├─entity			# 实体类 ( User )
│              │
│              ├─event			# 事件
│              │
│              ├─exception		# 自定义异常
│              │
│              ├─filter			# 过滤器
│              │
│              ├─interceptor	# 拦截器
│              │
│              ├─listener		# 事件监听器
│              │
│              ├─repository		# 数据访问层
│              │
│              ├─serializer		# 自定义序列化
│              │
│              ├─service		# 服务层
│              │  │
│              │  └─impl
│              │
│              ├─utils			# 工具类
│              │
│              └─wrapper		# 包装类
│
└─resources
    │
    ├─sql						# 初始化数据库
    │
    ├─static					# 静态文件
    │
    └─templates					# 模板文件
        └─mail					# 邮箱模板
```

## 📑 接口文档

| 版本                                |
| ----------------------------------- |
| [version 1.0](docs/API_DOC-v1.0.md) |

## 🤝 贡献指南

欢迎通过 Issue 或 PR 参与改进：

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/your-feature`)
3. 提交修改 (`git commit -am 'Add some feature'`)
4. 推送分支 (`git push origin feature/your-feature`)
5. 创建 Pull Request

## 📄 开源协议

[MIT License](/LICENSE)

---

> 💡 **使用提示**：将 `/src/main/java/com/simpleLogin` 目录整体复制到目标项目，根据需求调整包路径和配置即可快速集成用户管理功能。建议配合 [Spring Profile](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.profiles) 进行环境隔离配置
