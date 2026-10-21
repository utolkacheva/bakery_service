package ru.vsu.cs.tolkacheva_u_a.task1.entity;

import ru.vsu.cs.tolkacheva_u_a.task1.ex.ValidationException;
import ru.vsu.cs.tolkacheva_u_a.task1.util.StringUtils;

import java.math.BigDecimal;

public class Ingredient extends AbstractEntity {
    private final String name;
    private final String unit;
    private BigDecimal quantity;

    public Ingredient(String name, String unit, BigDecimal quantity) {
        if (StringUtils.isNullOrBlank(name)) throw new ValidationException("Название ингредиента не может быть пустым.");
        if (StringUtils.isNullOrBlank(unit)) throw new ValidationException("Единица измерения не может быть пустой.");
        if (quantity == null || quantity.signum() < 0) throw new ValidationException("Количество не может быть отрицательным.");
        this.name = name.trim();
        this.unit = unit.trim();
        this.quantity = quantity;
    }

    public String getName() { return name; }
    public String getUnit() { return unit; }
    public BigDecimal getQuantity() { return quantity; }

    public void addStock(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) throw new ValidationException("Пополнение должно быть больше нуля.");
        quantity = quantity.add(amount);
    }

    public void subtractStock(BigDecimal amount) {
        if (amount == null || amount.signum() < 0 || quantity.compareTo(amount) < 0)
            throw new ValidationException("Недостаточно ингредиента на складе: " + name);
        quantity = quantity.subtract(amount);
    }

    @Override
    public String toString() {
        return String.format("%s: %s %s | ID: %s", name, quantity.toPlainString(), unit, getId());
    }
}
