package com.shpp.p2p.cs.dnokhrina.assignment12;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

/** Processes an Image: looks for silhouettes, cleans them, etc. */
public class ImageProcessor {
    /** Image to process */
    Image image;
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
    /** Matrix which tracks which pixels contain silhouettes */
    private final boolean[][] silhouetteMap;
    /** All silhouettes */
    private final ArrayList<Silhouette> silhouettes = new ArrayList<>();
    /**
     * ImageProcessor is responsible for all manipulations with an image and its silhouettes
     *
     * @param image           the image to process
     * @param colorThreshold  The minimum difference between the background value and a color, so that
     *                        the color is not considered part of the background.
     * @param backgroundColor rgb value of the background color
     */
    ImageProcessor(Image image, int colorThreshold, int backgroundColor) {
        this.image = image;
        this.colorThreshold = colorThreshold;

        //Some images break an algorithm, but I couldn't figure the reason (it isn't the alpha channel).
        if (this.colorThreshold == 0) System.err.println("WARNING: color threshold < 1.");

        this.backgroundColor = backgroundColor;
        backgroundColorsMap.put(backgroundColor, true);

        pixelMap = createImageSearchMap(image);
        silhouetteMap = new boolean[image.height][image.width];
    }

    private Pixel[][] createImageSearchMap(Image image) {
        Pixel[][] map = new Pixel[image.height][image.width];
        for (int y = 0; y < image.height; y++) {
            for (int x = 0; x < image.width; x++) {
                map[y][x] = new Pixel(x, y, image.getPixelRGB(y, x));
            }
        }
        return map;
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

    /**

     */
    private ArrayList<Pixel> addNeighboursToQueue(int x, int y) {
        ArrayList<Pixel> p = new ArrayList<>();
        for (int tempY = y - 1; tempY < y + 2; tempY++) {
            for (int tempX = x - 1; tempX < x + 2; tempX++) {
                if (coordinatesAreInLimits(tempY, tempX) && !pixelMap[tempY][tempX].isChecked()) {
                    p.add(pixelMap[tempY][tempX]);
                }
            }
        }
        return p;
    }

    private void processSilhouette(Pixel pixel) {
        Queue<Pixel> queue = new ArrayDeque<>();
        queue.addAll(addNeighboursToQueue(pixel.x, pixel.y));
        Silhouette s = new Silhouette((byte) silhouettes.size());
        silhouettes.add(s);
        //System.out.println(queue.size());
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
     * Checks if coordinates are inside image
     *
     * @param y the y coordinate
     * @param x the x coordinate
     * @return are they inside image
     */
    private boolean coordinatesAreInLimits(int y, int x) {
        return y >= 0 && x >= 0 && y < image.height && x < image.width;
    }

    /**
     * Saves all silhouettes inside file with given name + ".png"
     *
     * @param fileName the name of a file where to save
     */
    void savePicture(String fileName) {
        BufferedImage bi = new BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < pixelMap.length; y++) {
            for (int x = 0; x < pixelMap[0].length; x++) {
                if (pixelMap[y][x].isSilhouette()) bi.setRGB(x, y, Color.black.getRGB());
                else bi.setRGB(x, y, Color.white.getRGB());
            }
        }

        File f = new File(fileName + ".png");
        try {
            ImageIO.write(bi, "PNG", f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes noise from silhouettes: finds them and reverse them from main matrix (silhouetteMap)
     *
     * @param noiseSize number of pixels needed to not count object as noise
     */
    void cleanNoiseFromSilhoettes(int noiseSize) {
        //TODO: REDO
//        int i = 0;
//        while (i != silhouettes.size()) {
//            if (silhouettes.get(i).getPixelCount() < noiseSize) {
//                silhouettes.get(i).applyMaskTo(silhouetteMap);
//                silhouettes.remove(i);
//            } else i++;
//        }
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
     * Converts noise from percents to specific value by applying this percent to the largest silhouette
     *
     * @param noisePercent The percentage to be taken from the largest silhouette
     * @return number of pixels which will be a threshold between noise and silhouette
     */
    int convertNoisePercentToValue(int noisePercent) {
        if (silhouettes.isEmpty()) throw new RuntimeException("List of Silhouettes is empty.");
        return silhouettes.stream().map(Silhouette::getPixelCount)
                .max(Integer::compare).orElseThrow() * noisePercent / 100;
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
