package com.tugasbesar.models.item.kitchen_utensil;

import com.tugasbesar.models.abstracts.KitchenUtensil;
import com.tugasbesar.models.interfaces.Preparable;
import com.tugasbesar.models.interfaces.CookingDevice;
import com.tugasbesar.models.item.Ingredient;

public abstract class BaseCookingDevice extends KitchenUtensil implements CookingDevice {

    protected int capacityLimit;
    
    // Timer Variables
    protected boolean isCooking = false;
    protected int currentTick = 0;
    protected final int TICKS_TO_COOK = 5;
    protected final int TICKS_TO_BURN = 10;

    public BaseCookingDevice(String name, int capacityLimit) {
        super(name); 
        this.capacityLimit = capacityLimit;
    }

    // --- INTERFACE IMPLEMENTATION ---

    @Override
    public boolean isPortable() { return true; }

    @Override
    public int capacity() { return capacityLimit; }

    @Override
    public void addIngredient(Preparable item) {
        if (contents.size() >= capacity()) {
            System.out.println("[!] Penuh!");
            return;
        }
        if (canAccept(item)) {
            super.addIngredient(item); // Masuk ke List Parent
            System.out.println("[Alat] " + item.getName() + " masuk ke " + getName());
        }
    }

    @Override
    public void startCooking() {
        if (!contents.isEmpty() && !isBurned()) {
            this.isCooking = true;
            System.out.println("[Alat] " + getName() + " api dinyalakan...");
        }
    }

    @Override
    public void processCookingTick() {
        // Auto-start safety
        if (!isCooking && !contents.isEmpty()) startCooking();

        if (!isCooking || contents.isEmpty() || isBurned()) return;

        currentTick++;

        if (currentTick == TICKS_TO_COOK) {
            cookContents();
            System.out.println(">>> [MATANG] " + getName() + " selesai masak!");
        } 
        else if (currentTick >= TICKS_TO_BURN) {
            burnContents();
            System.out.println(">>> [GOSONG] " + getName() + " hangus!");
            isCooking = false;
        }
    }

    // --- HELPER METHODS ---

    @Override
    public boolean isBurned() {
        if (contents.isEmpty()) return false;
        
        // PAKAI LIST JADI GAMPANG: Cek index 0
        Preparable item = contents.get(0);
        
        if (item instanceof Ingredient) {
            return ((Ingredient) item).getState().toString().equals("BURNED");
        }
        return false;
    }

    @Override
    public boolean isCooked() {
        return currentTick >= TICKS_TO_COOK && currentTick < TICKS_TO_BURN;
    }
    
    @Override
    public int getCookingPercentage() {
        if (currentTick >= TICKS_TO_BURN) return 100;
        return (int) ((currentTick / (double) TICKS_TO_COOK) * 100);
    }

    private void cookContents() {
        for (Preparable p : contents) p.cook();
    }

    private void burnContents() {
        for (Preparable p : contents) {
            if (p instanceof Ingredient) ((Ingredient) p).burn();
        }
    }

    @Override
    public String toString() {
        return getName() + " [" + contents.size() + "/" + capacity() + "]";
    }
}