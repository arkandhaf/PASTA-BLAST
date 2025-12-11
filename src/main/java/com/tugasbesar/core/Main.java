package com.tugasbesar.core;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        IntroVideoPlayer.playIntro(Main::launchGameWindow);
    }

    private static void launchGameWindow() {
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame();
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setResizable(false);
            window.setTitle("PASTA - BLAST");

            GamePanel gamePanel = new GamePanel();
            window.add(gamePanel);

            gamePanel.setupGame();

            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);

            gamePanel.startGameThread();
        });
    }
}
