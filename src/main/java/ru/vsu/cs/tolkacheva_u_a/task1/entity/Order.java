package ru.vsu.cs.tolkacheva_u_a.task1.entity;

import ru.vsu.cs.tolkacheva_u_a.task1.ex.ValidationException;

import java.math.BigDecimal;
import java.util.UUID;

public class Order extends AbstractEntity {
    private final UUID productId;
    private final int quantity;
    private final BigDecimal totalPrice;
    private OrderStatus status = OrderStatus.NEW;

    public Order(UUID productId, int quantity, BigDecimal unitPrice) {
        if (productId == null) throw new ValidationException("Не выбрано изделие.");
        if (quantity <= 0) throw new ValidationException("Количество в заказе должно быть больше нуля.");
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public UUID getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public OrderStatus getStatus() { return status; }

    public void markReady() {
        if (status != OrderStatus.NEW) throw new ValidationException("Производить можно только новый заказ.");
        status = OrderStatus.READY;
    }

    public void cancel() {
        if (status != OrderStatus.NEW) throw new ValidationException("Отменить можно только новый заказ.");
        status = OrderStatus.CANCELLED;
    }

    @Override
    public String toString() {
        return String.format("Заказ %s | изделие=%s | количество=%d | сумма=%s руб. | статус=%s",
                getId(), productId, quantity, totalPrice.toPlainString(), status);
    }
}
