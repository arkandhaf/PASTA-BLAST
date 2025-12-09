import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements KeyListener {

    private final int tileSize = 48;
    private final int rows = 10;
    private final int cols = 14;
    
    private Chef chef;
    private Set<Integer> pressedKeys;

    private final char[][] map = {
        {'A','A','R','R','A','A','X','X','X','X','X','X','X','X'},
        {'I','.','.','.','.','A','X','X','X','.','.','.','.','W'},
        {'I','.','.','.','.','A','X','X','X','.','.','.','.','W'},
        {'I','.','V','.','.','A','X','X','X','.','.','.','.','A'},
        {'A','.','.','.','.','X','X','X','X','.','.','.','.','R'},
        {'P','.','.','.','.','X','X','X','C','.','.','.','.','R'},
        {'S','.','.','.','.','X','X','X','C','.','.','V','.','I'},
        {'S','.','.','.','.','X','X','X','A','.','.','.','.','I'},
        {'A','.','.','.','.','.','.','.','.','.','.','.','.','T'},
        {'X','X','X','X','X','X','X','X','X','X','X','X','X','X'}
    };

    public GamePanel() {
        chef = new Chef(1, 1, tileSize, cols, rows);
        pressedKeys = new HashSet<>();
        
        setFocusable(true);
        addKeyListener(this);
        
        // Start game loop
        startGameLoop();
    }

    private void startGameLoop() {
        Thread gameThread = new Thread(() -> {
            while (true) {
                update();
                repaint();
                try {
                    Thread.sleep(16); // ~60 FPS
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        gameThread.setDaemon(true);
        gameThread.start();
    }

    private void update() {
        chef.update();
        
        // Handle continuous key input
        if (pressedKeys.contains(KeyEvent.VK_UP)) {
            chef.move(Chef.Direction.UP);
        } else if (pressedKeys.contains(KeyEvent.VK_DOWN)) {
            chef.move(Chef.Direction.DOWN);
        } else if (pressedKeys.contains(KeyEvent.VK_LEFT)) {
            chef.move(Chef.Direction.LEFT);
        } else if (pressedKeys.contains(KeyEvent.VK_RIGHT)) {
            chef.move(Chef.Direction.RIGHT);
        } else {
            chef.idle();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Enable rendering hints for smoother graphics
        if (g instanceof java.awt.Graphics2D) {
            java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, 
                java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        }

        // Draw tiles
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                char tile = map[r][c];
                BufferedImage tileImage = AssetManager.getTile(tile);

                if (tileImage != null) {
                    g.drawImage(tileImage, c * tileSize, r * tileSize, tileSize, tileSize, null);
                } else {
                    // Fallback to colored rectangles if image fails to load
                    if (tile == 'X') {
                        g.setColor(Color.darkGray);
                    } else {
                        g.setColor(Color.lightGray);
                    }
                    g.fillRect(c * tileSize, r * tileSize, tileSize, tileSize);
                }

                // Draw station markers on non-floor tiles
                if (tile != 'X' && tile != '.') {
                    g.setColor(new Color(255, 165, 0, 150)); // Semi-transparent orange
                    g.fillOval(c * tileSize + 12, r * tileSize + 12, 24, 24);
                }
            }
        }
        
        // Draw chef
        BufferedImage chefImage = chef.getImage();
        if (chefImage != null) {
            g.drawImage(chefImage, chef.getPixelX(), chef.getPixelY(), tileSize, tileSize, null);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        pressedKeys.add(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}

