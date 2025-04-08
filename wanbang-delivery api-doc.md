## 项目介绍
<font style="color:rgb(30, 30, 30);">针对万邦管理系统，开发了关键的订单调度模块，完整交付了管理端（订单指派、监控）和司机端（接单、状态更新）的后端服务。关键性地，为管理端后台设计了支持多数据库实例连接的架构，显著提升了系统的可扩展性和部署灵活性。</font>

## 项目技术栈
<font style="color:rgb(30, 30, 30);">SpringBoot + SpringMVC + MyBatis-Plus + MySQL + Redis + Redisson + Swagger + RabbitMQ + SpringTask</font>

## 项目主要业务
+ <font style="color:rgb(30, 30, 30);">项目架构：采用 SpringBoot 构建，包含管理端与司机端后台。管理端实现了多数据源连接能力，通过为不同数据库实例配置独立的 </font>**<font style="color:rgb(30, 30, 30);">DataSource </font>**<font style="color:rgb(30, 30, 30);">和 </font>**<font style="color:rgb(30, 30, 30);">SqlSessionFactory</font>**<font style="color:rgb(30, 30, 30);">，并整合 </font>**<font style="color:rgb(30, 30, 30);">Seata </font>**<font style="color:rgb(30, 30, 30);">(DataSourceProxy) 进行分布式事务管理，支持复杂跨库业务。</font>
+ <font style="color:rgb(30, 30, 30);">用户认证：设计并实现了一套基于 SpringMVC 拦截器和 Redis 的用户认证方案，用于校验 Token 有效性并解析用户身份信息（用户ID、角色等）。利用 </font>**<font style="color:rgb(30, 30, 30);">ThreadLocal </font>**<font style="color:rgb(30, 30, 30);">在单次请求线程内传递用户上下文，避免了下游服务重复解析 Token 的开销。</font>
+ <font style="color:rgb(30, 30, 30);">实时调度：引入 </font>**<font style="color:rgb(30, 30, 30);">Redisson </font>**<font style="color:rgb(30, 30, 30);">分布式锁，有效解决了多个司机同时抢夺同一订单时可能出现的并发问题。</font>
+ <font style="color:rgb(30, 30, 30);">定时任务：利用 </font>**<font style="color:rgb(30, 30, 30);">SpringTask</font>**<font style="color:rgb(30, 30, 30);"> 与 </font>**<font style="color:rgb(30, 30, 30);">CRON 表达式</font>**<font style="color:rgb(30, 30, 30);">，自动化执行每周的司机认证状态更新任务。</font>
+ <font style="color:rgb(30, 30, 30);">事务管理：针对涉及跨库操作（如订单状态更新与库存扣减可能在不同库）的场景，引入分布式事务解决方案 Seata，使用其 AT 模式和 </font>**<font style="color:rgb(30, 30, 30);">@GlobalTransactional</font>**<font style="color:rgb(30, 30, 30);"> 注解确保跨服务、跨数据库操作的原子性和一致性。</font>
+ <font style="color:rgb(30, 30, 30);">异步任务处理：集成 </font>**<font style="color:rgb(30, 30, 30);">RabbitMQ </font>**<font style="color:rgb(30, 30, 30);">消息队列，将订单完成后触发的通知、统计等非核心业务逻辑进行异步化处理。</font>

## 数据库SQL
```sql
-- 创建万邦送货数据库
CREATE DATABASE IF NOT EXISTS `wanbang_delivery` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `wanbang_delivery`;

-- 司机信息表
CREATE TABLE IF NOT EXISTS `driver_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '司机ID',
  `name` VARCHAR(50) NOT NULL COMMENT '司机姓名',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
  `avatar` VARCHAR(255) COMMENT '头像URL',
  `audit_status` TINYINT DEFAULT 0 COMMENT '审核状态(0=未审核,1=已通过,2=已拒绝)',
  `work_status` TINYINT DEFAULT 3 COMMENT '工作状态(1=空闲,2=忙碌,3=离线)',
  `openid` VARCHAR(100) COMMENT '微信OpenID',
  `money` DECIMAL COMMENT '总金额',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_phone` (`phone`),
  UNIQUE KEY `uniq_openid` (`openid`),
  KEY `idx_status` (`work_status`),
  KEY `idx_audit_status` (`audit_status`)
) ENGINE=InnoDB COMMENT='司机信息表';

-- 司机审核记录表
CREATE TABLE IF NOT EXISTS `driver_audit_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `driver_id` BIGINT NOT NULL COMMENT '司机ID',
  `audit_status` TINYINT NOT NULL COMMENT '审核状态(0=未审核,1=已通过,2=已拒绝)',
  `audit_remark` VARCHAR(255) COMMENT '审核备注',
  `auditor` VARCHAR(50) COMMENT '审核人',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_driver_id` (`driver_id`)
) ENGINE=InnoDB COMMENT='司机审核记录表';

-- 配送订单表
CREATE TABLE IF NOT EXISTS `delivery_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `order_no` VARCHAR(30) NOT NULL COMMENT '订单编号',
  `driver_id` BIGINT COMMENT '司机ID',
  `customer_phone` VARCHAR(11) COMMENT '司机手机号',
  `delivery_address` VARCHAR(100) NOT NULL COMMENT '派送地址',
  `delivery_status` TINYINT DEFAULT 1 COMMENT '配送状态(1=待派送,2=待接单,3=配送中,4=已完成,5=已取消)',
  `delivery_fee` DECIMAL(10,2) DEFAULT 0.00 COMMENT '配送费用',
  `delivery_note` VARCHAR(500) COMMENT '配送备注',
  `goods_weight` DECIMAL(10,2) COMMENT '货物重量(吨)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_driver_id` (`driver_id`),
  KEY `idx_status` (`delivery_status`)
) ENGINE=InnoDB COMMENT='配送订单表';

