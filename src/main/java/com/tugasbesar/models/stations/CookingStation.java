package com.tugasbesar.models.stations;

import java.awt.Color;
import java.awt.Graphics2D;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item; 

// Import Logic Person 2 & 3
import com.tugasbesar.models.interfaces.Cookable; 
import com.tugasbesar.models.interfaces.Processable; 
import com.tugasbesar.models.item.kitchen_utensil.BaseCookingDevice;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import com.tugasbesar.models.enums.IngredientState; 
import com.tugasbesar.models.item.Dish; 

public class CookingStation extends Station {

    public CookingStation(int x, int y) {
        super(x, y, "Stove", "S"); 
        this.itemOnStation = null;
    }

    public CookingStation(int x, int y, BaseCookingDevice startingUtensil) {
        super(x, y, "Stove", "S"); 
        this.itemOnStation = startingUtensil; 
    }

    @Override
    public void interact(Chef chef) {
        Item hand = chef.getHeldItem();
        Item tableItem = itemOnStation;

        // --- 1. LOGIC UTAMA: JIKA ADA ALAT MASAK (PANCI/WAJAN) DI KOMPOR ---
        if (tableItem instanceof BaseCookingDevice) {
            BaseCookingDevice utensil = (BaseCookingDevice) tableItem;

            // A. PLATING: Piring di Tangan -> Ambil Makanan dari Panci
            if (hand instanceof Plate) {
                Plate plate = (Plate) hand;
                if (!utensil.isEmpty() && utensil.isCookedOrBurned()) {
                    Processable result = utensil.getContents().get(0);
                    
                    if (!plate.isDirty() && plate.canAccept(result)) {
                        Item food = utensil.takeItem();
                        plate.addIngredient((Processable) food);
                        System.out.println("✅ [Stove] Memindahkan " + food.getName() + " ke Piring.");
                        return;
                    }
                }
            }

            // B. MASAK: Bahan di Tangan -> Masuk Panci
            if (hand instanceof Cookable) {
                Cookable ingredient = (Cookable) hand;
                if (utensil.canAccept(ingredient)) {
                    utensil.addIngredient(ingredient);
                    utensil.startCooking();
                    chef.setHeldItem(null);
                    System.out.println("🔥 [Stove] Memasak " + ((Processable)ingredient).getName());
                    return;
                }
            }
            
            // C. AMBIL HASIL: Tangan Kosong -> Ambil Makanan Matang
            if (hand == null && !utensil.isEmpty()) {
                Processable result = utensil.getContents().get(0);
                if (result.getState() == IngredientState.COOKED || result.getState() == IngredientState.BURNED) {
                    chef.setHeldItem(utensil.takeItem());
                    System.out.println("✅ [Stove] Mengambil " + result.getName());
                    return;
                }
            }
            
            // D. SWAP: Tukar Panci di Tangan dengan Panci di Kompor
            if (hand instanceof BaseCookingDevice) {
                chef.setHeldItem(tableItem);
                this.itemOnStation = hand;
                System.out.println("🔄 [Stove] Tukar Panci.");
                return;
            }
        }

        // --- 2. SAFETY: JANGAN TARUH PIRING LANGSUNG DI API ---
        if (isEmpty() && hand instanceof Plate) {
            System.out.println("⚠️ [Stove] Jangan taruh piring di api!");
            return;
        }

        // --- 3. SAFETY: JANGAN TARUH BAHAN LANGSUNG DI API ---
        if (isEmpty() && hand instanceof Cookable) {
             System.out.println("⚠️ [Stove] Taruh Panci dulu, baru bahan!");
             return;
        }

        // --- 4. DEFAULT: ANGKAT/TARUH PANCI ---
        defaultInteract(chef);
    }

    @Override
    public void update() {
        if (itemOnStation instanceof BaseCookingDevice) {
            ((BaseCookingDevice) itemOnStation).processCookingTick();
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2); // Gambar kotak
        
        // Visualisasi Api/Progress jika sedang masak
        if (itemOnStation instanceof BaseCookingDevice) {
            BaseCookingDevice utensil = (BaseCookingDevice) itemOnStation;
            if (!utensil.isEmpty()) {
                int tileSize = 48; // Hardcode size
                g2.setColor(Color.ORANGE);
                g2.fillRect(posX * tileSize + 5, posY * tileSize + 5, 12, 12);
            }
        }
    }
}