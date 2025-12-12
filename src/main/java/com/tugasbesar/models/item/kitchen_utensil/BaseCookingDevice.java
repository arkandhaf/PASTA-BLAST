package com.tugasbesar.models.item.kitchen_utensil;

import com.tugasbesar.models.abstracts.KitchenUtensil;
import com.tugasbesar.models.interfaces.Cookable;
import com.tugasbesar.models.interfaces.Processable;
import com.tugasbesar.models.interfaces.CookingDevice;
import com.tugasbesar.models.item.Ingredient;
import com.tugasbesar.models.enums.IngredientState;

public abstract class BaseCookingDevice extends KitchenUtensil implements CookingDevice {

    protected int capacityLimit;
    protected boolean isCooking = false;
    protected int currentTick = 0;
    protected final int TICKS_TO_COOK = 720; 
    protected final int TICKS_TO_BURN = 1500; 

    public BaseCookingDevice(String name, int capacityLimit) {
        super(name);
        this.capacityLimit = capacityLimit;
    }

    @Override
    public boolean isPortable() { return true; }

    @Override
    public int capacity() { return capacityLimit; }

    @Override
    public void addIngredient(Cookable item) {
        if (contents.size() >= capacity()) {
            System.out.println("[!] Penuh!");
            return;
        }
        this.currentTick = 0;
        this.isCooking = false; // Reset: Harus tekan E lagi buat mulai

        if (canAccept(item)) {
            super.addIngredient((Processable) item);
            System.out.println("[Alat] " + ((Processable) item).getName() + " masuk ke " + getName());
        }
    }

    @Override
    public abstract boolean canAccept(Cookable item);

    @Override
    public boolean canAccept(Processable item) {
        if (item instanceof Cookable) return this.canAccept((Cookable) item);
        return false;
    }

    // --- LOGIC UTAMA: Ditekan E -> isCooking = true ---
    @Override
    public void startCooking() {
        if (!contents.isEmpty() && !isBurned() && !isCooked()) {
            this.isCooking = true;
            System.out.println("🔥 Api dinyalakan!");
        }
    }
    
    // Getter penting buat CookingStation
    public boolean isCooking() { return isCooking; }

    @Override
    public void processCookingTick() {
        // HAPUS AUTO-START DISINI
        // if (!isCooking && !contents.isEmpty()) startCooking(); 

        if (!isCooking || contents.isEmpty() || isBurned()) return;

        currentTick++;

        if (currentTick == TICKS_TO_COOK) {
            cookContents();
            System.out.println(">>> [MATANG] " + getName() + " selesai masak!");
        } else if (currentTick >= TICKS_TO_BURN) {
            burnContents();
            System.out.println(">>> [GOSONG] " + getName() + " hangus!");
            isCooking = false;
        }
    }

    @Override
    public boolean isBurned() {
        if (contents.isEmpty()) return false;
        Processable item = contents.get(0);
        if (item instanceof Ingredient) return ((Ingredient) item).getState() == IngredientState.BURNED;
        return false;
    }

    @Override
    public boolean isCooked() {
        if (contents.isEmpty()) return false;
        Processable item = contents.get(0);
        if (item instanceof Ingredient) return ((Ingredient) item).getState() == IngredientState.COOKED;
        return false;
    }

    @Override
    public int getCookingPercentage() {
        if (contents.isEmpty()) return 0;
        if (currentTick >= TICKS_TO_BURN) return 100;
        int percentage = (int) ((currentTick / (double) TICKS_TO_COOK) * 100);
        return Math.min(percentage, 100);
    }

    private void cookContents() {
        for (Processable item : contents) {
            if (item instanceof Cookable) ((Cookable) item).cook();
        }
    }

    private void burnContents() {
        for (Processable item : contents) {
            if (item instanceof Ingredient) ((Ingredient) item).burn();
        }
    }
}