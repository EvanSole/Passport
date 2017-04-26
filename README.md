# Passport统一登录认证中心
 
## 前言    
 > 登录认证，是每个应用都需要的基础功能。但很多的时候，却都被大家所忽略，不仅安全漏洞严重，而且代码紧耦合，
 混乱不堪。Passport项目，正是为了解决登陆认证的事情，让认证模块更透明，减少耦合！

## 目录
* 什么是认证(Authentication)
* Passport项目介绍
* Passport集成配置说明
* Possport restful api

### 什么是登录认证(Authentication)
> 认证又称“验证”、“鉴权”，是指通过一定的手段，完成对用户身份的确认。身份验证的方法有很多，基本上可分为：基于
共享密钥的身份验证、基于生物学特征的身份验证和基于公开密钥加密算法的身份验证。登录认证，是用户在访问应用或者网站
时，通过是先注册的用户名和密码，告诉应用使用者的身份，从而获得访问权限的一种操作.几乎所有的应用都需要登陆认证.

###  Passport项目介绍
> Passport项目基于OAuth2.0协议，Oauth2.0协议为用户资源的授权提供了一个安全的、开放而又简易的标准.它以统一身份认
证服务为核心。用户登录统一身份认证服务后，即可使用所有支持统一身份认证服务的管理应用系统。


#### OAuth2.0大致步骤:
1. 客户端从资源拥有者（最终用户）那里请求授权。授权请求能够直接发送给资源拥有者，或者间接的通过授权服务器发送请求；
2. 资源拥有者为客户端授权，给客户端发送一个访问许可(Authorization Code)；
3. 客户端出示自己的私有证书(client_id 和client_secret)和上一步拿到的访问许可，来向授权服务器请求一个访问令牌；
4. 授权服务器验证客户端的私有证书和访问许可的有效性，如果验证有效，则向客户端发送一个访问令牌，访问令牌包括许可的作用域、
有效时间和一些其他属性信息；
5. 客户端出示访问令牌向资源服务器请求受保护资源；
6. 资源服务器对访问令牌做出响应。


> Passport项目提供应用管理、用户管理、用户登录认证、密码修改等相关功能.系统采用B/S结构，提供客户端passport-client和服务端
passport-server.客户端只做集成使用，提供统一的filter(也可自行实现filter)，然后通过web.xml配置加入应用做到最小侵入;服务端
提供web管理界面，用来管理应用、用户、授权码、令牌等信息；同时提供统一登录页，登录日志查询.

### Passport项目部分功能展示

