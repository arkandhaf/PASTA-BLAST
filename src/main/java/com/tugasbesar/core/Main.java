package com.tugasbesar.core;

import javax.swing.JFrame;
// import com.tugasbesar.core.GamePanel; // Tidak perlu di-import kalau satu package, tapi dibiarkan juga gapapa

public class Main {
    public static void main(String[] args) {
        
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("PASTA - BLAST");

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        // --- [SETUP DULUAN] ---
        // Kita load map SEBELUM window ditampilkan
        // Biar pas window muncul, mapnya udah siap.
        gamePanel.setupGame(); 
        // ----------------------

        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gamePanel.startGameThread();
    }
} 