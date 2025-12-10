package com.tugasbesar.core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    GamePanel gp; // Variabel penampung

    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean interactPressed;
    public boolean dashPressed;
    public boolean turnSwapPressed = false; 

    // Constructor menerima GamePanel
    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        
        // --- TITLE STATE (Menu Awal) ---
        if (gp.gameState == gp.titleState) {
            if (code == KeyEvent.VK_ENTER) {
                gp.gameState = gp.playState; // Masuk ke game
                // [FIX] Tidak perlu panggil startGameTimer() karena sudah jalan di GamePanel
            }
        }
        
        // --- PLAY STATE (Lagi Main) ---
        else if (gp.gameState == gp.playState) {
            // Gerakan
            if (code == KeyEvent.VK_W) upPressed = true;
            if (code == KeyEvent.VK_S) downPressed = true;
            if (code == KeyEvent.VK_A) leftPressed = true;
            if (code == KeyEvent.VK_D) rightPressed = true;
            
            // Interaksi
            if (code == KeyEvent.VK_SPACE) interactPressed = true;
            if (code == KeyEvent.VK_SHIFT) dashPressed = true;
            
            // Ganti Pemain
            if (code == KeyEvent.VK_ENTER) turnSwapPressed = true;
            
            // Pause
            if (code == KeyEvent.VK_P) gp.gameState = gp.pauseState;
        }
        
        // --- PAUSE STATE (Lagi Pause) ---
        else if (gp.gameState == gp.pauseState) {
            if (code == KeyEvent.VK_P) gp.gameState = gp.playState;
        }
        
        // --- GAME OVER STATE ---
        else if (gp.gameState == gp.gameOverState) {
            if (code == KeyEvent.VK_ENTER) {
                // Reset game
                gp.gameState = gp.titleState;
                gp.gameTime = 180;
                gp.isGameRunning = true;
            }
            if (code == KeyEvent.VK_ESCAPE) {
                System.exit(0); // Quit game
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        
        if (code == KeyEvent.VK_W) upPressed = false;
        if (code == KeyEvent.VK_S) downPressed = false;
        if (code == KeyEvent.VK_A) leftPressed = false;
        if (code == KeyEvent.VK_D) rightPressed = false;
        
        if (code == KeyEvent.VK_SPACE) interactPressed = false;
        if (code == KeyEvent.VK_SHIFT) dashPressed = false; 
        if (code == KeyEvent.VK_ENTER) turnSwapPressed = false; 
    }
}