* [Demo](http://106.14.180.178//passport)  

##  Passport集成配置说明

*  步骤1. 在项目pom.xml中引入Passport-client依赖包, 和Oauth2 client 依赖包

```xml
<!--oauth2 client -->
<dependency>
    <groupId>org.apache.oltu.oauth2</groupId>
    <artifactId>org.apache.oltu.oauth2.authzserver</artifactId>
    <version>1.0.0</version>
</dependency>

<dependency>
    <groupId>org.apache.oltu.oauth2</groupId>
    <artifactId>org.apache.oltu.oauth2.resourceserver</artifactId>
    <version>1.0.0</version>
</dependency>

<dependency>
    <groupId>org.apache.oltu.oauth2</groupId>
    <artifactId>org.apache.oltu.oauth2.common</artifactId>
    <version>1.0.0</version>
</dependency>

<dependency>
    <groupId>org.apache.oltu.oauth2</groupId>
    <artifactId>org.apache.oltu.oauth2.client</artifactId>
    <version>1.0.2</version>
</dependency>

<!-- passport-client -->
<dependency>
  <groupId>com.hmt.oauth.passport</groupId>
  <artifactId>passport-client</artifactId>
  <version>1.0.0</version>
</dependency>
```

* 步骤2. 创建系统配置文件*systemConfigs.properties,请务必保证文件名一致，并在系统启动时加载到内存中*,配置示例如下:

```properties
passport.client.id=877645414466310
passport.client.secret=7210878826c026946ffcd485b6170086cb935cc9
passport.url=http://127.0.0.1:8081
passport.redirect.url=http://127.0.0.1:8080/app/index.html
```

* 步骤3. 在项目*web.xml*文件中新增filter节点.

```xml
  <filter>
    <filter-name>sessionFilter</filter-name>
    <filter-class>com.hmt.oauth.passport.web.filter.HmtSessionFilter</filter-class>
    <init-param>
      <param-name>exceptUrlRegex</param-name>
      <!-- 不需要拦截的请求路径，支持正则表达式 -->
      <param-value>/(login|logout|status|validate.jpg)</param-value>
    </init-param>
    <init-param>
      <!-- 过滤静态资源文件目录，多个目录使用逗号","分隔 -->
      <param-name>ignoreUrlPattern</param-name>
      <param-value>/app/bower_components/**,/app/images/**</param-value>
    </init-param>
  </filter>
  <filter-mapping>
    <filter-name>sessionFilter</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>
```


## Possport restful api

### 1.术语和缩略词

| 缩写、术语及符号 | 解释          |
| ------------- | ----------- |
| app_id      | 应用唯一标志，对应app key |
| app_secret  | 应用唯一标志，对应secret或者app secret |
| response_type  | 授权码标识，系统使用code |
| redirect_url  | 回调地址 |
| access_token  | 登录令牌 |
| refresh_token  | 刷新令牌 |
| authorization code  | 授权代码 |
| scope  | 作用域 |

### 2.接口API说明文档

`1.获取 authorization_code授权码` 

> 通过在浏览器中访问下面的地址，来引导用户授权，并获得 authorization_code

```
https://passport.xxx.com/oauth2/authorize
```

参数：

| 参数名称  | 参数说明|
| ------------- | ----------- 
| client_id      | 必选参数，应用的唯一标识，对应于appKey |
| redirect_uri  | 必选参数，应用回掉地址 |
| response_type  | 必选参数，授权类型，默认code |
| loginName  | 必选参数，用户登录名 |
| password  | 必选参数，用户登录密码 |

* 注意:此请求必须是 HTTP POST 方式

接口返回:
```
{
  "code": "string",
  "data": {},
  "message": "string"
}
```

`2.根据authorization_code授权码获取accessToken` 

> 通过在浏览器中访问下面的地址，来引导用户授权，并获得 access_token

```
https://passport.xxx.com/oauth2/accessToken
```

参数：

| 参数名称  | 参数说明|
| ------------- | ----------- 
| client_id      | 必选参数，应用的唯一标识，对应于appKey |
| client_secret      | 必选参数，应用secret，对应于appSecret |
| redirect_uri  | 必选参数，应用回掉地址 |
| grant_type  | 必选参数，授权码类型，默认code |
| redirect_uri  | 必选参数，授权后回掉地址|
| code  | 必选参数，授权码 |

* 注意:此请求必须是 HTTP POST 方式

接口返回:
```
{
  "code": "string",
  "data": {},
  "message": "string"
}
```

`3.Token安全检查验证checkAccessToken` 

> 通过在浏览器中访问下面的地址，来引导用户验证token是否有效

```
https://passport.xxx.com/oauth2/checkAccessToken
```

参数：

| 参数名称  | 参数说明|
| ------------- | ----------- 
| accessToken      | 必选参数，应用授权Token |

* 注意:此请求必须是 HTTP POST 方式

接口返回:
```
{
  "code": "string",
  "data": {},
  "message": "string"
}
```

`4.通过accessToken获取用户信息` 

> 通过在浏览器中访问下面的地址，来引导用户通过授权token换取用户信息.

```
https://passport.xxx.com/api/vi/userInfo
```

参数：

| 参数名称  | 参数说明|
| ------------- | ----------- 
| access_token      | 必选参数，应用授权Token |

* 注意:此请求必须是 HTTP POST 方式

接口返回:
```
{
  "code": "string",
  "data": {},
  "message": "string"
}
```
### 3.TODO  
（无）



