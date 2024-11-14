package main;

import entity.Player;
import tile.Layer1;
import tile.TileManager;
import utilities.Helper;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {
    // SCREEN SETTINGS
    static final int ORIGINALTILESIZE = 16;
    static final int SCALE = 3;
    public static final int TILESIZE = ORIGINALTILESIZE * SCALE;
    final int MAXSCREENCOL = 16;
    final int MAXSCREENROW = 12;
    public final int SCREENWIDTH = TILESIZE * MAXSCREENCOL;
    public final int SCREENHEIGHT = TILESIZE * MAXSCREENROW;

    // FPS
    int FPS = 60;

    Keyhandler keyH = new Keyhandler();
    Mousehandler mouseH = new Mousehandler(this);
    Thread gameThread;
    Player player = new Player(this, keyH, mouseH);
    TileManager tileManager = new TileManager(this);
    Layer1 layer1 = new Layer1(this);

    public GamePanel() {
        this.setPreferredSize(new Dimension(SCREENWIDTH, SCREENHEIGHT));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.addMouseListener(mouseH);
        this.setFocusable(true);
        initClasses();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void initClasses() {
        player.loadMapData(TileManager.getMapData());
    }

    @Override
    public void run() {
        double drawInterval = (double) 1000000000 / FPS; //0.01666s
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            update();
            repaint();
            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000;

                if (remainingTime < 0) {
                    remainingTime = 0;
                }

                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void update() {
        player.update();
        layer1.update();
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics;
        tileManager.draw(graphics2D);
        layer1.draw(graphics2D);
        player.draw(graphics2D);
        graphics2D.dispose();
    }

    public Player getPlayer() {
        return player;
    }

    public TileManager getTileManager() {
        return tileManager;
    }
    public Layer1 getLayer1(){
        return layer1;
    }
}
