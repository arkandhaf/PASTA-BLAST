package com.tugasbesar.models.actors;

import com.tugasbesar.core.AssetManager;
import com.tugasbesar.core.GamePanel;
import com.tugasbesar.core.KeyHandler;
import com.tugasbesar.models.abstracts.Entity;
import com.tugasbesar.models.abstracts.Item;
import com.tugasbesar.models.stations.Station;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Chef extends Entity {

    GamePanel gp;
    private int playerID;
    private String name;
    private Item heldItem;
    private boolean isBusy;
    private Color chefColor;
    private boolean moving;

    private AssetManager assetManager;
    private Map<String, BufferedImage> standingSprites;
    private Map<String, BufferedImage[]> walkingSprites;

    // Tambahan untuk debounce tombol
    private boolean prevGrabPressed = false;
    private boolean prevUsePressed = false;

    public Chef(GamePanel gp, KeyHandler keyH, String name, int playerID) {
        this.gp = gp;
        this.name = name;
        this.playerID = playerID;

        // Hitbox
        int hitSize = gp.tileSize / 2;
        int hitPad = gp.tileSize / 4;

        this.solidArea = new Rectangle(hitPad, hitPad, hitSize, hitSize);
        this.solidAreaDefaultX = solidArea.x;
        this.solidAreaDefaultY = solidArea.y;

        assetManager = AssetManager.getInstance();
        loadSprites();

        setDefaultValues();
    }

    public void setDefaultValues() {
        speed = 4;
        direction = "down";
        this.heldItem = null;
        this.isBusy = false;
        this.moving = false;

        if (playerID == 1)
            chefColor = Color.RED;
        else
            chefColor = Color.BLUE;
    }

    public void setDefaultValues(int startCol, int startRow) {
        this.x = startCol * gp.tileSize;
        this.y = startRow * gp.tileSize;
        setDefaultValues();
    }

    public void update(KeyHandler inputKeyH) {
        if (inputKeyH == null)
            return;
        if (isBusy)
            return;

        boolean up = inputKeyH.upPressed;
        boolean down = inputKeyH.downPressed;
        boolean left = inputKeyH.leftPressed;
        boolean right = inputKeyH.rightPressed;
        boolean dash = inputKeyH.dashPressed;

        // --- INPUT BARU: SPACE & E ---
        boolean grab = inputKeyH.interactPressed; // SPACE
        boolean use = inputKeyH.usePressed; // E

        int currentSpeed = dash ? 8 : speed;
        boolean wantsToMove = false;

        if (up) {
            direction = "up";
            wantsToMove = true;
        } else if (down) {
            direction = "down";
            wantsToMove = true;
        } else if (left) {
            direction = "left";
            wantsToMove = true;
        } else if (right) {
            direction = "right";
            wantsToMove = true;
        }

        int nextX = x;
        int nextY = y;

        if (wantsToMove) {
            switch (direction) {
                case "up":
                    nextY -= currentSpeed;
                    break;
                case "down":
                    nextY += currentSpeed;
                    break;
                case "left":
                    nextX -= currentSpeed;
                    break;
                case "right":
                    nextX += currentSpeed;
                    break;
            }
        }

        collisionOn = false;
        int originalSpeed = speed;
        speed = currentSpeed;
        gp.cChecker.checkObject(this, true);
        speed = originalSpeed;
        gp.cChecker.checkWindowBoundary(this);

        boolean moved = false;
        if (wantsToMove && !collisionOn) {
            if (!gp.isChefCollision(this, nextX, nextY)) {
                x = nextX;
                y = nextY;
                moved = true;
            } else {
                collisionOn = true;
            }
        }

        moving = moved;

        if (!wantsToMove || !moving)
            updateIdleAnimation();
        else
            advanceWalkAnimation();

        // --- INTERAKSI (DEBOUNCED) ---

        // GRAB (Space) - Hanya sekali tekan
        if (grab && !prevGrabPressed) {
            interact("grab");
        }

        // USE (E) - Bisa ditahan (untuk Cutting) atau sekali tekan (untuk Stove)
        if (use) {
            // Kita panggil terus menerus (CuttingStation butuh ini)
            // Untuk Stove, kita handle di dalam CookingStation biar gak spam start
            interact("use");
        }

        // Update status tombol sebelumnya
        prevGrabPressed = grab;
        prevUsePressed = use;
    }

    @Override
    public void update() {
    }

    public void interact(String type) {
        int centerX = this.x + gp.tileSize / 2;
        int centerY = this.y + gp.tileSize / 2;
        int reach = gp.tileSize / 2 + 10;

        int sensorX = centerX;
        int sensorY = centerY;

        switch (direction) {
            case "up":
                sensorY -= reach;
                break;
            case "down":
                sensorY += reach;
                break;
            case "left":
                sensorX -= reach;
                break;
            case "right":
                sensorX += reach;
                break;
        }

        int targetCol = sensorX / gp.tileSize;
        int targetRow = sensorY / gp.tileSize;

        // Cari Station di depan muka
        Station targetStation = null;
        for (int i = 0; i < gp.station.length; i++) {
            if (gp.station[i] != null) {
                if (gp.station[i].getPosX() == targetCol && gp.station[i].getPosY() == targetRow) {
                    targetStation = gp.station[i];
                    break;
                }
            }
        }

        // Kalau ada station, panggil method yang sesuai
        if (targetStation != null) {
            if (type.equals("grab")) {
                // System.out.println("🖐️ GRAB: " + targetStation.getName());
                targetStation.interactGrab(this);
            } else if (type.equals("use")) {
                // System.out.println("🔨 USE: " + targetStation.getName());
                targetStation.interactUse(this);
            }
            return;
        }

        // Drop di lantai (Hanya Grab)
        if (gp != null && type.equals("grab")) {
            if (hasItem()) {
                gp.pushTilePopup(targetCol, targetRow, heldItem, null, "Put down?", null, new Color(255, 193, 7));
            } else {
                gp.pushTilePopup(targetCol, targetRow, null, null, "Nothing here", "Try a station",
                        new Color(255, 193, 7));
            }
        }
    }

    // ... (Sisa method draw, getter/setter sama persis) ...
    @Override
    public void draw(Graphics2D g2) {
        BufferedImage sprite = getCurrentSprite();
        if (sprite != null) {
            g2.drawImage(sprite, x, y, gp.tileSize, gp.tileSize, null);
        } else {
            int padding = 8;
            int drawSize = gp.tileSize - (padding * 2);
            int drawX = x + padding;
            int drawY = y + padding;
            g2.setColor(chefColor);
            g2.fillRect(drawX, drawY, drawSize, drawSize);
            g2.setColor(Color.WHITE);
            int eyeSize = 6;
            if (direction.equals("up"))
                g2.fillRect(drawX + drawSize / 2 - 8, drawY + 2, 16, eyeSize);
            if (direction.equals("down"))
                g2.fillRect(drawX + drawSize / 2 - 8, drawY + drawSize - 8, 16, eyeSize);
            if (direction.equals("left"))
                g2.fillRect(drawX + 2, drawY + drawSize / 2 - 8, eyeSize, 16);
            if (direction.equals("right"))
                g2.fillRect(drawX + drawSize - 8, drawY + drawSize / 2 - 8, eyeSize, 16);
        }

        boolean isActivePlayer = gp != null && gp.activePlayerID == playerID;
        if (isActivePlayer) {
            g2.setFont(g2.getFont().deriveFont(10F));
            java.awt.FontMetrics metrics = g2.getFontMetrics();
            int textWidth = metrics.stringWidth(name);
            int textHeight = metrics.getHeight();
            int pointerWidth = textWidth + 8;
            int pointerHeight = textHeight;
            int pointerX = x + (gp.tileSize / 2) - (pointerWidth / 2);
            int pointerY = y + gp.tileSize + 4;

            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRoundRect(pointerX, pointerY, pointerWidth, pointerHeight, 6, 6);

            g2.setColor(Color.WHITE);
            int textX = x + (gp.tileSize / 2) - (textWidth / 2);
            int textY = pointerY + metrics.getAscent();
            g2.drawString(name, textX, textY);
        }

        if (heldItem != null) {
            g2.setColor(new Color(255, 215, 0));
            g2.fillOval(x + (gp.tileSize / 2) - 8, y - 12, 16, 16);
        }
    }

    private void loadSprites() {
        standingSprites = assetManager.loadChefStanding();
        walkingSprites = new HashMap<>();
        walkingSprites.put("up", toFrameArray(assetManager.loadChefWalking("up")));
        walkingSprites.put("down", toFrameArray(assetManager.loadChefWalking("down")));
        walkingSprites.put("left", toFrameArray(assetManager.loadChefWalking("left")));
        walkingSprites.put("right", toFrameArray(assetManager.loadChefWalking("right")));
    }

    private BufferedImage[] toFrameArray(Map<String, BufferedImage> frames) {
        if (frames == null || frames.isEmpty())
            return null;
        BufferedImage first = frames.get("left");
        BufferedImage second = frames.get("right");
        if (first == null && second == null)
            return null;
        if (first == null)
            first = second;
        if (second == null)
            second = first;
        return new BufferedImage[] { first, second };
    }

    private BufferedImage getCurrentSprite() {
        String facing = direction != null ? direction.toLowerCase() : "down";
        if (moving) {
            BufferedImage[] frames = walkingSprites != null ? walkingSprites.get(facing) : null;
            if (frames != null) {
                int index = (spriteNum == 1) ? 0 : 1;
                BufferedImage frame = frames[index];
                if (frame != null)
                    return frame;
            }
        }
        if (standingSprites != null) {
            BufferedImage sprite = standingSprites.get(facing);
            if (sprite == null)
                sprite = standingSprites.get("down");
            return sprite;
        }
        return null;
    }

    private void advanceWalkAnimation() {
        spriteCounter++;
        if (spriteCounter > 12) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }

    private void updateIdleAnimation() {
        moving = false;
        spriteCounter = 0;
        spriteNum = 1;
    }

    public String getName() {
        return name;
    }

    public Item getHeldItem() {
        return heldItem;
    }

    public void setHeldItem(Item item) {
        this.heldItem = item;
    }

    public boolean hasItem() {
        return heldItem != null;
    }

    public String getDirection() {
        return direction;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        this.isBusy = busy;
    }

    public int getPlayerID() {
        return playerID;
    }
}