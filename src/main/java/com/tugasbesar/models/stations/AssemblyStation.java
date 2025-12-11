package com.tugasbesar.models.stations;

import java.util.ArrayList;
import java.util.List;

import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.actors.Chef; 
import com.tugasbesar.models.enums.IngredientState;
import com.tugasbesar.models.interfaces.Processable;
import com.tugasbesar.models.item.Dish;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import com.tugasbesar.models.manager.OrderManager;
import com.tugasbesar.models.manager.Recipe;


public class AssemblyStation extends Station {

    public AssemblyStation(int x, int y) {
        super(x, y, "Assembly Station", "A"); 
    }

    @Override
    public void interact(Chef chef) {
        Item hand = chef.getHeldItem();
        Item tableItem = itemOnStation;

        // 1. RAKIT (ASSEMBLY)
        // Jika chef tangan kosong & di meja ada piring berisi -> Coba rakit jadi Dish
        if (hand == null && tableItem instanceof Plate && !((Plate)tableItem).isEmpty()) {
            performAssembly((Plate) tableItem);
            return;
        }
        // Jika chef bawa piring berisi & meja kosong -> Coba rakit jadi Dish
        if (hand instanceof Plate && !((Plate)hand).isEmpty() && tableItem == null) {
            performAssembly((Plate) hand);
            return;
        }
        
        // --- LOGIC PLATING (Menaruh Bahan ke Piring) ---
        
        // Case A: Piring di Tangan, Bahan di Meja
        if (hand instanceof Plate && tableItem instanceof Processable) { 
            performPlating((Plate) hand, (Processable) tableItem);
            this.itemOnStation = null; // hapus bahan dari meja
            return;
        }

        // Case B: Bahan di Tangan, Piring di Meja
        if (hand instanceof Processable && tableItem instanceof Plate) { 
            performPlating((Plate) tableItem, (Processable) hand);
            chef.setHeldItem(null); // hapus bahan dari tangan
            return;
        }

        // --- LOGIC TARUH/AMBIL STANDAR ---
        
        // Taruh Piring (Hanya piring yang boleh ditaruh jika meja kosong)
        if (hand instanceof Plate && isEmpty()) {
            placeItem(hand);
            chef.setHeldItem(null);
            System.out.println("✅ [Assembly] Menaruh Piring.");
            return;
        }
        
        // Ambil Item dari meja
        if (hand == null && !isEmpty()) {
            chef.setHeldItem(takeItem());
            System.out.println("✅ [Assembly] Mengambil " + chef.getHeldItem().getName());
            return;
        }

        // Blocker: Jangan taruh sampah sembarangan
        if (chef.hasItem() && isEmpty() && !(hand instanceof Plate)) {
            System.out.println("⚠️ [Assembly] Hanya Piring yang boleh ditaruh di sini.");
            return;
        }
    }

    // Helper: Taruh bahan ke piring
    private void performPlating(Plate plate, Processable item) {
        // Cek validasi bahan
        if (!isValidForPlating(item)) {
            return;
        }

        // Cek apakah piring sudah ada Dish jadi
        if (plate.getContents().stream().anyMatch(content -> content instanceof Dish)) {
            System.out.println("⚠️ [Assembly] Piring sudah ada Hidangan Jadi.");
            return;
        }
        
        plate.addIngredient(item); 
        System.out.println("🥗 [Assembly] Menambahkan " + item.getName() + " ke piring.");
    }
    
    // Helper: Validasi bahan sebelum masuk piring
    private boolean isValidForPlating(Processable item) {
        if (item.getState() == IngredientState.RAW || item.getState() == IngredientState.BURNED) {
            System.out.println("⚠️ [Assembly] Gagal: Bahan harus matang (COOKED) atau dipotong (CHOPPED).");
            return false;
        }
        return true;
    }

    // Helper: Cek Resep & Jadikan Dish
    private void performAssembly(Plate plate) {
        List<Processable> contents = plate.getContents();
        
        // Cek apakah isinya sudah Dish?
        if (contents.size() == 1 && contents.get(0) instanceof Dish) {
            System.out.println("✅ [Assembly] Hidangan sudah matang sempurna.");
            return;
        }
        
        // Ambil nama bahan-bahan
        List<String> ingredientNames = new ArrayList<>();
        for (Processable item : contents) {
            ingredientNames.add(item.getName());
        }
        
        // Cek Resep ke OrderManager
        Recipe recipeMatch = OrderManager.getInstance().findMatchingRecipe(ingredientNames); 
        
        if (recipeMatch != null) {
            plate.clearContents(); 
        
            // buat objek dish baru 
            Dish finalDish = new Dish(recipeMatch.getRecipeName(), ingredientNames); 
            
            // Tambahkan dish tunggal ke piring
            plate.addIngredient(finalDish); 
            
            System.out.println("🎉 [Assembly] SUKSES! Jadi: " + finalDish.getRecipeName());
            
        } else {
            // String ingredientList = String.join(" + ", ingredientNames);
            System.out.println("❌ [Assembly] Gagal: Resep tidak ditemukan untuk kombinasi ini.");
        }
    }
}