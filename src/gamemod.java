import javax.swing.*;
        import java.awt.*;
        import java.awt.event.*;
        import java.io.File;
import javax.imageio.ImageIO;

public class gamemod extends JFrame implements MouseListener {
    Image img;
    int x = 100;
    int y = 100;
    int clickX;
    int clickY;
    boolean isDragging = false;

    gamemod() {
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            img = ImageIO.read(new File("src/images.png"));
        } catch (Exception e) {
        }
        addMouseListener(this);

        Timer timer = new Timer(16, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isDragging) {
                    Point p = getMousePosition();
                    if (p != null) {
                        x = p.x - clickX;
                        y = p.y - clickY;
                    }
                }
                repaint();
            }
        });
        timer.start();

        setVisible(true);
    }

    public void paint(Graphics g) {
        super.paint(g);
        if (img != null) {
            g.drawImage(img, x, y, this);
        }
    }

    public void mousePressed(MouseEvent e) {
        if (img != null) {
            int w = img.getWidth(this);
            int h = img.getHeight(this);

            if (e.getX() >= x && e.getX() <= x + w && e.getY() >= y && e.getY() <= y + h) {
                clickX = e.getX() - x;
                clickY = e.getY() - y;
                isDragging = true;
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        isDragging = false;
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public static void main(String[] args) {
        new gamemod();
    }
}