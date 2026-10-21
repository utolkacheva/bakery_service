package ru.vsu.cs.tolkacheva_u_a.task1;

import ru.vsu.cs.tolkacheva_u_a.task1.core.BakeryCore;
import ru.vsu.cs.tolkacheva_u_a.task1.repository.impl.*;
import ru.vsu.cs.tolkacheva_u_a.task1.ui.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        BakeryCore core = new BakeryCore(
                new InMemoryProductRepository(),
                new InMemoryIngredientRepository(),
                new InMemoryRecipeRepository(),
                new InMemoryOrderRepository(),
                new InMemoryProductionBatchRepository()
        );
        new ConsoleUI(core).start();
    }
}
