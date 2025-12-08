package com.tugasbesar.models.item;

import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.enums.IngredientState;
import com.tugasbesar.models.interfaces.Preparable;

public class Ingredient extends Item implements Preparable {
    
    private IngredientState state;
    private boolean isChoppable; // Fitur bagus kamu: Flag boleh potong/tidak

    public Ingredient(String name, boolean isChoppable) {
        super(name);
        this.state = IngredientState.RAW; 
        this.isChoppable = isChoppable;
    }

    // --- LOGIC UTAMA ---

    @Override
    public boolean canBeChopped() {
        // Gabungan logic: Harus sifatnya bisa dipotong DAN kondisinya masih mentah
        return isChoppable && state == IngredientState.RAW;
    }

    @Override
    public void chop() {
        if (canBeChopped()) {
            this.state = IngredientState.CHOPPED;
            // System.out.println tidak perlu di sini, biar Station yang print (supaya rapi)
        }
    }

    @Override
    public boolean canBeCooked() {
        // Bisa dimasak kalau RAW (misal Pasta) atau CHOPPED (misal Daging Potong)
        return state == IngredientState.RAW || state == IngredientState.CHOPPED;
    }

    @Override
    public void cook() {
        // Method ini dipanggil oleh Station SAAT WAKTU MASAK HABIS
        // Jadi statusnya langsung jadi MATANG (COOKED)
        if (canBeCooked()) {
            this.state = IngredientState.COOKED;
        }
    }
    
    // Tambahan buat fitur gosong nanti
    public void burn() {
        this.state = IngredientState.BURNED;
    }

    @Override
    public boolean canBePlacedOnPlate() {
        // Biasanya bahan mentah gaboleh di piring (kecuali Salad/Sushi)
        // Tapi kita buat simple: Semua boleh masuk piring asal bukan GOSONG
        return state != IngredientState.BURNED;
    }

    // --- GETTER / SETTER ---

    @Override
    public IngredientState getState() {
        return state;
    }

    public void setState(IngredientState state) {
        this.state = state;
    }

    // --- VISUAL ---
    @Override
    public String toString() {
        // Tampilan otomatis: "Tomato (RAW)" atau "Tomato (COOKED)"
        return name + " (" + state + ")";
    }
}