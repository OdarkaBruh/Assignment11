package com.shpp.p2p.cs.dnokhrina.assignment12;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** Contains information about image: its width, height, rgb matrix, etc */
public class ImageManager {
    /** Buffered original picture*/
    private final BufferedImage mainImage;
    /** matrix with all rgb values */
    private final int[][] imageRGB;
    /** the color of background (aka the most popular color on the picture)*/
    private final int backgroundColor;

    /** image's width*/
    public final int width;
    /** image's height*/
    public final int height;

    /** Difference between value of the background and color to count this color as the noise of background. */
    private final int threshhold;

    /** Constructor which tries to read the file, sets height/width, calls to count rgb values, background and threshold.
     *
     * @param imageName the name of the file (with ext, for example: "1.png")
     */
    ImageManager(String imageName) {
        this.mainImage = readImage(imageName);
        width = mainImage.getWidth();
        height = mainImage.getHeight();

        imageRGB = readRGBValues();
        backgroundColor = calculateBackgroundColor();
        threshhold = calculateThreshold();
    }

    /**
     * if debug is "set value" than just returns "THRESHOLD_SET_VALUE"
     * else calculates needed percent (THRESHOLD_IN_PERCENT) from difference between min and max value.
     * @return threshold of the processing.
     */
    private int calculateThreshold() {
        if (Assignment12Part1.THRESHOLD_IS_SET_VARIABLE_NOT_PERCENT) return Assignment12Part1.THRESHOLD_SET_VALUE;
        int min = 255;
        int max = 0;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if (imageRGB[h][w] < min) min = imageRGB[h][w];
                else if (imageRGB[h][w] > max) max = imageRGB[h][w];
            }
        }
        return (int) ((max-min)* Assignment12Part1.THRESHOLD_IN_PERCENT);
    }

    /**
     * Returns whether pixel is background (false) or object (true)
     * @param x x-coordinate of the pixel
     * @param y y-coordinate of the pixel
     * @return whether it's not a background
     */
    public boolean isObject(int x, int y) {
        return Math.abs(backgroundColor - imageRGB[y][x]) >= threshhold;
    }

    /**
     * Gets average RGB (average value of red, green and blue values) for each pixel
     * @return matrix of pixels
     */
    private int[][] readRGBValues() {
        int[][] matrixRGB = new int[height][width];
        for (int h = 0; h < mainImage.getHeight(); h++) {
            for (int w = 0; w < mainImage.getWidth(); w++) {
                matrixRGB[h][w] = getAvgRGB(w, h);
            }
        }
        return matrixRGB;
    }

    /**
     * Gets average value of red, green and blue values of the pixel;
     * @param x x-coordinate of the pixel
     * @param y y-coordinate of the pixel
     * @return average of RGB values
     */
    private int getAvgRGB(int x, int y) {
        int rgb = mainImage.getRGB(x, y);
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return (red + green + blue) / 3;
    }

    /** reads the image from file.
     *
     * @param name file's name
     * @return BufferedImage of read image
     */
    private BufferedImage readImage(String name) {
        try {
            File inputFile = new File(name);
            return ImageIO.read(inputFile);
        } catch (IOException e) {
            System.out.println("Error while reading image: " + e.getMessage());
        }
        return null;
    }

    /**
     * Counts how many pixels of each color there are and the most popular is returned as background color.
     * @return average rgb value of the most popular color
     */
    private int calculateBackgroundColor() {
        HashMap<Integer, Integer> colors = new HashMap<>();
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                colors.put(imageRGB[h][w], colors.containsKey(imageRGB[h][w]) ? colors.get(imageRGB[h][w]) + 1 : 0);
            }
        }

        int color = 0;
        int max = 0;

        for (Map.Entry<Integer, Integer> set : colors.entrySet()) {
            if (max < set.getValue()) {
                max = set.getValue();
                color = set.getKey();
            }
        }
        return color;
    }
}
