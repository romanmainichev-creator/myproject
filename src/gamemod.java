import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;

public class gamemod extends JFrame implements MouseListener, MouseMotionListener {

    static final int CELL = 50;
    static final int COLS = 8, ROWS = 8;
    static final int BOARD_X = 100, BOARD_Y = 80;
    static final int TRAY_Y = BOARD_Y + ROWS * CELL + 20;
    static final int TRAY_H = 200;
    static final int W = 600;
    static final int H = TRAY_Y + TRAY_H + 20;

    final Random rnd = new Random();
    final ArrayList<int[][]> allShapes = new ArrayList<>();


    final ArrayList<int[][]> pieces = new ArrayList<>();
    final boolean[] used = new boolean[3];
    final int[][] grid = new int[ROWS][COLS];

    Image img;
    int dragging = -1;
    int dragX, dragY;
    int score = 0;
    boolean gameOver = false;
    gamemod() {

        loadImage();
        loadShapes();
        spawnPieces();

        setVisible(true);
        setResizable(false);

        setTitle("Block Blast");
       setSize(W,H);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        pole.setPreferredSize(new Dimension(W, H));
        pole.setFocusable(true);
        pole.addMouseListener(this);
        pole.addMouseMotionListener(this);
        setContentPane(pole);
        setLocationRelativeTo(null);


    }

    void loadImage() {
        try {
            img = ImageIO.read(new File("block_black.png"));
        } catch (Exception e) {

        }
    }

    void loadShapes() {
        allShapes.add(new int[][]{{1}});
        allShapes.add(new int[][]{{1, 1}});
        allShapes.add(new int[][]{{1, 1, 1}});
        allShapes.add(new int[][]{{1, 1, 1, 1}});
        allShapes.add(new int[][]{{1, 1}, {1, 1}});
        allShapes.add(new int[][]{{1, 0}, {1, 0}, {1, 1}});
        allShapes.add(new int[][]{{0, 1}, {0, 1}, {1, 1}});
        allShapes.add(new int[][]{{1, 1, 1}, {0, 1, 0}});
        allShapes.add(new int[][]{{1}, {1}, {1}});
        allShapes.add(new int[][]{{1, 1, 1}, {1, 0, 0}});
        allShapes.add(new int[][]{{1, 1, 1}, {0, 0, 1}});
        allShapes.add(new int[][]{{1, 0, 0}, {1, 1, 1}});
        allShapes.add(new int[][]{{0, 1}, {1, 1}});
        allShapes.add(new int[][]{{1, 0}, {1, 1}});
    }

    void spawnPieces() {
        pieces.clear(); // убираем очищаем старые
        for (int i = 0; i < 3; i++) {
            pieces.add(allShapes.get(rnd.nextInt(allShapes.size()))); // добавляем 3 новых
            used[i] = false;
        }
    }
    public JPanel pole = new JPanel(){
        @Override
        public void paintComponent(Graphics g2){
            super.paintComponent(g2);
            draw((Graphics2D) g2);

        }
    };



    // рисуем
        public void draw(Graphics2D original) {
        //super.paint(g);
        Graphics2D g2 = (Graphics2D) original.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(24, 26, 45));
        g2.fillRect(0, 0, W, H);

