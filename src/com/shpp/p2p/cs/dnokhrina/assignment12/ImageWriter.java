package com.shpp.p2p.cs.dnokhrina.assignment12;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageWriter {
    private final String fileName;
    private final BufferedImage image;

    private static final int BACKGROUND_COLOR = Color.white.getRGB();
    private static final int SILHOUETTE_COLOR = Color.black.getRGB();

    ImageWriter(String fileName, int width, int height) {
        this.fileName = fileName;
        this.image = createBufferImage(width, height);
    }

    BufferedImage createBufferImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.setRGB(x, y, BACKGROUND_COLOR);
            }
        }
        return image;
    }

    void drawSilhouette(Pixel[][] map){
        fillMap(map, SILHOUETTE_COLOR);
    }

    void save(){
        File f = new File(fileName + ".png");
        try {
            ImageIO.write(image, "PNG", f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     */
    void fillMap(Pixel[][] map, int color) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (map[y][x].isSilhouette())
                    image.setRGB(x, y, color);
            }
        }
    }

}
