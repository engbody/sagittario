package edu.illinois.cs125.sagittario.sagittario;

import java.io.Serializable;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class MineSweeper implements Serializable {

    public static final int COVERED = 0;
    public static final int UNCOVERED = 1;
    public static final int FLAGGED = 2;

    public final int fieldSize;
    public final int nBombs;

    // Grid storing bomb locations
    private boolean[][] grid;
    // Grid showing how many nBombs are neighboring each slot
    public int[][] neighborGrid;
    /**
     * DISPLAY GRID ~ grid shown to player
     * 0: Covered
     * 1: Uncovered
     * 2: Flagged
     */
    public int[][] displayGrid;

    // Boolean indicating the game status
    boolean gameOver = false;
    boolean gameWon = false;

    // Count of flags put up
    public int flagCount = 0;
    // Count tiles uncovered
    public int tilesUncovered = 0;

    private boolean outOfBounds(final int x, final int y) {
        return x < 0 || x >= this.fieldSize || y < 0 || y >= this.fieldSize;
    }

    // Fills the boolean grid with nBombs ('True' means there is a bomb)
    private void initializeGrid() {
        int count = this.nBombs;
        while (count > 0) {
            int randX = ThreadLocalRandom.current().nextInt(0, this.fieldSize);
            int randY = ThreadLocalRandom.current().nextInt(0, this.fieldSize);
            if (!this.grid[randX][randY]) {
                this.grid[randX][randY] = true;
                count--;
            }
        }
    }

    // Fills Neighbor Grid with corresponding values
    private void updateNeighbors() {
        for (int i = 0; i < this.fieldSize; i++) {
            for (int j = 0; j < this.fieldSize; j++) {
                if (!this.grid[i][j]) {
                    this.neighborGrid[i][j] = this.neighborCount(i, j);
                } else {
                    this.neighborGrid[i][j] = 9;
                }
            }
        }
    }

    // Counts number of bombs in orbiting tiles
    private int neighborCount(final int xCoord, final int yCoord) {
        int count = 0;
        for (int i = xCoord - 1; i < xCoord + 2; i++) {
            for (int j = yCoord - 1; j < yCoord + 2; j++) {
                // Check if neighbor tile is in bounds
                if (outOfBounds(i, j)) {
                    continue;
                }
                if (grid[i][j]) {
                    count++;
                }
            }
        }
        return count;
    }

    // flags a tile
    public void flag(final int xCoord, final int yCoord) {
        if(outOfBounds(xCoord, yCoord)){
            return;
        }
        if (this.displayGrid[xCoord][yCoord] == FLAGGED) {
            this.displayGrid[xCoord][yCoord] = COVERED;
            flagCount--;
        } else if (this.displayGrid[xCoord][yCoord] == COVERED) {
            this.displayGrid[xCoord][yCoord] = FLAGGED;
            flagCount++;
        }
    }


    // Function to attempt to reveal a tile. It won't work on
    //      out of bounds or flagged tiles
    public void choose(final int xCoord, final int yCoord) {
        if (outOfBounds(xCoord, yCoord)) {
            return;
        }
        if (this.displayGrid[xCoord][yCoord] == FLAGGED) {
            return;
        } else if (this.displayGrid[xCoord][yCoord] == COVERED) {
            if (this.grid[xCoord][yCoord]) {
                gameOver = true;
                return;
            }
            this.reveal(xCoord, yCoord, true);
            if (tilesUncovered == (fieldSize * fieldSize - nBombs)) {
                gameOver = true;
                gameWon = true;
            }
        } else if (this.displayGrid[xCoord][yCoord] == UNCOVERED) {
            return;
        }
    }

    // Reveals all inter-connected 0 spaces;
    public void reveal(final int xCoord, final int yCoord) {
        System.out.println("Running at coords (" + xCoord + "," + yCoord + ")");
        if (outOfBounds(xCoord, yCoord)) {
            return;
        }
        if (!this.grid[xCoord][yCoord] && this.displayGrid[xCoord][yCoord] == COVERED) {
            this.displayGrid[xCoord][yCoord] = UNCOVERED;
            tilesUncovered++;
            if (this.neighborGrid[xCoord][yCoord] == 0) {
                this.reveal(xCoord + 1, yCoord);
                this.reveal(xCoord - 1, yCoord);
                this.reveal(xCoord + 1, yCoord + 1);
                this.reveal(xCoord - 1, yCoord - 1);
                this.reveal(xCoord, yCoord + 1);
                this.reveal(xCoord, yCoord - 1);
                this.reveal(xCoord + 1, yCoord - 1);
                this.reveal(xCoord - 1, yCoord + 1);
            }
        }
    }

    public void reveal(final int xCoord, final int yCoord, final boolean check) {
        if (outOfBounds(xCoord, yCoord)) {
            return;
        }
        if (this.neighborGrid[xCoord][yCoord] != 0) {
            this.displayGrid[xCoord][yCoord] = UNCOVERED;
            tilesUncovered++;
            return;
        }
        this.displayGrid[xCoord][yCoord] = UNCOVERED;
        tilesUncovered++;
        this.reveal(xCoord + 1, yCoord);
        this.reveal(xCoord - 1, yCoord);
        this.reveal(xCoord + 1, yCoord + 1);
        this.reveal(xCoord - 1, yCoord - 1);
        this.reveal(xCoord, yCoord + 1);
        this.reveal(xCoord, yCoord - 1);
        this.reveal(xCoord + 1, yCoord - 1);
        this.reveal(xCoord - 1, yCoord + 1);
    }


    public MineSweeper(final int size, final int numBombs) {
        this.fieldSize = size;
        this.nBombs = numBombs;
        grid = new boolean[this.fieldSize][this.fieldSize];
        neighborGrid = new int[this.fieldSize][this.fieldSize];
        // Arrays of integers are initialized with zeros.
        displayGrid = new int[this.fieldSize][this.fieldSize];
        this.initializeGrid();
        this.updateNeighbors();
    }
}
