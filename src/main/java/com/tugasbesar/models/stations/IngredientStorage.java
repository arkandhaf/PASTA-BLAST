package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.interfaces.Processable; 
import com.tugasbesar.models.item.IngredientFactory;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import com.tugasbesar.models.item.kitchen_utensil.BaseCookingDevice; 
import com.tugasbesar.models.enums.IngredientState;

import java.awt.Color;

public class IngredientStorage extends Station {
    
    private String ingredientName; 

    public IngredientStorage(int x, int y, String ingredientName) {
        super(x, y, "Storage: " + ingredientName, "I");
        this.ingredientName = ingredientName;
        this.itemOnStation = null; 
    }

    // --- [FIX] GANTI NAMA METHOD ---
    @Override
    public void interactGrab(Chef chef) {
        Item hand = chef.getHeldItem();
        Item tableItem = itemOnStation;
        
        // 1. PLATING DARI UTENSIL KE MEJA
        if (tableItem instanceof Plate plateOnTable && chef.hasItem() && hand instanceof BaseCookingDevice utensilInHand) { 
            if (!utensilInHand.isEmpty() && !plateOnTable.isDirty()) {
                Processable ingredient = utensilInHand.getContents().get(0);
                if (plateOnTable.canAccept(ingredient)) {
                    Processable ingredientToPlate = (Processable) utensilInHand.takeItem(); 
                    plateOnTable.addIngredient(ingredientToPlate); 
                    notifyInteraction("Plated from Utensil", new Color(33, 150, 243));
                    return;
                } 
            }
        }
        
        // 2. PLATING DARI SUMBER KE TANGAN
        if (chef.hasItem() && hand instanceof Plate plateInHand) {
            Item ingredientSource = createNewIngredient();
            if (ingredientSource instanceof Processable ingredientOnTable && !plateInHand.isDirty() && plateInHand.canAccept(ingredientOnTable)) {
                plateInHand.addIngredient(ingredientOnTable); 
                notifyInteraction("Plated " + ingredientOnTable.getName(), new Color(76, 175, 80));
                return;
            }
        }
        
        // 3. AMBIL ITEM TERTINGGAL
        if (!chef.hasItem() && tableItem != null) {
             chef.setHeldItem(takeItem());
             notifyInteraction("Took " + chef.getHeldItem().getName(), new Color(156, 39, 176));
             return;
        }

        // 4. AMBIL BAHAN BARU
        if (!chef.hasItem() && tableItem == null) {
            Item newItem = createNewIngredient();
            if (newItem != null) {
                chef.setHeldItem(newItem);
                notifyInteraction("Picked " + newItem.getName(), new Color(76, 175, 80));
            } else {
                notifyInteraction("Missing " + ingredientName, new Color(244, 67, 54));
            }
            return;
        }
        
        // 5. TARUH/SWAP
        defaultInteract(chef);
    }

    // --- [FIX] METHOD USE KOSONG ---
    @Override
    public void interactUse(Chef chef) {
        // Storage gak butuh tombol E
    }

    @Override
    public void update() {}

    private Item createNewIngredient() {
        switch (ingredientName.toLowerCase()) {
            case "tomato": return IngredientFactory.createTomato();
            case "beef": return IngredientFactory.createBeef();
            case "pasta": return IngredientFactory.createPasta();
            case "fish": return IngredientFactory.createFish();
            case "shrimp": return IngredientFactory.createShrimp();
            default: return null;
        }
    }

    public String getIngredientName() {
        return ingredientName;
    }
}