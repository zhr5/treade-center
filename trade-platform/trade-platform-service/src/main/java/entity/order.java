package entity;

import constant.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class order {
    private String orderId;       // 全局唯一订单号（规则：社区ID+时间戳+序列）
    private String bizType;       // 业务类型（community_mall/property_fee等）
    private String userId;        // 用户中心统一ID
    private BigDecimal amount;    // 订单金额
    private OrderStatus status;   // 订单状态枚举
    private List<OrderItem> items;// 订单项列表
    private Map<String, Object> extData; // 社区扩展字段（JSON存储）

    // 社区特色字段（根据bizType动态解析）
    public String getCommunityId() {
        return (String) extData.getOrDefault("communityId", "");
    }

    public String getPickupLockerCode() {
        return (String) extData.getOrDefault("pickupLockerCode", "");
    }
}
