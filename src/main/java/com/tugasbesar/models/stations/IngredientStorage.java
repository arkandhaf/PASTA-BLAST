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
        
    
        
        // Kasus Piring di tangan (Plating Item di Meja)
        if (hand instanceof Plate && tableItem instanceof Processable) {
            Plate plate = (Plate) hand;
            Processable ingredient = (Processable) tableItem;
            
            if (!plate.isDirty() && plate.canAccept(ingredient)) {
                
                takeItem(); 
                plate.addIngredient(ingredient);
                
                //auto spawn new ingre di station
                itemOnStation = createNewIngredient(); 
                
                System.out.println("[Storage: Assembly] " + ingredient.getName() + " berhasil memasukkan ke piring."); //posisi plate dipegang
                return;
            }
        }
        
        // B. Kasus Piring di Meja (Plating Item di Tangan)
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
        
        //AMBIL ITEM DARI MEJA
        if (hand == null) {

            if (tableItem != null) {
                // Hanya izinkan ambil ingredient utama dari storage, bukan item lain yang ditaruh di atasnya.
                if (tableItem.getName().equalsIgnoreCase(ingredientName)) {
                    
                    chef.setHeldItem(takeItem()); // Chef ambil item
                    
                    //unlimited source
                    itemOnStation = createNewIngredient(); 
                    
                    System.out.println("[Storage] " + chef.getName() + " mengambil " + chef.getHeldItem().getName() + " & Stasiun terisi kembali.");
                } else {
                   
                    chef.setHeldItem(takeItem());
                    System.out.println("[Storage] " + chef.getName() + " mengambil " + chef.getHeldItem().getName() + " yang tertinggal di atas kotak.");
                }
            } 
            return;
        }

    
        System.out.println("[!] Tempat penuh atau interaksi tidak valid (Bukan Plating/Ambil).");
    }

    // Helper: membuat ingredient baru (tanpa pesan log)
    private Item createNewIngredient() {
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
                System.out.println("[Error] Tipe bahan tidak dikenal: " + ingredientName);
                return null;
        }
    }
}