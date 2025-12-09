package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.interfaces.Processable; 
import com.tugasbesar.models.interfaces.Placeable; // Pertahankan ini untuk kompatibilitas
import com.tugasbesar.models.enums.IngredientState;

import com.tugasbesar.models.item.Dish;
import com.tugasbesar.models.item.kitchen_utensil.Plate;

import com.tugasbesar.core.models.manager.OrderManager; 
import com.tugasbesar.core.models.manager.Recipe; 

import java.util.ArrayList;
import java.util.List;


public class AssemblyStation extends Station {

    public AssemblyStation(int x, int y) {
        super(x, y, "Assembly Station", "A"); 
    }

    @Override
    public void interact(Chef chef) {
        Item hand = chef.getHeldItem();
        Item tableItem = itemOnStation;

    
        if (hand == null && tableItem instanceof Plate && !((Plate)tableItem).isEmpty()) {
            performAssembly((Plate) tableItem);
            return;
        }
        if (hand instanceof Plate && !((Plate)hand).isEmpty() && tableItem == null) {
            performAssembly((Plate) hand);
            return;
        }
        
        // Platting
        // C1: piring di tangan, bahan di meja
        if (hand instanceof Plate && tableItem instanceof Processable) { 
            Plate plate = (Plate) hand;
            Processable ingredient = (Processable) tableItem;
            
            if (!plate.isDirty() && plate.canAccept(ingredient)) {
                
                Item takenItem = takeItem(); // Ambil Item dari Meja
                performPlating(plate, (Processable) takenItem); 
                return;
            }
        }

        // C2: bahan di tangan, piring di meja
        if (hand instanceof Processable && tableItem instanceof Plate) { 
            Plate plate = (Plate) tableItem;
            Processable ingredient = (Processable) hand;
            
            if (!plate.isDirty() && plate.canAccept(ingredient)) {
                performPlating(plate, ingredient);
                chef.setHeldItem(null); // hapus bahan dari tangan
                return;
            }
        }

        
        // taruh piring (Hanya piring yang boleh ditaruh)
        if (hand instanceof Plate && isEmpty()) {
            placeItem(hand);
            chef.setHeldItem(null);
            System.out.println("[Assembly] " + chef.getName() + " menaruh Piring.");
            return;
        }
        
        // ambil item dari meja
        if (hand == null && !isEmpty()) {
            chef.setHeldItem(takeItem());
            System.out.println("[Assembly] " + chef.getName() + " mengambil " + chef.getHeldItem().getName());
            return;
        }

        // blocker untuk item selain piring
        if (chef.hasItem() && isEmpty() && !(hand instanceof Plate)) {
            System.out.println("[!] Hanya Piring yang boleh ditaruh di sini.");
            return;
        }
        
        
        defaultInteract(chef);
    }

    // helper untuk Plating 
    private void performPlating(Plate plate, Processable item) {
        // cek piring sudah berisi Dish Final
        if (plate.getContents().stream().anyMatch(content -> content instanceof Dish)) {
            System.out.println("[!] Piring sudah berisi Hidangan Final. Tidak bisa ditambah.");
            return;
        }
        
        // cek item memiliki state yang valid
        if (item.getState() == null) {
             System.out.println("[!] Bahan tidak valid untuk Plating.");
             return;
        }
        
        plate.addIngredient(item); 
        System.out.println("[Assembly: Plating] Menambahkan " + item.getName() + " ke piring.");
    }
    
    // helper untuk Assembly 
    private void performAssembly(Plate plate) {
        List<Processable> contents = plate.getContents();
        
        // 
        if (contents.size() == 1 && contents.get(0) instanceof Dish) {
            System.out.println("[Assembly] Hidangan sudah selesai dirakit.");
            return;
        }
        
        // 2. Cek semua bahan harus COOKED 
        List<String> ingredientNames = new ArrayList<>();
        for (Processable item : contents) {
            // hanya menerima COOKED
            if (item.getState() != IngredientState.COOKED) { 
                System.out.println("[Assembly] Gagal: Ada bahan yang belum matang (COOKED)! (" + item.getName() + ")");
                return; 
            }
            ingredientNames.add(item.getName());
        }
        
        // cek resep ke ordermanagernya
        Recipe recipeMatch = OrderManager.getInstance().findMatchingRecipe(ingredientNames); 
        
        if (recipeMatch != null) {
            
            
            // kosongin piring lalu buat dish baru
            plate.clearContents(); 
            Dish finalDish = new Dish(recipeMatch.getDishName(), ingredientNames); 
            
            // tambahkan dish tunggal ke piring
            plate.addIngredient(finalDish); 
            
            System.out.println("🎉 [Assembly] Hidangan selesai: " + finalDish.getRecipeName() + "!");
            
        } else {
            // ga cocok sama recipe
            String ingredientList = String.join(" + ", ingredientNames);
            System.out.println("[Assembly] Gagal: Kombinasi bahan TIDAK COCOK dengan resep manapun: " + ingredientList);
        }
    }
}