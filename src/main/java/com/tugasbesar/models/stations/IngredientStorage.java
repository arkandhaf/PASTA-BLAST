package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.interfaces.Processable; 
import com.tugasbesar.models.item.IngredientFactory;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import com.tugasbesar.models.item.kitchen_utensil.BaseCookingDevice; 

public class IngredientStorage extends Station {
    
    private String ingredientName; 

    public IngredientStorage(int x, int y, String ingredientName) {
        super(x, y, "Storage: " + ingredientName, "I");
        this.ingredientName = ingredientName;
        this.itemOnStation = createNewIngredient(); 
    }

    @Override
    public void interact(Chef chef) {
        Item hand = chef.getHeldItem();
        Item tableItem = itemOnStation;
        
        // --- 1. INTERAKSI KHUSUS PLATING/ASSEMBLY (Prioritas Tertinggi) ---

        // A. PLATING KE STASIUN (Utensil di tangan -> Piring di Meja)
        if (tableItem instanceof Plate plateOnTable && chef.hasItem() && hand instanceof BaseCookingDevice utensilInHand) { 
            
            if (!utensilInHand.isEmpty() && !plateOnTable.isDirty()) {
                Processable ingredient = utensilInHand.getContents().get(0);
                
                if (plateOnTable.canAccept(ingredient)) {
                    Processable ingredientToPlate = (Processable) utensilInHand.takeItem(); 
                    plateOnTable.addIngredient(ingredientToPlate); 
                    
                    System.out.println("✅ [Storage: Plating (Utensil->Plate)] " + ingredientToPlate.getName() + " pindah dari Utensil ke Piring di meja.");
                    return;
                } 
            }
        }
        
        // B. PLATING DARI STASIUN (Bahan di Meja -> Piring di Tangan)
        if (tableItem != null && tableItem.getName().equalsIgnoreCase(ingredientName) && chef.hasItem() && hand instanceof Plate plateInHand) {
            
            Processable ingredientOnTable = (Processable) tableItem; 
            
            if (!plateInHand.isDirty() && plateInHand.canAccept(ingredientOnTable)) {
                
                Item takenItem = takeItem(); // Ambil bahan sumber
                plateInHand.addIngredient((Processable) takenItem); // Plating ke Piring di tangan
                itemOnStation = createNewIngredient(); // Isi kembali stasiun
                
                System.out.println("✅ [Storage: Plating (Meja->Plate)] " + ingredientOnTable.getName() + " pindah ke piring di tangan.");
                return;
            } 
        }

        // --- 2. AMBIL ITEM TERTINGGAL/NON-SUMBER (Sesuai Spek Poin 2) ---

        // Jika tangan kosong DAN item di meja BUKAN bahan sumber
        // (Ini berarti ada Plate atau item lain yang tertinggal/ditaruh di atas Storage)
        if (!chef.hasItem() && tableItem != null && !tableItem.getName().equalsIgnoreCase(ingredientName)) {
             chef.setHeldItem(takeItem()); // Ambil item yang tertinggal itu
             System.out.println("🥄 [Storage] " + chef.getName() + " mengambil item tertinggal: " + chef.getHeldItem().getName());
             return;
        }


        // --- 3. AMBIL BAHAN SUMBER (Infinite Source - Sesuai Spek Poin 1) ---

        // Chef tangan kosong DAN item di meja adalah bahan sumber
        if (!chef.hasItem() && tableItem != null && tableItem.getName().equalsIgnoreCase(ingredientName)) {
            
            chef.setHeldItem(takeItem()); // Ambil bahan sumber
            itemOnStation = createNewIngredient(); // Isi kembali stasiun (Infinite source logic)
            
            System.out.println("🥄 [Storage] " + chef.getName() + " mengambil " + chef.getHeldItem().getName() + " & Stasiun terisi kembali.");
            return;
        }
        
        // --- 4. FALLBACK (Item di tangan dan Meja Kosong/Ada Item Lain) ---
        
        // Ini akan menangani:
        // - SWAP (Jika Chef bawa item dan ada Plate/item lain tertinggal di meja)
        // - PLACE (Jika Chef bawa item dan meja kosong)
        defaultInteract(chef);
    }

    @Override
    public void update() {
        // IngredientStorage tidak memiliki proses yang berjalan otomatis
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
                return null;
        }
    }
}