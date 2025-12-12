package com.tugasbesar.models.item;

import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.interfaces.Processable;
import com.tugasbesar.models.enums.IngredientState;
import java.util.List;

public class Dish extends Item implements Processable {

    private List<String> ingredients;

    // Constructor 1: Cuma Nama
    public Dish(String name) {
        super(name);
    }

    // Constructor 2: Nama + List Bahan (Dipakai AssemblyStation)
    public Dish(String name, List<String> ingredients) {
        super(name);
        this.ingredients = ingredients;
    }

    public List<String> getRequiredIngredients() {
        return ingredients;
    }

    // --- [FIX] INI METHOD YANG TADI ERROR ---
    public String getRecipeName() {
        return this.name; // Kembalikan nama dish (misal: "Pasta Marinara")
    }
    // ----------------------------------------

    // Implementasi Processable
    @Override
    public IngredientState getState() {
        return IngredientState.SERVED;
    }

    @Override
    public String getName() {
        return name;
    }
}
