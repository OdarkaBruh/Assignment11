package com.shpp.p2p.cs.dnokhrina.assignment12;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/** Contains information about image: its width, height, rgb matrix, etc */
public class Image {
    /** matrix with all rgb values */
    private final int[][] imageRGB;

    /** image's width */
    final int width;
    /** image's height */
    final int height;

    /**
     * Constructor which tries to read the file, sets height/width, calls to count rgb values, background and threshold.
     *
     * @param image the name of the file (with ext, for example: "1.png")
     */
    Image(BufferedImage image) {
        this.width = image.getWidth();
        this.height = image.getHeight();

        this.imageRGB = getMatrixRGB(image);
    }

    /**
     * if debug is "set value" than just returns "THRESHOLD_SET_VALUE"
     * else calculates needed percent (THRESHOLD_IN_PERCENT) from difference between min and max value.
     *
     * @return threshold of the processing.
     */
    int getMaxColorDifference() {
        int min = imageRGB[0][0];
        int max = imageRGB[0][0];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if (imageRGB[h][w] < min) min = imageRGB[h][w];
                else if (imageRGB[h][w] > max) max = imageRGB[h][w];
            }
        }
        return ColorCalculator.calculateDistanceBetweenColors(min, max);
    }

    /**
     * Gets average RGB (average value of red, green and blue values) for each pixel
     *
     * @return matrix of pixels
     */
    int[][] getMatrixRGB(BufferedImage image) {
        int[][] imageRGB = new int[height][width];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                imageRGB[h][w] = image.getRGB(w, h);
            }
        }
        return imageRGB;
    }

    /**
     * Counts how many pixels of each color there are and the most popular is returned as background color.
     *
     * @return average rgb value of the most popular color
     */
    int calculateMainColor() {
        HashMap<Integer, Integer> colors = new HashMap<>();
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                colors.put(imageRGB[h][w], colors.containsKey(imageRGB[h][w]) ? colors.get(imageRGB[h][w]) + 1 : 1);
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

    /**
     * returns rgb of specific pixel
     * @param y y-coordinate of the pixel
     * @param x x-coordinate of the pixel
     * @return rgb value of this pixel
     */
    int getPixelRGB(int y, int x) {
        return imageRGB[y][x];
    }
}
