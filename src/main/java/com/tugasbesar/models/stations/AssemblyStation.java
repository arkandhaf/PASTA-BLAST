package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.interfaces.Processable; 
import com.tugasbesar.models.interfaces.Placeable; 
import com.tugasbesar.models.enums.IngredientState;

import com.tugasbesar.models.item.Dish;
import com.tugasbesar.models.item.kitchen_utensil.Plate;

import com.tugasbesar.core.models.manager.OrderManager; 
import com.tugasbesar.core.models.manager.Recipe; 

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class AssemblyStation extends Station {

    public AssemblyStation(int x, int y) {
        super(x, y, "Assembly Station", "A"); 
    }

    @Override
    public void interact(Chef chef) {
        Item hand = chef.getHeldItem();
        Item tableItem = itemOnStation;

        // Cek apakah Chef ingin menjalankan Assembly (jika Plate di tangan atau di meja)
        if ((hand == null && tableItem instanceof Plate && !((Plate)tableItem).isEmpty()) ||
            (hand instanceof Plate && !((Plate)hand).isEmpty() && tableItem == null)) {
            
            Plate plateToAssemble = (Plate) (hand != null ? hand : tableItem);
            
            // Assembly hanya berjalan jika piring belum berisi Dish final
            if (!plateToAssemble.getContents().stream().anyMatch(content -> content instanceof Dish)) {
                performAssembly(plateToAssemble);
                return;
            } else {
                System.out.println("[Assembly] Hidangan sudah selesai dirakit. Siap disajikan.");
                // Jika sudah Dish, langsung ambil (Logic Ambil item akan menanganinya)
            }
        }
        
        // --- LOGIC PLATING (Menambah Bahan ke Piring) ---
        
        // C1: piring di tangan, bahan di meja
        if (hand instanceof Plate && tableItem instanceof Processable) { 
            Plate plate = (Plate) hand;
            Processable ingredient = (Processable) tableItem;
            
            if (isValidForPlating(ingredient) && !plate.isDirty() && plate.canAccept(ingredient)) {
                Item takenItem = takeItem(); 
                performPlating(plate, (Processable) takenItem); 
                return;
            }
        }

        // C2: bahan di tangan, piring di meja
        if (hand instanceof Processable && tableItem instanceof Plate) { 
            Plate plate = (Plate) tableItem;
            Processable ingredient = (Processable) hand;
            
            if (isValidForPlating(ingredient) && !plate.isDirty() && plate.canAccept(ingredient)) {
                performPlating(plate, ingredient);
                chef.setHeldItem(null); 
                return;
            }
        }
        
        // --- LOGIC MEJA (Taruh/Ambil) ---

        // Taruh piring (Hanya piring yang boleh ditaruh)
        if (hand instanceof Plate && isEmpty()) {
            placeItem(hand);
            chef.setHeldItem(null);
            System.out.println("[Assembly] " + chef.getName() + " menaruh Piring.");
            return;
        }
        
        // Ambil item dari meja
        if (hand == null && !isEmpty()) {
            chef.setHeldItem(takeItem());
            System.out.println("[Assembly] " + chef.getName() + " mengambil " + chef.getHeldItem().getName());
            return;
        }

        // Blocker untuk item selain piring
        if (chef.hasItem() && isEmpty() && !(hand instanceof Plate)) {
            System.out.println("[!] Hanya Piring yang boleh ditaruh di sini.");
            return;
        }
        
        defaultInteract(chef);
    }
    
    // Helper: Cek apakah bahan valid untuk Plating (tidak boleh RAW/BURNED)
    private boolean isValidForPlating(Processable item) {
        // Asumsi: Hanya bahan yang sudah diproses (CHOPPED, COOKED) yang boleh di Plating, selain RAW atau BURNED
        if (item.getState() == IngredientState.RAW || item.getState() == IngredientState.BURNED) {
            System.out.println("[Assembly] Gagal Plating: Bahan harus matang (COOKED) atau dipotong (CHOPPED).");
            return false;
        }
        return true;
    }

    // Helper untuk Plating 
    private void performPlating(Plate plate, Processable item) {
        // Cek piring sudah berisi Dish Final (sudah ditangani di atas, tapi jaga-jaga)
        if (plate.getContents().stream().anyMatch(content -> content instanceof Dish)) {
            System.out.println("[!] Piring sudah berisi Hidangan Final. Tidak bisa ditambah.");
            return;
        }
        
        plate.addIngredient(item); 
        System.out.println("[Assembly: Plating] Menambahkan " + item.getName() + " ke piring.");
    }
    
    // Helper untuk Assembly (Mengubah Bahan di Piring menjadi Dish Final)
    private void performAssembly(Plate plate) {
        List<Processable> contents = plate.getContents();
        
        // 1. Dapatkan List<String> format "Nama (STATE)" dari konten piring.
        List<String> contentsInStringFormat = contents.stream()
            .map(Processable::toString) // Asumsi Processable::toString mengembalikan "Nama (STATE)"
            .collect(Collectors.toList()); 
        
        // 2. Cari resep yang cocok (OrderManager harus memiliki instance)
        // OrderManager akan mencocokkan List<String> ini dengan Recipe.matches()
        Recipe recipeMatch = OrderManager.getInstance().findMatchingRecipe(contentsInStringFormat); 
        
        if (recipeMatch != null) {
            // Kosongkan piring
            plate.clearContents(); 
            
            // Buat Dish baru menggunakan Nama Resep dan List<String> konten yang sudah divalidasi
            Dish finalDish = new Dish(recipeMatch.getRecipeName(), contentsInStringFormat); 
            
            // Tambahkan Dish tunggal ke piring
            plate.addIngredient(finalDish); 
            
            System.out.println("🎉 [Assembly] Hidangan selesai: " + finalDish.getRecipeName() + "!");
            
        } else {
            // Ga cocok sama recipe
            String ingredientList = String.join(" + ", contentsInStringFormat);
            System.out.println("[Assembly] Gagal: Kombinasi bahan TIDAK COCOK dengan resep manapun. " + ingredientList);
        }
    }
}