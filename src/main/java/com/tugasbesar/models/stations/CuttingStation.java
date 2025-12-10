package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item; 
import com.tugasbesar.models.interfaces.Choppable; 
import com.tugasbesar.models.interfaces.Processable; 
import com.tugasbesar.models.enums.IngredientState;
import com.tugasbesar.models.item.kitchen_utensil.Plate; 
import com.tugasbesar.models.item.kitchen_utensil.BaseCookingDevice; 

public class CuttingStation extends Station {
    
    private int cutProgress = 0;
    private final int CUT_SPEED = 34; 

    public CuttingStation(int x, int y) {
        super(x, y, "Cutting Station", "C");
    }

    @Override
    public void interact(Chef chef) {
        Item hand = chef.getHeldItem();
        Item tableItem = itemOnStation;

        if (chef.isBusy()){
            chef.setBusy(false);
            System.out.println("[Cutting] " + chef.getName() + " menghentikan pemotongan. Progress tersimpan (" + cutProgress + "%)");
            return;
        }
        
        if (tableItem instanceof Plate && hand instanceof BaseCookingDevice) {
            Plate plateOnTable = (Plate) tableItem;
            BaseCookingDevice utensilInHand = (BaseCookingDevice) hand;
            
            
            if (!utensilInHand.getContents().isEmpty() && !plateOnTable.isDirty()) {
                
                Processable ingredientToValidate = utensilInHand.getContents().get(0);
                
                if (plateOnTable.canAccept(ingredientToValidate)) {
                    
                    Processable takenIngredient = (Processable) utensilInHand.takeItem(); 
                    plateOnTable.addIngredient(takenIngredient); 
                    
                    System.out.println("[Cutting: Plating (Utensil->Plate)] Isi Utensil (" + takenIngredient.getName() + ") pindah ke Piring di meja.");
                    return;
                }
            }
        }
        

        if (hand instanceof Plate && tableItem instanceof Processable) {
            Plate plate = (Plate) hand;
            Processable ingredient = (Processable) tableItem;
            
            
            if (!plate.isDirty() && plate.canAccept(ingredient)) { 
                
                Item takenItem = takeItem(); 
                plate.addIngredient((Processable) takenItem); 
                
                System.out.println("[Cutting: Plating (Plate->Inventory)] " + ingredient.getName() + " pindah ke piring di tangan.");
                cutProgress = 0; 
                return;
            }
        }
        
        
        if (tableItem instanceof Choppable && tableItem instanceof Processable) {
            Processable pItem = (Processable) tableItem;
            Choppable cItem = (Choppable) tableItem;
            
            
            if (pItem.getState() == IngredientState.CHOPPED && !chef.hasItem()) {
                chef.setHeldItem(takeItem());
                cutProgress = 0; // Reset progress
                System.out.println("[Cutting] " + chef.getName() + " mengambil " + chef.getHeldItem().getName() + " (CHOPPED)");
                return;
            }
            

            if (pItem.getState() == IngredientState.RAW && cItem.canBeChopped() && !chef.hasItem()) {
                chef.setBusy(true); // Chef masuk ke busy state
                System.out.println("[Cutting] " + chef.getName() + " mulai memotong " + pItem.getName());
                return;
            }
        }
        

        if (isEmpty() && chef.hasItem()) {
            
            
            if (hand instanceof Plate) {
                Item itemToPlace = chef.getHeldItem(); 
                placeItem(itemToPlace); 
                chef.setHeldItem(null);
                System.out.println("[Cutting] " + chef.getName() + " menaruh Piring di meja.");
                cutProgress = 0; 
                return;
            }
            
            
            if (hand instanceof Choppable && hand instanceof Processable) {
                Choppable chopItem = (Choppable) hand;
                Processable pItem = (Processable) hand;
                
                
                if (chopItem.canBeChopped() && pItem.getState() == IngredientState.RAW) { 
                    Item itemToPlace = chef.getHeldItem(); 
                    placeItem(itemToPlace); 
                    chef.setHeldItem(null);
                    cutProgress = 0; 
                    
                    System.out.println("[Cutting] " + chef.getName() + " menaruh " + itemToPlace.getName());
                } else {
                    System.out.println(">>> [TOLAK] Item harus RAW dan bisa dipotong!");
                }
            } else {
                 System.out.println(">>> [TOLAK] Item ini tidak bisa dipotong atau ditaruh di stasiun ini!");
            }
            return;
        }

        
        if (!isEmpty() && !chef.hasItem()) {
             chef.setHeldItem(takeItem());
             System.out.println("[Cutting] " + chef.getName() + " mengambil " + chef.getHeldItem().getName());
             return;
        }
        
        
        defaultInteract(chef);
    }

    @Override
    public void update() {
        if (chefAtStation == null || !chefAtStation.isBusy()) return;

        if (itemOnStation instanceof Choppable && itemOnStation instanceof Processable) {
            Processable pItem = (Processable) itemOnStation;
            Choppable cItem = (Choppable) itemOnStation;
            
        
            if (cItem.canBeChopped() && pItem.getState() == IngredientState.RAW) { 
                cutProgress += CUT_SPEED;
                
                if (cutProgress >= 100) {
                    cItem.chop(); 
                    cutProgress = 100;
                    chefAtStation.setBusy(false); 
                    System.out.println(">>> [SELESAI] " + chefAtStation.getName() + " berhasil memotong " + pItem.getName() + "!");
                }
            } else {
                
                chefAtStation.setBusy(false); 
            }
        } else {
            
            chefAtStation.setBusy(false);
        }
    }

    public int getCutProgress() {
        return cutProgress;
    }
}