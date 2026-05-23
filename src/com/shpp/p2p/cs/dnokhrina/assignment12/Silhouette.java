package com.shpp.p2p.cs.dnokhrina.assignment12;

/**
 * (Note: This class is really bare right now, but I think that second part of the task will have something
 * to do with this)
 * Contains id of the silhouette and general counter.
 */
public class Silhouette {
    /** Count of created silhouettes */
    static int silhouetteCount = 0;
    /** id of this silhouette */
    final int id;

    /** Constructor: increase mumber of silhouettes and sets value to this one as id*/
    public Silhouette() {
        silhouetteCount++;
        id = silhouetteCount;
    }

    /**
     * Looks at 8 neighbouring pixels and if they are not background and weren't checked before - "goes deeper"
     * by calling itself, but now with these new coordinates at center. Recursion will be ended when all silhouette
     * is mapped.
     *
     * @param posX x-coordinate of central pixel
     * @param posY y-coordinate of central pixel
     * @param searched matrix which has information was this pixel mapped before or not
     * @param imageManager manages image: gets width / height and checks if it is object or a background.
     */
    public void checkNearestPixelsForThisSilhouette(int posX, int posY, int[][] searched, ImageManager imageManager) {
        searched[posY][posX] = id;
        int x;
        int y;
        for (int i = -1; i < 2; i++) {
            y = posY + i;
            for (int j = -1; j < 2; j++) {
                x = posX + j;
                if (x > 0 && y > 0 && x < imageManager.width && y < imageManager.height && searched[y][x] == 0) {
                    if (imageManager.isObject(x, y)) checkNearestPixelsForThisSilhouette(x, y, searched, imageManager);
                    else searched[y][x] = -1;
                }
            }
        }
    }

}
