package com.shpp.p2p.cs.dnokhrina.assignment12;

/**
 * Counts how different colors are
 */
public class ColorCalculator {
    /**
     * Gets value of a specific channel (red, green, blue, alpha) from int RGB
     *
     * @param rgb      value of rgb with all channels
     * @param rgbShift which channel needs to be got
     * @return the value of the specified channel of this RGB color
     */
    private static int getSpecificRGBValue(int rgb, int rgbShift) {
        return (rgb >> 8 * rgbShift) & 0xFF;
    }

    /**
     * calculates how different colors are using vectors
     *
     * @param rgb1 first color
     * @param rgb2 second color
     * @return the length of the line between them
     */
    public static int calculateDistanceBetweenColors(int rgb1, int rgb2) {
        if (rgb1 == rgb2) return 0;

        double result = 0;
        for (int i = 0; i < 3; i++) {
            result += Math.pow((getSpecificRGBValue(rgb1, i) - getSpecificRGBValue(rgb2, i)), 2);
        }
        return (int) Math.sqrt(result);
    }

}
