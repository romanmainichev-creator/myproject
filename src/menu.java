import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class menu extends JFrame {
    public BufferedImage bg, button_start, button_setting, button_exit;

    MouseListener ml = new MouseListener()
    {

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            //System.out.println(e.getPoint().x+ " "+ e.getPoint().y);

            if (e.getPoint().x>275 && e.getPoint().x<525 && e.getPoint().y>150 && e.getPoint().y<233)
            {
                new test2();
//                System.out.println("start!!!");
//                System.exit(0);
            }
            if (e.getPoint().x>275 && e.getPoint().x<376 && e.getPoint().y>280 && e.getPoint().y<381)
            {
                JFrame settings = new JFrame("Настройки");
                settings.setSize(300, 200);
                settings.setVisible(true);
            }
            if (e.getPoint().x>275 && e.getPoint().x<504 && e.getPoint().y>410 && e.getPoint().y<484)
            {
                System.exit(0);
            }

        }

        @Override
        public void mouseReleased(MouseEvent e) {


        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    };

    menu()
    {
        try {
            bg = ImageIO.read(new File("src/fon2.png"));
            button_start = ImageIO.read(new File("src/playbutton.png"));
            button_setting = ImageIO.read(new File("src/settings.png"));
            button_exit = ImageIO.read(new File("src/quitbutton.png"));
        } catch (Exception e){
            throw new RuntimeException(e);
        }


        setSize(800,600);
        setVisible(true);
        setTitle("game");
        addMouseListener(ml);
    }
    public void paint(Graphics g)
    {
        super.paint(g);
        g.drawImage(bg,0,0,this);
        g.drawImage(button_start, 275, 150, this);   // Было 108, 100
        g.drawImage(button_setting, 275, 280, this); // Было 108, 300
        g.drawImage(button_exit, 275, 410, this);    // Было 108, 500

    }
    public static void main(String[] args)
    {
        new menu();
    }

}