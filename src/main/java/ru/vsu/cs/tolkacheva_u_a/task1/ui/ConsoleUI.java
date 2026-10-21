package ru.vsu.cs.tolkacheva_u_a.task1.ui;

import ru.vsu.cs.tolkacheva_u_a.task1.core.BakeryCore;
import ru.vsu.cs.tolkacheva_u_a.task1.entity.Ingredient;
import ru.vsu.cs.tolkacheva_u_a.task1.entity.Product;
import ru.vsu.cs.tolkacheva_u_a.task1.entity.Recipe;
import ru.vsu.cs.tolkacheva_u_a.task1.ex.AppException;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class ConsoleUI {
    private final BakeryCore core;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleUI(BakeryCore core) {
        this.core = core;
    }

    public void start() {
        System.out.println("BAKERY SERVICE");
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> addProduct();
                    case "2" -> listProducts();
                    case "3" -> deleteProduct();
                    case "4" -> addIngredient();
                    case "5" -> listIngredients();
                    case "6" -> restockIngredient();
                    case "7" -> addRecipe();
                    case "8" -> listRecipes();
                    case "9" -> createOrder();
                    case "10" -> listOrders();
                    case "11" -> produceOrder();
                    case "12" -> cancelOrder();
                    case "13" -> listBatches();
                    case "0" -> running = false;
                    default -> System.out.println("Неизвестная команда. Выберите пункт меню.");
                }
            } catch (AppException | IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Не удалось выполнить операцию. Проверьте введённые данные.");
            }
        }
        System.out.println("Работа приложения завершена. Данные хранились только в памяти.");
    }

    private void printMenu() {
        System.out.println("""

                     МЕНЮ
                1. Добавить изделие
                2. Показать каталог
                3. Удалить изделие по ID
                4. Добавить ингредиент
                5. Показать склад
                6. Пополнить ингредиент
                7. Создать рецепт
                8. Показать рецепты
                9. Создать заказ
                10. Показать заказы
                11. Запустить производство заказа
                12. Отменить заказ
                13. Показать производственные партии
                0. Выход
                Выберите действие:""");
    }

    private void addProduct() {
        String name = readText("Название изделия: ");
        String category = readText("Категория: ");
        BigDecimal price = readDecimal("Цена: ");
        Product product = core.addProduct(name, category, price);
        System.out.println("Изделие добавлено: " + product);
    }

    private void listProducts() {
        if (core.getProducts().isEmpty()) { System.out.println("Каталог пуст."); return; }
        core.getProducts().forEach(System.out::println);
    }

    private void deleteProduct() {
        core.deleteProduct(readUuid("ID изделия: "));
        System.out.println("Изделие удалено.");
    }

    private void addIngredient() {
        String name = readText("Название ингредиента: ");
        String unit = readText("Единица измерения (кг, л, шт.): ");
        BigDecimal quantity = readDecimal("Начальный остаток: ");
        System.out.println("Ингредиент добавлен: " + core.addIngredient(name, unit, quantity));
    }

    private void listIngredients() {
        if (core.getIngredients().isEmpty()) { System.out.println("Склад пуст."); return; }
        core.getIngredients().forEach(System.out::println);
    }

    private void restockIngredient() {
        UUID id = readUuid("ID ингредиента: ");
        BigDecimal amount = readDecimal("Количество для пополнения: ");
        core.restockIngredient(id, amount);
        System.out.println("Остаток обновлён.");
    }

    private void addRecipe() {
        listProducts();
        UUID productId = readUuid("ID изделия для рецепта: ");
        listIngredients();
        int count = readInt("Количество видов ингредиентов в рецепте: ");
        if (count <= 0) throw new IllegalArgumentException("Количество ингредиентов должно быть положительным.");
        Map<UUID, BigDecimal> amounts = new LinkedHashMap<>();
        for (int i = 0; i < count; i++) {
            UUID ingredientId = readUuid("ID ингредиента #" + (i + 1) + ": ");
            BigDecimal amount = readDecimal("Расход на одну единицу изделия: ");
            if (amounts.putIfAbsent(ingredientId, amount) != null)
                throw new IllegalArgumentException("Один ингредиент нельзя добавлять в рецепт дважды.");
        }
        Recipe recipe = core.addRecipe(productId, amounts);
        System.out.println("Рецепт создан ID: " + recipe.getId());
    }

    private void listRecipes() {
        if (core.getRecipes().isEmpty()) { System.out.println("Рецептов пока нет."); return; }
        for (Recipe recipe : core.getRecipes()) {
            Product product = core.getProduct(recipe.getProductId());
            System.out.println("Рецепт " + recipe.getId() + " изделие: " + product.getName());
            recipe.getIngredientAmounts().forEach((id, amount) -> {
                Ingredient ingredient = core.getIngredient(id);
                System.out.println("  - " + ingredient.getName() + ": " + amount + " " + ingredient.getUnit());
            });
        }
    }

    private void createOrder() {
        listProducts();
        UUID productId = readUuid("ID изделия: ");
        int quantity = readInt("Количество изделий: ");
        System.out.println("Заказ создан: " + core.createOrder(productId, quantity));
    }

    private void listOrders() {
        if (core.getOrders().isEmpty()) { System.out.println("Заказов пока нет."); return; }
        core.getOrders().forEach(System.out::println);
    }

    private void produceOrder() {
        listOrders();
        UUID orderId = readUuid("ID заказа для производства: ");
        System.out.println("Производство завершено: " + core.produceOrder(orderId));
    }

    private void cancelOrder() {
        core.cancelOrder(readUuid("ID заказа: "));
        System.out.println("Заказ отменён.");
    }

    private void listBatches() {
        if (core.getBatches().isEmpty()) { System.out.println("Производственных партий пока нет."); return; }
        core.getBatches().forEach(System.out::println);
    }

    private String readText(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private BigDecimal readDecimal(String prompt) {
        System.out.print(prompt);
        String value = scanner.nextLine().trim().replace(',', '.');
        try { return new BigDecimal(value); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Введите корректное число."); }
    }

    private int readInt(String prompt) {
        System.out.print(prompt);
        try { return Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Введите целое число."); }
    }

    private UUID readUuid(String prompt) {
        System.out.print(prompt);
        try { return UUID.fromString(scanner.nextLine().trim()); }
        catch (IllegalArgumentException e) { throw new IllegalArgumentException("Введите полный корректный UUID из списка."); }
    }
}
