package com.tugasbesar.core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    GamePanel gp;

    // KONTROL UMUM (WASD) - Dipakai oleh chef manapun yang sedang aktif
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean interactPressed, dashPressed;
    
    // TOMBOL SWAP
    public boolean turnSwapPressed = false; 

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        
        if (gp.gameState == gp.titleState) {
            if (code == KeyEvent.VK_ENTER) gp.gameState = gp.playState; 
        }
        else if (gp.gameState == gp.playState) {
            
            // --- SWAP PLAYER (TAB / ENTER) ---
            if (code == KeyEvent.VK_TAB || code == KeyEvent.VK_ENTER) {
                turnSwapPressed = true;
            }

            // --- GERAK (WASD) ---
            if (code == KeyEvent.VK_W) upPressed = true;
            if (code == KeyEvent.VK_S) downPressed = true;
            if (code == KeyEvent.VK_A) leftPressed = true;
            if (code == KeyEvent.VK_D) rightPressed = true;
            
            // --- AKSI ---
            if (code == KeyEvent.VK_SPACE) interactPressed = true; // Spasi
            if (code == KeyEvent.VK_SHIFT) dashPressed = true;     // Shift (Lari)

            // PAUSE
            if (code == KeyEvent.VK_P) gp.gameState = gp.pauseState;
        }
        else if (gp.gameState == gp.pauseState) {
            if (code == KeyEvent.VK_P) gp.gameState = gp.playState;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        
        if (code == KeyEvent.VK_TAB || code == KeyEvent.VK_ENTER) turnSwapPressed = false;

        if (code == KeyEvent.VK_W) upPressed = false;
        if (code == KeyEvent.VK_S) downPressed = false;
        if (code == KeyEvent.VK_A) leftPressed = false;
        if (code == KeyEvent.VK_D) rightPressed = false;
        
        if (code == KeyEvent.VK_SPACE) interactPressed = false;
        if (code == KeyEvent.VK_SHIFT) dashPressed = false; 
    }
}