package com.tugasbesar.models.actors;

import com.tugasbesar.core.GamePanel;
import com.tugasbesar.core.KeyHandler;
import com.tugasbesar.models.abstracts.Entity;
import com.tugasbesar.models.abstracts.Item; 
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Chef extends Entity {

    // --- VIEW/CONTROLLER VARIABELS ---
    GamePanel gp;
    // Hapus KeyHandler keyH; dari field, karena sekarang KeyHandler dipass ke update()
    private int playerID; 

    // --- MODEL/LOGIC VARIABELS ---
    private String name;
    private Item heldItem; 
    private String orientation; 
    private boolean isBusy; 

    private Color chefColor; 

    // Constructor BARU (tidak menyimpan keyH sebagai field, tapi kita tetap butuh di sini)
    public Chef(GamePanel gp, KeyHandler keyH, String name, int playerID) { 
        this.gp = gp;
        this.name = name;
        this.playerID = playerID;
        setDefaultValues();
    }

    public void setDefaultValues() {
        speed = 4;
        
        // Penempatan awal berdasarkan ID pemain
        if (playerID == 1) {
            x = 100;
            y = 100;
            chefColor = Color.RED;
        } else { // playerID == 2
            x = 500;
            y = 100;
            chefColor = Color.BLUE;
        }

        this.solidArea = new Rectangle(8, 16, 32, 32); 
        
        this.heldItem = null; 
        this.orientation = "down"; 
        this.isBusy = false;
    }

    // ------------------------------------------------------------------------
    // --- UPDATE METHOD (Hanya bergerak jika menerima KeyHandler) ---
    // ------------------------------------------------------------------------
    
    // NOTE: Override public void update() yang lama harus diganti dengan ini
    public void update(KeyHandler inputKeyH) {
        
        // Cek: Jika tidak ada KeyHandler yang dipass (bukan giliran), keluar dari method.
        if (inputKeyH == null) {
            // Kita bisa atur speed menjadi 0 atau membiarkan Chef diam
            return; 
        }

        // --- CHEF AKTIF: LOGIC MOVEMENT DAN AKSI ---
        speed = 4;
        
        if (inputKeyH.dashPressed) { 
            speed = 8; 
        } 
        
        // 1. GERAKAN
        if (inputKeyH.upPressed || inputKeyH.downPressed || inputKeyH.leftPressed || inputKeyH.rightPressed) {
            if (inputKeyH.upPressed) { orientation = "up"; y -= speed; }
            else if (inputKeyH.downPressed) { orientation = "down"; y += speed; }
            else if (inputKeyH.leftPressed) { orientation = "left"; x -= speed; }
            else if (inputKeyH.rightPressed) { orientation = "right"; x += speed; }
        }
        
        // 2. INTERAKSI
        if (inputKeyH.interactPressed) { 
            // interact(); // Panggil logic interaksi
            inputKeyH.interactPressed = false; // Matikan tombol interaksi
        }
        
        // 3. COLLISION CHECK (gunakan gp.cChecker karena gp adalah field)
        gp.cChecker.checkWindowBoundary(this); 
    }

    // Metode update() kosong untuk kompatibilitas jika Entity/Runnable membutuhkannya
    @Override
    public void update() {
        // Ini adalah metode kosong yang dipanggil jika update(KeyHandler) tidak dipanggil.
    }

    // ------------------------------------------------------------------------
    // --- DRAW METHOD ---
    // ... (tetap sama) ...
    
    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(chefColor);
        g2.fillRect(x, y, gp.tileSize, gp.tileSize); 
        
        g2.setColor(Color.WHITE);
        g2.drawString(name, x, y - 5); 

        g2.setColor(Color.BLACK);
        switch(orientation) {
             case "up": g2.fillRect(x + 20, y + 5, 8, 8); break;
             case "down": g2.fillRect(x + 20, y + 35, 8, 8); break;
             case "left": g2.fillRect(x + 5, y + 20, 8, 8); break;
             case "right": g2.fillRect(x + 35, y + 20, 8, 8); break;
        }

        if (heldItem != null) {
            g2.setColor(new Color(255, 165, 0)); 
            g2.fillOval(x + 12, y - 10, 24, 24); 
        }
    }

    // ------------------------------------------------------------------------
    // --- LOGIC GETTERS/SETTERS ---
    // ------------------------------------------------------------------------
    
    public String getName() { return name; }
    public Item getHeldItem() { return heldItem; }
    public void setHeldItem(Item item) { this.heldItem = item; }
    public boolean hasItem() { return heldItem != null; } 

    public String getOrientation() { return orientation; }
    public boolean isBusy() { return isBusy; }
    public void setBusy(boolean busy) { this.isBusy = busy; }
    public int getPlayerID() { return playerID; }
}