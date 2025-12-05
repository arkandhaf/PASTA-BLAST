import javax.swing.JFrame;

public class GameWindow extends JFrame {

    public GameWindow() {
        setTitle("PASTA-BLAST");
        setSize(840, 600); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        GamePanel panel = new GamePanel();
        add(panel);

        setVisible(true);
    }

    public static void main(String[] args) {
        new GameWindow();
    }
}
