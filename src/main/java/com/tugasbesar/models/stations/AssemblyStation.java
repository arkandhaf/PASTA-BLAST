package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.interfaces.Processable;
import com.tugasbesar.models.enums.IngredientState;
import com.tugasbesar.models.item.Dish;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import com.tugasbesar.models.item.kitchen_utensil.BaseCookingDevice; // Import ini
import com.tugasbesar.models.manager.OrderManager;
import com.tugasbesar.models.manager.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.awt.Color;

public class AssemblyStation extends Station {

    public AssemblyStation(int x, int y) {
        super(x, y, "Assembly Station", "A");
    }

    @Override
    public void interactGrab(Chef chef) {
        Item hand = chef.getHeldItem();
        Item tableItem = itemOnStation;

        // =========================================================
        // FITUR BARU: TUANG DARI PANCI (Tangan) KE PIRING (Meja)
        // =========================================================
        if (hand instanceof BaseCookingDevice && tableItem instanceof Plate) {
            BaseCookingDevice utensil = (BaseCookingDevice) hand;
            Plate plate = (Plate) tableItem;
            
            if (!utensil.isEmpty()) {
                // Cek isi panci
                Processable food = utensil.getContents().get(0);
                
                // Cek piring mau terima gak (Harus Cooked)
                if (plate.canAccept(food)) {
                    // Pindahkan
                    utensil.getContents().clear(); // Kosongkan panci di tangan
                    plate.addIngredient(food);     // Isi piring di meja
                    
                    System.out.println("🥣 Menuang " + food.getName() + " ke Piring.");
                    notifyInteraction("Poured " + food.getName(), Color.CYAN);
                    return;
                } else {
                    notifyInteraction("Plate refused!", Color.RED);
                }
            } else {
                notifyInteraction("Pot Empty", Color.GRAY);
            }
            return;
        }
        // =========================================================

        // ... (SISANYA SAMA SEPERTI SEBELUMNYA) ...
        
        // 1. AMBIL PIRING / DISH
        if (hand == null && tableItem instanceof Plate) {
            Plate p = (Plate) tableItem;
            if (!p.isEmpty() && p.getContents().get(0) instanceof Dish) {
                chef.setHeldItem(takeItem());
                notifyInteraction("Dish Ready!", Color.GREEN);
                return;
            }
            if (!p.isEmpty()) {
                if (performAssembly(p)) return;
            }
            chef.setHeldItem(takeItem());
            notifyInteraction("Plate Picked", Color.WHITE);
            return;
        }

        // 2. RAKIT DI TANGAN
        if (hand instanceof Plate && tableItem == null) {
            Plate p = (Plate) hand;
            if (!p.isEmpty()) performAssembly(p);
        }

        // 3. PLATING (Manual Hand -> Table)
        if (hand instanceof Processable && tableItem instanceof Plate) {
            performPlating((Plate) tableItem, (Processable) hand);
            chef.setHeldItem(null);
            return;
        }
        
        // 4. TARUH BARANG
        if (hand != null && isEmpty()) {
            placeItem(hand);
            chef.setHeldItem(null);
            notifyInteraction("Placed", Color.WHITE);
        }
    }

    @Override
    public void interactUse(Chef chef) {}

    // Helper methods (performPlating, performAssembly) tetap sama...
    // Copy paste logic helper methods dari AssemblyStation sebelumnya
    private void performPlating(Plate plate, Processable item) {
        if (!isValidForPlating(item)) return;
        if (plate.getContents().stream().anyMatch(content -> content instanceof Dish)) {
            notifyInteraction("Finished Dish!", Color.RED);
            return;
        }
        plate.addIngredient(item);
        notifyInteraction("Added", Color.CYAN);
    }

    private boolean isValidForPlating(Processable item) {
        if (item.getState() == IngredientState.RAW || item.getState() == IngredientState.BURNED) {
            notifyInteraction("Needs Prep", Color.RED);
            return false;
        }
        return true;
    }

    private boolean performAssembly(Plate plate) {
        List<Processable> contents = plate.getContents();
        if (contents.size() == 1 && contents.get(0) instanceof Dish) return false;

        List<String> ingredientNames = new ArrayList<>();
        for (Processable item : contents) ingredientNames.add(item.getName());

        Recipe recipeMatch = OrderManager.getInstance().findMatchingRecipe(ingredientNames);

        if (recipeMatch != null) {
            plate.clearContents();
            Dish finalDish = new Dish(recipeMatch.getRecipeName(), ingredientNames);
            plate.addIngredient(finalDish);
            notifyInteraction("Assembled!", Color.MAGENTA);
            return true;
        }
        return false;
    }
}