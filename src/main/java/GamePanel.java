import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class GamePanel extends JPanel {

    private final int tileSize = 48;
    private final int rows = 10;
    private final int cols = 14;

    private final char[][] map = {
            {'X','X','X','X','X','X','X','X','X','X','X','X','X','X'},
            {'X','.','.','.','.','.','.','C','.','R','.','.','.','X'},
            {'X','.','A','.','.','.','X','X','X','.','.','I','.','X'},
            {'X','.','.','.','.','.','.','.','.','.','.','.','.','X'},
            {'X','.','W','.','.','.','X','X','X','.','.','P','.','X'},
            {'X','.','.','.','.','.','.','.','.','.','.','.','.','X'},
            {'X','.','S','.','.','.','X','X','X','.','.','T','.','X'},
            {'X','.','.','.','.','.','.','.','.','.','.','.','.','X'},
            {'X','.','.','.','.','.','.','.','.','.','.','.','.','X'},
            {'X','X','X','X','X','X','X','X','X','X','X','X','X','X'},
    };

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                char tile = map[r][c];

                if (tile == 'X') {
                    g.setColor(Color.darkGray);
                } else {
                    g.setColor(Color.lightGray);
                }

                g.fillRect(c * tileSize, r * tileSize, tileSize, tileSize);

                // warna station sementara
                if (tile != 'X' && tile != '.') {
                    g.setColor(Color.orange);
                    g.fillOval(c * tileSize + 12, r * tileSize + 12, 24, 24);
                }
            }
        }
    }
}
