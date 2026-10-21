import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.vsu.cs.tolkacheva_u_a.task1.core.BakeryCore;
import ru.vsu.cs.tolkacheva_u_a.task1.entity.*;
import ru.vsu.cs.tolkacheva_u_a.task1.ex.ValidationException;
import ru.vsu.cs.tolkacheva_u_a.task1.repository.impl.*;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BakeryCoreTest {
    private BakeryCore core;

    @BeforeEach
    void setUp() {
        core = new BakeryCore(
                new InMemoryProductRepository(),
                new InMemoryIngredientRepository(),
                new InMemoryRecipeRepository(),
                new InMemoryOrderRepository(),
                new InMemoryProductionBatchRepository()
        );
    }

    @Test
    void productionConsumesIngredientsAndMarksOrderReady() {
        Product bread = core.addProduct("Хлеб", "Выпечка", new BigDecimal("80"));
        Ingredient flour = core.addIngredient("Мука", "кг", new BigDecimal("10"));
        core.addRecipe(bread.getId(), Map.of(flour.getId(), new BigDecimal("0.5")));
        Order order = core.createOrder(bread.getId(), 2);

        ProductionBatch batch = core.produceOrder(order.getId());

        assertNotNull(batch);
        assertEquals(OrderStatus.READY, core.getOrder(order.getId()).getStatus());
        assertEquals(0, core.getIngredient(flour.getId()).getQuantity().compareTo(new BigDecimal("9.0")));
    }

    @Test
    void insufficientStockDoesNotPartiallyConsumeIngredients() {
        Product bread = core.addProduct("Хлеб", "Выпечка", new BigDecimal("80"));
        Ingredient flour = core.addIngredient("Мука", "кг", new BigDecimal("0.5"));
        Ingredient salt = core.addIngredient("Соль", "кг", new BigDecimal("0.01"));
        core.addRecipe(bread.getId(), Map.of(
                flour.getId(), new BigDecimal("0.4"),
                salt.getId(), new BigDecimal("0.02")
        ));
        Order order = core.createOrder(bread.getId(), 1);

        assertThrows(ValidationException.class, () -> core.produceOrder(order.getId()));
        assertEquals(0, core.getIngredient(flour.getId()).getQuantity().compareTo(new BigDecimal("0.5")));
        assertEquals(OrderStatus.NEW, core.getOrder(order.getId()).getStatus());
    }
}
