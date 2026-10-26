import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class gamemod2 extends JPanel implements MouseListener, MouseMotionListener {

    // ======================== КОНФИГ ========================
    static final int COLS = 8, ROWS = 8;
    static final int CELL       = 56;
    static final int PIECE_CELL = 38;
    static final int SIDE       = 24;
    static final int TOP        = 70;
    static final int GAP        = 28;
    static final int TRAY_H     = 200;

    static final int BOARD_X = SIDE;
    static final int BOARD_Y = TOP;
    static final int BOARD_W = COLS * CELL;
    static final int BOARD_H = ROWS * CELL;
    static final int TRAY_Y  = BOARD_Y + BOARD_H + GAP;

    static final int W = BOARD_W + SIDE * 2;
    static final int H = TRAY_Y + TRAY_H + SIDE;

    static final File SAVE_FILE =
            new File(System.getProperty("user.home"), ".blockblast_best");

    // ======================== СОСТОЯНИЕ ========================
    final int[][]   grid     = new int[ROWS][COLS];
    final int[][][] pieces   = new int[3][][];
    final int[]     pieceCol = new int[3];
    final boolean[] used     = new boolean[3];
    final float[][] flash    = new float[ROWS][COLS];

    int dragging   = -1;
    int dragX, dragY;
    int hoverPiece = -1;

    int score = 0, bestScore = 0, displayedScore = 0;
    int combo = 0;
    boolean gameOver   = false;
    boolean isNewRecord = false;

    float shakeAmount = 0f, shakeX = 0f, shakeY = 0f;

    final ArrayList<Particle>     particles = new ArrayList<>(); //это маленькие квадратики искры
    final ArrayList<FloatingText> floats    = new ArrayList<>(); //всплывающий текст
    final ArrayList<Star>         stars     = new ArrayList<>(); //звезды на заднем фоне

    final Random rnd = new Random();
    final Timer  timer;

    final Color[] palette = {
            null,
            new Color(255,  86,  99),
            new Color( 80, 156, 255),
            new Color(109, 219, 122),
            new Color(255, 199,  89),
            new Color(185, 122, 255),
            new Color( 74, 222, 222),
    };

    static final int[][][] SHAPES = {
            {{1}},
            {{1,1}},
            {{1,1,1}},
            {{1,1,1,1}},
            {{1,1},{1,1}},
            {{1,0},{1,0},{1,1}},
            {{0,1},{0,1},{1,1}},
            {{1,1,1},{0,1,0}},
            {{1,0},{1,0},{1,0}},
            {{1,1,1},{1,0,0}},
            {{1,1,1},{0,0,1}},
            {{1,0,0},{1,1,1}},
            {{0,1},{1,1}},
            {{1,0},{1,1}},
    };

    // ======================== ИНИЦИАЛИЗАЦИЯ ========================
    public gamemod2() {
        setPreferredSize(new Dimension(W, H));
        setBackground(new Color(24, 26, 45));
        addMouseListener(this);
        addMouseMotionListener(this);

        loadBest();
        initStars();
        spawnPieces();

        timer = new Timer(16, e -> tick());
        timer.start();
    }

    void initStars() {
        for (int i = 0; i < 70; i++) stars.add(new Star());
    }

    // ======================== ФАЙЛ РЕКОРДА ========================
    void loadBest() {
        try {
            if (SAVE_FILE.exists()) {
                try (BufferedReader r = new BufferedReader(new FileReader(SAVE_FILE))) {
                    bestScore = Math.max(0, Integer.parseInt(r.readLine().trim()));
                }
            }
        } catch (Exception ignore) { bestScore = 0; }
    }

    void saveBest() {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(SAVE_FILE))) {
            w.write(String.valueOf(bestScore));
        } catch (Exception ignore) { }
    }

    // ======================== ЛОГИКА ========================
    void spawnPieces() {
        for (int i = 0; i < 3; i++) {
            pieces[i]   = SHAPES[rnd.nextInt(SHAPES.length)];
            pieceCol[i] = 1 + rnd.nextInt(6);
            used[i]     = false;
        }
    }

    boolean canPlace(int[][] shape, int gr, int gc) {
        int sh = shape.length, sw = shape[0].length;
        for (int r = 0; r < sh; r++)
            for (int c = 0; c < sw; c++)
                if (shape[r][c] == 1) {
                    int rr = gr + r, cc = gc + c;
                    if (rr < 0 || rr >= ROWS || cc < 0 || cc >= COLS) return false;
                    if (grid[rr][cc] != 0) return false;
                }
        return true;
    }

    boolean canPlaceAnywhere(int idx) {
        int[][] s = pieces[idx];
        for (int r = 0; r <= ROWS - s.length; r++)
            for (int c = 0; c <= COLS - s[0].length; c++)
                if (canPlace(s, r, c)) return true;
        return false;
    }

    boolean isGameOver() {
        for (int i = 0; i < 3; i++)
            if (!used[i] && canPlaceAnywhere(i)) return false;
        return true;
    }

    void placePiece(int idx, int gr, int gc) {
        int[][] shape = pieces[idx];
        Color col = palette[pieceCol[idx]];
        int placed = 0;
        for (int r = 0; r < shape.length; r++)
            for (int c = 0; c < shape[0].length; c++)
                if (shape[r][c] == 1) {
                    grid[gr + r][gc + c] = pieceCol[idx];
                    placed++;
                    int px = BOARD_X + (gc + c) * CELL + CELL / 2;
                    int py = BOARD_Y + (gr + r) * CELL + CELL / 2;
                    for (int k = 0; k < 3; k++) particles.add(new Particle(px, py, col, false));
                }
        used[idx] = true;
        score += placed;

        int lines = clearLines();
        if (lines > 0) {
            combo++;
            int gained = lines * 10 * combo;
            score += gained;
            floats.add(new FloatingText("+" + gained,
                    BOARD_X + BOARD_W / 2f, BOARD_Y + BOARD_H / 2f,
                    new Color(255, 220, 100), 34));
            if (combo > 1)
                floats.add(new FloatingText("COMBO x" + combo,
                        BOARD_X + BOARD_W / 2f, BOARD_Y + BOARD_H / 2f - 50,
                        new Color(255, 140, 60), 28));
            triggerShake(10 + combo * 3);
        } else {
            combo = 0;
        }

        if (score > bestScore) {
            bestScore = score;
            isNewRecord = true;
            saveBest();
        }

        if (used[0] && used[1] && used[2]) spawnPieces();
        else if (isGameOver()) {
            gameOver = true;
            if (isNewRecord) for (int i = 0; i < 90; i++) spawnConfetti();
        }
    }

    int clearLines() {
        boolean[] rows = new boolean[ROWS];
        boolean[] cols = new boolean[COLS];

        for (int r = 0; r < ROWS; r++) {
            boolean full = true;
            for (int c = 0; c < COLS; c++) if (grid[r][c] == 0) { full = false; break; }
            rows[r] = full;
        }
        for (int c = 0; c < COLS; c++) {
            boolean full = true;
            for (int r = 0; r < ROWS; r++) if (grid[r][c] == 0) { full = false; break; }
            cols[c] = full;
        }

        int lines = 0;
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                if (rows[r] || cols[c]) {
                    if (grid[r][c] != 0) {
                        flash[r][c] = 1f;
                        int px = BOARD_X + c * CELL + CELL / 2;
                        int py = BOARD_Y + r * CELL + CELL / 2;
                        Color col = palette[grid[r][c]];
                        for (int k = 0; k < 6; k++) particles.add(new Particle(px, py, col, false));
                    }
                    grid[r][c] = 0;
                }
        for (int r = 0; r < ROWS; r++) if (rows[r]) lines++;
        for (int c = 0; c < COLS; c++) if (cols[c]) lines++;
        return lines;
    }

    void triggerShake(float amt) { shakeAmount = Math.max(shakeAmount, amt); }

    void spawnConfetti() {
        Color[] cs = {palette[1], palette[2], palette[3], palette[4], palette[5], palette[6]};
        Color c = cs[rnd.nextInt(cs.length)];
        Particle p = new Particle(rnd.nextFloat() * W, -20, c, true);
        p.vy = 2 + rnd.nextFloat() * 3f;
        p.vx = rnd.nextFloat() * 6f - 3f;
        particles.add(p);
    }

    void restart() {
        for (int[] row : grid) Arrays.fill(row, 0);
        for (float[] row : flash) Arrays.fill(row, 0f);
        particles.clear();
        floats.clear();
        score = 0; displayedScore = 0; combo = 0;
        gameOver = false; isNewRecord = false;
        shakeAmount = 0; shakeX = 0; shakeY = 0;
        dragging = -1;
        spawnPieces();
        repaint();
    }

    // ======================== ТИК АНИМАЦИИ ========================
    void tick() {
        boolean dirty = false;

        if (shakeAmount > 0.4f) {
            shakeAmount *= 0.85f;
            shakeX = (rnd.nextFloat() * 2 - 1) * shakeAmount;
            shakeY = (rnd.nextFloat() * 2 - 1) * shakeAmount;
            dirty = true;
        } else {
            shakeAmount = 0; shakeX = 0; shakeY = 0;
        }

        for (Iterator<Particle> it = particles.iterator(); it.hasNext(); ) {
            Particle p = it.next();
            p.update();
            if (p.dead()) it.remove();
            dirty = true;
        }
        for (Iterator<FloatingText> it = floats.iterator(); it.hasNext(); ) {
            FloatingText f = it.next();
            f.update();
            if (f.dead()) it.remove();
            dirty = true;
        }

        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                if (flash[r][c] > 0f) {
                    flash[r][c] = Math.max(0f, flash[r][c] - 0.07f);
                    dirty = true;
                }

        if (displayedScore != score) {
            int diff = score - displayedScore;
            displayedScore += Integer.signum(diff) * Math.max(1, Math.abs(diff) / 8);
            if (Math.abs(score - displayedScore) < 1) displayedScore = score;
            dirty = true;
        }

        if (dirty) repaint();
    }

    // ======================== РИСОВКА ========================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,    RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        g2.setPaint(new GradientPaint(0, 0, new Color(32, 34, 58), 0, H, new Color(16, 18, 34)));
        g2.fillRect(0, 0, W, H);
        for (Star s : stars) {
            g2.setColor(new Color(255, 255, 255, s.alpha));
            g2.fillOval((int) s.x, (int) s.y, (int) s.size, (int) s.size);
        }

        g2.translate(shakeX, shakeY);

        drawHeader(g2);
        drawBoard(g2);
        drawTray(g2);

        if (dragging >= 0) drawDragAndGhost(g2);

        for (Particle p : particles) p.draw(g2);
        for (FloatingText f : floats) f.draw(g2);

        g2.translate(-shakeX, -shakeY);

        if (gameOver) drawGameOver(g2);

        g2.dispose();
    }

    void drawHeader(Graphics2D g2) {
        g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
        g2.setColor(new Color(255, 255, 255, 235));
        g2.drawString("BLOCK BLAST", BOARD_X, 40);

        if (combo > 1) {
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.setColor(new Color(255, 160, 80));
            g2.drawString("COMBO x" + combo, BOARD_X, 58);
        }

        String s = String.valueOf(displayedScore);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
        int sw = g2.getFontMetrics().stringWidth(s);
        g2.setColor(new Color(255, 220, 120, 60));
        g2.drawString(s, W - SIDE - sw + 1, 41);
        g2.setColor(new Color(255, 220, 120));
        g2.drawString(s, W - SIDE - sw, 40);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2.setColor(new Color(180, 180, 205));
        String lbl = "BEST " + bestScore;
        int lw = g2.getFontMetrics().stringWidth(lbl);
        g2.drawString(lbl, W - SIDE - lw, 56);
    }

    void drawBoard(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 90));
        g2.fillRoundRect(BOARD_X - 6, BOARD_Y - 2, BOARD_W + 12, BOARD_H + 14, 26, 26);
        g2.setColor(new Color(40, 42, 72));
        g2.fillRoundRect(BOARD_X - 10, BOARD_Y - 10, BOARD_W + 20, BOARD_H + 20, 26, 26);

        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++) {
                int x = BOARD_X + c * CELL;
                int y = BOARD_Y + r * CELL;

                if (grid[r][c] == 0) {
                    g2.setColor(new Color(48, 50, 82));
                    g2.fillRoundRect(x + 3, y + 3, CELL - 6, CELL - 6, 10, 10);
                    g2.setColor(new Color(58, 60, 92, 140));
                    g2.fillRoundRect(x + 5, y + 5, CELL - 10, (CELL - 10) / 2, 8, 8);
                } else {
                    drawBlock(g2, x, y, CELL, palette[grid[r][c]]);
                }

                if (flash[r][c] > 0f) {
                    int a = (int) (flash[r][c] * 235);
                    g2.setColor(new Color(255, 255, 255, a));
                    g2.fillRoundRect(x + 3, y + 3, CELL - 6, CELL - 6, 10, 10);
                }
            }
    }

    void drawTray(Graphics2D g2) {
        for (int i = 0; i < 3; i++) {
            if (used[i] || i == dragging) continue;
            int[][] s = pieces[i];
            int px = pieceScreenX(i), py = pieceScreenY(i);

            boolean hovered = (i == hoverPiece);
            if (hovered) {
                g2.setColor(new Color(255, 255, 255, 22));
                g2.fillRoundRect(px - 10, py - 10,
                        s[0].length * PIECE_CELL + 20,
                        s.length    * PIECE_CELL + 20, 16, 16);
            }

            for (int r = 0; r < s.length; r++)
                for (int c = 0; c < s[0].length; c++)
                    if (s[r][c] == 1)
                        drawBlock(g2, px + c * PIECE_CELL, py + r * PIECE_CELL,
                                PIECE_CELL, palette[pieceCol[i]]);
        }
    }

    void drawDragAndGhost(Graphics2D g2) {
        int[][] s = pieces[dragging];
        Color col = palette[pieceCol[dragging]];

        int gc = Math.round((float) (dragX - BOARD_X) / CELL);
        int gr = Math.round((float) (dragY - BOARD_Y) / CELL);
        boolean valid = canPlace(s, gr, gc);

        if (valid) {
            for (int r = 0; r < s.length; r++)
                for (int c = 0; c < s[0].length; c++)
                    if (s[r][c] == 1) {
                        int x = BOARD_X + (gc + c) * CELL;
                        int y = BOARD_Y + (gr + r) * CELL;
                        g2.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), 120));
                        g2.fillRoundRect(x + 3, y + 3, CELL - 6, CELL - 6, 10, 10);
                        g2.setColor(new Color(255, 255, 255, 180));
                        g2.setStroke(new BasicStroke(1.6f));
                        g2.drawRoundRect(x + 3, y + 3, CELL - 6, CELL - 6, 10, 10);
                    }
        } else {
            g2.setColor(new Color(255, 80, 80, 45));
            g2.setStroke(new BasicStroke(2f));
            for (int r = 0; r < s.length; r++)
                for (int c = 0; c < s[0].length; c++)
                    if (s[r][c] == 1) {
                        int x = BOARD_X + (gc + c) * CELL;
                        int y = BOARD_Y + (gr + r) * CELL;
                        g2.drawRoundRect(x + 4, y + 4, CELL - 8, CELL - 8, 10, 10);
                    }
        }

        for (int r = 0; r < s.length; r++)
            for (int c = 0; c < s[0].length; c++)
                if (s[r][c] == 1)
                    drawBlock(g2, dragX + c * CELL, dragY + r * CELL, CELL, col);
    }

    void drawBlock(Graphics2D g2, int x, int y, int size, Color c) {
        int pad = 3;
        int w = size - pad * 2, h = size - pad * 2;
        int xx = x + pad, yy = y + pad;
        int arc = Math.max(8, size / 5);

        g2.setColor(new Color(0, 0, 0, 90));
        g2.fillRoundRect(xx + 2, yy + 3, w, h, arc, arc);

        g2.setPaint(new GradientPaint(xx, yy, c.brighter().brighter(),
                xx, yy + h, c));
        g2.fillRoundRect(xx, yy, w, h, arc, arc);

        g2.setColor(new Color(0, 0, 0, 45));
        g2.fillRoundRect(xx + 3, yy + h - h / 3, w - 6, h / 3 - 3, arc / 2, arc / 2);

        g2.setColor(new Color(255, 255, 255, 135));
        g2.fillRoundRect(xx + 3, yy + 3, w - 6, h / 4, arc / 2, arc / 2);

        g2.setColor(new Color(255, 255, 255, 55));
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(xx, yy, w - 1, h - 1, arc, arc);
    }

    void drawGameOver(Graphics2D g2) {
        RadialGradientPaint rg = new RadialGradientPaint(
                new Point(W / 2, H / 2), Math.max(W, H) / 2f,
                new float[]{0f, 1f},
                new Color[]{new Color(10, 10, 25, 180), new Color(5, 5, 15, 235)});
        g2.setPaint(rg);
        g2.fillRect(0, 0, W, H);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 38));
        String t = "GAME OVER";
        int tw = g2.getFontMetrics().stringWidth(t);
        int tx = (W - tw) / 2, ty = H / 2 - 40;

        g2.setColor(new Color(255, 120, 120, 90));
        g2.drawString(t, tx + 2, ty + 2);
        g2.setColor(Color.WHITE);
        g2.drawString(t, tx, ty);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        String s1 = "Score: " + score;
        int s1w = g2.getFontMetrics().stringWidth(s1);
        g2.setColor(new Color(255, 220, 120));
        g2.drawString(s1, (W - s1w) / 2, H / 2 + 6);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        String s2 = isNewRecord ? "\u2605 NEW RECORD \u2605" : "Best: " + bestScore;
        int s2w = g2.getFontMetrics().stringWidth(s2);
        g2.setColor(isNewRecord ? new Color(120, 255, 180) : new Color(190, 190, 215));
        g2.drawString(s2, (W - s2w) / 2, H / 2 + 34);

        float pulse = 0.5f + 0.5f * (float) Math.sin(System.currentTimeMillis() / 300.0);
        int a = (int) (120 + pulse * 135);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        g2.setColor(new Color(255, 255, 255, a));
        String s3 = "Click anywhere to restart";
        int s3w = g2.getFontMetrics().stringWidth(s3);
        g2.drawString(s3, (W - s3w) / 2, H / 2 + 76);
    }

    // ======================== КООРДИНАТЫ ЛОТКА ========================
    int slotW() { return BOARD_W / 3; }

    int pieceScreenX(int i) {
        int[][] s = pieces[i];
        return BOARD_X + i * slotW() + (slotW() - s[0].length * PIECE_CELL) / 2;
    }
    int pieceScreenY(int i) {
        int[][] s = pieces[i];
        return TRAY_Y + (TRAY_H - s.length * PIECE_CELL) / 2;
    }
    boolean overPiece(int i, int mx, int my) {
        if (used[i]) return false;
        int px = pieceScreenX(i), py = pieceScreenY(i);
        int sw = pieces[i][0].length * PIECE_CELL;
        int sh = pieces[i].length    * PIECE_CELL;
        return mx >= px && mx <= px + sw && my >= py && my <= py + sh;
    }

    // ======================== МЫШЬ ========================
    @Override
    public void mousePressed(MouseEvent e) {
        if (gameOver) { restart(); return; }

        int mx = e.getX(), my = e.getY();
        for (int i = 0; i < 3; i++) {
            if (overPiece(i, mx, my)) {
                dragging = i;
                int[][] s = pieces[i];
                dragX = mx - s[0].length * CELL / 2;
                dragY = my - s.length    * CELL / 2;
                repaint();
                return;
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragging < 0) return;
        int[][] s = pieces[dragging];
        dragX = e.getX() - s[0].length * CELL / 2;
        dragY = e.getY() - s.length    * CELL / 2;
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (dragging < 0) return;
        int gc = Math.round((float) (dragX - BOARD_X) / CELL);
        int gr = Math.round((float) (dragY - BOARD_Y) / CELL);
        if (canPlace(pieces[dragging], gr, gc))
            placePiece(dragging, gr, gc);
        dragging = -1;
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int newHover = -1;
        for (int i = 0; i < 3; i++)
            if (overPiece(i, e.getX(), e.getY())) { newHover = i; break; }
        if (newHover != hoverPiece) {
            hoverPiece = newHover;
            repaint();
        }
    }

    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e)  { hoverPiece = -1; repaint(); }

    // ======================== ВНУТРЕННИЕ КЛАССЫ ЭФФЕКТОВ ========================
    class Particle {
        float x, y, vx, vy, life, maxLife, size;
        Color color;
        boolean confetti;

        Particle(float x, float y, Color c, boolean confetti) {
            this.x = x; this.y = y; this.color = c;
            this.confetti = confetti;
            this.vx = (rnd.nextFloat() * 8 - 4);
            this.vy = (rnd.nextFloat() * -6 - 2);
            this.life = 1f; this.maxLife = 1f;
            this.size = confetti ? (4 + rnd.nextFloat() * 4)
                    : (3 + rnd.nextFloat() * 4);
        }
        void update() {
            x += vx; y += vy;
            vy += confetti ? 0.20f : 0.35f;
            vx *= 0.985f;
            life -= confetti ? 0.008f : 0.025f;
        }
        boolean dead() { return life <= 0; }
        void draw(Graphics2D g2) {
            float a = Math.max(0, Math.min(1, life / maxLife));
            int alpha = (int) (a * 255);
            Color c = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
            g2.setColor(c);
            if (confetti) g2.fillRect((int) x, (int) y, (int) size, (int) size);
            else          g2.fillOval((int) x, (int) y, (int) size, (int) size);
        }
    }

    class FloatingText {
        String text; float x, y, life; Color color; int size;
        FloatingText(String t, float x, float y, Color c, int s) {
            this.text = t; this.x = x; this.y = y;
            this.color = c; this.size = s; this.life = 1.4f;
        }
        void update() { y -= 1.3f; life -= 0.018f; }
        boolean dead() { return life <= 0; }
        void draw(Graphics2D g2) {
            float a = Math.max(0, Math.min(1, life / 1.4f));
            g2.setFont(new Font("Segoe UI", Font.BOLD, size));
            int tw = g2.getFontMetrics().stringWidth(text);
            int alpha = (int) (a * 255);
            g2.setColor(new Color(0, 0, 0, alpha / 2));
            g2.drawString(text, (int) x - tw / 2 + 2, (int) y + 2);
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
            g2.drawString(text, (int) x - tw / 2, (int) y);
        }
    }

    class Star {
        float x, y, size; int alpha;
        Star() {
            x = rnd.nextFloat() * W;
            y = rnd.nextFloat() * H;
            size = 1 + rnd.nextFloat() * 2;
            alpha = 40 + rnd.nextInt(100);
        }
    }

    // ======================== ЗАПУСК ========================
    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Block Blast \u2014 gamemod2");
            f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            f.add(new gamemod2());
            f.pack();
            f.setLocationRelativeTo(null);
            f.setResizable(false);
            f.setVisible(true);
        });
    }

    public static void main(String[] args) {
        launch();
    }
}