        drawHeader(g2);
        drawBoard(g2);
        drawTray(g2);
        if (dragging >= 0) drawDrag(g2);
    }

    void drawHeader(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.drawString("BLOCK BLAST", BOARD_X, 45);

        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        g2.setColor(new Color(200, 200, 220));
        String s = "Score: " + score;
        int w = g2.getFontMetrics().stringWidth(s);
        g2.drawString(s, W - BOARD_X - w, 45);
    }

    void drawBoard(Graphics2D g2) {
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++) {
                int px = BOARD_X + c * CELL;
                int py = BOARD_Y + r * CELL;
                if (grid[r][c] != 0 && img != null) {
                    g2.drawImage(img, px, py, CELL, CELL, this);
                } else {
                    g2.setColor(new Color(50, 52, 80));
                    g2.drawRect(px, py, CELL, CELL);
                }
            }
    }

    void drawTray(Graphics2D g2) {
        for (int i = 0; i < 3; i++) {
            if (used[i] || i == dragging) continue;
            drawPiece(g2, pieces.get(i), pieceX(i), pieceY(i));
        }
    }

    void drawPiece(Graphics2D g2, int[][] shape, int px, int py) {
        if (img == null) return;
        for (int r = 0; r < shape.length; r++)
            for (int c = 0; c < shape[0].length; c++)
                if (shape[r][c] == 1)
                    g2.drawImage(img, px + c * CELL, py + r * CELL, CELL, CELL, this);
    }

    void drawDrag(Graphics2D g2) {
        int[][] s = pieces.get(dragging); // берем из списка
        int gc = Math.round((float) (dragX - BOARD_X) / CELL);
        int gr = Math.round((float) (dragY - BOARD_Y) / CELL);

        if (canPlace(s, gr, gc)) {

            for (int r = 0; r < s.length; r++)
                for (int c = 0; c < s[0].length; c++)
                    if (s[r][c] == 1)
                        g2.drawImage(img,
                                BOARD_X + (gc + c) * CELL,
                                BOARD_Y + (gr + r) * CELL,
                                CELL, CELL, this);
        }
        drawPiece(g2, s, dragX, dragY);
    }

    // геометрия фигур которые внизу
    int pieceX(int i) {
        int slotW = W / 3;
        int shapeW = pieces.get(i)[0].length * CELL;
        return i * slotW + (slotW - shapeW) / 2;
    }

    int pieceY(int i) {
        int shapeH = pieces.get(i).length * CELL;
        return TRAY_Y + (TRAY_H - shapeH) / 2;
    }

    boolean overPiece(int i, int mx, int my) {
        if (used[i] || i == dragging) return false;
        int px = pieceX(i);
        int py = pieceY(i);
        int w = pieces.get(i)[0].length * CELL;
        int h = pieces.get(i).length * CELL;
        return mx >= px && mx <= px + w && my >= py && my <= py + h;
    }
    boolean hasAnyMove() {
        for (int i = 0; i < 3; i++) {
            if (used[i]) continue;
            for (int r = 0; r < ROWS; r++)
                for (int c = 0; c < COLS; c++)
                    if (canPlace(pieces.get(i), r, c)) return true;
        }
        return false;
    }
    boolean canPlace(int[][] shape, int gr, int gc) {
        for (int r = 0; r < shape.length; r++)
            for (int c = 0; c < shape[0].length; c++)
                if (shape[r][c] == 1) {
                    int rr = gr + r;
                    int cc = gc + c;
                    if (rr < 0 || rr >= ROWS || cc < 0 || cc >= COLS) return false;
                    if (grid[rr][cc] != 0) return false;
                }
        return true;
    }

    // мышь
    public void mousePressed(MouseEvent e) {
            if (gameOver) return;
        for (int i = 0; i < 3; i++) {
            if (overPiece(i, e.getX(), e.getY())) {
                dragging = i;
                dragX = e.getX() - pieces.get(i)[0].length * CELL / 2;
                dragY = e.getY() - pieces.get(i).length * CELL / 2;
                pole.repaint();
                return;
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (dragging < 0) return;
        int[][] s = pieces.get(dragging);
        int gc = Math.round((float) (dragX - BOARD_X) / CELL);
        int gr = Math.round((float) (dragY - BOARD_Y) / CELL);

        if (canPlace(s, gr, gc)) {
            for (int r = 0; r < s.length; r++)
                for (int c = 0; c < s[0].length; c++)
                    if (s[r][c] == 1) {
                        grid[gr + r][gc + c] = 1;
                        score++;
                    }
            clearLines();
            used[dragging] = true;

            if (used[0] && used[1] && used[2]) spawnPieces();
            if(!hasAnyMove()) gameOver = true;
        }
        dragging = -1;
        pole.repaint();
    }

    public void mouseDragged(MouseEvent e) {
        if (dragging < 0) return;
        dragX = e.getX() - pieces.get(dragging)[0].length * CELL / 2;
        dragY = e.getY() - pieces.get(dragging).length * CELL / 2;
        pole.repaint();
    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}

    //линии
    void clearLines() {
        for (int r = 0; r < ROWS; r++) {
            if (isRowFull(r)) {
                for (int c = 0; c < COLS; c++) grid[r][c] = 0;
                score += 10;
            }
        }
        for (int c = 0; c < COLS; c++) {
            if (isColFull(c)) {
                for (int r = 0; r < ROWS; r++) grid[r][c] = 0;
                score += 10;
            }
        }
    }

    boolean isRowFull(int r) {
        for (int c = 0; c < COLS; c++)
            if (grid[r][c] == 0) return false;
        return true;
    }

    boolean isColFull(int c) {
        for (int r = 0; r < ROWS; r++)
            if (grid[r][c] == 0) return false;
        return true;
    }

    public static void main(String[] args) {
        new gamemod();
    }
}
