package edu.illinois.cs125.sagittario.sagittario;

import java.util.concurrent.ThreadLocalRandom;

public class MineSweeper {
    private static final int LENGTH = 8;
    private static final int BOMBS = 10;

    // Grid storing bomb locations
    private boolean[][] grid = new boolean[LENGTH][LENGTH];
    // Grid showing how many bombs are neighboring each slot
    private int[][] neighborGrid = new int[LENGTH][LENGTH]

    // Retrieve the entire grid
    public boolean[][] getGrid() {
        return this.grid;
    }

    // Fills the boolean grid with bombs ('True' means there is a bomb)
    private void fillGrid() {
        int count = BOMBS;
        while (count >= 0) {
            int randX = ThreadLocalRandom.current().nextInt(0, LENGTH);
            int randY = ThreadLocalRandom.current().nextInt(0, LENGTH);
            if (this.grid[randX][randY] == false) {
                this.grid[randX][randY] = true;
                count--;
            }
        }
    }

    public void getNeighbors() {
        for (int i = 0; i < LENGTH; i++) {
            for (int j = 0; j < LENGTH; j++) {
                if (!this.grid[i][j]) {
                    this.neighborGrid[i][j] = this.neighborCount(i, j);
                } else {
                    this.neighborGrid[i][j] = -1;
                }
            }
        }
    }

    private int neighborCount(final int xCoord, final int yCoord) {

    }


    // Displays the entire grid ('X' for a bomb, 'O' for no bomb)
    //     - Created for debugging
    public static void printGrid(final boolean[][] grid) {
        System.out.println();
        for (int j = 0; j < LENGTH; j++) {
            System.out.print("|");
            for (int i = 0; i < LENGTH; i++) {
                char val;
                if (grid[i][j]) {
                    val = 'X';
                } else {
                    val = 'O';
                }
                System.out.print(val + "|");
            }
            System.out.println();
        }
    }

    public static void main(String[] unused) {
        MineSweeper x = new MineSweeper();
        x.fillGrid();
        MineSweeper.printGrid(x.grid);
    }
}
