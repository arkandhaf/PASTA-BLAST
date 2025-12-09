package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item;

public abstract class Station {
    
    protected int posX;
    protected int posY;
    protected String name;   // nama station
    protected String symbol; // inisialisasi nama station

    protected Item itemOnStation;   // item yang ada di meja
    protected Chef chefAtStation;   // chef yang sedang berdiri di stationnya

    
    public Station(int x, int y, String name, String symbol) {
        this.posX = x;
        this.posY = y;
        this.name = name;
        this.symbol = symbol;
        this.itemOnStation = null;
    }


    // dipanggil ketika menekan tombol Interaksi (Space/E)
    public abstract void interact(Chef chef);


    
    // dipanggil main Loop setiap detik/tick
    public void update() {
        // Hanya di-override oleh station yang butuh waktu (Cooking, Cutting, Washing)
    }


    //helper method
    //taruh item ke meja, return true jika berhasil.
    public boolean placeItem(Item item) {
        if (itemOnStation != null) {
            return false; // gagal, meja penuh
        }
        this.itemOnStation = item;
        return true;
    }

    // ambil item dari meja + return itemnya.
    public Item takeItem() {
        Item temp = itemOnStation;
        this.itemOnStation = null;
        return temp;
    }
    

    public boolean isEmpty() {
        return itemOnStation == null;
    }


    // dipanggil main saat chef bergerak masuk/keluar koordinat ini
    public void setChef(Chef chef) {
        this.chefAtStation = chef;
    }

    public void removeChef() {
        this.chefAtStation = null;
    }


    

    protected void defaultInteract(Chef chef) {
        Item hand = chef.getHeldItem();

        // taruh item(meja kosong + chef megang)
        if (hand != null && isEmpty()) {
            placeItem(hand);
            chef.setHeldItem(null);
            System.out.println("[Action] Menaruh " + itemOnStation.getName() + " di " + name);
        }
        // ambil (meja ada item + chef tangan kosong)
        else if (hand == null && !isEmpty()) {
            chef.setHeldItem(takeItem());
            System.out.println("[Action] Mengambil " + chef.getHeldItem().getName() + " dari " + name);
        }
        
        else {
            
        }
    }

    //untuk map bro 
    public String getName() { 
        return name; 
    }
    public String getSymbol() { 
        return symbol; 
    }
    public Item getItemOnStation() { 
        return itemOnStation; 
    }
    public int getPosX() { 
        return posX; 
    }
    public int getPosY() { 
        return posY; 
    }

    // untuk print info status meja
    public String getStatusDisplay() {
        return String.format("[%s] %s (%d,%d) | Isi: %s", 
            symbol, name, posX, posY, 
            isEmpty() ? "Kosong" : itemOnStation.getName());
    }
    
    @Override
    public String toString() {
        return getStatusDisplay();
    }
}