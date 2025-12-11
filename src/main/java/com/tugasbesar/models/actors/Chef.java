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

        // Hitbox kecil biar enak
        int hitSize = gp.tileSize / 2;
        int hitPad = gp.tileSize / 4;

        this.solidArea = new Rectangle(hitPad, hitPad, hitSize, hitSize);
        this.solidAreaDefaultX = solidArea.x;
        this.solidAreaDefaultY = solidArea.y;

        setDefaultValues();
    }

    public void setDefaultValues() {
        speed = 4; // Scale 4 -> Speed 4 (Pas)
        direction = "down";
        this.heldItem = null;
        this.isBusy = false;

        if (playerID == 1)
            chefColor = Color.RED;
        else
            chefColor = Color.BLUE;
    }

    public void setDefaultValues(int startCol, int startRow) {
        this.x = startCol * gp.tileSize;
        this.y = startRow * gp.tileSize;
        setDefaultValues();
    }

    public void update(KeyHandler inputKeyH) {

        // [LOGIC SWAP]
        // Jika inputKeyH NULL, artinya Chef ini SEDANG TIDAK AKTIF.
        // Maka dia tidak melakukan apa-apa (Diam).
        if (inputKeyH == null)
            return;

        if (isBusy)
            return;

        // [FIX] SEMUA CHEF PAKAI TOMBOL YANG SAMA (WASD)
        // Karena GamePanel hanya mengirim inputKeyH ke Chef yang sedang AKTIF.
        boolean up = inputKeyH.upPressed;
        boolean down = inputKeyH.downPressed;
        boolean left = inputKeyH.leftPressed;
        boolean right = inputKeyH.rightPressed;
        boolean dash = inputKeyH.dashPressed;
        boolean interact = inputKeyH.interactPressed;

        int currentSpeed = dash ? 8 : speed;
        boolean isMoving = false;

        if (up) {
            direction = "up";
            isMoving = true;
        } else if (down) {
            direction = "down";
            isMoving = true;
        } else if (left) {
            direction = "left";
            isMoving = true;
        } else if (right) {
            direction = "right";
            isMoving = true;
        }

        collisionOn = false;
        gp.cChecker.checkObject(this, true);
        gp.cChecker.checkWindowBoundary(this);

        if (isMoving && !collisionOn) {
            switch (direction) {
                case "up":
                    y -= currentSpeed;
                    break;
                case "down":
                    y += currentSpeed;
                    break;
                case "left":
                    x -= currentSpeed;
                    break;
                case "right":
                    x += currentSpeed;
                    break;
            }
        }

        if (interact) {
            interact();
            // Reset tombol di KeyHandler biar gak spam
            inputKeyH.interactPressed = false;
        }
    }

    @Override
    public void update() {
    }

    public void interact() {
        int centerX = this.x + gp.tileSize / 2;
        int centerY = this.y + gp.tileSize / 2;
        int reach = gp.tileSize / 2 + 10;

        int sensorX = centerX;
        int sensorY = centerY;

        switch (direction) {
            case "up":
                sensorY -= reach;
                break;
            case "down":
                sensorY += reach;
                break;
            case "left":
                sensorX -= reach;
                break;
            case "right":
                sensorX += reach;
                break;
        }

        int targetCol = sensorX / gp.tileSize;
        int targetRow = sensorY / gp.tileSize;

        for (int i = 0; i < gp.station.length; i++) {
            if (gp.station[i] != null) {
                if (gp.station[i].getPosX() == targetCol && gp.station[i].getPosY() == targetRow) {
                    System.out.println("✅ P" + playerID + " INTERAKSI: " + gp.station[i].getName());
                    gp.station[i].interact(this);
                    return;
                }
            }
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        // Visual Chef Kecil (Proporsional)
        int padding = 8;
        int drawSize = gp.tileSize - (padding * 2);
        int drawX = x + padding;
        int drawY = y + padding;

        g2.setColor(chefColor);
        g2.fillRect(drawX, drawY, drawSize, drawSize);

        g2.setColor(Color.WHITE);
        int eyeSize = 6;
        if (direction.equals("up"))
            g2.fillRect(drawX + drawSize / 2 - 8, drawY + 2, 16, eyeSize);
        if (direction.equals("down"))
            g2.fillRect(drawX + drawSize / 2 - 8, drawY + drawSize - 8, 16, eyeSize);
        if (direction.equals("left"))
            g2.fillRect(drawX + 2, drawY + drawSize / 2 - 8, eyeSize, 16);
        if (direction.equals("right"))
            g2.fillRect(drawX + drawSize - 8, drawY + drawSize / 2 - 8, eyeSize, 16);

        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(10F));
        int textLen = (int) g2.getFontMetrics().getStringBounds(name, g2).getWidth();
        g2.drawString(name, drawX + (drawSize / 2) - (textLen / 2), drawY - 2);

        if (heldItem != null) {
            g2.setColor(new Color(255, 215, 0));
            g2.fillOval(drawX + (drawSize / 2) - 8, drawY + (drawSize / 2) - 8, 16, 16);
        }
    }

    // Getters & Setters
    public String getName() {
        return name;
    }

    public Item getHeldItem() {
        return heldItem;
    }

    public void setHeldItem(Item item) {
        this.heldItem = item;
    }

    public boolean hasItem() {
        return heldItem != null;
    }

    public String getDirection() {
        return direction;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        this.isBusy = busy;
    }

    public int getPlayerID() {
        return playerID;
    }
}