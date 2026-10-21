package ru.vsu.cs.tolkacheva_u_a.task1.entity;

import ru.vsu.cs.tolkacheva_u_a.task1.ex.ValidationException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class Recipe extends AbstractEntity {
    private final UUID productId;
    private final Map<UUID, BigDecimal> ingredientAmounts;

    public Recipe(UUID productId, Map<UUID, BigDecimal> ingredientAmounts) {
        if (productId == null) throw new ValidationException("Для рецепта нужно выбрать изделие.");
        if (ingredientAmounts == null || ingredientAmounts.isEmpty())
            throw new ValidationException("Рецепт должен содержать хотя бы один ингредиент.");
        Map<UUID, BigDecimal> copy = new LinkedHashMap<>();
        ingredientAmounts.forEach((id, amount) -> {
            if (id == null || amount == null || amount.signum() <= 0)
                throw new ValidationException("Расход ингредиента должен быть больше нуля.");
            copy.put(id, amount);
        });
        this.productId = productId;
        this.ingredientAmounts = Collections.unmodifiableMap(copy);
    }

    public UUID getProductId() { return productId; }
    public Map<UUID, BigDecimal> getIngredientAmounts() { return ingredientAmounts; }
}