package com.shpp.p2p.cs.dnokhrina.assignment12;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

/** Processes an Image: looks for silhouettes, cleans them, etc. */
public class ImageProcessor {
    /**
     * The minimum difference between the background value and a color, so that the color is not considered part
     * of the background.
     */
    int colorThreshold;
    /** rgb value of the background color */
    int backgroundColor;

    /** Contains already checked colors and whether they are a color */
    private final HashMap<Integer, Boolean> backgroundColorsMap = new HashMap<>();
    /** Matrix which tracks which pixels were visited */
    private final Pixel[][] pixelMap;
    /** All silhouettes */
    private final ArrayList<Silhouette> silhouettes = new ArrayList<>();

    ImageProcessor(BufferedImage image) {
        this.pixelMap = new Pixel[image.getHeight()][image.getWidth()];
        fillPixelMap(image);
    }

    ImageProcessor(BufferedImage image, int colorThreshold, int backgroundColor) {
        this.pixelMap = new Pixel[image.getHeight()][image.getWidth()];
        fillPixelMap(image);
        this.colorThreshold = colorThreshold;
        this.backgroundColor = backgroundColor;
    }
    private void fillPixelMap(BufferedImage image){
        for (int y = 0; y < pixelMap.length; y++) {
            for (int x = 0; x < pixelMap[0].length; x++) {
                pixelMap[y][x] = new Pixel(x, y, image.getRGB(x, y));
            }
        }
    }

    public void setBackgroundColor(int backgroundColor) {
        backgroundColorsMap.put(backgroundColor, true);
        this.backgroundColor = backgroundColor;
    }

    public void setColorThreshold(int colorThreshold) {
         this.colorThreshold = colorThreshold;

    }

    public Pixel[][] getPixelMap() {
        return pixelMap;
    }

    /**
     * Finds all silhouettes (with noise)
     */
    void findSilhouettes() {
        for (int y = 0; y < pixelMap.length; y++) {
            for (int x = 0; x < pixelMap[0].length; x++) {
                if (!pixelMap[y][x].isChecked()) {
                    pixelMap[y][x].setChecked(true);
                    if (isObject(pixelMap[y][x])) {
                        processSilhouette(pixelMap[y][x]);
                    }
                }
            }
        }
    }

    private void processSilhouette(Pixel pixel) {
        Queue<Pixel> queue = new ArrayDeque<>(addNeighboursToQueue(pixel.x, pixel.y));
        Silhouette s = new Silhouette((byte) silhouettes.size());
        silhouettes.add(s);

        pixel.assignSilhouette(s);
        while (!queue.isEmpty()) {
            pixel = queue.poll();
            if (!pixel.isChecked()) {
                pixel.setChecked(true);
                if (isObject(pixel)) {
                    s.addPixel(pixel);
                    queue.addAll(addNeighboursToQueue(pixel.x, pixel.y));
                }
            }
        }
    }

    /**

     */
    private ArrayList<Pixel> addNeighboursToQueue(int x, int y) {
        ArrayList<Pixel> newPixels = new ArrayList<>();
        for (int tempY = y - 1; tempY < y + 2; tempY++) {
            for (int tempX = x - 1; tempX < x + 2; tempX++) {
                if (coordinatesAreInLimits(tempY, tempX) && !pixelMap[tempY][tempX].isChecked()) {
                    newPixels.add(pixelMap[tempY][tempX]);
                }
            }
        }
        return newPixels;
    }


    /**
     * Checks if coordinates are inside image
     *
     * @param y the y coordinate
     * @param x the x coordinate
     * @return are they inside image
     */
    private boolean coordinatesAreInLimits(int y, int x) {
        return y >= 0 && x >= 0 && y < pixelMap.length && x < pixelMap[0].length;
    }



    /**
     * Removes noise from silhouettes: finds them and reverse them from main matrix (silhouetteMap)
     *
     * @param noiseSize number of pixels needed to not count object as noise
     */
    void cleanNoiseFromSilhouettes(int noiseSize) {
        System.out.println(noiseSize);
        int i = 0;
        while (i != silhouettes.size()) {
            if (silhouettes.get(i).getPixelCount() < noiseSize) {
                silhouettes.get(i).deleteSilhouette();
                silhouettes.remove(i);
            } else i++;
        }
    }

    /**
     * Returns the number of silhouettes
     *
     * @return the number of silhouettes
     */
    int getSilhouettesCount() {
        return silhouettes.size();
    }

    /**
     * Determines if the pixel contains object (silhouette) or background.
     *
     * @param pixel the pixel
     * @return whether the pixel contains silhouette
     */
    private boolean isObject(Pixel pixel) {
        int pixelColor = pixel.getColor();
        if (backgroundColorsMap.containsKey(pixelColor)) return !backgroundColorsMap.get(pixelColor);
        else {
            boolean isBackground = ColorCalculator.calculateDistanceBetweenColors(pixelColor, backgroundColor)
                    < colorThreshold;
            backgroundColorsMap.put(pixelColor, isBackground);
            return !isBackground;
        }
    }
}
