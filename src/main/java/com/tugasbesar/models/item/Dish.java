package com.tugasbesar.models.item; 

import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.interfaces.Placeable; 
import com.tugasbesar.models.enums.IngredientState; 
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Asumsi: Dish adalah produk akhir yang dibuat setelah validasi Plate.
public class Dish extends Item implements Placeable {

    private final String recipeName; 
    
    // Ini menyimpan representasi String dari bahan-bahan yang dirakit (misalnya, ["Beef (COOKED)", "Pasta (COOKED)"])
    private final List<String> dishContents; 
    
    private final IngredientState state = IngredientState.SERVED; 

    /**
     * Constructor untuk Dish (Hidangan Jadi)
     * @param finalName Nama dari resep (RecipeName)
     * @param contentsString Daftar String (Nama + State) dari bahan yang BARU SAJA dirakit.
     */
    public Dish(String finalName, List<String> contentsString) {
        super(finalName); 
        this.recipeName = finalName;
        
        // Menyimpan konten yang dirakit. PENTING: Harus diurutkan untuk perbandingan yang andal di Recipe.matches().
        List<String> sortedContents = new ArrayList<>(contentsString);
        Collections.sort(sortedContents);
        this.dishContents = sortedContents;
        
        setEdible(true); 
    }

    // --- Getters untuk OrderManager/Recipe ---

    public String getRecipeName() { 
        return recipeName; 
    }
    
    /**
     * Mengembalikan daftar string konten (Nama+State) dari Dish.
     * Nama method ini tetap 'getRequiredIngredients()' agar sesuai dengan pemanggilan di Recipe.java Anda.
     */
    public List<String> getRequiredIngredients() { 
        return dishContents; 
    }
    
    // --- Overrides ---

    @Override
    public IngredientState getState() { 
        return this.state; 
    }
    
    @Override
    public boolean canBePlacedOnPlate() { 
        return false; 
    }
    
    @Override
    public String toString() {
        return "Dish: " + recipeName + " (" + this.state + ")";
    }
}