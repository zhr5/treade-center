package entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItem {
    private String skuId;
    private String skuName;
    private int quantity;
    private BigDecimal price;
}
