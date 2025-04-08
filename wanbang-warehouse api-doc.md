## 项目介绍
<font style="color:rgb(30, 30, 30);">开发并维护了一个高效的库存与配送管理平台，专为瓷砖、洁具等建材行业设计。该系统旨在帮助店家实现精准的仓储管理（包括型号、位置、库存查询）和智能化的注册司机管理，以优化配送流程，提升整体运营效率。</font>

## 项目技术栈
<font style="color:rgb(30, 30, 30);">SpringBoot + SpringMVC + MyBatis-Plus + MySQL + Redis + Swagger + MinIO + Sa-Token</font>

## 项目主要业务
+ <font style="color:rgb(30, 30, 30);">权限管理: 基于 Sa-Token 框架设计并实现了系统的无状态认证与授权机制，支持动态权限校验，保障系统安全性。</font>
+ <font style="color:rgb(30, 30, 30);">存储优化：负责设计数据存储方案，利用 MySQL 存储核心业务结构化数据，并集成 MinIO 对象存储服务，用于高效管理非结构化文件。</font>
+ <font style="color:rgb(30, 30, 30);">API 友好性：集成 Swagger 工具，自动生成并维护 RESTful API 文档，提升了前后端及团队间的协作效率。</font>
+ <font style="color:rgb(30, 30, 30);">库存一致性：引入并实现了库存冲正（</font>**<font style="color:rgb(30, 30, 30);">Reversal</font>**<font style="color:rgb(30, 30, 30);">）机制，结合数据库事务，确保在人为异常操作时库存数据能够准确回滚，维护数据一致性。</font>
+ <font style="color:rgb(30, 30, 30);">事务管理：应用 Spring 的 </font>**<font style="color:rgb(30, 30, 30);">@Transactional</font>**<font style="color:rgb(30, 30, 30);"> 注解，精细化管理服务层方法，确保涉及多表操作的业务逻辑具有原子性，通过 rollbackFor = Exception.class 保证异常时数据正确回滚。</font>
+ <font style="color:rgb(30, 30, 30);">数据缓存：运用 </font>**<font style="color:rgb(30, 30, 30);">Redis </font>**<font style="color:rgb(30, 30, 30);">作为缓存中间件，缓存热点数据（如商品信息、用户信息等），显著降低数据库访问压力，有效提升了系统查询性能和响应速度。</font>

## 数据库SQL
```sql
CREATE DATABASE IF NOT EXISTS `wanbang_db` 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_general_ci;

USE `wanbang_db`;

-- 系统用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '登录账号',
  `password` VARCHAR(100) NOT NULL COMMENT '加密密码',
  `phone` VARCHAR(20) DEFAULT '' COMMENT '手机号',
  `avatar` VARCHAR(255) DEFAULT '' COMMENT '头像URL',
  `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标记',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_username` (`username`),
  UNIQUE KEY `uniq_phone` (`phone`)
) ENGINE=InnoDB COMMENT='系统用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `role_key` VARCHAR(100) NOT NULL COMMENT '角色标识',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '角色描述',
  `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标记',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_role_key` (`role_key`)
) ENGINE=InnoDB COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`),
  KEY `idx_role_id` (`role_id`),
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='用户角色关联表';
-- 瓷砖库存表
CREATE TABLE IF NOT EXISTS `inventory_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '库存ID',
  `model_number` VARCHAR(50) NOT NULL COMMENT '产品型号',
  `manufacturer` VARCHAR(50) NOT NULL COMMENT '制造厂商',
  `specification` VARCHAR(20) COMMENT '规格（如：600x600mm）',
  `surface` TINYINT COMMENT '表面处理（1=抛光 2=哑光 3=釉面 4=通体大理石 5=微晶石 6=岩板）',
  `category` TINYINT COMMENT '分类（1=墙砖 2=地砖 3=胶 4=洁具）',
  `warehouse_num` SMALLINT NOT NULL COMMENT '仓库编码',
  `total_pieces` INT NOT NULL DEFAULT 0 COMMENT '总片数',
  `price_per_piece` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '单片价格（单位：元）',
  `pieces_per_box` INT  DEFAULT 1 COMMENT '每箱片数',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_model_warehouse` (`model_number`, `warehouse_num`),
  KEY `idx_model_number` (`model_number`),
  CONSTRAINT `chk_pieces` CHECK (`total_pieces` >= 0 AND `pieces_per_box` > 0)
) ENGINE=InnoDB COMMENT='瓷砖库存表';

-- 库存操作日志表
CREATE TABLE IF NOT EXISTS `inventory_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `inventory_item_id` BIGINT NOT NULL COMMENT '库存项ID',
  `operation_type` TINYINT NOT NULL COMMENT '操作类型（1=入库 2=出库 3=调拨 4=冲正）',
  `quantity_change` INT NOT NULL COMMENT '数量变化',
  `operator_id` BIGINT NOT NULL COMMENT '操作人ID',
  `source_warehouse` SMALLINT COMMENT '源仓库编码',
  `target_warehouse` SMALLINT COMMENT '目标仓库编码',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '操作备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_inventory_item_id` (`inventory_item_id`),
  CONSTRAINT `fk_inventory_log_item` FOREIGN KEY (`inventory_item_id`) REFERENCES `inventory_item` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_inventory_log_operator` FOREIGN KEY (`operator_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='库存操作日志表';
