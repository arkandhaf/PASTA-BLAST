package com.tugasbesar.core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    GamePanel gp;

    // GAMEPLAY CONTROLS
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean interactPressed; // SPACE (Grab)
    public boolean usePressed;      // E (Process)
    public boolean dashPressed;     // SHIFT
    public boolean turnSwapPressed = false; 

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // 1. MAIN MENU (Navigasi Keyboard Cadangan)
        if (gp.gameState == gp.titleState) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.commandNum--;
                if(gp.ui.commandNum < 0) gp.ui.commandNum = 2;
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.commandNum++;
                if(gp.ui.commandNum > 2) gp.ui.commandNum = 0;
            }
            
            // ENTER biar bisa masuk walau mouse macet
            if (code == KeyEvent.VK_ENTER) {
                if(gp.ui.commandNum == 0) gp.gameState = gp.stageSelectState;
                if(gp.ui.commandNum == 1) gp.gameState = gp.howToPlayState;
                if(gp.ui.commandNum == 2) System.exit(0);
            }
        }
        
        // 2. STAGE SELECT
        else if (gp.gameState == gp.stageSelectState) {
            if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                gp.currentStageIdx--;
                if(gp.currentStageIdx < 0) gp.currentStageIdx = gp.stageData.length - 1;
            }
            if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                gp.currentStageIdx++;
                if(gp.currentStageIdx >= gp.stageData.length) gp.currentStageIdx = 0;
            }
            if (code == KeyEvent.VK_ENTER) {
                gp.retryGame(); // START
            }
            if (code == KeyEvent.VK_ESCAPE) {
                gp.gameState = gp.titleState;
            }
        }

        // 3. HOW TO PLAY
        else if (gp.gameState == gp.howToPlayState) {
            if (code == KeyEvent.VK_ESCAPE) gp.gameState = gp.titleState;
        }

        // 4. RESULT SCREEN
        else if (gp.gameState == gp.resultState) {
            if (code == KeyEvent.VK_ENTER) {
                gp.gameState = gp.stageSelectState;
            }
        }

        // 5. GAMEPLAY
        else if (gp.gameState == gp.playState) {
            // Movement
            if (code == KeyEvent.VK_W) upPressed = true;
            if (code == KeyEvent.VK_S) downPressed = true;
            if (code == KeyEvent.VK_A) leftPressed = true;
            if (code == KeyEvent.VK_D) rightPressed = true;
            
            // Actions
            if (code == KeyEvent.VK_SPACE) interactPressed = true; // GRAB
            if (code == KeyEvent.VK_E) usePressed = true;          // USE
            if (code == KeyEvent.VK_SHIFT) dashPressed = true;
            
            // System
            if (code == KeyEvent.VK_TAB || code == KeyEvent.VK_ENTER) turnSwapPressed = true;
            if (code == KeyEvent.VK_P) gp.gameState = gp.pauseState;
        }
        
        // 6. PAUSE
        else if (gp.gameState == gp.pauseState) {
            if (code == KeyEvent.VK_P) gp.gameState = gp.playState;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (gp.gameState == gp.playState) {
            int code = e.getKeyCode();
            if (code == KeyEvent.VK_W) upPressed = false;
            if (code == KeyEvent.VK_S) downPressed = false;
            if (code == KeyEvent.VK_A) leftPressed = false;
            if (code == KeyEvent.VK_D) rightPressed = false;
            
            if (code == KeyEvent.VK_SPACE) interactPressed = false;
            if (code == KeyEvent.VK_E) usePressed = false;
            
            if (code == KeyEvent.VK_SHIFT) dashPressed = false;
            if (code == KeyEvent.VK_TAB || code == KeyEvent.VK_ENTER) turnSwapPressed = false;
        }
    }
}