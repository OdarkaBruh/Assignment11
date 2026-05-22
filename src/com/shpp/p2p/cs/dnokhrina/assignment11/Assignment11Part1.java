package com.shpp.p2p.cs.dnokhrina.assignment11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

public final class Assignment11Part1 {
    private static BufferedImage mainImage;
    private static final int threshold = 255 / 2;
    private static HashSet<Silhouette> silhouettes = new HashSet<>();

    public static void main(String[] args) {
        readImage("2.png");
        processImage_rowByRow();
    }

    private static void processImage_rowByRow() {
        for (int h = 0; h < mainImage.getHeight(); h++) {
            for (int w = 0; w < mainImage.getWidth(); w++) {
                int rgb = mainImage.getRGB(w, h);
                if (isObject(rgb)) {
                    ArrayList<Silhouette> foundIn = new ArrayList<>();
                    for (Silhouette s : silhouettes) {
                        if (s.pixelsConnected(w, h)) foundIn.add(s);
                    }

                    if (foundIn.isEmpty()) silhouettes.add(new Silhouette(w, h));
                    else if (foundIn.size() == 1) foundIn.getFirst().addPixel(w, h);
                    else {
                        Silhouette main = foundIn.getFirst();
                        for (int i = 1; i < foundIn.size(); i++) {
                            main.mergeWith(foundIn.get(i));
                            silhouettes.remove(foundIn.get(i));
                        }
                        main.addPixel(w, h);
                    }
                }
            }
        }
        System.out.println(silhouettes.size());
    }

    private static boolean isObject(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        //System.out.println(red);
        if (red < threshold) return true;
        else return false;
    }

    private static BufferedImage readImage(String name) {
        try {
            File inputFile = new File(name);
            mainImage = ImageIO.read(inputFile);
        } catch (IOException e) {
            System.out.println("Error while reading image: " + e.getMessage());
        }
        return null;
    }
}