package com.tugasbesar.core;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * MouseHandler - Handles mouse input events for the game
 * Can be used for menu interactions and UI clicks
 */
public class MouseHandler implements MouseListener {
    
    private GamePanel gp;
    
    public MouseHandler(GamePanel gp) {
        this.gp = gp;
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        
        // Handle title screen clicks
        if (gp.gameState == gp.titleState) {
            // Can add clickable buttons here later
            // For now, let keyboard handle it
        }
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        // Can be used for click-drag interactions
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        // Handle mouse release events
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        // Handle mouse entering the window
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        // Handle mouse exiting the window
    }
}
