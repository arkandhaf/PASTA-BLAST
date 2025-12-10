package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.item.kitchen_utensil.Plate;
import com.tugasbesar.models.item.kitchen_utensil.BaseCookingDevice;

public class TrashStation extends Station {

    public TrashStation(int x, int y) {
        super(x, y, "Trash Can", "T");
    }

    @Override
    public void interact(Chef chef) {
        if (!chef.hasItem()) return;

        Item item = chef.getHeldItem();

  
        // C1: piring --> buang isi doang, piringnya tidak

        if (item instanceof Plate) {
            Plate plate = (Plate) item;
            
            if (!plate.getContents().isEmpty()) {
                plate.getContents().clear(); // kosongkan isi
                
                
                System.out.println("[Trash] Membuang sisa makanan dari Piring.");
            } else {
                System.out.println("[!] Piring sudah kosong, tidak ada yang dibuang.");
            }
            return; 
        }

       
        // C2: PANCI/WAJAN (buang isi, alat tetap)
        if (item instanceof BaseCookingDevice) {
            BaseCookingDevice utensil = (BaseCookingDevice) item;
            
            // cek apakah ada isinya 
            if (!utensil.getContents().isEmpty()) {
                utensil.getContents().clear(); // kosongkan masakan 
                
                System.out.println("[Trash] Membuang masakan dari " + utensil.getName());
            } else {
                System.out.println("[!] " + utensil.getName() + " sudah bersih/kosong.");
            }
            return; 
        }

 
        // C3: BAHAN (buang Itemnya)
        chef.setHeldItem(null);
        System.out.println("[Trash] " + item.getName() + " dibuang ke tempat sampah.");
    }
}