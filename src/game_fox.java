import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class game_fox extends JFrame {
    public BufferedImage bg, fox;
    public int x=50;
    public ArrayList<fish> fish_ = new ArrayList<fish>();
    public KeyListener kl = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {
        }
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_RIGHT)
            {                x++;                repaint();            }
            if (e.getKeyCode()==KeyEvent.VK_LEFT)
            {                x--;                repaint();            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE)
            {
                if (fish_.size()>0)
                {
                    fish_.remove(new Random().nextInt(fish_.size()));
                    repaint();
                }
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {
        }
    };

    ActionListener al = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            for (int i=0; i<fish_.size();i++ )
                fish_.get(i).move();
            repaint();
        }
    };
    Timer time = new Timer(100,al);

    game_fox()
    {
        try {
            bg = ImageIO.read(new File("src/fon.jpg"));
            fox = ImageIO.read(new File("src/playbutton.png"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        setSize(900,450);
        setVisible(true);
        addKeyListener(kl);
        for (int i=0; i< 5;i++)
        {
            fish_.add(new fish());
        }
        time.start();
    }
    public void paint(Graphics g)
    {
        super.paint(g);
        g.drawImage(bg,0,0,this);
        g.drawImage(fox,x,300,this);
        for (int i=0; i< fish_.size(); i++)
        {
            g.drawImage(fish_.get(i).image, fish_.get(i).x, fish_.get(i).y, this);
        }
    }

    public static void main(String[] args)
    {
        new game_fox();
    }




}
