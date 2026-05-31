package com.shpp.p2p.cs.dnokhrina.assignment12;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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

    /**
     * if true -> processor will treat NOISE_THRESHOLD_VALUE as a percentage derived from the biggest silhouette.
     * if false -> just looks if a silhouette has >=NOISE_THRESHOLD_VALUE pixels;
     */
    static final boolean NOISE_THRESHOLD_IS_IN_PERCENT = true;
    /** Number of pixels needed to NOT count silhouette as noise */
    static final int NOISE_THRESHOLD_VALUE = 10;

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

        Image image = new Image(readImage(fileName));
        ImageProcessor imageProcessor = createImageProcessor(image);

        processImage(imageProcessor);
    }

    /**
     * creates instance of ImageProcessor, calculates color threshold and background color
     *
     * @param image image which will be processed by this processor later
     * @return created instance of ImageProcessor
     */
    private static ImageProcessor createImageProcessor(Image image) {
        int backgroundColor = image.calculateMainColor();
        int colorThreshold = COLOR_THRESHOLD_IS_IN_PERCENT ?
                (image.getMaxColorDifference() * COLOR_THRESHOLD_VALUE / 100) :
                COLOR_THRESHOLD_VALUE;
        return new ImageProcessor(image, colorThreshold, backgroundColor);
    }

    /**
     * processes Image inside ImageProcessor: finds silhouettes, cleans them, prints result.
     *
     * @param imageProcessor processor with needed image to process
     */
    private static void processImage(ImageProcessor imageProcessor) {
        imageProcessor.findSilhouettes();

        int noiseThreshold = NOISE_THRESHOLD_IS_IN_PERCENT ?
                imageProcessor.convertNoisePercentToValue(NOISE_THRESHOLD_VALUE)
                : NOISE_THRESHOLD_VALUE;
        imageProcessor.cleanNoiseFromSilhoettes(noiseThreshold);

        System.out.println("Result: " + imageProcessor.getSilhouettesCount());
        imageProcessor.savePicture("Output");
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