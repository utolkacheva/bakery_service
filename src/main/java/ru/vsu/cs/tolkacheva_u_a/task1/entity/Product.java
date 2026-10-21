package ru.vsu.cs.tolkacheva_u_a.task1.entity;

import ru.vsu.cs.tolkacheva_u_a.task1.util.StringUtils;
import ru.vsu.cs.tolkacheva_u_a.task1.ex.ValidationException;

import java.math.BigDecimal;

public class Product extends AbstractEntity {
    private final String name;
    private final String category;
    private final BigDecimal price;

    public Product(String name, String category, BigDecimal price) {
        if (StringUtils.isNullOrBlank(name)) throw new ValidationException("Название изделия не может быть пустым.");
        if (StringUtils.isNullOrBlank(category)) throw new ValidationException("Категория не может быть пустой.");
        if (price == null || price.signum() <= 0) throw new ValidationException("Цена должна быть больше нуля.");
        this.name = name.trim();
        this.category = category.trim();
        this.price = price;
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
    public BigDecimal getPrice() { return price; }

    @Override
    public String toString() {
        return String.format("%s | %s | %s руб. | ID: %s", name, category, price.toPlainString(), getId());
    }
}
