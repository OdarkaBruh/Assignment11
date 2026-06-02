package com.shpp.p2p.cs.dnokhrina.assignment12;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Receives the name of the file as args[0]. If it's not empty - reads file, if it's null - reads "test.jpg".
 * Treats image as black-and-white image by using only average of red, green and blue values.
 * Detects and counts contrasting silhouettes on the image by checking row by row, but if it finds a contrasting pixel
 * - calls DFS algorithm to map this silhouette and continues row-by-row scanning, but skipping already scanned lines.
 * <p>
 * Note: "Silhouette" counts as connected pixels of contrasting color (doesn't check whether color is the same).
 * If a black circle is inside another black circle, but they don't connect - it will be counted as two separate
 * silhouettes
 */
public final class Assignment12Part1 {

    //=========== DEBUG OPTIONS ========//
    /**
     * if true -> processor will treat COLOR_THRESHOLD_VALUE as a percentage derived from
     * the difference between the minimum and maximum color values.
     * if false -> The color will be treated as the background color if
     * the difference between it and the background is greater than COLOR_THRESHOLD_VALUE
     */
    static final boolean COLOR_THRESHOLD_IS_IN_PERCENT = true;
    /**
     * The minimum difference between the background value and a color, so that the color is not considered part
     * of the background.
     * <p>
     * So, if the background color is 20 and COLOR_THRESHOLD_VALUE is 10, all colors with a difference
     * between 10 and 30 will be considered part of the background.
     */
    static final int COLOR_THRESHOLD_VALUE = 20;


    static final int NOISE_REMOVAL_RADIUS = 2;
    /** Number of pixels needed to NOT count silhouette as noise */
    static final int STUCK_THRESHOLD = 1;

    // ============= END OF DEBUG ==================

    /**
     * Reads first argument and if it is not empty - calls to create image from this file.
     * If args is empty - reads default file "test.jpg"
     * <p>
     * Then process this picture
     *
     * @param args name of the file (with ext) to read
     */
    public static void main(String[] args) {
        String fileName = args.length == 0 ? "test.jpg" : args[0];

        ImageProcessor imageProcessor = new ImageProcessor(readImage(fileName));
        processImage(imageProcessor);
    }

    /**
     * processes Image inside ImageProcessor: finds silhouettes, cleans them, prints result.
     *
     * @param imageProcessor processor with needed image to process
     */
    private static void processImage(ImageProcessor imageProcessor) {
        int colorThreshold = COLOR_THRESHOLD_IS_IN_PERCENT ?
                (getDifferenceBetweenMinAndMaxColor(imageProcessor.getPixelMap()) * COLOR_THRESHOLD_VALUE / 100) :
                COLOR_THRESHOLD_VALUE;

        int backgroundColor = calculateMainColor(imageProcessor.getPixelMap());

        imageProcessor.setColorThreshold(colorThreshold);
        imageProcessor.setBackgroundColor(backgroundColor);
        imageProcessor.findSilhouettes();
        //System.out.println(imageProcessor.getSilhouettesCount());
        Filters f = new Filters(imageProcessor.getPixelMap(),"Output");

        f.cleanData(1);

    }

    private static int getDifferenceBetweenMinAndMaxColor(Pixel[][] pixelMap) {
        int max = pixelMap[0][0].getColor();
        int min = pixelMap[0][0].getColor();
        for (int y = 0; y < pixelMap.length; y++) {
            for (int x = 0; x < pixelMap[0].length; x++) {
                if (min > pixelMap[y][x].getColor()) min = pixelMap[y][x].getColor();
                else if (max < pixelMap[y][x].getColor()) max = pixelMap[y][x].getColor();
            }
        }
        return ColorCalculator.calculateDistanceBetweenColors(min,max);
    }

    private static int calculateMainColor(Pixel[][] pixelMap){
        HashMap<Integer, Integer> colors = new HashMap<>();
        for (int y = 0; y < pixelMap.length; y++) {
            for (int x = 0; x < pixelMap[0].length; x++) {
                colors.put(pixelMap[y][x].getColor(), colors.getOrDefault(pixelMap[y][x].getColor(), 0) + 1);
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
     * Reads the image from file.
     *
     * @param name file's name
     * @return BufferedImage of read image
     */
    static BufferedImage readImage(String name) {
        try {
            File inputFile = new File(name);
            if (!inputFile.exists()) {
                name = name.substring(0, name.length() - 3) + (name.endsWith("jpg") ? "png" : "jpg");
                inputFile = new File(name);
            }
            return ImageIO.read(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while reading image.");
        }
    }
}