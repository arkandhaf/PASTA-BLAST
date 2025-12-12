package com.tugasbesar.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class UI {

    GamePanel gp;
    Font arial_40, arial_80B, arial_20;
    public int commandNum = 0;

    public UI(GamePanel gp) {
        this.gp = gp;
        arial_40 = new Font("Arial", Font.PLAIN, 40);
        arial_80B = new Font("Arial", Font.BOLD, 80);
        arial_20 = new Font("Arial", Font.PLAIN, 20);
    }

    public void draw(Graphics2D g2) {
        if (gp.gameState == gp.titleState) {
            drawTitleScreen(g2);
        } else if (gp.gameState == gp.stageSelectState) {
            drawStageSelect(g2);
        } else if (gp.gameState == gp.howToPlayState) {
            drawHowToPlay(g2);
        } else if (gp.gameState == gp.resultState) {
            drawResultScreen(g2);
        }
    }

    // 1. MAIN MENU (MOUSE SUPPORT ENABLED)
    private void drawTitleScreen(Graphics2D g2) {
        g2.setColor(new Color(20, 20, 20));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Judul
        g2.setFont(arial_80B);
        String text = "PASTA-BLAST";
        int x = getXforCenteredText(text, g2);
        int y = gp.tileSize * 3;

        g2.setColor(Color.GRAY);
        g2.drawString(text, x + 5, y + 5);
        g2.setColor(Color.ORANGE);
        g2.drawString(text, x, y);

        g2.setFont(arial_40);
        text = "The Ultimate Smash Sauce";
        x = getXforCenteredText(text, g2);
        y += gp.tileSize;
        g2.setColor(Color.WHITE);
        g2.drawString(text, x, y);

        // --- MENU OPTIONS ---
        String[] options = { "START GAME", "HOW TO PLAY", "EXIT" };
        y += gp.tileSize;

        int optionCount = options.length;
        int[] optionX = new int[optionCount];
        int[] optionY = new int[optionCount];
        int optionSpacing = gp.tileSize + 15;
        int menuHeight = 40;

        int cursorY = y;
        for (int i = 0; i < optionCount; i++) {
            cursorY += optionSpacing;
            optionX[i] = getXforCenteredText(options[i], g2);
            optionY[i] = cursorY;
        }

        int hoveredIndex = -1;
        if (gp.mouseH != null) {
            for (int i = 0; i < optionCount; i++) {
                int textTop = optionY[i] - 30;
                if (gp.mouseH.mouseX > optionX[i] - 20 && gp.mouseH.mouseX < optionX[i] + 200
                        && gp.mouseH.mouseY > textTop && gp.mouseH.mouseY < textTop + menuHeight) {
                    hoveredIndex = i;
                    break;
                }
            }
        }

        if (hoveredIndex != -1) {
            commandNum = hoveredIndex;
        }

        for (int i = 0; i < optionCount; i++) {
            boolean highlight = (i == hoveredIndex) || (hoveredIndex == -1 && commandNum == i);
            if (highlight) {
                g2.setColor(Color.YELLOW);
                g2.drawString("> " + options[i], optionX[i] - 30, optionY[i]);

                if (i == hoveredIndex && gp.mouseH != null && gp.mouseH.mouseClicked) {
                    gp.mouseH.mouseClicked = false;
                    performMenuAction(i);
                }
            } else {
                g2.setColor(Color.WHITE);
                g2.drawString(options[i], optionX[i], optionY[i]);
            }
        }
    }

    // Helper untuk aksi menu (biar rapi)
    private void performMenuAction(int commandIndex) {
        if (commandIndex == 0) { // START
            gp.gameState = gp.stageSelectState;
        }
        if (commandIndex == 1) { // HOW TO PLAY
            gp.gameState = gp.howToPlayState;
        }
        if (commandIndex == 2) { // EXIT
            System.exit(0);
        }
    }

    // 2. STAGE SELECT MENU
    private void drawStageSelect(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 50));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setColor(Color.WHITE);
        g2.setFont(arial_80B);
        String text = "SELECT STAGE";
        int x = getXforCenteredText(text, g2);
        int y = gp.tileSize * 2;
        g2.drawString(text, x, y);

        // Kotak Preview
        int boxWidth = 300;
        int boxHeight = 200;
        int boxX = gp.screenWidth / 2 - boxWidth / 2;
        int boxY = y + 50;

        g2.setColor(Color.GRAY);
        g2.fillRect(boxX, boxY, boxWidth, boxHeight);
        g2.setColor(Color.WHITE);
        g2.drawRect(boxX, boxY, boxWidth, boxHeight);

        g2.setFont(arial_40);
        g2.drawString("Map Preview", boxX + 40, boxY + 110);

        // Navigasi Stage
        y = boxY + boxHeight + 60;
        String stageName = "STAGE " + (gp.currentStageIdx + 1);
        x = getXforCenteredText(stageName, g2);

        g2.setColor(Color.YELLOW);
        g2.drawString("<  " + stageName + "  >", x, y);

        // Info Score
        y += 50;
        g2.setFont(arial_20);
        g2.setColor(Color.WHITE);
        String target = "Target Score: " + gp.stageData[gp.currentStageIdx][1];
        x = getXforCenteredText(target, g2);
        g2.drawString(target, x, y);

        y += 30;
        boolean cleared = gp.stageCleared[gp.currentStageIdx];
        String status = cleared ? "STATUS: SUCCESS ✅" : "STATUS: NOT CLEARED ❌";
        g2.setColor(cleared ? Color.GREEN : Color.RED);
        x = getXforCenteredText(status, g2);
        g2.drawString(status, x, y);

        g2.setColor(Color.WHITE);
        g2.setFont(arial_20);
        g2.drawString("[ENTER] Start   [ESC] Back", 20, gp.screenHeight - 20);
    }

    // 3. RESULT SCREEN
    private void drawResultScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        int targetScore = Integer.parseInt(gp.stageData[gp.currentStageIdx][1]);
        int currentScore = gp.orderManager.getScore();
        boolean isPass = currentScore >= targetScore;

        if (isPass) {
            gp.stageCleared[gp.currentStageIdx] = true;
        }

        String title = isPass ? "STAGE CLEARED!" : "STAGE FAILED";
        g2.setFont(arial_80B);
        g2.setColor(isPass ? Color.GREEN : Color.RED);
        int x = getXforCenteredText(title, g2);
        int y = gp.screenHeight / 2 - 50;
        g2.drawString(title, x, y);

        g2.setFont(arial_40);
        g2.setColor(Color.WHITE);

        String scoreText = "Score: " + currentScore + " / " + targetScore;
        x = getXforCenteredText(scoreText, g2);
        y += 60;
        g2.drawString(scoreText, x, y);

        g2.setFont(arial_20);
        g2.setColor(Color.YELLOW);
        String prompt = "Press [ENTER] to Return to Menu";
        x = getXforCenteredText(prompt, g2);
        y += 80;
        g2.drawString(prompt, x, y);
    }

    // 4. HOW TO PLAY (UPDATED CONTROLS)
    private void drawHowToPlay(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setColor(Color.WHITE);
        g2.setFont(arial_40);
        String text = "CONTROLS";
        int x = getXforCenteredText(text, g2);
        int y = 80;
        g2.drawString(text, x, y);

        g2.setFont(arial_20);
        int leftAlign = gp.screenWidth / 3;
        y += 60;

        // --- [UPDATE] INFO KONTROL BARU ---
        g2.drawString("Move:", leftAlign, y);
        g2.drawString("W, A, S, D", leftAlign + 180, y);
        y += 40;

        g2.drawString("Grab / Drop:", leftAlign, y);
        g2.drawString("Space Bar", leftAlign + 180, y);
        y += 40;

        g2.drawString("Work / Use:", leftAlign, y);
        g2.drawString("E", leftAlign + 180, y);
        y += 40;

        g2.drawString("Dash / Run:", leftAlign, y);
        g2.drawString("Shift", leftAlign + 180, y);
        y += 40;

        g2.drawString("Swap Chef:", leftAlign, y);
        g2.drawString("Tab / Enter", leftAlign + 180, y);
        y += 40;
        // ----------------------------------

        y += 80;
        text = "Press [ESC] to Back";
        x = getXforCenteredText(text, g2);
        g2.setColor(Color.YELLOW);
        g2.drawString(text, x, y);
    }

    private int getXforCenteredText(String text, Graphics2D g2) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return gp.screenWidth / 2 - length / 2;
    }
}