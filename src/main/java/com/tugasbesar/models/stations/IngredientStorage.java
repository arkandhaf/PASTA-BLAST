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
        this.itemOnStation = createNewIngredient(); 
    }

    @Override
    public void interact(Chef chef) {
        Item hand = chef.getHeldItem();
        Item tableItem = itemOnStation;
        
        // --- 1. INTERAKSI PLATING (ASSEMBLY) ---
        
        // A. Piring di tangan Chef dan stasiun berisi bahan sumber: GANTI LOGIKA JADI SWAP/PLACE PLATE
        if (chef.hasItem() && hand instanceof Plate && tableItem != null && tableItem.getName().equalsIgnoreCase(ingredientName)) {
            
            // Chef menaruh Piring, lalu mengambil bahan sumber ke tangannya. (Swap Implisit)
            Item itemToPlace = chef.getHeldItem();
            chef.setHeldItem(takeItem()); // Ambil Bahan Sumber
            placeItem(itemToPlace); // Taruh Plate
            
            // Stasiun diisi kembali dengan bahan baru
            itemOnStation = createNewIngredient(); 
            
            System.out.println("🔄 [Storage: Place Plate] Piring ditaruh di meja. Chef sekarang memegang " + chef.getHeldItem().getName() + ".");
            return;
        }

        // B. Piring di meja: Masukkan bahan di tangan ke piring
        if (tableItem instanceof Plate && chef.hasItem() && hand instanceof Processable) { 
            Plate p = (Plate) tableItem;
            Processable ing = (Processable) hand;
            
            // Validasi tambahan: Piring di Stasiun hanya boleh menerima bahan yang sudah dimasak/diproses (COOKED/CUT)
            // Namun, karena ini IngredientStorage, kita asumsikan piring di sini hanya untuk Assembly RAW/CUT.
            // Jika Anda ingin hanya COOKED, logika di Plate.canAccept() harus memvalidasinya.
            
            if (p.canAccept(ing)) { 
                p.addIngredient(ing);
                chef.setHeldItem(null); 
                
                System.out.println("✅ [Storage: Assembly] Memasukkan " + ing.getName() + " ke dalam Piring di meja.");
                return;
            } else {
                System.out.println("⚠️ [Storage] Tidak bisa dimasukkan. Piring kotor atau bahan tidak siap/cocok.");
                return;
            }
        }
        
        // --- 2. INTERAKSI AMBIL BAHAN SUMBER (Infinite Source) ---

        // Chef tangan kosong DAN item di meja adalah bahan sumber
        if (!chef.hasItem() && tableItem != null && tableItem.getName().equalsIgnoreCase(ingredientName)) {
            
            // Ambil bahan sumber (selalu ke tangan Chef)
            chef.setHeldItem(takeItem()); 
            
            // Isi kembali stasiun dengan item baru (Infinite source logic)
            itemOnStation = createNewIngredient(); 
            
            System.out.println("🥄 [Storage] " + chef.getName() + " mengambil " + chef.getHeldItem().getName() + " & Stasiun terisi kembali.");
            return;
        }

        // --- 3. FALLBACK (Item/Plate di Meja, Swap, atau Taruh) ---
        
        // Ini akan menangani:
        // - SWAP (Jika Chef bawa item dan ada Plate/item lain tertinggal di meja)
        // - TAKE (Jika Chef tangan kosong dan ada Plate/item tertinggal di meja)
        // - PLACE (Jika Chef bawa item dan meja kosong)
        defaultInteract(chef);
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