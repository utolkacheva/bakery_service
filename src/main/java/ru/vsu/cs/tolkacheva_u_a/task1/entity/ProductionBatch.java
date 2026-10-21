package ru.vsu.cs.tolkacheva_u_a.task1.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class ProductionBatch extends AbstractEntity {
    private final UUID orderId;
    private final LocalDateTime completedAt = LocalDateTime.now();
    private final ProductionStatus status = ProductionStatus.COMPLETED;

    public ProductionBatch(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getOrderId() { return orderId; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public ProductionStatus getStatus() { return status; }

    @Override
    public String toString() {
        return "Партия " + getId() + " | заказ=" + orderId + " | статус=" + status + " | завершено=" + completedAt;
    }
}
