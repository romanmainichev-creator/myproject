import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class character {
    public static int x;
    public static int y;
    public static boolean isVisible;
    public static BufferedImage image;
    character()
    {
        x=250;
        y=250;
        isVisible = true;
        try {
            image = ImageIO.read(new File("src/images.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        };

    }
    public static void move (int dx, int dy)
    {
        x-=dx;
        y-=dy;
    }
}
