-- 会员平台
1.会员表
2.会员等级表
3.会员积分表
4.会员优惠券表
5.会员地址表
6.会员收藏表
7.会员订单表
8.会员购物车表
9.会员评论表
10.会员退款表
11.会员售后表
12.会员投诉表
13.会员举报表
14.会员私信表
15.会员消息表
16.会员通知表
17.会员签到表
18.会员分享表
19.会员点赞表
20.会员关注表
21.会员浏览表
22.会员搜索表
23.会员收藏夹表

-- 商品平台
    1.商品表
    2.商品分类表
    3.商品品牌表
    4.商品规格表
    5.商品属性表
    6.商品SKU表
    7.商品图片表
    8.商品视频表
    9.商品评论表

-- 订单平台
    1.订单表
    2.订单商品表
    3.订单物流表
    4.订单支付表
    5.订单退款表
    6.订单售后表
    10.订单消息表
    11.订单通知表

-- 支付平台
    1.支付表
    2.支付订单表
    3.支付退款表
    4.支付日志表
    5.支付回调表
    6.支付通知表
    7.支付日志表
    8.支付统计表
    9.支付配置表

-- 物流平台
    1.物流表
    2.物流订单表
    3.物流轨迹表
    4.物流配置表
    5.物流模板表
    6.物流推送表
    7.物流统计表
    8.物流查询表
    9.物流轨迹表
    10.物流推送表
    11.物流统计表
    12.物流查询表
-- 物流平台
-- 售后平台
-- 积分平台
-- 优惠券平台
-- 营销平台
-- 分销平台
-- 社区平台
-- 社交平台
-- 游戏平台
-- 物联网平台
-- 人工智能平台

-- 用户表
CREATE TABLE `user`
(
    `user_id`   VARCHAR(64) PRIMARY KEY COMMENT '用户id',
    -- 其他用户属性字段...
    CREATE_TIME DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UPDATE_TIME DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = InnoDB CHARSET = utf8mb4 COMMENT '用户表';

-- 卖家表
CREATE TABLE `seller`
(
    `seller_id` VARCHAR(64) PRIMARY KEY COMMENT '卖家id',
    -- 其他卖家属性字段...
    CREATE_TIME DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UPDATE_TIME DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = InnoDB CHARSET = utf8mb4 COMMENT '卖家表';

-- 店铺表
CREATE TABLE `shop`
(
    `shop_id`   VARCHAR(64) PRIMARY KEY COMMENT '店铺id',
    `seller_id` VARCHAR(64) NOT NULL COMMENT '卖家id',
    -- 其他店铺属性字段...
    CREATE_TIME DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UPDATE_TIME DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = InnoDB CHARSET = utf8mb4 COMMENT '店铺表';

-- 商品表
CREATE TABLE `product`
(
    `product_id`   VARCHAR(64) PRIMARY KEY COMMENT '商品id',
    `skuid`        VARCHAR(64) NOT NULL COMMENT '商品唯一标识',
    `product_name` VARCHAR(255) COMMENT '商品名称',
    -- 其他商品属性字段...
    UNIQUE KEY `uk_skuid` (`skuid`),
    CREATE_TIME    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UPDATE_TIME    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = InnoDB CHARSET = utf8mb4 COMMENT '商品表';

-- 交易主订单表
CREATE TABLE `trade_main_order`
(
    `main_order_no` VARCHAR(64) PRIMARY KEY COMMENT '主订单号',
    `user_id`       VARCHAR(64) NOT NULL COMMENT '买家id',
    `seller_id`     VARCHAR(64) NOT NULL COMMENT '卖家id',
    `shop_id`       VARCHAR(64) NOT NULL COMMENT '店铺id',
    -- 其他主订单属性字段...
    CREATE_TIME     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UPDATE_TIME     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = InnoDB CHARSET = utf8mb4 COMMENT '交易主订单表';

-- 交易子订单表
CREATE TABLE `trade_sub_order`
(
    `sub_order_no`  VARCHAR(64) PRIMARY KEY COMMENT '子订单号',
    `main_order_no` VARCHAR(64) NOT NULL COMMENT '主订单号',
    `product_id`    VARCHAR(64) NOT NULL COMMENT '商品id',
    `skuid`         VARCHAR(64) NOT NULL COMMENT '商品唯一标识',
    -- 其他子订单属性字段...
    CREATE_TIME     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UPDATE_TIME     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = InnoDB CHARSET = utf8mb4 COMMENT '交易子订单表';

-- 购物车表
CREATE TABLE `shopping_cart`
(
    `cart_id`      VARCHAR(64) PRIMARY KEY COMMENT '购物车id',
    `user_id`      VARCHAR(64) NOT NULL COMMENT '买家id',
    `product_list` TEXT COMMENT '商品列表（JSON格式存储）',
    -- 其他购物车属性字段...
    CREATE_TIME    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UPDATE_TIME    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = InnoDB CHARSET = utf8mb4 COMMENT '购物车表';

-- 售后订单表
CREATE TABLE `after_sale_order`
(
    `after_sale_id` VARCHAR(64) PRIMARY KEY COMMENT '售后订单号',
    `main_order_no` VARCHAR(64) COMMENT '关联主订单号',
    `sub_order_no`  VARCHAR(64) COMMENT '关联子订单号',
    -- 其他售后订单属性字段...
    CREATE_TIME     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UPDATE_TIME     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = InnoDB CHARSET = utf8mb4 COMMENT '售后订单表';

-- 支付订单表
CREATE TABLE `payment_order`
(
    `payment_order_no` VARCHAR(64) PRIMARY KEY COMMENT '支付订单号',
    `main_order_no`    VARCHAR(64) NOT NULL COMMENT '主订单号',
    `payment_amount`   DECIMAL(10, 2) COMMENT '支付金额',
    -- 其他支付订单属性字段...
    CREATE_TIME        DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UPDATE_TIME        DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = InnoDB CHARSET = utf8mb4 COMMENT '支付订单表';

-- 物流发货订单表
CREATE TABLE `logistics_ship_order`
(
    `ship_order_no` VARCHAR(64) PRIMARY KEY COMMENT '物流订单号',
    `main_order_no` VARCHAR(64) COMMENT '主订单号',
    `sub_order_no`  VARCHAR(64) COMMENT '子订单号',
    -- 其他物流订单属性字段...
    CREATE_TIME     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UPDATE_TIME     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = InnoDB CHARSET = utf8mb4 COMMENT '物流发货订单表';

