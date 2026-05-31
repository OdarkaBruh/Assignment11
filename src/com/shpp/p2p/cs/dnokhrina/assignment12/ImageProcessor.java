package com.shpp.p2p.cs.dnokhrina.assignment12;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
    private final boolean[][] searchedMap;
    /** Matrix which tracks which pixels contain silhouettes */
    private final boolean[][] silhouetteMap;
    /** All silhouettes */
    private final ArrayList<Silhouette> silhouettes = new ArrayList<>();

    /**
     * ImageProcessor is responsible for all manipulations with an image and its silhouettes
     * @param image the image to process
     * @param colorThreshold The minimum difference between the background value and a color, so that
     *                       the color is not considered part of the background.
     * @param backgroundColor rgb value of the background color
     */
    ImageProcessor(Image image, int colorThreshold, int backgroundColor) {
        this.image = image;
        this.colorThreshold = colorThreshold;

        //Some images break an algorithm, but I couldn't figure the reason (it isn't the alpha channel).
        if (this.colorThreshold == 0) System.err.println("WARNING: color threshold < 1.");

        this.backgroundColor = backgroundColor;

        backgroundColorsMap.put(backgroundColor, true);

        searchedMap = new boolean[image.height][image.width];
        silhouetteMap = new boolean[image.height][image.width];
    }

    /**
     * Finds all silhouettes (with noise)
     */
    void findSilhouettes() {
        for (int y = 0; y < image.height; y++) {
            for (int x = 0; x < image.width; x++) {
                if (!searchedMap[y][x]) {
                    searchedMap[y][x] = true;
                    if (isObject(y, x)) {
                        checkNeighbourPixels(x, y);
                        silhouettes.add(new Silhouette(silhouetteMap));
                    }
                }
            }
        }
    }

    /**
     * Looks at 8 neighbouring pixels and if they are not background and weren't checked before - "goes deeper"
     * by calling itself, but now with these new coordinates at center. Recursion will be ended when all silhouette
     * is mapped.
     *
     * @param x the central x coordinate around which the check should be performed
     * @param y the central y coordinate around which the check should be performed
     */
    private void checkNeighbourPixels(int x, int y) {
        silhouetteMap[y][x] = true;

        for (int tempY = y - 1; tempY < y + 2; tempY++) {
            for (int tempX = x - 1; tempX < x + 2; tempX++) {
                if (coordinatesAreInLimits(tempY, tempX) && !searchedMap[tempY][tempX]) {
                    searchedMap[tempY][tempX] = true;
                    if (isObject(tempY, tempX)) {
                        checkNeighbourPixels(tempX, tempY);
                    }
                }
            }
        }
    }

    /**
     * Checks if coordinates are inside image
     * @param y the y coordinate
     * @param x the x coordinate
     * @return are they inside image
     */
    private boolean coordinatesAreInLimits(int y, int x) {
        return y >= 0 && x >= 0 && y < image.height && x < image.width;
    }

    /**
     * Saves all silhouettes inside file with given name + ".png"
     * @param fileName the name of a file where to save
     */
    void savePicture(String fileName) {
        BufferedImage bi = new BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < silhouetteMap.length; y++) {
            for (int x = 0; x < silhouetteMap[0].length; x++) {
                if (silhouetteMap[y][x]) bi.setRGB(x, y, Color.black.getRGB());
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
     * @param noiseSize number of pixels needed to not count object as noise
     */
    void cleanNoiseFromSilhoettes(int noiseSize) {
        int i = 0;
        while (i != silhouettes.size()) {
            if (silhouettes.get(i).getPixelCount() < noiseSize) {
                silhouettes.get(i).applyMaskTo(silhouetteMap);
                silhouettes.remove(i);
            } else i++;
        }
    }

    /**
     * Returns the number of silhouettes
     * @return the number of silhouettes
     */
    int getSilhouettesCount() {
        return silhouettes.size();
    }

    /**
     * Converts noise from percents to specific value by applying this percent to the largest silhouette
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
     * @param y the y coordinate of pixel
     * @param x the x coordinate of pixel
     * @return whether the pixel contains silhouette
     */
    private boolean isObject(int y, int x) {
        int pixelColor = image.getPixelRGB(y, x);
        if (backgroundColorsMap.containsKey(pixelColor)) return !backgroundColorsMap.get(pixelColor);
        else {
            int r = ColorCalculator.calculateDistanceBetweenColors(pixelColor, backgroundColor);
            boolean isBackground = r < colorThreshold;
            backgroundColorsMap.put(pixelColor, isBackground);
            return !isBackground;
        }
    }
}
