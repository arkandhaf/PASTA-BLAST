package com.tugasbesar.core;

import javax.swing.JFrame;

public class GameWindow {

    public GameWindow(GamePanel gp) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Pasta NimonsCooked - Milestone 2");

        window.add(gp);
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}