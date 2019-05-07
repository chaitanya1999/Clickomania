package com.chaitanyav;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JLabel;

class Tile extends JLabel {

    private int row, col;
    private boolean destroyed = false;
    private Tile[][] game = null;

    public Tile(int side) {
        super.setSize(side, side);
        super.setBorder(BorderFactory.createLineBorder(Color.black));
    }

    public void setGame(Tile[][] game) {
        this.game = game;
    }

    public void setPos(int i, int j) {
        row = i;
        col = j;
    }

    public void destroy() {
        destroyed = true;
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder());
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void undestroy() {
        destroyed = false;
        setBorder(BorderFactory.createLineBorder(Color.black));
    }

    public int row() {
        return row;
    }

    public int col() {
        return col;
    }

    public Tile[][] game() {
        return game;
    }

    public void replicate(Tile tile) {
        replicate(tile, true);
    }

    public void replicate(Tile tile, boolean allowStateChange) {
        setBackground(tile.getBackground());
        if (allowStateChange) {
            if (tile.isDestroyed()) {
                destroy();
            } else if (isDestroyed()) {
                undestroy();
            }
        }
        repaint();
    }
}
