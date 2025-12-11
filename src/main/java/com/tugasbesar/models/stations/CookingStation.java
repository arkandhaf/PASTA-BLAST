package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.item.Ingredient;
import com.tugasbesar.models.enums.IngredientState;
import com.tugasbesar.models.interfaces.Processable;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import com.tugasbesar.models.item.kitchen_utensil.BaseCookingDevice;
import java.awt.Color;
import java.awt.Graphics2D;

public class CookingStation extends Station {

    private boolean isCooking = false;
    private int cookProgress = 0;
    private final int MAX_PROGRESS = 200; 
    private final int COOK_SPEED = 1;

    public CookingStation(int x, int y) {
        super(x, y, "Stove", "K");
    }

    @Override
    public void interact(Chef chef) {
        Item hand = chef.getHeldItem();

        // -----------------------------------------------------------
        // 1. PLATING LOGIC (Chef bawa Piring -> Ambil Masakan)
        // -----------------------------------------------------------
        if (hand instanceof Plate && !isEmpty()) {
            if (isCooking) {
                System.out.println("⚠️ Tunggu sampai matang!");
                return;
            }

            Plate plate = (Plate) hand;
            Item itemOnTable = itemOnStation;

            if (itemOnTable instanceof Processable) {
                Processable ingredient = (Processable) itemOnTable;
                
                // Masukkan ke piring
                if (plate.canAccept(ingredient)) {
                    Item takenItem = takeItem();
                    plate.addIngredient((Processable) takenItem);
                    cookProgress = 0; // Reset visual
                    System.out.println("🍽️ [Stove] " + takenItem.getName() + " dimasukkan ke Piring.");
                    return;
                }
            }
        }

        // -----------------------------------------------------------
        // 2. TARUH ITEM (Bahan atau Panci)
        // -----------------------------------------------------------
        if (chef.hasItem() && isEmpty()) {
            // Transit Alat
            if (hand instanceof Plate || hand instanceof BaseCookingDevice) {
                placeItem(hand);
                chef.setHeldItem(null);
                return;
            }

            // Masak Bahan
            if (hand instanceof Ingredient) {
                Ingredient ing = (Ingredient) hand;
                if (ing.canBeCooked()) {
                    placeItem(hand);
                    chef.setHeldItem(null);
                    
                    isCooking = true; 
                    cookProgress = 0;
                    System.out.println("🔥 [Stove] Mulai memasak " + ing.getName());
                } else {
                    System.out.println("⚠️ Bahan ini tidak bisa dimasak.");
                }
            }
            return;
        }

        // -----------------------------------------------------------
        // 3. AMBIL ITEM (Manual)
        // -----------------------------------------------------------
        if (!chef.hasItem() && !isEmpty()) {
            if (isCooking) {
                System.out.println("⚠️ Belum matang!");
                return;
            }
            chef.setHeldItem(takeItem());
            cookProgress = 0;
            System.out.println("⬆️ [Stove] Mengambil item.");
        }
    }

    @Override
    public void update() {
        if (isCooking && itemOnStation != null) {
            cookProgress += COOK_SPEED;
            if (cookProgress >= MAX_PROGRESS) finishCooking();
        }
    }

    private void finishCooking() {
        isCooking = false;
        if (itemOnStation instanceof Ingredient) {
            ((Ingredient) itemOnStation).cook(); 
            System.out.println("✅ [Stove] Matang!");
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2); 
        if (cookProgress > 0) {
            int width = (int) ((double)cookProgress / MAX_PROGRESS * 40);
            g2.setColor(isCooking ? Color.RED : Color.GREEN);
            g2.fillRect(posX * 48 + 4, posY * 48 - 10, width, 6);
        }
    }
}