# XNetty-http-server
基于netty构建的Web微服务应用框架

简介
---
  基于netty构建的Web微服务应用框架，框架围绕大道至简、开箱即用的原则，打造一套高效开发、性能卓越的web应用的最佳实践。
  
功能说明
---
  1.接口基于HTTP协议，采用GET/POST协议，POST数据传输支持application/json和 application/x-www-form-urlencoded
  2.接口采用类定义模式，一个接口一个类，访问实例通过母实例（应用启动后扫描接口类装载母实例）克隆产生，重写toString方法作为业务逻辑执行主体，这是与常见的反射方式最大的不同；
  3.数据库mysql，mongodb，redis支持多数据源，使用简单，参照 xhigher-mysql, xhigher-mongodb
  4.支持ElasticSearch

### 请求方式
  GET：获取数据
  POST: 增加，修改，删除

### 请求头部
  扩展参数如下
  X-QS-PEERID：peerid值
  X-QS-SESSIONID：sessionid值

### 请求URI
  三级路由结构模式：/v1/module/action, 组成字符一律使用英文小写、数字，下划线。
  v1：接口对应版本，v2,v3,v4,v5以此类推
  module：业务模块
  action: 业务指令


### 请求体
  application/x-www-form-urlencoded 或 application/json

### 请求状态码
  200 OK 成功
  400 BAD_REQUEST
  403 FORBIDDEN
  404 NOT_FOUND
  405 METHOD_NOT_ALLOWED

### 响应结果
  数据格式为JSON格式
  数据结构： {"errcode":0, "errinfo":"", "data":{}} 或  {"errcode":0, "errinfo":"", "data":[]}
  errcode: 错误码 0: 成功， 非0：失败
  errinfo: 错误标识符
  data: 业务数据，对象{} 或 数组[]
  
  4000 系统内部错误（代码bug，数据库或缓存等异常）
  4001 请求错误（URL错误）
  4002 请求方式错误（非GET，POST）
  4003 请求参数错误（缺少参数或参数值错误）
  4004 请求无效（缺少peerid或错误）
  4005 服务繁忙
  4006 禁止访问
  4007 授权失效
  4008 信息不存在
  

接口示例：
---

## 用户信息查询

  - URI：`/v1/user/info`
  - Method：`GET`
  - Header：
    - `X-QS-PEERID`
    - `X-QS-SESSIONID`
  - Parameters：
    - `type`
    - `data`
  - Response：

  ```
{
    "errcode": 0,
    "data": {
        "level": 0,
        "nickname": "510371537",
        "userid": "jc0a9hkbiyq4"
    },
    "errinfo": ""
}
  ```

  - Example：

  ```
  Example
  ```


## 用户信息更新

  - URI：`/v1/user/update`
  - Method：`POST`
  - Header：
    - `X-QS-PEERID`
    - `X-QS-SESSIONID`
  - Parameters：
    - `type`
    - `data`
  - Response：

  ```
{
    "errcode": 0,
    "data": {},
    "errinfo": ""
}
  ```

  - Example：

  ```
  Example
  ```


TODO
---



如何运行
---
  方法1: 本地工程直接run XStarter的main方法即可启动；  
  方法2: 服务器通过git/svn更新最新代码，采用maven命令： mvn clean install -Dmaven.test.skip=ture -P pro 打包，根据不同参数(test/pro)可以应用相应的application.properties进行打包，执行java -jar xnettey-http-server.jar即可启动；


License
---
  GPLv2.  

  [1]: https://github.com/netty/netty

