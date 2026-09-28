public class character {
    public static int x;
    public static int y;
    public static boolean isVisible;
    character()
    {
        x=0;
        y=1;
        isVisible = true;
    }
    public static void move (int dx, int dy)
    {
        x-=dx;
        y-=dy;
    }
}
