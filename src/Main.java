import java.awt.*;


public class Main extends Frame {

    public Main()
    {
        setTitle("first drawing");
        setSize(800,640);
        setVisible(true);
    }
    public void paint(Graphics g)
    {
        super.paint(g);
        Graphics2D isa = (Graphics2D) g;
        g.setColor(Color.YELLOW);
        g.fillOval(100,100, 50, 50);

        g.drawLine(125,75, 125, 90);
        g.drawLine(125,155, 125, 170);
        g.drawLine(95,125, 80, 125);
        g.drawLine(155,125, 170, 125);
        g.drawLine(104,104, 92, 92);
        g.drawLine(145,104, 157, 92);
        g.drawLine(104,146, 92, 157);
        g.drawLine(145,145, 157, 157);


    }

    public static void main(String[] args) {
        //System.out.println("test");
        new Main();
    }
}