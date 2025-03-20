package controller;

import constant.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.OrderService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/trade")
public class UnifiedOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CommunityOrderValidator communityValidator;

    /**
     * 统一下单接口（支持社区多场景）
     * @param request 下单请求参数
     * @return 统一响应格式
     */
    @PostMapping("/orders")
    public ApiResponse<OrderResult> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        // 1. 基础校验
        validateBaseRequest(request);

        // 2. 社区场景扩展校验
        if ("community_mall".equals(request.getBizType())) {
            communityValidator.validateStock(request.getItems());
            communityValidator.checkPickupLocker(request.getExtData());
        }

        // 3. 构建订单领域对象
        Order order = buildOrder(request);

        // 4. 保存订单 & 触发领域事件
        Order savedOrder = orderService.createOrder(order);
        eventPublisher.publishEvent(new OrderCreatedEvent(savedOrder));

        // 5. 创建支付预订单
        PaymentPrepayResult prepay = paymentService.createPrepay(savedOrder);

        return ApiResponse.success(new OrderResult(savedOrder, prepay));
    }

    // 构建订单领域对象（含社区扩展数据）
    private Order buildOrder(OrderCreateRequest request) {
        return Order.builder()
                .orderId(generateOrderId(request.getBizType()))
                .bizType(request.getBizType())
                .userId(request.getUserId())
                .amount(calculateTotalAmount(request.getItems()))
                .items(request.getItems())
                .extData(parseCommunityExtData(request))
                .status(OrderStatus.CREATED)
                .build();
    }

    // 社区扩展数据解析（示例）
    private Map<String, Object> parseCommunityExtData(OrderCreateRequest request) {
        Map<String, Object> ext = new HashMap<>();
        if ("community_mall".equals(request.getBizType())) {
            ext.put("pickupLockerCode", request.getExtParam("locker_code"));
            ext.put("communityId", request.getExtParam("community_id"));
        } else if ("property_fee".equals(request.getBizType())) {
            ext.put("paymentCycle", request.getExtParam("cycle"));
        }
        return ext;
    }
}
