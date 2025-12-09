import java.awt.image.BufferedImage;

public class Chef {
    public enum Direction {
        UP, DOWN, LEFT, RIGHT, IDLE
    }
    
    private int gridX;
    private int gridY;
    private int tileSize;
    
    // Animation states
    private Direction currentDirection = Direction.IDLE;
    private Direction lastDirection = Direction.DOWN; // Default facing down
    private int animationFrame = 0;
    private int animationCounter = 0;
    private static final int ANIMATION_SPEED = 5; // Lower = faster
    
    // Map boundaries
    private int mapCols;
    private int mapRows;

    public Chef(int startX, int startY, int tileSize, int mapCols, int mapRows) {
        this.gridX = startX;
        this.gridY = startY;
        this.tileSize = tileSize;
        this.mapCols = mapCols;
        this.mapRows = mapRows;
    }

    public void update() {
        // Update animation frame
        animationCounter++;
        if (animationCounter >= ANIMATION_SPEED) {
            animationCounter = 0;
            animationFrame = (animationFrame + 1) % 2; // Toggle between 0 and 1
        }
    }

    public void move(Direction direction) {
        // Don't move if already moving
        if (currentDirection != Direction.IDLE) {
            return;
        }

        int newX = gridX;
        int newY = gridY;

        switch (direction) {
            case UP:
                newY--;
                lastDirection = Direction.UP;
                break;
            case DOWN:
                newY++;
                lastDirection = Direction.DOWN;
                break;
            case LEFT:
                newX--;
                lastDirection = Direction.LEFT;
                break;
            case RIGHT:
                newX++;
                lastDirection = Direction.RIGHT;
                break;
        }

        // Check boundaries (avoid walls marked as 'X')
        if (isValidPosition(newX, newY)) {
            gridX = newX;
            gridY = newY;
            currentDirection = direction;
            animationFrame = 0;
            animationCounter = 0;
        }
    }

    private boolean isValidPosition(int x, int y) {
        // Check grid boundaries
        if (x < 0 || x >= mapCols || y < 0 || y >= mapRows) {
            return false;
        }
        
        // You can add more collision logic here if needed
        return true;
    }

    public void idle() {
        currentDirection = Direction.IDLE;
    }

    public BufferedImage getImage() {
        String imagePath = "chef/";
        
        // Determine which sprite to use based on direction and animation frame
        if (currentDirection == Direction.DOWN || lastDirection == Direction.DOWN) {
            if (currentDirection == Direction.IDLE) {
                imagePath += "standing_down.png";
            } else {
                imagePath += (animationFrame == 0) ? "walking_down_left.png" : "walking_down_right.png";
            }
        } else if (currentDirection == Direction.UP || lastDirection == Direction.UP) {
            if (currentDirection == Direction.IDLE) {
                imagePath += "standing_up.png";
            } else {
                imagePath += (animationFrame == 0) ? "walking_up_left.png" : "walking_up_right.png";
            }
        } else if (currentDirection == Direction.LEFT || lastDirection == Direction.LEFT) {
            if (currentDirection == Direction.IDLE) {
                imagePath += "standing_left.png";
            } else {
                imagePath += (animationFrame == 0) ? "walking_left_left.png" : "walking_left_right.png";
            }
        } else if (currentDirection == Direction.RIGHT || lastDirection == Direction.RIGHT) {
            if (currentDirection == Direction.IDLE) {
                imagePath += "standing_right.png";
            } else {
                imagePath += (animationFrame == 0) ? "walking_right_left.png" : "walking_right_right.png";
            }
        }

        return AssetManager.loadImage(imagePath);
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }

    public int getPixelX() {
        return gridX * tileSize;
    }

    public int getPixelY() {
        return gridY * tileSize;
    }
}
