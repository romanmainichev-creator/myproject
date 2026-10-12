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
            System.out.println(e.getPoint().x+ " "+ e.getPoint().y);

            if (e.getPoint().x>108 && e.getPoint().x<358 && e.getPoint().y>100 && e.getPoint().y<183)
            {
                new test2();
//                System.out.println("start!!!");
//                System.exit(0);
            }
            if (e.getPoint().x>108 && e.getPoint().x<209 && e.getPoint().y>300 && e.getPoint().y<401)
            {
                new JFrame("Настройки");
                setVisible(true);
//                System.out.println("start!!!");
//                System.exit(0);
            }
            if (e.getPoint().x>108 && e.getPoint().x<337 && e.getPoint().y>500 && e.getPoint().y<574)
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
            bg = ImageIO.read(new File("src/fon.jpg"));
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
        g.drawImage(button_start,108,100,this);
        g.drawImage(button_setting,108,300,this);
        g.drawImage(button_exit,108,500,this);

    }
    public static void main(String[] args)
    {
        new menu();
    }

}