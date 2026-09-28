import javax.swing.*;
import java.awt.*;

public class test2 extends JFrame {
    test2()
    {
        setVisible(true);
        setSize(800,600);
        setTitle("67");

    }
    public void paint(Graphics g)
    {
        super.paint(g);
        g.setColor(Color.BLACK);
        g.drawLine(200,200,300,300);
        g.drawLine(200,200,100,300);
        g.drawRect(100, 300, 200, 200);
        g.drawLine(150,250,150,150);
        g.drawLine(150,150,120,150);
        g.drawLine(120,150,120,280);
        g.drawRect(200, 380, 50, 120);
    }


        public static void main(String[] args) {
            new test2();
            character fox = new character();
            System.out.println(fox.x + " " + fox.y + " " + fox.isVisible);
            fox.move(0, 15);
            System.out.println(fox.x + " " + fox.y + " " + fox.isVisible);
        }
    }
