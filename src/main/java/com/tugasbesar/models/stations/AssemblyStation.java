package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.interfaces.Processable; 
import com.tugasbesar.models.enums.IngredientState;
import com.tugasbesar.models.item.Dish;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import com.tugasbesar.models.item.kitchen_utensil.BaseCookingDevice; // <<< IMPORT BASECOOKINGDEVICE

import com.tugasbesar.models.manager.OrderManager;
import com.tugasbesar.models.manager.Recipe; 

import java.util.List;
import java.util.stream.Collectors;


public class AssemblyStation extends Station {

    private final OrderManager orderManager;

    public AssemblyStation(int x, int y, OrderManager orderManager) {
        super(x, y, "Assembly Station", "A"); 
        this.orderManager = orderManager;
    }

    @Override
    public void interact(Chef chef) {
        Item hand = chef.getHeldItem();
        Item tableItem = itemOnStation;
        boolean isPlateInHand = hand instanceof Plate && !((Plate)hand).isEmpty();
        boolean isPlateOnTable = tableItem instanceof Plate && !((Plate)tableItem).isEmpty();
        
        if (isPlateInHand || isPlateOnTable) {
            Plate plateToAssemble = (Plate) (isPlateInHand ? hand : tableItem);
            
            if (!plateToAssemble.isDirty() && !plateToAssemble.getContents().stream().anyMatch(content -> content instanceof Dish)) {
                performAssembly(plateToAssemble);
                return;
            } else if (plateToAssemble.getContents().stream().anyMatch(content -> content instanceof Dish)) {
                System.out.println("[Assembly] Hidangan sudah selesai dirakit. Siap disajikan.");
                
            }
        }
        
        if (hand instanceof BaseCookingDevice utensilInHand && tableItem instanceof Plate plateOnTable) {
            if (!utensilInHand.isEmpty() && !plateOnTable.isDirty()) {
                Processable ingredient = utensilInHand.getContents().get(0

                if (isValidForPlating(ingredient) && plateOnTable.canAccept(ingredient)) {
                    Processable takenIngredient = (Processable) utensilInHand.takeItem(); 
                    performPlating(plateOnTable, takenIngredient); 
                    // Utensil di tangan sekarang kosong
                    return;
                }
            }
        }
        

        if (hand instanceof Plate plate && tableItem instanceof Processable ingredient) { 
            
            if (isValidForPlating(ingredient) && !plate.isDirty() && plate.canAccept(ingredient)) {
                Item takenItem = takeItem(); // Ambil dari meja
                performPlating(plate, (Processable) takenItem); // Tambah ke Plate di tangan
                return;
            }
        }

       
        if (hand instanceof Processable ingredient && tableItem instanceof Plate plate) { 
            
            if (isValidForPlating(ingredient) && !plate.isDirty() && plate.canAccept(ingredient)) {
                performPlating(plate, ingredient); // Tambah ke Plate di meja
                chef.setHeldItem(null); // Kosongkan tangan chef
                return;
            }
        }
        
        
        if (hand instanceof Plate && isEmpty()) {
            placeItem(hand);
            chef.setHeldItem(null);
            System.out.println("[Assembly] " + chef.getName() + " menaruh Piring.");
            return;
        }
        
       
        if (hand == null && !isEmpty()) {
            chef.setHeldItem(takeItem());
            System.out.println("[Assembly] " + chef.getName() + " mengambil " + chef.getHeldItem().getName());
            return;
        }

        if (chef.hasItem() && isEmpty() && !(hand instanceof Plate)) {
            System.out.println("[!] Hanya Piring yang boleh ditaruh di sini.");
            return;
        }
        
        
        defaultInteract(chef);
    }
    
    private boolean isValidForPlating(Processable item) {
        if (item.getState() == IngredientState.RAW || item.getState() == IngredientState.BURNED) {
            System.out.println("[Assembly] Gagal Plating: Bahan harus matang (COOKED) atau dipotong (CHOPPED).");
            return false;
        }
        return true;
    }

    
    private void performPlating(Plate plate, Processable item) {
        if (plate.getContents().stream().anyMatch(content -> content instanceof Dish)) {
            System.out.println("[!] Piring sudah berisi Hidangan Final. Tidak bisa ditambah.");
            return;
        }
        
        plate.addIngredient(item); 
        System.out.println("✅ [Assembly: Plating] Menambahkan " + item.getName() + " ke piring.");
    }
    
    
    private void performAssembly(Plate plate) {
        List<Processable> contents = plate.getContents();
        
       
        List<String> contentsInStringFormat = contents.stream()
            .map(Processable::toString)
            .collect(Collectors.toList()); 
        
        
        Recipe recipeMatch = orderManager.findMatchingRecipe(contentsInStringFormat); 
        
        if (recipeMatch != null) {
            
            plate.clearContents(); 
            
            Dish finalDish = new Dish(recipeMatch.getRecipeName(), contentsInStringFormat); 
            
            plate.addIngredient(finalDish); 
            
            System.out.println("🎉 [Assembly] Hidangan selesai: " + finalDish.getRecipeName() + "!");
            
        } else {
            String ingredientList = String.join(" + ", contentsInStringFormat);
            System.out.println("⚠️ [Assembly] Gagal: Kombinasi bahan TIDAK COCOK dengan resep manapun. " + ingredientList);
        }
    }
}