package tile;

import main.GamePanel;
import utilities.LoadAndSave;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static utilities.Helper.GetMapData;

public class TileManager extends Tile {
    GamePanel gamePanel;
    public int widthMap;
    public int heightMap;
    private static BufferedImage map = null;
    private Map<Point, BufferedImage> modifiedTiles = new HashMap<>(); // Lưu các tile đã được thay đổi


    public TileManager(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        getTilesImage();
        getModifyTilesImage();
        map = LoadAndSave.LoadImage("/map/map.png");
        assert map != null;
        widthMap = map.getWidth();
        heightMap = map.getHeight();
    }

    private void getTilesImage() {
        tileImage = LoadAndSave.LoadImage("/tiles/Hills.png");
        assert tileImage != null;
        int widthImage = tileImage.getWidth();
        int heightImage = tileImage.getHeight();
        int cols = 11;
        int rows = 7;
        int width = widthImage / cols;
        int height = heightImage / rows;
        tileSprite = new BufferedImage[rows * cols];
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                int index = y * cols + x;
                tileSprite[index] = tileImage.getSubimage(x * width, y * height, width, height);
            }
        }
    }

    private void getModifyTilesImage() {
        modifyTile = LoadAndSave.LoadImage("/tiles/Tilled_Dirt.png");
        assert modifyTile != null;
        int widthImage = modifyTile.getWidth();
        int heightImage = modifyTile.getHeight();
        int cols = 11;
        int rows = 7;
        int width = widthImage / cols;
        int height = heightImage / rows;
        modifyTileSprite = new BufferedImage[rows * cols];
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                int index = y * cols + x;
                modifyTileSprite[index] = modifyTile.getSubimage(x * width, y * height, width, height);
            }
        }
    }

    public static int[][] getMapData() {
        return GetMapData(map);
    }

    public void interactWithTile(int mouseX, int mouseY) {
        // Chuyển tọa độ chuột sang tọa độ thế giới (tính toán theo tile)
        int tileX = mouseX / gamePanel.TILESIZE;
        int tileY = mouseY / gamePanel.TILESIZE;

        if (tileX >= 0 && tileX < widthMap && tileY >= 0 && tileY < heightMap) {

            modifiedTiles.put(new Point(tileX, tileY), modifyTileSprite[12]);
        }
    }


    public boolean hasModifiedTile(int x, int y) {
        int _x = x / gamePanel.TILESIZE;
        int _y = y / gamePanel.TILESIZE;

        return modifiedTiles.containsKey(new Point(_x, _y));
    }

    public void draw(Graphics2D graphics2D) {
        int cameraX = gamePanel.getPlayer().worldX - gamePanel.getPlayer().screenX;
        int cameraY = gamePanel.getPlayer().worldY - gamePanel.getPlayer().screenY;

        for (int y = 0; y < heightMap; y++) {
            for (int x = 0; x < widthMap; x++) {
                Color color = new Color(map.getRGB(x, y));
                int value = color.getGreen(); // Lấy giá trị màu xanh

                if (value > 0 && value <= tileSprite.length) {
                    int worldX = x * gamePanel.TILESIZE;
                    int worldY = y * gamePanel.TILESIZE;
                    int screenX = worldX - cameraX;
                    int screenY = worldY - cameraY;

//                    System.out.println(   modifiedTiles.size());
                    // Kiểm tra nếu tile này đã được thay đổi (modified)
                    if (modifiedTiles.containsKey(new Point(x, y))) {
                        // Vẽ tile đã thay đổi
                        graphics2D.drawImage(modifiedTiles.get(new Point(x, y)), screenX, screenY, gamePanel.TILESIZE, gamePanel.TILESIZE, null);
                    } else {
                        // Vẽ tile mặc định
                        if (screenX + gamePanel.TILESIZE > 0 && screenX < gamePanel.SCREENWIDTH &&
                                screenY + gamePanel.TILESIZE > 0 && screenY < gamePanel.SCREENHEIGHT) {
                            graphics2D.drawImage(tileSprite[value - 1], screenX, screenY, gamePanel.TILESIZE, gamePanel.TILESIZE, null);
                        }
                    }
                }
            }
        }
    }

}
