package ru.vsu.cs.tolkacheva_u_a.task1.core;

import ru.vsu.cs.tolkacheva_u_a.task1.entity.*;
import ru.vsu.cs.tolkacheva_u_a.task1.ex.EntityNotFoundException;
import ru.vsu.cs.tolkacheva_u_a.task1.ex.ValidationException;
import ru.vsu.cs.tolkacheva_u_a.task1.repository.Repository;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BakeryCore {
    private final Repository<Product> products;
    private final Repository<Ingredient> ingredients;
    private final Repository<Recipe> recipes;
    private final Repository<Order> orders;
    private final Repository<ProductionBatch> batches;

    public BakeryCore(Repository<Product> products,
                      Repository<Ingredient> ingredients,
                      Repository<Recipe> recipes,
                      Repository<Order> orders,
                      Repository<ProductionBatch> batches) {
        this.products = products;
        this.ingredients = ingredients;
        this.recipes = recipes;
        this.orders = orders;
        this.batches = batches;
    }

    public Product addProduct(String name, String category, BigDecimal price) {
        Product product = new Product(name, category, price);
        products.save(product);
        return product;
    }

    public List<Product> getProducts() { return products.findAll(); }

    public Product getProduct(UUID id) {
        return products.findById(id).orElseThrow(() -> new EntityNotFoundException("Изделие не найдено: " + id));
    }

    public void deleteProduct(UUID id) {
        getProduct(id);
        boolean referenced = recipes.findAll().stream().anyMatch(r -> r.getProductId().equals(id))
                || orders.findAll().stream().anyMatch(o -> o.getProductId().equals(id));
        if (referenced) throw new ValidationException("Нельзя удалить изделие, которое используется в рецепте или заказе.");
        products.deleteById(id);
    }

    public Ingredient addIngredient(String name, String unit, BigDecimal initialQuantity) {
        Ingredient ingredient = new Ingredient(name, unit, initialQuantity);
        ingredients.save(ingredient);
        return ingredient;
    }

    public List<Ingredient> getIngredients() { return ingredients.findAll(); }

    public Ingredient getIngredient(UUID id) {
        return ingredients.findById(id).orElseThrow(() -> new EntityNotFoundException("Ингредиент не найден: " + id));
    }

    public void restockIngredient(UUID id, BigDecimal amount) {
        Ingredient ingredient = getIngredient(id);
        ingredient.addStock(amount);
        ingredients.save(ingredient);
    }

    public Recipe addRecipe(UUID productId, Map<UUID, BigDecimal> amounts) {
        getProduct(productId);
        for (UUID ingredientId : amounts.keySet()) getIngredient(ingredientId);
        boolean alreadyExists = recipes.findAll().stream().anyMatch(r -> r.getProductId().equals(productId));
        if (alreadyExists) throw new ValidationException("Для этого изделия рецепт уже существует.");
        Recipe recipe = new Recipe(productId, amounts);
        recipes.save(recipe);
        return recipe;
    }

    public List<Recipe> getRecipes() { return recipes.findAll(); }

    public Order createOrder(UUID productId, int quantity) {
        Product product = getProduct(productId);
        boolean hasRecipe = recipes.findAll().stream().anyMatch(r -> r.getProductId().equals(productId));
        if (!hasRecipe) throw new ValidationException("Нельзя создать производственный заказ без рецепта.");
        Order order = new Order(productId, quantity, product.getPrice());
        orders.save(order);
        return order;
    }

    public List<Order> getOrders() { return orders.findAll(); }

    public Order getOrder(UUID id) {
        return orders.findById(id).orElseThrow(() -> new EntityNotFoundException("Заказ не найден: " + id));
    }

    public void cancelOrder(UUID id) {
        Order order = getOrder(id);
        order.cancel();
        orders.save(order);
    }

    public ProductionBatch produceOrder(UUID orderId) {
        Order order = getOrder(orderId);
        if (order.getStatus() != OrderStatus.NEW)
            throw new ValidationException("Производить можно только заказ со статусом NEW.");

        Recipe recipe = recipes.findAll().stream()
                .filter(r -> r.getProductId().equals(order.getProductId()))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Для изделия отсутствует рецепт."));

        Map<Ingredient, BigDecimal> required = new LinkedHashMap<>();
        for (Map.Entry<UUID, BigDecimal> entry : recipe.getIngredientAmounts().entrySet()) {
            Ingredient ingredient = getIngredient(entry.getKey());
            BigDecimal amount = entry.getValue().multiply(BigDecimal.valueOf(order.getQuantity()));
            if (ingredient.getQuantity().compareTo(amount) < 0) {
                throw new ValidationException("Недостаточно ингредиента \"" + ingredient.getName()
                        + "\". Нужно: " + amount + " " + ingredient.getUnit()
                        + ", на складе: " + ingredient.getQuantity() + " " + ingredient.getUnit());
            }
            required.put(ingredient, amount);
        }

        required.forEach(Ingredient::subtractStock);
        required.keySet().forEach(ingredients::save);
        order.markReady();
        orders.save(order);

        ProductionBatch batch = new ProductionBatch(orderId);
        batches.save(batch);
        return batch;
    }

    public List<ProductionBatch> getBatches() { return batches.findAll(); }
}
