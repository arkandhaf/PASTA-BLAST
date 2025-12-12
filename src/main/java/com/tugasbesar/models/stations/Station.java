package com.tugasbesar.models.stations;

import com.tugasbesar.models.actors.Chef;
import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.core.GamePanel;

// --- [WAJIB ADA BIAR GAK ERROR] ---
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
// ----------------------------------

public abstract class Station {

    protected int posX;
    protected int posY;
    protected String name;
    protected String symbol;

    protected Item itemOnStation;

    protected GamePanel gamePanel;
    // [PENTING] Variabel ini dikembalikan biar CuttingStation gak error
    protected Chef chefAtStation;

    public Station(int x, int y, String name, String symbol) {
        this.posX = x;
        this.posY = y;
        this.name = name;
        this.symbol = symbol;
        this.itemOnStation = null;
        this.chefAtStation = null;
        this.gamePanel = null;
    }

    public abstract void interact(Chef chef);

    public void update() {
    }

    // --- Helper Methods ---
    public boolean placeItem(Item item) {
        if (itemOnStation != null)
            return false;
        this.itemOnStation = item;
        return true;
    }

    public Item takeItem() {
        Item temp = itemOnStation;
        this.itemOnStation = null;
        return temp;
    }

    public boolean isEmpty() {
        return itemOnStation == null;
    }

    // Method buat set Chef yang lagi aktif di station (dipake CuttingStation)
    public void setChef(Chef chef) {
        this.chefAtStation = chef;
    }

    public Chef getChef() {
        return chefAtStation;
    }

    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    protected void notifyInteraction(Item item, String message, Color accent) {
        if (gamePanel != null) {
            gamePanel.pushStationFeedback(this, item, message, accent);
        }
    }

    protected void notifyInteraction(String message, Color accent) {
        notifyInteraction(null, message, accent);
    }

    // --- Default Interact ---
    protected void defaultInteract(Chef chef) {
        Item hand = chef.getHeldItem();

        if (hand != null && isEmpty()) {
            placeItem(hand);
            chef.setHeldItem(null);
            System.out.println("⬇️ [Action] Menaruh " + itemOnStation.getName() + " di " + name);
            notifyInteraction(itemOnStation, "Placed", new Color(0, 188, 212));
        } else if (hand == null && !isEmpty()) {
            chef.setHeldItem(takeItem());
            System.out.println("⬆️ [Action] Mengambil " + chef.getHeldItem().getName() + " dari " + name);
            notifyInteraction(chef.getHeldItem(), "Picked", new Color(255, 193, 7));
        }
    }

    // --- Visualisasi ---
    public void draw(Graphics2D g2) {
        int tileSize = 48;
        int screenX = posX * tileSize;
        int screenY = posY * tileSize;

        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(screenX, screenY, tileSize, tileSize);

        g2.setColor(Color.BLACK);
        g2.drawRect(screenX, screenY, tileSize, tileSize);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString(symbol, screenX + 15, screenY + 30);

        if (itemOnStation != null) {
            g2.setColor(Color.YELLOW);
            g2.fillOval(screenX + 12, screenY + 12, 24, 24);
        }
    }

    // Getters
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

    @Override
    public String toString() {
        return String.format("[%s] %s (%d,%d)", symbol, name, posX, posY);
    }
}