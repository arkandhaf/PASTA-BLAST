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
        
        // Cek Variable State (Sekarang udah aman karena ada di GamePanel)
        if (gp.gameState == gp.titleState) {
            if (code == KeyEvent.VK_ENTER) {
                gp.gameState = gp.playState; 
                gp.startGameTimer(); 
            }
        }
        else if (gp.gameState == gp.playState) {
            if (code == KeyEvent.VK_W) upPressed = true;
            if (code == KeyEvent.VK_S) downPressed = true;
            if (code == KeyEvent.VK_A) leftPressed = true;
            if (code == KeyEvent.VK_D) rightPressed = true;
            if (code == KeyEvent.VK_SPACE) interactPressed = true;
            if (code == KeyEvent.VK_SHIFT) dashPressed = true;
            if (code == KeyEvent.VK_ENTER) turnSwapPressed = true;
            if (code == KeyEvent.VK_P) gp.gameState = gp.pauseState;
        }
        else if (gp.gameState == gp.pauseState) {
            if (code == KeyEvent.VK_P) gp.gameState = gp.playState;
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