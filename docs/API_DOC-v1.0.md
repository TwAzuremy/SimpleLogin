# **接口文档**

## **1. 文档概述**

- **文档版本**：v1.0
- **最后更新时间**：2025-02-12
- **维护人员**：Azuremy

------

## **2. 基础信息**

### **2.1 服务地址**

- 开发环境：`http://localhost:13900`

### **2.2 全局约定**

- **请求方式**：`POST`/`GET`/`PATCH`/`DELETE`

- **接口签名方式**：使用 `HmacSHA256` 加密： [示例](#接口签名生成示例)

- **请求头**：

  ```json
  {
      "Content-Type": "application/json",
      "Authorization": "Bearer {token}"
  }
  ```

- **响应格式**：

  ```json
  {
      "status": 200,
      "message": "OK",
      "data": {}	
  }
  ```

## 3. 接口列表

### 3.1 用户接口

#### 3.1.1 用户注册

- 接口说明：用户注册

- 请求地址：`/users/register`

- 请求方法：`POST`

- 请求头：

  | 名称        | 类型   | 必填 | 描述             |
  | ----------- | ------ | ---- | ---------------- |
  | X-Signature | String | 是   | 接口签名认证     |
  | X-Timestamp | String | 是   | 接口签名是否过期 |
  | X-Nonce     | String | 是   | 防止重复提交     |

- 请求参数：

  | 参数名          | 类型   | 必填 | 描述                      |
  | --------------- | ------ | ---- | ------------------------- |
  | `user.username` | String | 是   | 用户名称                  |
  | `user.password` | String | 是   | 用户密码 ( SHA-256 加密 ) |
  | `user.email`    | String | 是   | 用户邮箱                  |
  | `captcha`       | String | 是   | 注册时发送的邮箱验证码    |

- 请求示例：

  ```json
  {
      "user": {
          "username": "Azuremy",
          "password": "123456",
          "email": "xxx@yyy.com"
      },
      "captcha": "N41QQ6"
  }
  ```

- 响应成功示例：

  ```json
  {
      "status": 201,
      "message": "User registered successfully.",
      "data": true
  }
  ```

- 响应失败示例：

  ```json
  {
      "status": 409,
      "message": "User already exists",
      "data": false
  }
  ```

- 补充说明：

  > 其他可能的错误码请参考：[注册相关错误码](#4111-用户注册)

#### 3.1.2 用户登录

- 接口说明：用户登录

- 请求地址：`/users/login`

- 请求方法：`POST`

- 请求头：

  | 名称        | 类型   | 必填 | 描述             |
  | ----------- | ------ | ---- | ---------------- |
  | X-Signature | String | 是   | 接口签名认证     |
  | X-Timestamp | String | 是   | 接口签名是否过期 |
  | X-Nonce     | String | 是   | 防止重复提交     |

- 请求参数：

  | 参数名     | 类型   | 必填 | 描述                      |
  | ---------- | ------ | ---- | ------------------------- |
  | `email`    | String | 是   | 用户邮箱 ( 账号 )         |
  | `password` | String | 是   | 用户密码 ( SHA-256 加密 ) |

- 请求示例：

  ```json
  {
      "email": "xxx@yyy.com",
      "password": "123456"
  }
  ```

- 响应成功示例：

  ```json
  {
      "status": 200,
      "message": "OK",
      "data": "token string"
  }
  ```

- 响应失败示例：

  响应头：
  
  | 名称         | 类型 | 必返 | 描述                       |
  | ------------ | ---- | ---- | -------------------------- |
  | Retry-After  | long | 否   | 账号锁定后多长时间可以重试 |
  | X-Rate-Limit | int  | 是   | 登录失败次数               |
  
  ```json
  {
      "status": 401,
      "message": "The account or password is incorrect",
      "data": false
  }
  ```

- 补充说明：

  > 其他可能的错误码请参考：[登录相关错误码](#4112-用户登录)

#### 3.1.3 用户登出

- 接口说明：用户登出

- 请求地址：`/users/logou`

- 请求方法：`POST`

- 请求头：

  | 名称          | 类型   | 必填 | 描述         |
  | ------------- | ------ | ---- | ------------ |
  | Authorization | String | 是   | 用于身份认证 |

- 响应成功示例：

  ```json
  {
      "status": 200,
      "message": "OK",
      "data": true
  }
  ```

- 响应失败示例：

  ```json
  {
      "status": 401,
      "message": "Jwt verification failed",
      "data": false
  }
  ```

#### 3.1.4 获取用户信息

- 接口说明：获取用户信息

- 请求地址：`/users/get-info`

- 请求方法：`GET`

- 请求头：

  | 名称          | 类型   | 必填 | 描述         |
  | ------------- | ------ | ---- | ------------ |
  | Authorization | String | 是   | 用于身份认证 |

- 响应成功示例：

  ```json
  {
      "status": 200,
      "message": "OK",
      "data": {
          "id": 1,
          "username": "Azuremy",
          "email": "xxx@yyy.com",
          "profile": "Here's the bio"
      }
  }
  ```

- 响应失败示例：

  ```json
  {
      "status": 401,
      "message": "Jwt verification failed",
      "data": false
  }
  ```

#### 3.1.5 修改密码

- 接口说明：修改用户密码

- 请求地址：`/users/modify-password`

- 请求方法：`PATCH`

- 请求头：

  | 名称          | 类型   | 必填 | 描述         |
  | ------------- | ------ | ---- | ------------ |
  | Authorization | String | 是   | 用于身份认证 |

- 请求参数：

  | 参数名                   | 类型   | 必填 | 描述                             |
  | ------------------------ | ------ | ---- | -------------------------------- |
  | `attachment.oldPassword` | String | 是   | 用户旧密码                       |
  | `attachment.newPassword` | String | 是   | 用户新密码                       |
  | `captcha`                | String | 是   | 正式修改密码前发送到邮箱的验证码 |

- 请求示例：

  ```json
  {
      "attachment": {
          "oldPassword": "123456",
          "newPassword": "456789"
      },
      "captcha": "02M7CL"
  }
  ```

- 响应成功示例：

  ```json
  {
      "status": 200,
      "message": "OK",
      "data": 1
  }
  ```

- 响应失败示例：

  ```json
  {
      "status": 400,
      "message": "The new password cannot be the same as the old password",
      "data": false
  }
  ```

- 补充说明：

  > 其他可能的错误码请参考：[修改密码相关错误码](#4113-修改密码)

#### 3.1.6 重置密码

- 接口说明：在忘记密码功能上重置密码

- 请求地址：`/users/reset-password`

- 请求方法：`PATCH`

- 请求参数：

  | 参数名          | 类型   | 必填 | 描述                             |
  | --------------- | ------ | ---- | -------------------------------- |
  | `user.email`    | String | 是   | 用户邮箱                         |
  | `user.password` | String | 是   | 新密码                           |
  | `captcha`       | String | 是   | 正式修改密码前发送到邮箱的验证码 |

- 请求示例：

  ```json
  {
      "user": {
          "email": "xxx@yyy.com",
          "password": "123456"
      },
      "captcha": "02M7CL"
  }
  ```

- 响应成功示例：

  ```json
  {
      "status": 200,
      "message": "OK",
      "data": 1
  }
  ```

- 响应失败示例：

  ```json
  {
      "status": 401,
      "message": "Captcha verification failed",
      "data": false
  }
  ```

#### 3.1.7 查询用户名是否存在

- 接口说明：查询用户名是否存在，用于注册时用户名的唯一性

- 请求地址：`/users/exists-username`

- 请求方法：`GET`

- 请求参数：

  | 参数名     | 类型   | 必填 | 描述   |
  | ---------- | ------ | ---- | ------ |
  | `username` | String | 是   | 用户名 |

- 响应成功示例：

  ```json
  {
      "status": 200,
      "message": "OK",
      "data": true
  }
  ```

### 3.2 邮箱接口

#### 3.2.1 注册验证码发送

- 接口说明：用户注册

- 请求地址：`/email/send-register-captcha`

- 请求方法：`POST`

- 频率限制：

  - 同一邮箱：`1 次 / 分钟`
  - 超出频率后返回 `HTTP 429 Too Many Requests`

- 请求参数：

  | 参数名     | 类型   | 必填 | 描述     |
  | ---------- | ------ | ---- | -------- |
  | `email`    | String | 是   | 用户邮箱 |
  | `username` | String | 否   | 用户名称 |
  
- 请求示例：

  ```json
  {
      "email": "xxx@yyy.com",
      "username": "Azuremy"
  }
  ```
  
- 响应成功示例：

  ```json
  {
      "status": 200,
      "message": "OK",
      "data": true
  }
  ```

- 响应失败示例：

  ```json
  {
      "status": 400,
      "message": "Bad Request",
      "data": false
  }
  ```

- 补充说明：

  > 其他可能的错误码请参考：[邮箱相关错误码](#412-邮箱接口)

#### 3.2.2 修改密码验证码发送

- 接口说明：修改用户密码

- 请求地址：`/email/send-modify-password-captcha`

- 请求方法：`POST`

- 频率限制：

  - 同一邮箱：`1 次 / 分钟`
  - 超出频率后返回 `HTTP 429 Too Many Requests`

- 请求参数：

  | 参数名     | 类型   | 必填 | 描述     |
  | ---------- | ------ | ---- | -------- |
  | `email`    | String | 是   | 用户邮箱 |
  | `username` | String | 否   | 用户名称 |

- 请求示例：

  ```json
  {
      "email": "xxx@yyy.com",
      "username": "Azuremy"
  }
  ```

- 响应成功示例：

  ```json
  {
      "status": 200,
      "message": "OK",
      "data": true
  }
  ```

- 响应失败示例：

  ```json
  {
      "status": 400,
      "message": "Bad Request",
      "data": false
  }
  ```

- 补充说明：

  > 其他可能的错误码请参考：[邮箱相关错误码](#412-邮箱接口)

#### 3.2.3 重置密码验证发送

- 接口说明：修改用户密码

- 请求地址：`/email/send-reset-password-captcha`

- 请求方法：`POST`

- 频率限制：

  - 同一邮箱：`1 次 / 分钟`
  - 超出频率后返回 `HTTP 429 Too Many Requests`

- 请求参数：

  | 参数名     | 类型   | 必填 | 描述     |
  | ---------- | ------ | ---- | -------- |
  | `email`    | String | 是   | 用户邮箱 |
  | `username` | String | 否   | 用户名称 |

- 请求示例：

  ```json
  {
      "email": "xxx@yyy.com",
      "username": "Azuremy"
  }
  ```

- 响应成功示例：

  ```json
  {
      "status": 200,
      "message": "OK",
      "data": true
  }
  ```

- 响应失败示例：

  ```json
  {
      "status": 400,
      "message": "Bad Request",
      "data": false
  }
  ```

- 补充说明：

  > 其他可能的错误码请参考：[邮箱相关错误码](#412-邮箱接口)

### 3.3 第三方接口

#### 3.3.1 获取重定向链接

- 接口说明：获取重定向链接，用于跳转到第三方登录认证页面

- 请求地址：`/oauth/get-redirect-address`

- 请求方法：`GET`

- 请求参数：

  | 参数名     | 类型   | 必填 | 描述       |
  | ---------- | ------ | ---- | ---------- |
  | `provider` | String | 是   | 第三方名称 |

- 响应成功示例：

  ```json
  {
      "status": 200,
      "message": "OK",
      "data": "https://github.com/login/oauth/authorize?client_id=your_client_id&redirect_uri=http://localhost:13900/oauth/redirect&response_type=code&scope=user:email&state=ad0b1f035c4dc7b25fe2b08a9b8454a4"
  }
  ```

- 响应失败示例：

  ```json
  {
      "status": 400,
      "message": "Bad Request"
  }
  ```

#### 3.3.2 回调地址

- 接口说明：第三方认证后，触发的回调地址

- 请求地址：`/oauth/redirect`

- 请求方法：`GET`

- 请求参数：

  | 参数名         | 类型    | 必填 | 描述                                                         |
  | -------------- | ------- | ---- | ------------------------------------------------------------ |
  | `code`         | String  | 是   | 第三方认证后回调的 code 值                                   |
  | `state`        | String  | 是   | 用于防止跨站请求伪造攻击                                     |
  
- 响应成功示例：

  ```json
  {
      "status": 200,
      "message": "OK",
      "data": code
  }
  ```

#### 3.3.3 第三方登录

- 接口说明：第三方登录

- 请求地址：`/oauth/login`

- 请求方法：`POST`

- 请求参数：

  | 参数名     | 类型   | 必填 | 描述                                    |
  | ---------- | ------ | ---- | --------------------------------------- |
  | `code`     | String | 是   | 第三方认证后回调的 `code` 值            |
  | `provider` | String | 是   | 第三方名称 ( 比如 github, google, ... ) |

- 请求示例：

  ```json
  {
      "code": "123456789",
      "provider": "github"
  }
  ```

- 响应成功示例：

  ```json
  {
      "status": 200,
      "message": "OK",
      "data": "token string"
  }
  ```

- 补充说明：

  > 其他可能的错误码请参考：[第三方相关错误码](#413-第三方接口)

#### 3.3.4 用户绑定第三方

- 接口说明：已经登录的 **Simple Login 账户**绑定第三方账号

- 请求地址：`/oauth/bind`

- 请求方法：`POST`

- 请求头：

  | 名称          | 类型   | 必填 | 描述                                  |
  | ------------- | ------ | ---- | ------------------------------------- |
  | Authorization | String | 是   | 已经登录的用户 token ( 非第三方登录 ) |

- 请求参数：

  | 参数名     | 类型   | 必填 | 描述                                    |
  | ---------- | ------ | ---- | --------------------------------------- |
  | `code`     | String | 是   | 第三方认证后回调的 `code` 值            |
  | `provider` | String | 是   | 第三方名称 ( 比如 github, google, ... ) |

- 请求示例：

  ```json
  {
      "code": "123456789",
      "provider": "github"
  }
  ```

- 响应成功示例：

  ```json
  {
      "status": 200,
      "message": "OK",
      "data": 1
  }
  ```

- 响应失败示例：

  ```json
  {
      "status": 409,
      "message": "The account has been linked to another user."
  }
  ```

- 补充说明：

  > 其他可能的错误码请参考：[第三方相关错误码](#413-第三方接口)

#### 3.3.5 用户解绑第三方

- 接口说明：已经登录的 **Simple Login 账户**解除绑定第三方账号

- 请求地址：`/oauth/unbind`

- 请求方法：`DELETE`

- 请求头：

  | 名称          | 类型   | 必填 | 描述                                  |
  | ------------- | ------ | ---- | ------------------------------------- |
  | Authorization | String | 是   | 已经登录的用户 token ( 非第三方登录 ) |

- 请求参数：

  | 参数名        | 类型   | 必填 | 描述                   |
  | ------------- | ------ | ---- | ---------------------- |
  | `userId`      | int    | 是   | Simple Login 用户的 id |
  | `oauthUserId` | String | 是   | 第三方的 id            |

- 请求示例：

  ```json
  {
      "userId": 1,
      "oauthUserId": "a33c0974-4344-4dfd-8a0a-76d70c8b6201"
  }
  ```

- 响应成功示例：

  ```json
  {
      "status": 200,
      "message": "OK",
      "data": 1
  }
  ```

## **4. 错误码表**

### 4.1 接口其他错误码

##### 4.1.1 用户接口

###### 4.1.1.1 用户注册

| 错误码 | 描述                 | 解决方案                   |
| ------ | -------------------- | -------------------------- |
| 409    | 账号 ( 邮箱 ) 已存在 | 重新填写新的未注册过的邮箱 |

###### 4.1.1.2 用户登录

| 错误码 | 描述             | 解决方案                 |
| ------ | ---------------- | ------------------------ |
| 401    | 账号或密码错误   | 重新填写正确的账号或密码 |
| 429    | 登录失败次数过多 | 等待锁定时间过后重新登录 |

###### 4.1.1.3 修改密码

| 错误码 | 描述     | 解决方案           |
| ------ | -------- | ------------------ |
| 401    | 密码错误 | 重新填写正确的密码 |

#### 4.1.2 邮箱接口

| 错误码 | 描述          | 解决方案   |
| ------ | ------------- | ---------- |
| 504    | smtp 无法连接 | 联系管理员 |

#### 4.1.3 第三方接口

| 错误码 | 描述                | 解决方案   |
| ------ | ------------------- | ---------- |
| 503    | 第三方 API 链接失败 | 联系管理员 |

### 4.2 全局通用错误码

| 错误码 | 描述                                 | 解决方案                                   |
| ------ | ------------------------------------ | ------------------------------------------ |
| 401    | 认证失败，用于身份验证以及验证码验证 | 检查填写的信息是否有效，以及请求头是否缺失 |
| 500    | 服务器出现错误                       | 联系管理员                                 |

## 其他

### 接口签名生成示例

- `hmacSHA256` 加密前字符串结构

  ```
   POST /api/test ?prefix=Hello&suffix=World &body={"separate":","} &timestamp=1739002152986 &nonce=3d1cff
   ---- ---------  -------------------------  ---------------------  -----------------------  ------------
  Method   URI             Query                     Body                  Timestamp              Nonce
  ```

- 用于 Postman 预请求脚本

```javascript
// ==================== variables ====================

const KEY = pm.collectionVariables.get("SIGNATURE_SECRET");
const URL = "/" + pm.request.url.path.join("/");

const METHOD = pm.request.method;
const TIMESTAMP = Date.now();
const NONCE = Math.random().toString(36).substr(2, 8);

let QUERY = pm.request.url.query.map(item => `${item.key}=${item.value}`).join('&');
let BODY = JSON.stringify(pm.request.body.raw)
				.replace(/\\[rn]/g, "")
				.replace(/[\\\s]+/g, "")
				.replace(/^"|"$/g, "");

// ==================== processing ====================

QUERY = QUERY ? QUERY + "&" : "";
BODY = BODY ? "body=" + BODY + "&" : "";

let DATA = METHOD + URL + "?" + QUERY + BODY + "timestamp=" + TIMESTAMP + "&nonce=" + NONCE;

// Hide sensitive fields (such as password)
DATA = DATA.replace(/("?password"?\s*[:=]\s*)(["']?)([^&"'\s]+?)(["']?)(?=&|\s|$|\b)/gi,"$1$2***$4");

// ==================== encryption ====================

const SIGNATURE = CryptoJS.HmacSHA256(DATA, KEY).toString(CryptoJS.enc.Hex);

// ================ set request headers ================

pm.request.headers.add({ key: 'X-Signature', value: SIGNATURE });
pm.request.headers.add({ key: 'X-Timestamp', value: TIMESTAMP.toString() });
pm.request.headers.add({ key: 'X-Nonce', value: NONCE });
```

