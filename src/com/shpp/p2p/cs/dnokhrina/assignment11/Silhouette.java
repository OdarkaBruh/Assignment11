package com.shpp.p2p.cs.dnokhrina.assignment11;

import java.util.HashSet;

public class Silhouette {
    private final HashSet<Coordinates> pixels = new HashSet<>();

    public Silhouette(int pixelX, int pixelY) {
        pixels.add(new Coordinates(pixelX, pixelY));
    }

    public boolean pixelsConnected(int x, int y) {
        for (Coordinates c: pixels) {
            if (Math.abs(x - c.x)  + Math.abs(y - c.y) <= 2) return true;
        }
        return false;
    }


    public void addPixel(int x, int y) {
        pixels.add(new Coordinates(x, y));
    }

    public void mergeWith(Silhouette silhouette) {
        pixels.addAll(silhouette.pixels);
    }
}
