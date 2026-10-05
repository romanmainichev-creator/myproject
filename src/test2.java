import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class test2 extends JFrame {
    public character pers = new character();
    public BufferedImage fon;

    public KeyListener kl = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                System.out.println("l");
                pers.x--;
                repaint();
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                System.out.println("r");
                pers.x++;
                repaint();
            }
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                System.out.println("u");
                pers.y--;
                repaint();
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                System.out.println("d");
                pers.y++;
                repaint();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            
        }
    };
        public void keyReleased(KeyEvent e) {
    };
    test2()
    {
        try {
            fon = ImageIO.read(new File("src/fon.jpg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        };
        setVisible(true);
        setSize(800,569);
        setTitle("67");
        addKeyListener(kl);

    }
    public void paint(Graphics g)
    {
        super.paint(g);
        g.setColor(Color.BLACK);
        g.drawImage(fon, 0, 0, this);
        g.drawImage(pers.image, pers.x, pers.y, this);


    }


        public static void main(String[] args) {
            new test2();
            character fox = new character();
            System.out.println(fox.x + " " + fox.y + " " + fox.isVisible);
            fox.move(0, 15);
            System.out.println(fox.x + " " + fox.y + " " + fox.isVisible);
        }
    }
