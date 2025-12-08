package com.tugasbesar.core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    // Hanya KONTROL DASAR (Satu Set Input: WASD, SPACE, SHIFT)
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean interactPressed;
    public boolean dashPressed;
    
    // Tombol untuk MENGGANTI GILIRAN (TURN)
    public boolean turnSwapPressed = false; 

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        
        // --- KONTROL GERAKAN (WASD) ---
        if (code == KeyEvent.VK_W) upPressed = true;
        if (code == KeyEvent.VK_S) downPressed = true;
        if (code == KeyEvent.VK_A) leftPressed = true;
        if (code == KeyEvent.VK_D) rightPressed = true;
        
        // --- KONTROL AKSI ---
        if (code == KeyEvent.VK_SPACE) interactPressed = true;
        if (code == KeyEvent.VK_SHIFT) dashPressed = true;
        
        // --- KONTROL GANTI GILIRAN (Misalnya tombol ENTER) ---
        if (code == KeyEvent.VK_ENTER) turnSwapPressed = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        
        // --- KONTROL GERAKAN ---
        if (code == KeyEvent.VK_W) upPressed = false;
        if (code == KeyEvent.VK_S) downPressed = false;
        if (code == KeyEvent.VK_A) leftPressed = false;
        if (code == KeyEvent.VK_D) rightPressed = false;
        
        // --- KONTROL AKSI ---
        if (code == KeyEvent.VK_SPACE) interactPressed = false;
        if (code == KeyEvent.VK_SHIFT) dashPressed = false; 
        
        // --- KONTROL GANTI GILIRAN (Penting: harus dimatikan di sini) ---
        if (code == KeyEvent.VK_ENTER) turnSwapPressed = false; 
    }
}