package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.interfaces.Preparable;
import com.tugasbesar.models.item.IngredientFactory;
import com.tugasbesar.models.item.kitchen_utensil.Plate;

public class IngredientStorage extends Station {
    
    private String ingredientName; // "Tomato", "Beef", dll

    public IngredientStorage(int x, int y, String ingredientName) {
        // x, y, Nama, Symbol "I"
        super(x, y, "Storage: " + ingredientName, "I");
        this.ingredientName = ingredientName;
    }

    @Override
    public void interact(Chef chef) {
        Item hand = chef.getHeldItem();
        Item tableItem = itemOnStation;

        // ==========================================
        // 1. TANGAN CHEF KOSONG (AMBIL)
        // ==========================================
        if (hand == null) {
            
            // A. Kalau ada barang tertinggal di atas kotak -> AMBIL ITU DULU
            if (tableItem != null) {
                chef.setHeldItem(takeItem());
                System.out.println("[Storage] Mengambil " + chef.getHeldItem().getName() + " dari atas kotak.");
            } 
            
            // B. Kalau kotak kosong -> SPAWN BAHAN BARU (Unlimited)
            else {
                spawnIngredient(chef);
            }
            return;
        }

        // ==========================================
        // 2. TANGAN CHEF ADA ITEM (TARUH / RAKIT)
        // ==========================================
        if (hand != null) {

            // C. LOGIC RAKIT (ASSEMBLY LITE)
            // Kasus: Di atas kotak ada Piring, Chef bawa Bahan -> Masukin Bahan ke Piring
            if (tableItem instanceof Plate && hand instanceof Preparable) {
                Plate p = (Plate) tableItem;
                Preparable ing = (Preparable) hand;
                
                // Cek apakah piring mau nerima (Logic Plate biasa)
                // (Kamu bisa copy logic validasi Pasta dari AssemblyStation kalau mau strict, 
                //  atau biarkan Plate.addIngredient yang handle validasi dasar)
                if (p.canAccept(ing)) { // Asumsi Plate punya method ini/mirip
                    p.addIngredient(ing);
                    chef.setHeldItem(null); // Bahan masuk piring
                    System.out.println("[Storage] Merakit " + ing.getName() + " ke dalam Piring.");
                    return;
                }
            }

            // D. LOGIC TARUH BIASA
            // Kalau kotak kosong -> Taruh barang apa aja (Piring/Panci/Bahan)
            if (isEmpty()) {
                placeItem(hand);
                chef.setHeldItem(null);
                System.out.println("[Storage] Menaruh " + itemOnStation.getName() + " di atas kotak " + ingredientName);
            } 
            else {
                System.out.println("[!] Tempat penuh! Tidak bisa menumpuk barang.");
            }
        }
    }

    // Helper untuk Spawn Bahan (Sama kayak factory sebelumnya)
    private void spawnIngredient(Chef chef) {
        switch (ingredientName.toLowerCase()) {
            case "tomato":
                chef.setHeldItem(IngredientFactory.createTomato());
                break;
            case "beef":
                chef.setHeldItem(IngredientFactory.createBeef());
                break;
            case "pasta":
                chef.setHeldItem(IngredientFactory.createPasta());
                break;
            default:
                System.out.println("[Error] Tipe bahan tidak dikenal: " + ingredientName);
                return;
        }
        System.out.println("[Storage] Spawn bahan baru: " + ingredientName);
    }
}