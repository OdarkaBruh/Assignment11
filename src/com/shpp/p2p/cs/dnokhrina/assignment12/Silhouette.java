package com.shpp.p2p.cs.dnokhrina.assignment12;

import java.util.ArrayList;

/**
 * Contains information about each silhouette and represents it.
 */
public class Silhouette {
    byte id;
    ArrayList<Pixel> pixels = new ArrayList<>();

    Silhouette(byte id) {
        this.id = id;
    }

    void addPixel(Pixel pixel) {
        pixels.add(pixel);
        pixel.assignSilhouette(this);
    }

    /**
     * Counts how many pixels this silhouette has.
     *
     * @return the number of pixels
     */
    int getPixelCount() {
        return pixels.size();
    }

    void deleteSilhouette(){
        for (Pixel pixel: pixels) {
            pixel.setSilhouette(false);
        }
    }
}
