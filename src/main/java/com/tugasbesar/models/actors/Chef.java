package com.tugasbesar.models.actors;

import com.tugasbesar.core.GamePanel;
import com.tugasbesar.core.KeyHandler;
import com.tugasbesar.models.abstracts.Entity;
import com.tugasbesar.models.abstracts.Item; 
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Chef extends Entity {

    GamePanel gp;
    private int playerID; 
    private String name;
    private Item heldItem; 
    private boolean isBusy; 
    private Color chefColor; 

    public Chef(GamePanel gp, KeyHandler keyH, String name, int playerID) { 
        this.gp = gp;
        this.name = name;
        this.playerID = playerID;
        
        // Hitbox (Area Tabrakan)
        this.solidArea = new Rectangle(8, 16, 32, 32); 
        this.solidAreaDefaultX = solidArea.x;
        this.solidAreaDefaultY = solidArea.y;

        setDefaultValues();
    }

    public void setDefaultValues() {
        speed = 4;
        direction = "down"; 
        
        if (playerID == 1) {
            x = 100; y = 100; chefColor = Color.RED;
        } else { 
            x = 500; y = 100; chefColor = Color.BLUE;
        }
        
        this.heldItem = null; 
        this.isBusy = false;
    }

    public void setDefaultValues(int startCol, int startRow) {
        setDefaultValues();
        this.x = startCol * gp.tileSize;
        this.y = startRow * gp.tileSize;
    }

    // ------------------------------------------------------------------------
    // --- UPDATE LOGIC (FIXED: DASH ADDED) ---
    // ------------------------------------------------------------------------
    public void update(KeyHandler inputKeyH) {
        
        if (inputKeyH == null) return; 

        // --- 1. SET KECEPATAN (DASH LOGIC) ---
        // Default jalan santai
        speed = 4; 
        
        // Kalau tombol Shift ditekan -> Lari!
        if (inputKeyH.dashPressed == true) {
            speed = 8;
        }

        // --- 2. DETEKSI NIAT GERAK (Set Arah) ---
        boolean isMoving = false;

        if (inputKeyH.upPressed) {
            direction = "up";
            isMoving = true;
        } else if (inputKeyH.downPressed) {
            direction = "down";
            isMoving = true;
        } else if (inputKeyH.leftPressed) {
            direction = "left";
            isMoving = true;
        } else if (inputKeyH.rightPressed) {
            direction = "right";
            isMoving = true;
        }

        // --- 3. CEK TABRAKAN (SEBELUM GERAK) ---
        collisionOn = false;
        gp.cChecker.checkObject(this, true); // Cek nabrak Station?
        gp.cChecker.checkWindowBoundary(this); // Cek tembok layar?

        // --- 4. EKSEKUSI GERAK ---
        // Hanya gerak kalau tombol ditekan DAN tidak nabrak tembok
        if (isMoving == true && collisionOn == false) {
            switch (direction) {
                case "up":    y -= speed; break;
                case "down":  y += speed; break;
                case "left":  x -= speed; break;
                case "right": x += speed; break;
            }
        }
        
        // --- 5. INTERAKSI ---
        if (inputKeyH.interactPressed) { 
            interact(); 
            inputKeyH.interactPressed = false; // Reset tombol
        }
    }

    @Override
    public void update() {}

    // ------------------------------------------------------------------------
    // --- INTERACTION LOGIC ---
    // ------------------------------------------------------------------------
    public void interact() {
        
        int centerX = this.x + gp.tileSize / 2;
        int centerY = this.y + gp.tileSize / 2;

        // Sensor reach (Jangkauan)
        int reach = gp.tileSize / 2 + 6; 
        
        int sensorX = centerX;
        int sensorY = centerY;

        switch(direction) {
            case "up":    sensorY -= reach; break;
            case "down":  sensorY += reach; break;
            case "left":  sensorX -= reach; break;
            case "right": sensorX += reach; break;
        }

        int targetCol = sensorX / gp.tileSize;
        int targetRow = sensorY / gp.tileSize;

        boolean hitStation = false;

        for(int i = 0; i < gp.station.length; i++) {
            if(gp.station[i] != null) {
                int stationCol = gp.station[i].getPosX(); 
                int stationRow = gp.station[i].getPosY();

                if(stationCol == targetCol && stationRow == targetRow) {
                    System.out.println("✅ P" + playerID + " INTERAKSI SUKSES dengan " + gp.station[i].getName());
                    gp.station[i].interact(this);
                    hitStation = true;
                    break; 
                }
            }
        }
        
        if (!hitStation) {
            // Uncomment kalau mau debug failure
             System.out.println("❌ Gagal: Tidak ada apa-apa di depan");
        }
    }

    // ------------------------------------------------------------------------
    // --- DRAW METHOD ---
    // ------------------------------------------------------------------------
    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(chefColor);
        g2.fillRect(x, y, gp.tileSize, gp.tileSize); 
        
        // Gambar Mata
        g2.setColor(Color.WHITE); 
        if(direction.equals("up")) g2.fillRect(x + 10, y + 5, 28, 10);
        if(direction.equals("down")) g2.fillRect(x + 10, y + 30, 28, 10);
        if(direction.equals("left")) g2.fillRect(x + 5, y + 10, 10, 28);
        if(direction.equals("right")) g2.fillRect(x + 30, y + 10, 10, 28);
        
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(12F));
        g2.drawString(name, x + 12, y - 5); 

        if (heldItem != null) {
            g2.setColor(new Color(255, 165, 0)); 
            g2.fillOval(x + 12, y - 15, 24, 24); 
        }
    }

    // Getters & Setters
    public String getName() { return name; }
    public Item getHeldItem() { return heldItem; }
    public void setHeldItem(Item item) { this.heldItem = item; }
    public boolean hasItem() { return heldItem != null; } 
    public String getDirection() { return direction; }
    public boolean isBusy() { return isBusy; }
    public void setBusy(boolean busy) { this.isBusy = busy; }
    public int getPlayerID() { return playerID; }
}