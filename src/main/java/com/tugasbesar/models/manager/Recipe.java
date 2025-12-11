package com.tugasbesar.models.manager;

import java.util.ArrayList;
import java.util.List;
import com.tugasbesar.models.item.Ingredient;
import com.tugasbesar.models.item.Dish; // Pastikan import Dish
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import com.tugasbesar.models.interfaces.Processable;

public class Recipe {
    
    private final String recipeName;
    public List<Ingredient> requirements;

    public Recipe(String recipeName, List<Ingredient> requirements){
        this.recipeName = recipeName;
        this.requirements = requirements;
    }

    public List<String> getIngredientNames() {
        List<String> names = new ArrayList<>();
        for(Ingredient i : requirements) {
            names.add(i.getName());
        }
        return names;
    }

    public boolean matches(Plate plate) {
        if (plate == null || plate.getContents().isEmpty()) return false;

        List<Processable> plateItems = plate.getContents();

        // ==================================================================
        // CEK 1: APAKAH SUDAH JADI DISH (MAKANAN JADI)?
        // ==================================================================
        // Kalau kamu udah rakit di Assembly Station, isinya jadi 1 item "Dish"
        if (plateItems.size() == 1 && plateItems.get(0) instanceof Dish) {
            Dish dish = (Dish) plateItems.get(0);
            
            // Bandingkan Nama Dish dengan Nama Resep
            // Contoh: Dish "Pasta Marinara" == Resep "Pasta Marinara" -> BENAR
            if (dish.getName().equalsIgnoreCase(this.recipeName)) {
                return true; 
            }
        }

        // ==================================================================
        // CEK 2: APAKAH MASIH BAHAN TERPISAH (MANUAL PLATING)?
        // ==================================================================
        // Kalau kamu cuma taruh-taruh bahan di piring tanpa Assembly
        
        // Cek Jumlah
        if (plateItems.size() != requirements.size()) {
            return false;
        }

        // Cek Satu per Satu
        List<Processable> checkList = new ArrayList<>(plateItems);

        for (Ingredient req : requirements) {
            boolean found = false;

            for (int i = 0; i < checkList.size(); i++) {
                Processable item = checkList.get(i);
                
                if (item instanceof Ingredient) {
                    Ingredient ing = (Ingredient) item;
                    
                    // Cek Nama & State (COOKED/CHOPPED)
                    if (ing.getName().equalsIgnoreCase(req.getName()) && 
                        ing.getState() == req.getState()) {
                        
                        checkList.remove(i);
                        found = true;
                        break;
                    }
                }
            }

            if (!found) return false;
        }

        return true;
    }

    public String getRecipeName(){ return this.recipeName; }
}