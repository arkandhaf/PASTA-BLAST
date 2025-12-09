package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.interfaces.Processable; 
import com.tugasbesar.models.item.IngredientFactory;
import com.tugasbesar.models.item.kitchen_utensil.Plate;

public class IngredientStorage extends Station {
    
    private String ingredientName; 

    public IngredientStorage(int x, int y, String ingredientName) {
        super(x, y, "Storage: " + ingredientName, "I");
        this.ingredientName = ingredientName;
        // Inisialisasi: Pastikan Item pertama sudah ada di Station saat game dimulai
        this.itemOnStation = createNewIngredient(); 
    }

    @Override
    public void interact(Chef chef) {
        Item hand = chef.getHeldItem();
        Item tableItem = itemOnStation;
        
        // --- 1. Plating (Assembly) Logic ---
        
        // A. Piring di tangan: Ambil bahan dari Storage ke piring
        if (hand instanceof Plate && tableItem instanceof Processable) {
            Plate plate = (Plate) hand;
            Processable ingredient = (Processable) tableItem;
            
            // Pastikan item di meja adalah ingredient utama dari storage
            if (tableItem.getName().equalsIgnoreCase(ingredientName) && !plate.isDirty() && plate.canAccept(ingredient)) {
                
                takeItem(); 
                plate.addIngredient(ingredient);
                
                // auto spawn new ingredient
                itemOnStation = createNewIngredient(); 
                
                System.out.println("[Storage: Assembly] " + ingredient.getName() + " berhasil dimasukkan ke piring.");
                return;
            }
        }
        
        // B. Piring di meja: Masukkan bahan di tangan ke piring
        if (tableItem instanceof Plate && hand instanceof Processable) { 
            Plate p = (Plate) tableItem;
            Processable ing = (Processable) hand;
            
            if (p.canAccept(ing)) { 
                
                p.addIngredient(ing);
                chef.setHeldItem(null); 
                
                System.out.println("[Storage: Assembly] Memasukkan " + ing.getName() + " ke dalam Piring di meja.");
                return;
            } else {
                System.out.println("[Storage] Tidak bisa dimasukkan. Piring kotor atau bahan tidak siap/cocok.");
                return;
            }
        }
        
        // --- 2. Take / Place / Swap Logic ---

        // C. TARUH ITEM (Chef bawa item, Stasiun ada item BUKAN ingredient utama)
        // Ini adalah skenario ketika Chef ingin menaruh Plate atau item lain di atas Storage.
        if (chef.hasItem() && tableItem != null && !tableItem.getName().equalsIgnoreCase(ingredientName)) {
             // Jika item di meja BUKAN item sumber (misal: piring lama), lakukan swap/place
             
             if (hand instanceof Plate || hand instanceof Processable) {
                // Lakukan swap item, karena itemOnStation saat ini adalah Plate/item lain, bukan bahan sumber
                Item temp = chef.getHeldItem();
                chef.setHeldItem(itemOnStation);
                itemOnStation = temp;
                System.out.println("[Storage: Swap] Item di tangan ditukar dengan item di meja (" + chef.getHeldItem().getName() + ").");
                return;
             } else {
                // Pengecekan keamanan: Jika item di tangan adalah sesuatu yang tidak relevan, tolak.
                System.out.println("[Storage] Tidak bisa menaruh item ini di atas item yang ada.");
                return;
             }
        }

        // D. AMBIL ITEM (Chef tangan kosong)
        if (hand == null) {
            if (tableItem != null) {
                
                // i. Ambil Ingredient Utama (Sumber Tak Terbatas)
                if (tableItem.getName().equalsIgnoreCase(ingredientName)) {
                    
                    chef.setHeldItem(takeItem()); 
                    
                    // Isi kembali stasiun dengan item baru
                    itemOnStation = createNewIngredient(); 
                    
                    System.out.println("[Storage] " + chef.getName() + " mengambil " + chef.getHeldItem().getName() + " & Stasiun terisi kembali.");
                
                // ii. Ambil Item Non-Sumber (Plate/Item Tertinggal)
                } else {
                    chef.setHeldItem(takeItem());
                    System.out.println("[Storage] " + chef.getName() + " mengambil " + chef.getHeldItem().getName() + " yang tertinggal di atas kotak.");
                }
            } 
            return;
        }

        // E. TARUH PLATE/ITEM (Chef bawa item, Stasiun ada ingredient utama)
        // Chef ingin menaruh Plate/item lain di atas Ingredient utama yang tak terbatas.
        if (chef.hasItem() && tableItem != null && tableItem.getName().equalsIgnoreCase(ingredientName)) {
            if (hand instanceof Plate || (hand instanceof Processable && !hand.getName().equalsIgnoreCase(ingredientName))) {
                
                Item itemToPlace = chef.getHeldItem();
                
                // Cek agar tidak menaruh dua item Processable di atas satu sama lain 
                // (kecuali Plate, karena Plate adalah wadah, tapi ini berisiko dalam logic Assembly B)
                if (tableItem instanceof Processable && hand instanceof Processable) {
                    System.out.println("[Storage] Tidak bisa menaruh item di atas bahan sumber yang terbuka.");
                    return;
                }
                
                // Jika Chef bawa Plate, taruh plate dan ambil bahan sumber di bawahnya (Swap Implisit)
                if (hand instanceof Plate) {
                    chef.setHeldItem(takeItem()); // Ambil Bahan Sumber
                    placeItem(itemToPlace); // Taruh Plate
                    // Catatan: Chef sekarang memegang bahan sumber
                    System.out.println("[Storage: Place Plate] Piring ditaruh. Chef sekarang memegang " + chef.getHeldItem().getName() + ".");
                    return;
                }
                
                System.out.println("[Storage] Tempat penuh atau interaksi menaruh tidak valid.");
                return;
            }
        }
        
        System.out.println("[!] Tempat penuh atau interaksi tidak valid (Bukan Plating/Ambil).");
    }

    // Helper: membuat ingredient baru (tanpa pesan log)
    private Item createNewIngredient() {
        // Implementasi tetap sama, diasumsikan IngredientFactory sudah benar.
        switch (ingredientName.toLowerCase()) {
            case "tomato":
                return IngredientFactory.createTomato();
            case "beef":
                return IngredientFactory.createBeef();
            case "pasta":
                return IngredientFactory.createPasta();
            case "fish": 
                return IngredientFactory.createFish();
            case "shrimp":
                return IngredientFactory.createShrimp();
            default:
                // Error pada runtime jika nama bahan tidak terdaftar
                return null;
        }
    }
}