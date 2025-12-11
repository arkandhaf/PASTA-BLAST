package com.tugasbesar.models.item.kitchen_utensil;

import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.enums.IngredientState;
import com.tugasbesar.models.interfaces.Processable;
import com.tugasbesar.models.item.Ingredient;
import com.tugasbesar.models.item.Dish;

import java.util.ArrayList;
import java.util.List;

public class Plate extends Item {

    private List<Processable> contents; 
    private boolean isDirty;

    public Plate() {
        super("Plate");
        this.contents = new ArrayList<>();
        this.isDirty = false;
    }

    public boolean canAccept(Processable item) {
        if (isDirty) return false;
        if (item instanceof Dish) return true;
        if (item instanceof Ingredient) {
            Ingredient ing = (Ingredient) item;
            if (ing.getState() == IngredientState.COOKED) return true;
            System.out.println("⚠️ [Plate] Menolak " + ing.getName() + " (Status: " + ing.getState() + ", Harusnya COOKED)");
            return false;
        }
        return false;
    }

    public void addIngredient(Processable item) {
        if (canAccept(item)) {
            contents.add(item);
            System.out.println("🍽️ [Plate] Ditambahkan: " + item.getName());
        }
    }

    public boolean isDirty() { return isDirty; }
    
    public void markDirty() { 
        this.isDirty = true; 
        this.contents.clear(); 
    }
    
    public void clean() { 
        this.isDirty = false; 
        this.contents.clear();
    }

    public void clearContents() {
        this.contents.clear();
    }

    public List<Processable> getContents() { return contents; }
    
    public boolean isEmpty() { return contents.isEmpty(); }

    public String getDishName() {
        if (contents.isEmpty()) return "Empty Plate";
        if (contents.get(0) instanceof Dish) return contents.get(0).getName();
        return "Ingredients Mix";
    }
    
    // [PENTING BUAT DEBUG]
    public String getContentsString() {
        StringBuilder sb = new StringBuilder("[");
        for (Processable p : contents) {
            if (p instanceof Ingredient) {
                Ingredient ing = (Ingredient) p;
                sb.append(ing.getName()).append("(").append(ing.getState()).append("), ");
            } else {
                sb.append(p.getName()).append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}