package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.interfaces.Processable; 
import com.tugasbesar.models.item.IngredientFactory;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import com.tugasbesar.models.item.kitchen_utensil.BaseCookingDevice; 
import com.tugasbesar.models.enums.IngredientState; // Pastikan Enum ini ada

import java.awt.Color;

public class IngredientStorage extends Station {
    
    private String ingredientName; 

    public IngredientStorage(int x, int y, String ingredientName) {
        super(x, y, "Storage: " + ingredientName, "I");
        this.ingredientName = ingredientName;
        // itemOnStation diinisialisasi sebagai null, 
        // karena bahan sumber tak terbatas (diambil dari factory saat interaksi).
        // itemOnStation hanya akan berisi item yang ditaruh oleh Chef.
        this.itemOnStation = null; 
    }

    @Override
    public void interact(Chef chef) {
        Item hand = chef.getHeldItem();
        Item tableItem = itemOnStation;
        
        // --- 1. LOGIC PLATING / ASSEMBLY ---
        
        // A. PLATING KE STASIUN (Utensil di tangan -> Piring di meja)
        if (tableItem instanceof Plate plateOnTable && chef.hasItem() && hand instanceof BaseCookingDevice utensilInHand) { 
            
            if (!utensilInHand.isEmpty() && !plateOnTable.isDirty()) {
                Processable ingredient = utensilInHand.getContents().get(0);
                
                if (plateOnTable.canAccept(ingredient)) {
                    Processable ingredientToPlate = (Processable) utensilInHand.takeItem(); 
                    plateOnTable.addIngredient(ingredientToPlate); 
                    
                    System.out.println("✅ [Storage: Plating (Utensil->Plate)] " + ingredientToPlate.getName() + " pindah dari Utensil ke Piring di meja.");
                    notifyInteraction("Plated from Utensil", new Color(33, 150, 243));
                    return;
                } 
            }
        }
        
        // B. PLATING DARI BAHAN SUMBER (Bahan Sumber -> Piring di Tangan)
        // Ini berlaku jika Chef memegang Piring dan berinteraksi dengan Storage (walaupun itemOnStation null, dianggap ada Bahan Sumber)
        if (chef.hasItem() && hand instanceof Plate plateInHand) {
            
            Item ingredientSource = createNewIngredient();
            if (ingredientSource instanceof Processable ingredientOnTable && !plateInHand.isDirty() && plateInHand.canAccept(ingredientOnTable)) {
                
                // Plating ke Piring di tangan, bahan sumber baru dibuat saat itu juga.
                plateInHand.addIngredient(ingredientOnTable); 
                
                System.out.println("✅ [Storage: Plating (Source->Plate)] " + ingredientOnTable.getName() + " pindah ke piring di tangan.");
                notifyInteraction("Plated " + ingredientOnTable.getName(), new Color(76, 175, 80));
                return;
            }
        }
        
        // --- 2. LOGIC PENGAMBILAN (TAKE) ---

        // C. AMBIL ITEM TERTINGGAL (Prioritas Utama, jika tangan kosong dan ada item ditaruh di sini)
        // Spesifikasi: "Jika di Ingredient Storage terdapat item di atasnya, yang diambil terlebih dahulu adalah item di atasnya..."
        if (!chef.hasItem() && tableItem != null) {
             chef.setHeldItem(takeItem()); // Ambil item yang tertinggal itu
             System.out.println("🥄 [Storage] " + chef.getName() + " mengambil item tertinggal: " + chef.getHeldItem().getName());
             notifyInteraction("Took " + chef.getHeldItem().getName(), new Color(156, 39, 176));
             return;
        }

        // D. AMBIL BAHAN SUMBER (Jika tangan kosong DAN TIDAK ADA item tertinggal)
        // Spesifikasi: "...inventori chef akan otomatis terisi oleh bahan ingredient terkait"
        if (!chef.hasItem() && tableItem == null) {
            Item newItem = createNewIngredient();

            if (newItem != null) {
                chef.setHeldItem(newItem);
                System.out.println("📦 [Storage] Mengambil " + newItem.getName() + " dari stok tak terbatas.");
                notifyInteraction("Picked " + newItem.getName(), new Color(76, 175, 80));
            } else {
                System.out.println("❌ Error: Bahan '" + ingredientName + "' tidak ditemukan di Factory.");
                notifyInteraction("Missing " + ingredientName, new Color(244, 67, 54));
            }
            return;
        }
        
        // --- 3. LOGIC PENARUHAN (PLACE/SWAP) ---
        
        // E. TARUH/SWAP (Jika tangan penuh)
        defaultInteract(chef);
    }

    @Override
    public void update() {
        // IngredientStorage tidak memiliki proses yang berjalan otomatis
    }

    // Helper: MENGHUBUNGKAN STRING DARI MAP KE FACTORY (Tidak perlu mengubahnya)
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