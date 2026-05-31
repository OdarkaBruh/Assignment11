package com.shpp.p2p.cs.dnokhrina.assignment12;

/**
 * Contains information about each silhouette and represents it.
 */
public class Silhouette {
    /**
     * a map of all silhouettes as of the time the last silhouette was created. If a new silhouette is mapped by
     * another method, it will identify where the new silhouette are by comparing where the values have changed.
     */
    private static boolean[][] allSilhouettesMap;
    /** the mask of this specific silhouette */
    private final boolean[][] silhouetteMask;

    /**
     * creates new instance of silhouette and creates its mask by comparing with matrix of all previous silhouettes
     *
     * @param newMap the current (updated, with this new silhouette) matrix of silhouettes
     */
    Silhouette(boolean[][] newMap) {
        if (allSilhouettesMap == null) {
            allSilhouettesMap = new boolean[newMap.length][newMap[0].length];
        }
        silhouetteMask = createMask(newMap);
    }

    /**
     * Calculates mask by comparing new and old version of silhouettes matrix
     *
     * @param newSilhouetteMap the current (updated, with this new silhouette) matrix of silhouettes
     * @return matric with a mask of this silhouette
     */
    private static boolean[][] createMask(boolean[][] newSilhouetteMap) {
        boolean[][] silhouetteMask = new boolean[newSilhouetteMap.length][newSilhouetteMap[0].length];
        for (int y = 0; y < newSilhouetteMap.length; y++) {
            for (int x = 0; x < newSilhouetteMap[0].length; x++) {
                if (allSilhouettesMap[y][x] != newSilhouetteMap[y][x]) {
                    allSilhouettesMap[y][x] = true;
                    silhouetteMask[y][x] = true;
                }
            }
        }
        return silhouetteMask;
    }

    /**
     * Counts how many pixels this silhouette has.
     *
     * @return the number of pixels
     */
    int getPixelCount() {
        int pixels = 0;
        for (int y = 0; y < silhouetteMask.length; y++) {
            for (int x = 0; x < silhouetteMask[0].length; x++) {
                if (silhouetteMask[y][x]) pixels++;
            }
        }
        return pixels;
    }

    /**
     * Applies mask of this silhouette to the given matrix
     *
     * @param onTheMask matrix to change
     * @return changed matrix (with applied mask now)
     */
    boolean[][] applyMaskTo(boolean[][] onTheMask) {
        for (int y = 0; y < onTheMask.length; y++) {
            for (int x = 0; x < onTheMask[0].length; x++) {
                if (silhouetteMask[y][x]) onTheMask[y][x] = !onTheMask[y][x];
            }
        }
        return onTheMask;
    }
}
