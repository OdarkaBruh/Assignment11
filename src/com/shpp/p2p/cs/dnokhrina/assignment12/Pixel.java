package com.shpp.p2p.cs.dnokhrina.assignment12;

public class Pixel {
    final int x;
    final int y;

    private final int color;
    private boolean isChecked = false;

    private boolean isSilhouette = false;
    private byte silhouetteId;

    Pixel(int x, int y, int color){
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void assignSilhouette(Silhouette silhouette) {
        isSilhouette = true;
        this.silhouetteId = silhouette.id;
        this.isChecked = true;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public boolean isSilhouette() {
        return isSilhouette;
    }
}
