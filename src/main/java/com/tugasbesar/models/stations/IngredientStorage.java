package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item;
// Hapus: import com.tugasbesar.models.interfaces.Preparable;

// Import interface baru yang relevan
import com.tugasbesar.models.interfaces.Processable; 

import com.tugasbesar.models.item.IngredientFactory;
import com.tugasbesar.models.item.kitchen_utensil.Plate;

public class IngredientStorage extends Station {
    
    private String ingredientName; 

    public IngredientStorage(int x, int y, String ingredientName) {
        super(x, y, "Storage: " + ingredientName, "I");
        this.ingredientName = ingredientName;
    }

    @Override
    public void interact(Chef chef) {
        Item hand = chef.getHeldItem();
        Item tableItem = itemOnStation;

        
        // 1. TANGAN CHEF KOSONG (AMBIL) 
        if (hand == null) {
            
            // A. kalau ada barang tertinggal di atas kotak -> AMBIL ITU DULU
            if (tableItem != null) {
                chef.setHeldItem(takeItem());
                System.out.println("[Storage] Mengambil " + chef.getHeldItem().getName() + " dari atas kotak.");
            } 
            
            // B. SPAWN BAHAN BARU (Unlimited)
            else {
                spawnIngredient(chef);
            }
            return;
        }


        // 2. TANGAN CHEF ADA ITEM (TARUH / RAKIT)
        if (hand != null) {
            
            // Logic Plating
            // kalau di atas kotak ada piring, Chef bawa bahan (Processable) -> masukin bahan ke piring
            if (tableItem instanceof Plate && hand instanceof Processable) { 
                Plate p = (Plate) tableItem;
                Processable ing = (Processable) hand;
                
                // Plate.canAccept sekarang menerima Processable
                if (p.canAccept(ing)) { 
                    p.addIngredient(ing);
                    chef.setHeldItem(null); 
                    System.out.println("[Storage] Merakit " + ing.getName() + " ke dalam Piring.");
                    return;
                } else {
                    // Pesan jika piring kotor atau item tidak bisa ditaruh (misal: gosong)
                    System.out.println("[Storage] Tidak bisa merakit. Piring kotor atau bahan tidak siap/cocok.");
                    return;
                }
            }

            // kalau di atas kotak kosong -> taruh barang apa aja (piring/panci/bahan)
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

    // helper untuk spawn bahan (sama kayak factory)
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
            case "fish": 
                chef.setHeldItem(IngredientFactory.createFish());
                break;
            case "shrimp":
                chef.setHeldItem(IngredientFactory.createShrimp());
                break;
            default:
                System.out.println("[Error] Tipe bahan tidak dikenal: " + ingredientName);
                return;
        }
        System.out.println("[Storage] Spawn bahan baru: " + ingredientName);
    }
}