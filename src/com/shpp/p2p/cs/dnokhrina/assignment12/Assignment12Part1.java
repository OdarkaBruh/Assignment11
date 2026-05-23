package com.shpp.p2p.cs.dnokhrina.assignment12;

import java.util.HashSet;

public final class Assignment12Part1 {

    //=========== DEBUG OPTIONS ========//
    /**
     * if true => variable "THRESHOLD_SET_VALUE" will be used
     * if false => variable "THRESHOLD_IN_PERCENT" will be used
     */
    public static final boolean THRESHOLD_IS_SET_VARIABLE_NOT_PERCENT = false;
    /**
     * Difference between value of the background and color to count this color as the noise of background.
     * So, if background = 0 and THRESHOLD_SET_VALUE = 10, all colors from 0 to 10 will be counted as a background.
     * <p>
     * More noisy picture => higher value
     * Should be in range from 0 to 250.
     */
    public static final int THRESHOLD_SET_VALUE = 0;

    /**
     * percent of difference between silhouettes and background (aka "(maxValue-minValue)/percent"
     * more sensitive, so will work better if photos have different contrast levels
     *
     */
    public static final double THRESHOLD_IN_PERCENT = 20.0 / 100;

    private static final boolean PRINT_MATRIX = false;

    // ============= END OF DEBUG ==================

    /** All found silhouettes */
    private static final HashSet<Silhouette> silhouettes = new HashSet<>();

    /**
     * Reads first argument and if it is not empty - calls to read a file with this name.
     * If args is empty - reads default file "test.jpg"
     * <p>
     * Then process this picture
     *
     * @param args name of the file (with ext) to read
     */
    public static void main(String[] args) {
        if (args.length == 0) processImage(new ImageManager("test.jpg"));
        else processImage(new ImageManager(args[0]));
    }

    /**
     * Counts silhouettes on the pictures.
     * <p>
     * Notes:
     * 1) Counts pixels as "touching" if they are joined diagonally;
     * 2) DEFAULT_THRESHHOLD sensitive
     *
     * @param imageManager object containing information about image: its width, height, rgb matrix, etc.
     */
    private static void processImage(ImageManager imageManager) {
        int[][] searched = new int[imageManager.height][imageManager.width];

        for (int h = 0; h < searched.length; h++) {
            for (int w = 0; w < searched[0].length; w++) {
                if (searched[h][w] == 0) {
                    if (imageManager.isObject(w, h)) {
                        Silhouette silhouette = new Silhouette();
                        silhouette.checkNearestPixelsForThisSilhouette(w, h, searched, imageManager);
                        silhouettes.add(silhouette);
                        if (PRINT_MATRIX) printSearched(searched);
                    } else searched[h][w] = -1;
                }
            }
        }
        System.out.println(silhouettes.size());
        printSearched(searched);
    }

    /**
     * This method isn't used, but can be useful to demonstrate the result. Due to limits of the console,
     * I used literally 10px x 10px images for this method.
     *
     * @param searched matrix of checked pixels
     */
    private static void printSearched(int[][] searched) {
        System.out.println("\n=============");
        StringBuilder stringBuilder = new StringBuilder();
        for (int h = 0; h < searched.length; h++) {
            stringBuilder.append("\n");
            for (int w = 0; w < searched[0].length; w++) {
                if (searched[h][w] == -1) stringBuilder.append(" - ");
                else if (searched[h][w] == 0) stringBuilder.append(" 0 ");
                else stringBuilder.append(" ").append(searched[h][w]).append(" ");
            }
        }
        System.out.println(stringBuilder);
    }
}