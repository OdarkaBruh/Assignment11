package com.shpp.p2p.cs.dnokhrina.assignment12;

public class Pixel {
    final int x;
    final int y;

    private final int color;
    private boolean isChecked = false;

    private boolean isSilhouette = false;
    byte silhouetteId;

    Pixel(int x, int y, int color){
        this.x = x;
        this.y = y;
        this.color = color;
    }

    Pixel(Pixel p) {
        this.x = p.x;
        this.y = p.y;
        this.isSilhouette = p.isSilhouette;
        this.silhouetteId = p.silhouetteId;
        this.color = p.color;
    }

    public int getColor() {
        return color;
    }

    public byte getSilhouetteId() {
        return silhouetteId;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void setSilhouette(boolean silhouette) {
        isSilhouette = silhouette;
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
