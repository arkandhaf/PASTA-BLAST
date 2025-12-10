package com.tugasbesar.core;

import javax.swing.JFrame;
import com.tugasbesar.core.GamePanel;

public class Main {
    public static void main(String[] args) {
        
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Pasta NimonsCooked - Milestone 2");

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // --- [PENTING] LOAD OBJEK STATION DULU ---
        // Kalau baris ini tidak ada, Kompor & Talenan tidak akan muncul!
        gamePanel.setupGame(); 
        // -----------------------------------------

        gamePanel.startGameThread();
    }
}