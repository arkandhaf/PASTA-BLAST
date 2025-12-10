package com.tugasbesar.models.item.kitchen_utensil;

import com.tugasbesar.models.abstracts.KitchenUtensil;
import com.tugasbesar.models.interfaces.Processable; 
import com.tugasbesar.models.interfaces.Placeable; 

import java.util.stream.Collectors;

public class Plate extends KitchenUtensil {

    private boolean isDirty;

    public Plate() {
        super("Plate"); 
        this.isDirty = false; 
    }

    @Override
    public void addIngredient(Processable item) {
        
        if (canAccept(item)) {
            super.addIngredient(item); 
            System.out.println("[Plate] " + item.getName() + " diletakkan di piring.");
        } 
    }

    @Override
    public boolean canAccept(Processable item) {
        if (isDirty) {
            System.out.println("[!] Piring kotor! Cuci dulu di Washing Station.");
            return false;
        }

        if (!(item instanceof Placeable)) {
            System.out.println("[!] Item ini tidak dapat diletakkan di piring.");
            return false;
        }
        
        Placeable placeableItem = (Placeable)item;
        
        if (!placeableItem.canBePlacedOnPlate()) {
            System.out.println("[!] Item belum siap untuk diletakkan di piring (misal: mentah atau gosong).");
            return false;
        }
        return true;
    }



    public void wash() {
        super.clearContents(); 
        this.isDirty = false; 
        System.out.println("[Plate] Piring bersih dan siap digunakan.");
    }

    public void markDirty() {
        this.isDirty = true; 
        System.out.println("[Plate] Piring ditandai kotor (masih ada sisa/kosong).");
    }

    public boolean isClean() {
        return !isDirty;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public String getDishName() {
        if (isDirty) return "Dirty Plate";
        if (contents.isEmpty()) return "Empty Plate";

        return contents.stream()
                .map(item -> item.getName())
                .collect(Collectors.joining(" + "));
    }

    @Override
    public String toString() {
        if (isDirty) return "Plate (Dirty)";
        if (contents.isEmpty()) return "Plate (Clean)";
        
        return "Plate [" + getDishName() + "]";
    }
}