-- 订单主表
CREATE TABLE IF NOT EXISTS `order_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID（主键）',
  `order_no` VARCHAR(30) NOT NULL UNIQUE COMMENT '订单编号（业务编号，雪花算法生成）',
  `customer_phone` VARCHAR(20) DEFAULT '' COMMENT '客户手机号',
  `operator_id` BIGINT COMMENT '操作人ID',
  `order_remark` VARCHAR(500) DEFAULT NULL COMMENT '订单备注',
  `total_amount` DECIMAL(10,2) NOT NULL COMMENT '原始订单总金额',
  `adjusted_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '调整后的总金额（多退少补后）',
  `aftersale_status` TINYINT DEFAULT NULL COMMENT '售后状态（1=新建 2=已解决）',
    `dispatch_status` INT DEFAULT 0 COMMENT '订单派送状态：0：未派送 1：派送中 2：派送完成',
  `order_create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '订单创建时间',
  `order_update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '订单更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_order_no` (`order_no`),
  KEY `idx_order_time` (`order_create_time`),
  KEY `idx_aftersale_status` (`aftersale_status`, `order_create_time`),
  KEY `idx_customer_phone` (`customer_phone`, `order_create_time`),
  CONSTRAINT `fk_order_operator` FOREIGN KEY (`operator_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB COMMENT='订单主表';

-- 订单项表
CREATE TABLE IF NOT EXISTS `order_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单项ID（主键）',
  `order_id` BIGINT NOT NULL COMMENT '订单ID（外键关联order_info表）',
  `item_id` BIGINT NOT NULL COMMENT '库存商品ID（外键关联inventory_item表）',
  `model_number` VARCHAR(50) NOT NULL COMMENT '产品型号',
  `quantity` INT NOT NULL COMMENT '原始购买数量',
  `price_per_piece` DECIMAL(10,2) NOT NULL COMMENT '单价',
  `subtotal` DECIMAL(10,2) NOT NULL COMMENT '小计金额',
  `adjusted_quantity` INT DEFAULT NULL COMMENT '调整后的数量',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '订单更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  CONSTRAINT `fk_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `order_info` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_order_item_item` FOREIGN KEY (`item_id`) REFERENCES `inventory_item` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='订单项表';

-- 订单售后日志表
CREATE TABLE IF NOT EXISTS `order_aftersale_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `order_item_id` BIGINT NOT NULL COMMENT '订单项ID',
  `aftersale_type` TINYINT NOT NULL COMMENT '售后类型（1=买多退货退款 2=买少补货补款）',
  `aftersale_status` TINYINT NOT NULL COMMENT '售后状态（1=新建 2=已解决）',
  `quantity_change` INT NOT NULL COMMENT '数量变化（负数表示退货，正数表示补货）',
  `amount_change` DECIMAL(10,2) NOT NULL COMMENT '金额变化（负数表示退款，正数表示补款）',
  `resolution_result` TEXT DEFAULT NULL COMMENT '处理结果说明',
  `aftersale_operator` BIGINT DEFAULT NULL COMMENT '售后处理人ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_item_id` (`order_item_id`),
  CONSTRAINT `fk_aftersale_order_id` FOREIGN KEY (`order_id`) REFERENCES `order_info` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_aftersale_order_item` FOREIGN KEY (`order_item_id`) REFERENCES `order_item` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_aftersale_log_operator` FOREIGN KEY (`aftersale_operator`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB COMMENT='订单售后日志表';

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

START TRANSACTION; 

-- 角色数据
INSERT INTO `sys_role` (`role_name`, `role_key`, `description`) 
VALUES 
('超级管理员', 'admin', '系统管理员'), 
('普通员工', 'employee', '基础权限用户') 
ON DUPLICATE KEY UPDATE `update_time` = NOW(); 

-- 用户数据 - 使用MD5加密密码 (admin的MD5值为: 21232f297a57a5a743894a0e4a801fc3)
INSERT INTO `sys_user` (`username`, `password`, `phone`) 
VALUES 
('admin', '21232f297a57a5a743894a0e4a801fc3', '13800138000') 
ON DUPLICATE KEY UPDATE `password` = '21232f297a57a5a743894a0e4a801fc3', `update_time` = NOW(); 

-- 用户与角色关联
INSERT INTO `sys_user_role` (`user_id`, `role_id`) 
SELECT 
    (SELECT `id` FROM `sys_user` WHERE `username` = 'admin'), 
    (SELECT `id` FROM `sys_role` WHERE `role_key` = 'admin') 
FROM DUAL 
WHERE 
    EXISTS (SELECT 1 FROM `sys_user` WHERE `username` = 'admin') 
    AND EXISTS (SELECT 1 FROM `sys_role` WHERE `role_key` = 'admin') 
ON DUPLICATE KEY UPDATE `user_id` = `user_id`;


COMMIT;
```

## 接口文档
### 用户登录相关接口 /api/auth/
#### 用户登录 POST
LoginReq  LoginResp

**URL**:  /api/auth/login  
**请求体**

```json
{
  "username": "admin",
  "password": "123456"
}
```

**响应体**

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
      "id": 1,
      "role_key": "admin",
      "username": "admin",
      "avatar": "http://example.com/avatar.jpg",
      "phone": "13800138000"
  }
}
```

#### 用户注册 POST
RegisterReq

**URL**: /api/auth/register  
**请求体**

```json
{
  "username": "newuser",
  "password": "newpassword123",
  "phone": "13800138001"
}
```

#### 用户重置密码 POST
ForgetPasswordReq



**URL**: /api/auth/reset-password  
**描述**: 用户通过用户名和手机号重置密码，无需登录状态。  
**请求体**

```json
{
  "username": "admin",
  "phone": "13800138000",
  "newPassword": "newpassword456"
}
```

#### 用户登出 POST
**URL**: /api/auth/logout  

### 用户信息相关接口 /api/users
#### 修改用户信息 PUT
UserInfoChangeReq  

**URL**: /api/users/{id}  
**请求体**

```json
{
  "avatar": "xxxxx",          
  "username": "xxxxx",      
  "phone": "123123",        
  "oldPassword": "123123",   
  "password": "123123"       
}
```

#### 查询所有用户 GET
UserListResp

**URL**: /api/users  
**请求参数**:

+ page (整数, 可选, 默认1): 当前页码
+ size (整数, 可选, 默认10): 每页条数

**响应体**

```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "total": 5,
    "page": 1,
    "size": 10,
    "items": [
      {
        "id": 1,
        "username": "1",
        "phone": "2222",
        "role_key": "admin",
        "description": "系统管理员"
      },
      {
        "id": 2,
        "username": "ad",
        "phone": "1111",
        "role_key": "admin",
        "description": "系统管理员"
      }
    ]
  }
}
```

#### 删除用户 DELETE （admin）
**URL**: /api/users/{id}  

#### 上传用户头像 POST
**URL**: /api/upload  
**请求参数**:

```java
@RequestParam("file") MultipartFile file
```

**响应体**

```json
{
  "code": 200,
  "message": "上传 URL 创建成功",
  "data": {
    "fileUrl": "http://minio.example.com/files/avatar.jpg"               
  }
}
```

### 销量相关接口 /api/sales/
#### 获取最火爆卖品数据 GET
TopSoldItemsResp

**URL**: /api/sales/top-products  
**描述**: 获取销量最高的产品列表（默认前 5 名）。  
**响应体**

```json
{
  "code": 200,
  "message": "成功",
  "data": [
    { "model_number": "MODEL-A", "sales": 500},
    { "model_number": "MODEL-B", "sales": 450},
    { "model_number": "MODEL-C", "sales": 300},
    { "model_number": "MODEL-D", "sales": 200},
    { "model_number": "MODEL-E", "sales": 150}
  ]
}
```

#### 获取销售趋势数据 GET
SalesTrendResp

**URL**: /api/sales/trend/{year}/{month}/{length}  
**描述**: 获取一段时间内(length)的销售趋势数据（如月度销量）纵坐标包括销售量和金额量。    

**响应体**

```json
{
  "code": 200,
  "message": "成功",
  "data": [
  {
    "dates": "2025-02",
    "salesValues": 2333,
    "amounts": 22333
  },
  {
    "dates": "2025-01",
    "salesValues": 2333,
    "amounts": 22333
  },
  {
    "dates": "2024-12",
    "salesValues": 2333,
    "amounts": 22333
  },
  {
    "dates": "2024-11",
    "salesValues": 2333,
    "amounts": 223334
  },
  ]
}
```

#### 获取今日销售金额 GET
TodaySaleAmountResp

**URL**: /api/sales/today-amount  
**响应体**

```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "today_amount": 5000
  }
}
```

#### 获取订单总销售金额 GET
TotalSaleAmountResp

**URL**: /api/sales/total-amount  
**响应体**

```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "total_amount": 150000
  }
}
```

### 查询库存物品相关接口 /api/inventory
#### 查询库存列表 GET
InventoryItemsResp

**URL**: /api/inventory/items

**描述**: 获取库存列表，支持分页和条件筛选。

**请求参数**:

+ page (整数, 可选, 默认1): 当前页码
+ size (整数, 可选, 默认10): 每页条数
+ category (整数, 可选): 产品分类 (1=墙砖, 2=地砖)
+ surface (整数, 可选): 表面处理 (1=抛光, 2=哑光, 3=釉面, 4=通体大理石, 5=微晶石, 6=岩板)

**响应体**

```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "total": 100,
    "page": 1,
    "size": 10,
    "items": [
      {
        "id": 1,
        "model_number": "TB6001",
        "manufacturer": "瓷都",
        "specification": "600x600mm",
        "surface": 1,
        "category": 2,
        "warehouse_num": 1,
        "total_pieces": 1000,
        "price_per_piece": 25.00,
        "pieces_per_box": 4,
        "remark": "首批入库",
        "create_time": "2025-02-21 10:00:00",
        "update_time": "2025-02-21 10:00:00"
      }
    ]
  }
}
```

#### 修改库存物品 PUT
InventoryItemsChangeReq  

**URL**: /api/inventory/items/{id}

**描述**: 修改指定库存项的所有字段。

**请求体**

```json
{
  "model_number": "TB6001",
  "manufacturer": "瓷都",
  "specification": "600x600mm",
  "surface": 1,
  "category": 2,
  "warehouse_num": 1,
  "total_pieces": 1000,
  "price_per_piece": 25.00,
  "pieces_per_box": 4,
  "remark": "修改后的备注"
}
```

#### 删除库存物品 DELETE （admin）
**URL**: /api/inventory/items/{id}

**描述**: 删除指定库存项。

#### 自动回填商品项Id GET
**URL**: /api/inventory/items/model/{modelNumber}

**描述**: 自动回填产品id

请求参数

+ modelNumber String

响应体

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "total_pieces": 2000,
        "item_id": 1
    }
}
```

### 出入调库相关接口 /api/logs
#### 查询记录 GET
InventoryLogResp

**URL**: /api/logs/

**描述**: 获取记录列表，支持分页和时间筛选。

**请求参数**:

+ page (整数, 可选, 默认1): 当前页码
+ size (整数, 可选, 默认10): 每页条数
+ operation_type (整数, 必填): 操作类型
+ start_time (日期时间, 可选): 开始时间 (如: 2025-02-01 00:00:00)
+ end_time (日期时间, 可选): 结束时间 (如: 2025-02-22 23:59:59)

**响应体**

```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "total": 50,
    "page": 1,
    "size": 10,
    "items": [
      {
        "id": 1,
        "inventory_item_id": 1,
        "operation_type": 1,
        "quantity_change": 1000,
        "operator_id": 1,
        "source_warehouse": 1,
        "target_warehouse": null,
        "remark": "初次入库",
        "create_time": "2025-02-01 00:00:00 ",
        "update_time": "2025-02-22 23:59:59 "
      }
    ]
  }
}
```

#### 提交入库记录 POST
PostInboundReq

**URL**: /api/logs/inbound



**描述**: 创建新的库存记录，通过传入的类型码判断。operation_type手动设置

请求体

```json
{
  "operator_id": 1,
  "model_number": "TB6002",
  "manufacturer": "瓷都",
  "specification": "800x800mm",
  "warehouse_num": 2,
  "category": 2,
  "surface": 2,
  "total_pieces": 600,
  "pieces_per_box": 3,
  "price_per_piece": 30.00,
  "remark": "默认"
}
```

#### 提交调库记录 POST
PostTransferReq

**URL**: /api/logs/transfer

**描述**: 创建新的库存记录，通过传入的类型码判断。operation_type手动设置

请求体

```json
{
  "inventory_item_id": 1,
  "operator_id": 1,
  "source_warehouse": 1,
  "target_warehouse": 1,
  "remark": "调整入库数量"
}
```

#### 修改记录 PUT
修改调库记录和入库记录时不需要输入不需要的参数 比如仓库号

InventoryLogChangeReq

**URL**: PUT /api/logs

请求参数

+ operation_type

请求体

```json
{
  "id": 1,
  "inventory_item_id": 1,
  "operator_id": 1,
  "operation_type": 1,
  "source_warehouse": 1,
  "target_warehouse": 1,
  "quantity_change": 1200,
  "remark": "调整入库数量"
}
```

#### 删除记录 DELETE （admin）
**URL**：/api/logs/{id}  

### 订单相关接口 /api/orders
#### 创建新订单 POST
OrderPostReq

**URL**: /api/orders

**描述**: 创建新订单，支持多种产品。

**请求体**

```json
{
  "customer_phone": "13912345678",
  "operator_id": 1,
  "order_remark": "客户批量订单",
  "total_amount": 4750.00,
  "items": [
    {
      "model_number": "TB6001",
      "item_id": 1,
      "quantity": 50,
      "price_per_piece": 25.00,
      "subtotal": 1250.00,
      "source_warehouse": 3
    },
    {
      "model_number": "TB8001",
      "item_id": 2,
      "quantity": 100,
      "price_per_piece": 35.00,
      "source_warehouse": 2,
      "subtotal": 3500.00
    }
  ]
}
```

**响应体**

```json
{
  "code": 201,
  "message": "订单创建成功",
  "data": {
%%     "id": 1,
    "order_no": "ORD202503020001",
    "total_amount": 5000.00,
    "items_count": 3 %%
  }
}
```

#### 查询订单列表 GET
OrderListResp

**URL**: /api/orders

**描述**: 获取订单列表，支持分页和筛选。

**请求参数**:

+ page (整数, 可选, 默认1): 当前页码
+ size (整数, 可选, 默认10): 每页条数
+ start_time (日期时间, 可选): 开始时间
+ end_time (日期时间, 可选): 结束时间
+ customer_phone (字符串, 可选): 客户手机号

**响应体**

```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "total": 20,
    "page": 1,
    "size": 10,
    "items": [
      {
        "id": 1,
        "order_no": "ORD202503020001",
        "customer_phone": "13912345678",
        "operator_id": 1,
        "total_amount": 5000.00,
        "adjusted_amount": 4800.00,
        "items_count": 3,
        "order_remark": "客户批量订单",
        "order_create_time": "2025-03-02 10:00:00",
        "order_update_time": "2025-03-02 12:00:00",
        "aftersale_status": 1
      },
      {
        "id": 2,
        "order_no": "ORD2025030200012",
        "customer_phone": "13912345678",
        "operator_id": 1,
        "total_amount": 5000.00,
        "adjusted_amount": 4800.00,
        "items_count": 3,
        "order_remark": "客户批量订单",
        "order_create_time": "2025-03-02 10:00:00",
        "order_update_time": "2025-03-02 12:00:00",
        "aftersale_status": 1
      }
    ]
  }
}
```

#### 查询订单详情 GET
OrderDetileResp

**URL**: /api/orders/{id}

**描述**: 获取指定订单的详细信息，包括所有订单项。

**响应体**

```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "id": 1,
    "order_no": "ORD202503020001",
    "customer_phone": "13912345678",
    "operator_id": 1,
    "total_amount": 5000.00,
    "adjusted_amount": 4800.00,
    "order_remark": "客户批量订单",
    "order_create_time": "2025-03-02 10:00:00",
    "order_update_time": "2025-03-02 12:00:00",
    "aftersale_status": 1,
    "items": [
      {
        "id": 1,
        "item_id": 1,
        "model_number": "TB6001",
        "specification": "600x600mm",
        "manufacturer": "瓷都",
        "quantity": 50,
        "price_per_piece": 25.00,
        "subtotal": 1250.00,
        "adjusted_quantity": 50
      },
      {
        "id": 2,
        "item_id": 2,
        "model_number": "TB8001",
        "specification": "800x800mm",
        "manufacturer": "瓷都",
        "quantity": 100,
        "price_per_piece": 35.00,
        "subtotal": 3500.00,
        "adjusted_quantity": 95
      },
      {
        "id": 3,
        "item_id": 3,
        "model_number": "TB6002",
        "specification": "600x600mm",
        "manufacturer": "瓷都",
        "quantity": 10,
        "price_per_piece": 25.00,
        "subtotal": 250.00,
        "adjusted_quantity": 10
      }
    ]
  }
}
```

#### 修改订单信息 PUT
OrderChangeReq

**URL**: /api/orders/{id}

**描述**: 修改订单基本信息，不包括订单项的变更。

**请求体**

```json
{
  "customer_phone": "13912345678",
  "operator_id": 1,
  "order_remark": "修改后的订单备注"
}
```

#### 订单项变更 PUT
OrderItemChangeReq

**URL**: /api/orders/items/{itemId}

这里的id是子订单id price_per_piece原则上与另外两个无关

**描述**: 修改指定订单项的信息。

**请求体**

```json
{
  "quantity": 60,
  "price_per_piece": 23.50,
  "subtotal": 1410.00
}
```

#### 添加订单项 POST
**URL**: /api/orders/{orderId}/items

这里的id是orderId

**描述**: 向现有订单添加新的订单项。

**请求体**

```json
{
  "model_number": "TB7001",
  "item_id": 4,
  "quantity": 30,
  "price_per_piece": 30.00,
  "subtotal": 900.00
}
```

#### 删除订单项 DELETE
**URL**: /api/orders/items/{itemId}

**描述**: 删除指定订单项。

#### 删除订单 DELETE
**URL**: /api/orders/{orderId}

**描述**: 删除指定订单及其所有订单项。

### 售后相关接口 /api/aftersales
#### 创建售后 POST
AftersalePostReq

**URL**: /api/aftersales

**描述**: 创建售后记录。

**请求体**

```json
{
  "order_id": 1,
  "aftersale_type": 1,
  "aftersale_status": 1,
  "items": [
    {
      "order_item_id": 2,
      "quantity_change": -5,
      "amount_change": -175.00
    }
  ],
  "resolution_result": "客户部分退货",
  "aftersale_operator": 1
}
```

#### 售后状态说明
+ `aftersale_status`: 售后状态
    - `1`: 新建(初始状态)
    - `2`: 已解决
+ 状态流转规则：
    1. 售后记录创建时，状态必须为 `1`(新建)
    2. 只能由人工操作从 `1` 变更到 `2`(已解决)
    3. 不支持其他状态转换

#### 获取订单售后记录 GET
AftersaleLogsResp

**URL**: /api/aftersales/order/{orderId}

**描述**: 获取指定订单ID的所有售后记录历史。

**响应体**

```json
{ "code": 200,
  "message": "成功", 
  "data": [{
   "id": 1, 
   "order_id": 1, 
   "aftersale_type": 1, 
   "aftersale_status": 2, 
   "resolution_result": "客户部分退货", 
   "aftersale_operator": 1, 
   "create_time": "2025-03-04T19:57:44.000+08:00", 
   "order_item_id": 1, 
   "quantity_change": -5, 
   "amount_change": -175 }, { 
   "id": 2, 
   "order_id": 1, 
   "aftersale_type": 1, 
   "aftersale_status": 1, 
   "resolution_result": "客户部分退货", 
   "aftersale_operator": 1, 
   "create_time": "2025-03-04T19:57:57.000+08:00", 
   "order_item_id": 1, 
   "quantity_change": -5, 
   "amount_change": -175 
   }] 
}
```

#### 更新售后状态 PUT
UpdateAftersaleStatusReq

**URL**: /api/aftersales/{id}/status

**描述**: 更新售后记录的状态，例如从"新建"变更为"已解决"。

**请求参数**:

+ id (路径参数): 售后记录ID

更新派送系统

/api/{orderId}/dispatch_status

把派送状态设置为1

