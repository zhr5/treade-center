package controller;

import entity.OrderItem;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommunityOrderValidator {

    @Autowired
    private StockService stockService;

    @Autowired
    private PropertyServiceClient propertyService;

    /**
     * 校验社区商品库存
     */
    public void validateStock(List<OrderItem> items) {
        items.forEach(item -> {
            int available = stockService.getAvailableStock(item.getSkuId());
            if (available < item.getQuantity()) {
                throw new BizException("STOCK_NOT_ENOUGH",
                        String.format("SKU:%s 库存不足，剩余%d件", item.getSkuId(), available));
            }
        });
    }

    /**
     * 校验自提柜可用性
     */
    public void checkPickupLocker(Map<String, Object> extData) {
        String lockerCode = (String) extData.get("pickupLockerCode");
        if (StringUtils.isNotEmpty(lockerCode)) {
            boolean available = propertyService.checkLockerAvailable(lockerCode);
            if (!available) {
                throw new BizException("LOCKER_OCCUPIED", "自提柜已被占用");
            }
        }
    }
}
