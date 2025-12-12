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

    // --- GRAB: TARUH / AMBIL (SPACE) ---
    @Override
    public void interactGrab(Chef chef) {
        Item hand = chef.getHeldItem();

        // 1. PLATING LOGIC (Piring ambil dari meja)
        if (hand instanceof Plate && !isEmpty()) {
            Plate plate = (Plate) hand;
            Item itemOnTable = itemOnStation;
            if (itemOnTable instanceof Processable) {
                Processable ingredient = (Processable) itemOnTable;
                if (plate.canAccept(ingredient)) {
                    Item takenItem = takeItem();
                    plate.addIngredient((Processable) takenItem);
                    
                    // Reset Status
                    resetCuttingStatus();
                    
                    notifyInteraction(takenItem, "Added to plate", new Color(3, 169, 244));
                    return;
                }
            }
        }

        // 2. TARUH ITEM (Input)
        if (chef.hasItem() && isEmpty()) {
            
            // A. Kalau bawa Panci/Piring (Cuma numpang taruh)
            if (hand instanceof Plate || hand instanceof BaseCookingDevice) {
                placeItem(hand);
                chef.setHeldItem(null);
                notifyInteraction(hand, "Placed", new Color(0, 188, 212));
                return;
            }
            
            // B. Kalau bawa Bahan (Ingredient)
            if (hand instanceof Ingredient) {
                Ingredient ing = (Ingredient) hand;
                
                // [FIX] Cek apakah bahan INI boleh dipotong?
                // Pasta (Raw) -> canBeChopped() = false -> Ditolak
                // Tomato (Raw) -> canBeChopped() = true -> Diterima
                if (ing.canBeChopped()) {
                    placeItem(hand);
                    chef.setHeldItem(null);
                    notifyInteraction(hand, "Ready to chop", new Color(63, 81, 181));
                } else {
                    System.out.println("⚠️ [Cutting] Bahan ini tidak perlu dipotong!");
                    notifyInteraction(hand, "No need chop", new Color(244, 67, 54));
                }
                return;
            }
        }

        // 3. AMBIL ITEM (Output)
        if (!chef.hasItem() && !isEmpty()) {
            // [FIX] Selalu izinkan ambil, dan paksa stop cutting
            chef.setHeldItem(takeItem());
            resetCuttingStatus(); // Reset progress bar & status
            
            System.out.println("⬆️ [Cutting] Mengambil item.");
            notifyInteraction(chef.getHeldItem(), "Picked", new Color(255, 193, 7));
        }
    }

    // --- USE: PROSES POTONG (E) ---
    @Override
    public void interactUse(Chef chef) {
        // Hanya bisa motong kalau ada barang & chef tangan kosong
        if (!isEmpty() && !chef.hasItem()) {
            if (itemOnStation instanceof Choppable && ((Choppable) itemOnStation).canBeChopped()) {
                if (itemOnStation instanceof Ingredient) {
                    Ingredient ing = (Ingredient) itemOnStation;
                    
                    // Hanya potong kalau masih RAW
                    if (ing.getState() == IngredientState.RAW) {
                        this.chefAtStation = chef;
                        chef.setBusy(true); // Tahan Chef
                        isCutting = true;
                        cutProgress += CUT_SPEED;
                        notifyInteraction(itemOnStation, "Cutting...", new Color(33, 150, 243));
                        
                        if (cutProgress >= MAX_PROGRESS) {
                            finishCutting();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void update() {
        // Jika tombol E dilepas (tidak ada interactUse), kita anggap stop cutting
        // Logic ini ditangani oleh Chef.java yang memanggil interactUse terus menerus saat ditahan.
        // Kita hanya perlu safety check.
        
        // Reset progress pelan-pelan kalau ditinggal (Opsional, tapi bagus buat gameplay)
        if (!isCutting && cutProgress > 0) {
            // cutProgress--; // Uncomment kalau mau progress turun sendiri saat ditinggal
        }
        
        // Reset flag cutting setiap frame, nanti interactUse akan men-set true lagi kalau ditekan
        isCutting = false; 
        if (chefAtStation != null) {
            chefAtStation.setBusy(false);
            chefAtStation = null;
        }
    }

    private void finishCutting() {
        isCutting = false;
        cutProgress = 0;
        
        if (itemOnStation instanceof Choppable)
            ((Choppable) itemOnStation).chop();
            
        if (itemOnStation != null) {
            notifyInteraction(itemOnStation, "Chopped!", new Color(0, 200, 83));
        }
    }
    
    private void resetCuttingStatus() {
        isCutting = false;
        cutProgress = 0;
        if (chefAtStation != null) {
            chefAtStation.setBusy(false);
            chefAtStation = null;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2);
        // Gambar Bar Hijau
        if (cutProgress > 0) {
            int width = (int) ((double) cutProgress / MAX_PROGRESS * 40);
            g2.setColor(Color.GREEN);
            g2.fillRect(posX * 48 + 4, posY * 48 - 10, width, 6);
            
            // Border Bar
            g2.setColor(Color.BLACK);
            g2.drawRect(posX * 48 + 4, posY * 48 - 10, 40, 6);
        }
    }
}