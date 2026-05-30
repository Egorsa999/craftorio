package io.github.craftorio.view;

public record VisibleBounds(int startX, int endX, int startY, int endY) {

    public boolean contains(double x, double y) {
        return x >= startX && x <= endX && y >= startY && y <= endY;
    }
}
