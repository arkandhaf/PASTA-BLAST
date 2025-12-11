package com.tugasbesar.core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    GamePanel gp;

    // Kontrol Gerak (WASD)
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    // Kontrol Aksi
    public boolean interactPressed, dashPressed;
    // Kontrol Swap
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
            
            // --- SWAP PLAYER (TAB atau ENTER) ---
            if (code == KeyEvent.VK_TAB || code == KeyEvent.VK_ENTER) {
                turnSwapPressed = true;
            }

            // --- MOVEMENT (WASD) ---
            if (code == KeyEvent.VK_W) upPressed = true;
            if (code == KeyEvent.VK_S) downPressed = true;
            if (code == KeyEvent.VK_A) leftPressed = true;
            if (code == KeyEvent.VK_D) rightPressed = true;
            
            // --- ACTION (SPASI & SHIFT) ---
            if (code == KeyEvent.VK_SPACE) interactPressed = true;
            if (code == KeyEvent.VK_SHIFT) dashPressed = true;

            // Pause
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