CREATE TABLE IF NOT EXISTS `undo_log`
(
    `id`            BIGINT(20)   NOT NULL AUTO_INCREMENT,
    `branch_id`     BIGINT(20)   NOT NULL,
    `xid`           VARCHAR(100) NOT NULL,
    `context`       VARCHAR(128) NOT NULL,
    `rollback_info` LONGBLOB     NOT NULL,
    `log_status`    INT(11)      NOT NULL,
    `log_created`   DATETIME     NOT NULL,
    `log_modified`  DATETIME     NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8;
```

## 接口文档
### 管理端
localhost:8000/api/

#### 获取待派送订单列表 GET
URL: /delivery/orders  
方法: GET  
请求参数:  
page: 页码（默认1）  
size: 每页数量（默认10）  
orderNo: 订单编号（可选）  
返回示例:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 150,
    "records": [
      {
        "id": 10001,
        "orderNo": "WB2023112700001",
        "driverId": null,
        "customerPhone": "13812345678",
        "deliveryAddress": "上海市浦东新区张江高科技园区博云路2号",
        "deliveryStatus": 1,
        "deliveryFee": 15.00,
        "deliveryNote": "请轻拿轻放，谢谢",
        "goodsWeight": 0.5,
        "createTime": "2023-11-27 10:20:30",
        "updateTime": "2023-11-27 10:20:30"
      }
    ]
  }
}
```

#### 派送订单 POST
URL: /delivery/orders  
方法: POST  
请求体:

```json
{
  "orderId": 5001,
  "orderNo": "WB2023112700003",
  "customerPhone": "13612345678",
  "deliveryAddress": "上海市徐汇区虹桥路1号港汇恒隆广场",
  "deliveryNote": "送货前请电话联系",
  "goodsWeight": 0.8,
  "deliveryFee": 18.00
}
```

返回示例:

```json
{
  "code": 200,
  "message": "派送订单创建成功",
  "data": null
}
```

#### 获取派送状态 GET
URL: /delivery/orders/{id}/status  
方法: GET  
请求参数: 无																		  
返回示例:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "deliveryStatus": 2,
    "driverId": 5001,
    "driverName": "张三",
    "driverPhone": "13888888888"
  }
}
```

#### 更新派送状态 PUT
URL: /delivery/orders/{id}/status  
方法: PUT  
请求参数:

```json
{
  "status": 3,
  "driverId": 5001
}
```

响应示例:

```json
{
  "code": 200,
  "message": "更新状态成功",
  "data": true
}
```

#### 取消派送 POST
URL: /delivery/orders/{id}/cancel  
方法: POST  
请求参数:

```json
{
  "operatorId": 1001
}
```

响应示例:

```json
{
  "code": 200,
  "message": "取消派送成功",
  "data": true
}
```

#### 获取司机列表 GET
URL: /delivery/drivers  
方法: GET  
请求参数:  
page: 页码（默认1）  
size: 每页数量（默认10）  
name: 司机姓名（可选）  
phone: 司机电话（可选）  
auditStatus: 审核状态（可选）  
workStatus: 工作状态（可选）  
返回示例:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 50,
    "records": [
      {
        "id": 5001,
        "name": "张三",
        "phone": "13888888888",
        "avatar": "https://example.com/avatars/zhangsan.jpg",
        "auditStatus": 1,
        "workStatus": 1,
        "openid": "wx123456789",
        "money": "233.22",
        "createTime": "2023-10-15 09:30:00",
        "updateTime": "2023-11-20 15:45:22"
      }
    ]
  }
}
```

#### 审核司机资格 PUT
URL: /delivery/drivers/{id}/approval  
方法: PUT  
请求参数:

```json
{
  "auditStatus": 1,
  "auditRemark": "资料齐全，通过审核",
  "auditor": "admin"
}
```

响应示例:

```json
{
  "code": 200,
  "message": "审核通过成功",
  "data": true
}
```

#### 拒绝司机资格 PUT
URL: /delivery/drivers/{id}/rejection  
方法: PUT  
请求参数:

```json
{
  "auditStatus": 2,
  "auditRemark": "证件照片不清晰，请重新上传",
  "auditor": "admin"
}
```

响应示例:

```json
{
  "code": 200,
  "message": "审核拒绝成功",
  "data": true
}
```

#### 删除司机 DELETE
URL: /delivery/drivers/{id}/delete  
方法: DELETE  
请求参数:

```json
{
  "auditor": "admin"
}
```

