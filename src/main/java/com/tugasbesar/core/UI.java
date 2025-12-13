package com.tugasbesar.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.image.BufferedImage;

public class UI {

    GamePanel gp;
    Font arial_40, arial_52B, arial_80B, arial_20;
    private final BufferedImage stagePreviewImage;
    private final BufferedImage titleBackground;
    private final BufferedImage blankBackground;
    public int commandNum = 0;

    public UI(GamePanel gp) {
        this.gp = gp;
        arial_40 = new Font("Lucida Console", Font.PLAIN, 40);
        arial_52B = new Font("Lucida Console", Font.BOLD, 52);
        arial_80B = new Font("Lucida Console", Font.BOLD, 80);
        arial_20 = new Font("Lucida Console", Font.PLAIN, 20);
        stagePreviewImage = AssetManager.getInstance().loadImage("maps/preview.png");
        titleBackground = AssetManager.getInstance().loadUIImage("bg-title");
        blankBackground = AssetManager.getInstance().loadUIImage("bg-blank");
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

    // 1. MAIN MENU
    private void drawTitleScreen(Graphics2D g2) {
        drawBackground(g2, titleBackground, Color.BLACK, 0.0f);

        int y = gp.tileSize * 3;
        g2.setFont(arial_40);
        y += gp.tileSize;

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

        java.awt.FontMetrics optionMetrics = g2.getFontMetrics();
        int paddingX = 60;
        int paddingY = 25;
        int minLeft = Integer.MAX_VALUE;
        int maxRight = Integer.MIN_VALUE;
        for (int i = 0; i < optionCount; i++) {
            int textWidth = optionMetrics.stringWidth(options[i]);
            int left = optionX[i] - paddingX;
            int right = optionX[i] + textWidth + paddingX;
            if (left < minLeft)
                minLeft = left;
            if (right > maxRight)
                maxRight = right;
        }
        int top = optionY[0] - optionMetrics.getAscent() - paddingY;
        int bottom = optionY[optionCount - 1] + optionMetrics.getDescent() + paddingY;
        int boxX = minLeft;
        int boxY = top;
        int boxWidth = maxRight - minLeft;
        int boxHeight = bottom - top;

        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 24, 24);
        g2.setColor(new Color(255, 255, 255, 30));
        g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 24, 24);

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
        drawBackground(g2, blankBackground, Color.BLACK, 0.7f);

        g2.setColor(Color.WHITE);
        g2.setFont(arial_52B);
        String text = "SELECT STAGE";
        int x = getXforCenteredText(text, g2);
        int y = gp.tileSize + 36;
        g2.drawString(text, x, y);

        // Kotak Preview
        int baseBoxWidth = 300;
        int baseBoxHeight = 200;
        int boxWidth = baseBoxWidth;
        int boxHeight = baseBoxHeight;
        int previewWidth = -1;
        int previewHeight = -1;
        boolean hasPreview = stagePreviewImage != null;

        if (hasPreview) {
            double scaleLimit = Math.min(baseBoxWidth / (double) stagePreviewImage.getWidth(),
                    baseBoxHeight / (double) stagePreviewImage.getHeight());
            scaleLimit = Math.min(scaleLimit, 1.0);
            previewWidth = (int) Math.round(stagePreviewImage.getWidth() * scaleLimit);
            previewHeight = (int) Math.round(stagePreviewImage.getHeight() * scaleLimit);
            boxWidth = previewWidth;
            boxHeight = previewHeight;
        }

        int boxX = gp.screenWidth / 2 - boxWidth / 2;
        int boxY = y + 36;

        if (stagePreviewImage != null) {
            int previewX = boxX + (boxWidth - previewWidth) / 2;
            int previewY = boxY + (boxHeight - previewHeight) / 2;
            g2.drawImage(stagePreviewImage, previewX, previewY, previewWidth, previewHeight, null);
        } else {
            g2.setColor(Color.GRAY);
            g2.fillRect(boxX, boxY, boxWidth, boxHeight);
            g2.setFont(arial_40);
            g2.setColor(Color.WHITE);
            g2.drawString("Map Preview", boxX + 40, boxY + 110);
        }
        g2.setColor(Color.WHITE);
        g2.drawRect(boxX, boxY, boxWidth, boxHeight);

        // Navigasi Stage
        y = boxY + boxHeight + 60;
        String stageName = "< STAGE " + (gp.currentStageIdx + 1) + " >";
        x = getXforCenteredText(stageName, g2);

        g2.setColor(Color.YELLOW);
        g2.drawString(stageName, x, y);

        // Info Score
        y += 42;
        g2.setFont(arial_20);
        g2.setColor(Color.WHITE);
        String target = "Target Score: " + gp.stageData[gp.currentStageIdx][1];
        x = getXforCenteredText(target, g2);
        g2.drawString(target, x, y);

        y += 30;
        boolean cleared = gp.stageCleared[gp.currentStageIdx];
        String status = cleared ? "STATUS: SUCCESS" : "STATUS: NOT CLEARED";
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
        drawBackground(g2, blankBackground, Color.BLACK, 0.7f);

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

    private void drawBackground(Graphics2D g2, BufferedImage image, Color fallbackColor, float overlayAlpha) {
        if (image != null) {
            double scale = Math.max(gp.screenWidth / (double) image.getWidth(),
                    gp.screenHeight / (double) image.getHeight());
            int drawWidth = (int) Math.round(image.getWidth() * scale);
            int drawHeight = (int) Math.round(image.getHeight() * scale);
            int drawX = (gp.screenWidth - drawWidth) / 2;
            int drawY = (gp.screenHeight - drawHeight) / 2;
            g2.drawImage(image, drawX, drawY, drawWidth, drawHeight, null);
        } else {
            g2.setColor(fallbackColor);
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        }

        if (overlayAlpha > 0f) {
            Composite original = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, overlayAlpha));
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
            g2.setComposite(original);
        }
    }
}