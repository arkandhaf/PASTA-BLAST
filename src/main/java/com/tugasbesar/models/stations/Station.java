package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item;
import java.awt.Graphics2D;
import java.awt.Color;

public abstract class Station {
    
    protected int posX;
    protected int posY;
    protected String name;   
    protected String symbol; 

    protected Item itemOnStation;   
    protected Chef chefAtStation;   

    public Station(int x, int y, String name, String symbol) {
        this.posX = x;
        this.posY = y;
        this.name = name;
        this.symbol = symbol;
        this.itemOnStation = null;
    }

    public abstract void interact(Chef chef);

    public void update() {}

    public boolean placeItem(Item item) {
        if (itemOnStation != null) return false;
        this.itemOnStation = item;
        return true;
    }

    public Item takeItem() {
        Item temp = itemOnStation;
        this.itemOnStation = null;
        return temp;
    }

    public boolean isEmpty() { return itemOnStation == null; }
    public void setChef(Chef chef) { this.chefAtStation = chef; }
    public void removeChef() { this.chefAtStation = null; }

    // --- [FIX 1] Method defaultInteract (Agar CookingStation tidak error) ---
    protected void defaultInteract(Chef chef) {
        Item hand = chef.getHeldItem();

        // Taruh Item
        if (hand != null && isEmpty()) {
            placeItem(hand);
            chef.setHeldItem(null);
            System.out.println("[Action] Menaruh " + itemOnStation.getName() + " di " + name);
        }
        // Ambil Item
        else if (hand == null && !isEmpty()) {
            chef.setHeldItem(takeItem());
            System.out.println("[Action] Mengambil " + chef.getHeldItem().getName() + " dari " + name);
        }
    }

    // --- [FIX 2] Method Draw (Agar muncul di layar) ---
    public void draw(Graphics2D g2) {
        int tileSize = 48; // Asumsi 48px
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(posX * tileSize, posY * tileSize, tileSize, tileSize);
        g2.setColor(Color.WHITE);
        g2.drawRect(posX * tileSize, posY * tileSize, tileSize, tileSize);
        g2.drawString(symbol, posX * tileSize + 20, posY * tileSize + 30);

        if (itemOnStation != null) {
            g2.setColor(Color.RED);
            g2.fillOval(posX * tileSize + 12, posY * tileSize + 12, 24, 24);
        }
    }

    public String getName() { return name; }
    public String getSymbol() { return symbol; }
    public Item getItemOnStation() { return itemOnStation; }
    public int getPosX() { return posX; }
    public int getPosY() { return posY; }

    @Override
    public String toString() {
        return String.format("[%s] %s (%d,%d)", symbol, name, posX, posY);
    }
}