响应示例:

```json
{
  "code": 200,
  "message": "司机删除成功",
  "data": true
}
```

#### 清零司机 POST
URL: /delivery/drivers/{id}/reset-money  
方法: POST  
请求参数:

```json
{
  "auditor": "admin"
}
```

响应示例:

```json
{
  "code": 200,
  "message": "司机清零成功",
  "data": true
}
```

### 司机端
localhost:8001/api/

#### 认证相关API
##### 司机登录 POST
+ **URL**: `/api/driver/auth/login`
+ **方法**: `POST`
+ **请求参数**:

```json
{
  "code": "微信小程序登录凭证", 
  "phone": "13800138000"
}
```

+ **返回示例**:

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "id": 1,
    "name": "张师傅",
    "phone": "13800138000",
    "avatar": "https://example.com/avatar.jpg",
    "audit_status": 1,
    "work_status": 1
  }
}
```

##### 司机退出登录 POST
+ **URL**: `/api/driver/auth/logout/{id}`
+ **方法**: `POST`
+ **返回示例**:

```json
{
  "code": 200,
  "message": "退出成功",
  "data": null
}
```

##### 获取审核状态 GET
+ **URL**: `/api/driver/auth/audit-status/{id}`
+ **方法**: `GET`
+ **返回示例**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": 1
}
```

#### 司机相关API		
##### 获取司机信息 GET
+ **URL**: `/api/driver/info/{id}`
+ **方法**: `GET`
+ **返回示例**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "name": "张师傅",
    "phone": "13800138000",
    "avatar": "https://example.com/avatar.jpg",
    "auditStatus": 1,
    "workStatus": 1
  }
}
```

##### 更新司机信息 POST
+ **URL**: `/api/driver/update-info/{id}`
+ **方法**: `POST`
+ **请求参数**:

```json
{
  "name": "张师傅",
}
```

+ **返回示例**:

```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

##### 更新工作状态 POST
+ **URL**: `/api/driver/status/{id}`
+ **方法**: `POST`
+ **请求参数**:

```json
{
  "workStatus": 1  // 1=空闲,2=忙碌,3=离线
}
```

+ **返回示例**:

```json
{
  "code": 200,
  "message": "状态更新成功",
  "data": null
}
```

##### 更新位置信息 POST
+ **URL**: `/api/driver/location/{id}`
+ **方法**: `POST`
+ **请求参数**:

```json
{
  "latitude": 31.230416,
  "longitude": 121.473701
}
```

+ **返回示例**:

```json
{
  "code": 200,
  "message": "位置更新成功",
  "data": null
}
```

##### 获取钱包信息 GET
+ **URL**: `/api/driver/wallet/{id}`
+ **方法**: `GET`
+ **请求参数**: 无
+ **返回示例**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": 1250.00
}
```

#### 订单相关API
##### 获取订单列表 GET
+ **URL**: `/api/order/list`
+ **方法**: `GET`
+ **请求参数**: 
    - `status`: 订单状态（可选）
    - `page`: 页码（默认1）
    - `size`: 每页数量（默认10）
+ **返回示例**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "total": 25,
    "list": [
      {
        "id": 1,
        "orderNo": "D20230315001",
        "deliveryAddress": "上海市浦东新区张江高科技园区",
        "deliveryStatus": 3,
        "deliveryFee": 55.00,
        "goodsWeight": 5.0,
        "createTime": "2023-03-15 10:30:00"
      }
    ]
  }
}
```

##### 接单 POST
+ 接口：`/api/order/accept`
+ 方法：POST
+ 请求参数：

```json
{
  "orderId": 2,
  "driverLatitude": 31.220416,
  "driverLongitude": 121.463701,
  "deliveryAddress": "上海市浦东新区陆家嘴金融贸易区",
  "deliveryLatitude": 31.238109,
  "deliveryLongitude": 121.501643
}
```

+ 返回示例：

```json
{
  "code": 200,
  "message": "接单成功",
  "data": null
}
```



##### 完成订单 POST
+ **URL**: `/api/order/complete`
+ **方法**: `POST`
+ **请求参数**:

```json
{
  "orderId": 2
}
```

+ **返回示例**:

```json
{
  "code": 200,
  "message": "订单完成",
  "data": null
}
```

##### 取消订单 POST
+ **URL**: `/api/order/cancel`
+ **方法**: `POST`
+ **请求参数**:

```json
{
  "orderId": 2,
  "cancelReason": "货物信息有误"
}
```

+ **返回示例**:

```json
{
  "code": 200,
  "message": "订单已取消",
  "data": null
}
```

#### 地图相关API
##### 路线规划 GET
+ **URL**: `/api/map/route`
+ **方法**: `GET`
+ **请求参数**: 
    - `fromLat`: 起点纬度
    - `fromLng`: 起点经度
    - `toLat`: 终点纬度
    - `toLng`: 终点经度
+ **返回示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "distance": 12500,
    "duration": 1800,
    "polyline": [
      [121.473701, 31.230416],
      [121.480610, 31.230987],
      // ... 更多路径点
      [121.585426, 31.210516]
    ]
  }
}
```

