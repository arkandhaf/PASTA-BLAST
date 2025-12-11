package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item; 
import com.tugasbesar.models.interfaces.Choppable; 
import com.tugasbesar.models.interfaces.Processable; 
import com.tugasbesar.models.enums.IngredientState;
import com.tugasbesar.models.item.Ingredient;
import com.tugasbesar.models.item.kitchen_utensil.Plate; 
import com.tugasbesar.models.item.kitchen_utensil.BaseCookingDevice; 

import java.awt.Color;
import java.awt.Graphics2D;

public class CuttingStation extends Station {
    
    private boolean isCutting = false;
    private int cutProgress = 0;
    private final int MAX_PROGRESS = 100;
    private final int CUT_SPEED = 2; 

    public CuttingStation(int x, int y) {
        super(x, y, "Cutting Board", "C");
    }

    @Override
    public void interact(Chef chef) {
        Item hand = chef.getHeldItem();

        // -----------------------------------------------------------
        // 1. PLATING LOGIC (PENTING: Chef bawa Piring -> Ambil isi Meja)
        // -----------------------------------------------------------
        if (hand instanceof Plate && !isEmpty()) {
            if (isCutting) {
                System.out.println("⚠️ Tunggu selesai memotong!");
                return;
            }

            Plate plate = (Plate) hand;
            Item itemOnTable = itemOnStation;

            // Cek apakah item di meja adalah bahan makanan (Processable)
            if (itemOnTable instanceof Processable) {
                Processable ingredient = (Processable) itemOnTable;
                
                // Coba masukkan ke piring
                if (plate.canAccept(ingredient)) {
                    // Ambil item dari meja
                    Item takenItem = takeItem();
                    // Masukkan ke piring
                    plate.addIngredient((Processable) takenItem);
                    
                    // Reset progress bar visual
                    cutProgress = 0; 
                    System.out.println("🍽️ [Cutting] " + takenItem.getName() + " dimasukkan ke Piring.");
                    return;
                }
            }
        }

        // -----------------------------------------------------------
        // 2. TARUH ITEM (Chef bawa barang -> Meja kosong)
        // -----------------------------------------------------------
        if (chef.hasItem() && isEmpty()) {
            // Logic taruh Piring/Alat/Bahan (Sama seperti sebelumnya)
            if (hand instanceof Plate || hand instanceof BaseCookingDevice) {
                placeItem(hand);
                chef.setHeldItem(null);
                return;
            }
            if (hand instanceof Ingredient) {
                placeItem(hand);
                chef.setHeldItem(null);
                return;
            }
        }

        // -----------------------------------------------------------
        // 3. MULAI MEMOTONG (Meja ada bahan, Chef tangan kosong)
        // -----------------------------------------------------------
        if (!chef.hasItem() && !isEmpty()) {
            // Cek bisa dipotong?
            if (itemOnStation instanceof Choppable && ((Choppable)itemOnStation).canBeChopped()) {
                // Cek status lagi biar aman
                if (itemOnStation instanceof Ingredient) {
                    Ingredient ing = (Ingredient) itemOnStation;
                    if (ing.getState() == IngredientState.RAW && !isCutting) {
                        this.chefAtStation = chef; 
                        chef.setBusy(true); 
                        isCutting = true;   
                        cutProgress = 0;
                        System.out.println("🔪 [Cutting] Mulai memotong...");
                        return; 
                    }
                }
            }
        }

        // -----------------------------------------------------------
        // 4. AMBIL ITEM (Manual pakai tangan)
        // -----------------------------------------------------------
        if (!chef.hasItem() && !isEmpty()) {
            if (isCutting) return;
            chef.setHeldItem(takeItem());
            cutProgress = 0;
            System.out.println("⬆️ [Cutting] Mengambil item.");
        }
    }

    @Override
    public void update() {
        if (isCutting && chefAtStation != null) {
            cutProgress += CUT_SPEED;
            if (cutProgress >= MAX_PROGRESS) finishCutting();
        }
    }

    private void finishCutting() {
        isCutting = false;
        cutProgress = 0;
        if (itemOnStation instanceof Choppable) ((Choppable) itemOnStation).chop();
        if (chefAtStation != null) {
            chefAtStation.setBusy(false); 
            chefAtStation = null;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2); 
        if (cutProgress > 0) {
            int width = (int) ((double)cutProgress / MAX_PROGRESS * 40);
            g2.setColor(Color.GREEN);
            g2.fillRect(posX * 48 + 4, posY * 48 - 10, width, 6);
        }
    }
}