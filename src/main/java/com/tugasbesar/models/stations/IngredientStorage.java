package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.item.IngredientFactory;

public class IngredientStorage extends Station {
    
    private String ingredientName; 

    public IngredientStorage(int x, int y, String ingredientName) {
        super(x, y, "Storage: " + ingredientName, "I");
        this.ingredientName = ingredientName;
    }

    @Override
    public void interact(Chef chef) {
        // 1. AMBIL BAHAN (Kalau tangan kosong)
        if (!chef.hasItem()) {
            Item newItem = createNewIngredient();
            
            if (newItem != null) {
                chef.setHeldItem(newItem);
                System.out.println("📦 [Storage] Mengambil " + newItem.getName());
            } else {
                System.out.println("❌ Error: Bahan '" + ingredientName + "' tidak ditemukan di Factory.");
            }
        } 
        // 2. STORAGE TIDAK MENERIMA BARANG
        else {
            System.out.println("⚠️ [Storage] Tangan penuh! Taruh dulu itemmu.");
        }
    }

    // --- MENGHUBUNGKAN STRING DARI MAP KE FACTORY ---
    private Item createNewIngredient() {
        switch (ingredientName.toLowerCase()) {
            case "tomato": 
                return IngredientFactory.createTomato();
            
            case "beef":   
                return IngredientFactory.createBeef();
            
            case "pasta":  
                return IngredientFactory.createPasta();
            
            case "fish":   
                return IngredientFactory.createFish();
            
            case "shrimp": 
                return IngredientFactory.createShrimp();
                
            default: 
                return null;
        }
    }
}