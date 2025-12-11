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
        
        this.solidArea = new Rectangle(8, 16, 32, 32); 
        this.solidAreaDefaultX = solidArea.x;
        this.solidAreaDefaultY = solidArea.y;

        setDefaultValues();
    }

    public void setDefaultValues() {
        speed = 4;
        direction = "down"; 
        this.heldItem = null; 
        this.isBusy = false;
        
        if(playerID == 1) chefColor = Color.RED;
        else chefColor = Color.BLUE;
    }

    public void setDefaultValues(int startCol, int startRow) {
        this.x = startCol * gp.tileSize;
        this.y = startRow * gp.tileSize;
        setDefaultValues();
    }

    public void update(KeyHandler inputKeyH) {
        
        // Jika inputKeyH null, berarti Chef ini sedang TIDAK AKTIF -> Diam
        if (inputKeyH == null) return; 
        
        // Jika sedang sibuk (motong/cuci), tidak bisa gerak
        if (isBusy) return;

        // --- BACA INPUT (Semua pakai WASD dari KeyHandler) ---
        // Karena GamePanel hanya mengirim KeyHandler ke Chef yang aktif.
        boolean up = inputKeyH.upPressed;
        boolean down = inputKeyH.downPressed;
        boolean left = inputKeyH.leftPressed;
        boolean right = inputKeyH.rightPressed;
        boolean dash = inputKeyH.dashPressed;
        boolean interact = inputKeyH.interactPressed;

        int currentSpeed = dash ? 8 : speed;
        boolean isMoving = false;

        if (up) { direction = "up"; isMoving = true; }
        else if (down) { direction = "down"; isMoving = true; }
        else if (left) { direction = "left"; isMoving = true; }
        else if (right) { direction = "right"; isMoving = true; }

        collisionOn = false;
        gp.cChecker.checkObject(this, true); 
        gp.cChecker.checkWindowBoundary(this);

        if (isMoving && !collisionOn) {
            switch (direction) {
                case "up":    y -= currentSpeed; break;
                case "down":  y += currentSpeed; break;
                case "left":  x -= currentSpeed; break;
                case "right": x += currentSpeed; break;
            }
        }
        
        if (interact) { 
            interact(); 
            inputKeyH.interactPressed = false; // Reset tombol agar tidak spam
        }
    }

    @Override
    public void update() {}

    public void interact() {
        int centerX = this.x + gp.tileSize / 2;
        int centerY = this.y + gp.tileSize / 2;
        int reach = gp.tileSize / 2 + 10; 
        
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

        for(int i = 0; i < gp.station.length; i++) {
            if(gp.station[i] != null) {
                if(gp.station[i].getPosX() == targetCol && gp.station[i].getPosY() == targetRow) {
                    System.out.println("✅ P" + playerID + " INTERAKSI: " + gp.station[i].getName());
                    gp.station[i].interact(this);
                    return; 
                }
            }
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        int drawSize = gp.tileSize - 8; 
        int drawX = x + 4;
        int drawY = y + 4;

        g2.setColor(chefColor);
        g2.fillRect(drawX, drawY, drawSize, drawSize); 
        
        g2.setColor(Color.WHITE); 
        if(direction.equals("up"))    g2.fillRect(drawX + 8, drawY + 2, 24, 8);
        if(direction.equals("down"))  g2.fillRect(drawX + 8, drawY + 24, 24, 8);
        if(direction.equals("left"))  g2.fillRect(drawX + 2, drawY + 8, 8, 24);
        if(direction.equals("right")) g2.fillRect(drawX + 24, drawY + 8, 8, 24);
        
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(10F));
        g2.drawString(name, drawX + 5, drawY - 2); 

        if (heldItem != null) {
            g2.setColor(new Color(255, 215, 0)); 
            g2.fillOval(drawX + 10, drawY - 12, 16, 16); 
        }
    }

    public String getName() { return name; }
    public Item getHeldItem() { return heldItem; }
    public void setHeldItem(Item item) { this.heldItem = item; }
    public boolean hasItem() { return heldItem != null; } 
    public String getDirection() { return direction; }
    public boolean isBusy() { return isBusy; }
    public void setBusy(boolean busy) { this.isBusy = busy; }
    public int getPlayerID() { return playerID; }
}