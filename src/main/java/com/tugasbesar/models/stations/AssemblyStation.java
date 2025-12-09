// File: com.tugasbesar.models.stations.AssemblyStation.java

package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.interfaces.Processable; 
import com.tugasbesar.models.interfaces.Placeable; 
import com.tugasbesar.models.enums.IngredientState;

import com.tugasbesar.models.item.Dish;
import com.tugasbesar.models.item.kitchen_utensil.Plate;

import com.tugasbesar.models.manager.OrderManager; 
import com.tugasbesar.models.manager.Recipe; 

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
        

        //Platting
        // Case A: piring di tangan, bahan di meja
        if (hand instanceof Plate && tableItem instanceof Placeable) { 
            performPlating((Plate) hand, (Placeable) tableItem);
            this.itemOnStation = null; // hapus bahan dari meja
            return;
        }

        // Case B: bahan di tangan, piring di meja
        if (hand instanceof Placeable && tableItem instanceof Plate) { 
            performPlating((Plate) tableItem, (Placeable) hand);
            chef.setHeldItem(null); // hapus bahan dari tangan
            return;
        }

        
        // taruh Piring (Hanya piring yang boleh ditaruh)
        if (hand instanceof Plate && isEmpty()) {
            placeItem(hand);
            chef.setHeldItem(null);
            System.out.println("[Assembly] " + chef.getName() + " menaruh Piring.");
            return;
        }
        
        // ambil Item dari meja
        if (hand == null && !isEmpty()) {
            chef.setHeldItem(takeItem());
            System.out.println("[Assembly] " + chef.getName() + " mengambil " + chef.getHeldItem().getName());
            return;
        }

        //blocker untuk item selain piring
        if (chef.hasItem() && isEmpty() && !(hand instanceof Plate)) {
            System.out.println("[!] Hanya Piring yang boleh ditaruh di sini.");
            return;
        }
    }

    // helper untuk Plating 
    private void performPlating(Plate plate, Placeable item) {
        if (plate.getContents().stream().anyMatch(content -> content instanceof Dish)) {
            System.out.println("[!] Piring sudah berisi Hidangan Final. Tidak bisa ditambah.");
            return;
        }
        plate.addIngredient((Processable)item); 
    }
    
    // helper untuk Assembly 
    private void performAssembly(Plate plate) {
        List<Processable> contents = plate.getContents();
        //cek udah dish apa belum
        if (contents.size() == 1 && contents.get(0) instanceof Dish) {
            System.out.println("[Assembly] Hidangan sudah selesai dirakit.");
            return;
        }
        
        //cek semua bahan
        List<String> ingredientNames = new ArrayList<>();
        for (Processable item : contents) {
            if (item.getState() != IngredientState.COOKED) { 
                System.out.println("[Assembly] Gagal: Ada bahan yang belum matang (COOKED)!");
                return; 
            }
            ingredientNames.add(item.getName());
        }
        
        // 3. Cek Resep ke OrderManager (Validasi Kreasi)
        Recipe recipeMatch = OrderManager.getInstance().findMatchingRecipe(ingredientNames); 
        
        if (recipeMatch != null) {
            plate.clearContents(); 
        
            // buat objek dish baru 
            Dish finalDish = new Dish(recipeMatch.getDishName(), ingredientNames); 
            
            // Tambahkan dish tunggal ke piring
            plate.addIngredient(finalDish); 
            
            System.out.println("🎉 [Assembly] Hidangan selesai: " + finalDish.getRecipeName() + "!");
            
        } else {
            String ingredientList = String.join(" + ", ingredientNames);
            System.out.println("[Assembly] Gagal: Kombinasi bahan TIDAK COCOK dengan resep manapun: " + ingredientList);
        }
    }
}