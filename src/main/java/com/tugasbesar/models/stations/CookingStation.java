package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.interfaces.Preparable;
import com.tugasbesar.models.item.kitchen_utensil.BaseCookingDevice;

public class CookingStation extends Station {

    public CookingStation(int x, int y, BaseCookingDevice startingUtensil) {
        super(x, y, "Stove", "S");
        this.itemOnStation = startingUtensil; // Pasang panci di awal
    }

    @Override
    public void interact(Chef chef) {
        // --- 1. Masukkan Bahan ke Panci ---
        if (itemOnStation instanceof BaseCookingDevice) {
            BaseCookingDevice utensil = (BaseCookingDevice) itemOnStation;

            if (chef.hasItem() && chef.getHeldItem() instanceof Preparable) {
                Preparable ingredient = (Preparable) chef.getHeldItem();

                if (utensil.canAccept(ingredient)) {
                    utensil.addIngredient(ingredient);

                    utensil.startCooking();

                    chef.setHeldItem(null);
                    System.out.println("[Stove] " + ingredient.getName() + " masuk ke " + utensil.getName());
                    return;
                }
            }
        }

        // --- 2. Safety: Cegah taruh barang sembarangan ---
        // Kalau meja kosong, hanya boleh taruh Panci/Wajan
        if (isEmpty() && chef.hasItem()) {
            if (!(chef.getHeldItem() instanceof BaseCookingDevice)) {
                System.out.println("[!] Bahaya! Jangan taruh " + chef.getHeldItem().getName() + " di api.");
                return;
            }
        }

        // --- 3. Angkat / Taruh Panci ---
        defaultInteract(chef);
    }

    @Override
    public void update() {
        // Masak otomatis (Timer jalan terus)
        if (itemOnStation instanceof BaseCookingDevice) {
            ((BaseCookingDevice) itemOnStation).processCookingTick();
        }
    }
}