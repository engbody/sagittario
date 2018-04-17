package edu.illinois.cs125.sagittario.sagittario;

import java.util.concurrent.ThreadLocalRandom;

public class MineSweeper {
    private static final int LENGTH = 8;
    private static final int BOMBS = 10;

    // Grid storing bomb locations
    private boolean[][] grid = new boolean[LENGTH][LENGTH];
    // Grid showing how many bombs are neighboring each slot
    private int[][] neighborGrid = new int[LENGTH][LENGTH];
    /**
     *   DISPLAY GRID ~ grid shown to player
     *      0: Covered
     *      1: Uncovered
     *      2: Flagged
     */
    public int[][] displayGrid = new int[LENGTH][LENGTH];

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

    // Fills Neighbor Grid with corresponding values
    private void getNeighbors() {
        for (int i = 0; i < LENGTH; i++) {
            for (int j = 0; j < LENGTH; j++) {
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
                if (i < 0 || i >= LENGTH || j < 0 || j >= LENGTH) {
                    break;
                }
                if (grid[i][j]) {
                    count++;
                }
            }
        }
        return count;
    }

    // Displays the Bomb Grid or Neighbor Grid
    //     - Created for debugging
    private static void printGrid(final boolean[][] grid) {
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
    private static void printGrid(final int[][] grid) {
        System.out.println();
        for (int j = 0; j < LENGTH; j++) {
            System.out.print("|");
            for (int i = 0; i < LENGTH; i++) {
                if (grid[i][j] == 9) {
                    System.out.print("X|");
                } else {
                    System.out.print(grid[i][j] + "|");
                }
            }
            System.out.println();
        }
    }

    // dislays what the player would see
    private void displayGrid() {
        System.out.println("  <DISPLAY GRID>");
        for (int j = 0; j < LENGTH; j++) {
            System.out.print("|");
            for (int i = 0; i < LENGTH; i++) {
                if (this.displayGrid[i][j] == 0) {
                    System.out.print("O");
                } else if (this.displayGrid[i][j] == 1) {
                    if (this.neighborGrid[i][j] == 0) {
                        System.out.print("_");
                    } else {
                        System.out.print(this.neighborGrid[i][j]);
                    }
                } else if (this.displayGrid[i][j] == 2) {
                    System.out.print("F");
                }
                System.out.print("|");
            }
            System.out.println();
        }
    }

    // flags a tile
    private void flag(final int xCoord, final int yCoord) {
        if (xCoord < 0 || xCoord >= LENGTH || yCoord < 0 || yCoord >= LENGTH) {
            return;
        }
        if (this.displayGrid[xCoord][yCoord] == 2) {
            this.displayGrid[xCoord][yCoord] = 0;
        } else if (this.displayGrid[xCoord][yCoord] == 0) {
            this.displayGrid[xCoord][yCoord] = 2;
        }
    }

    private void choose(final int xCoord, final int yCoord) {
        if (xCoord < 0 || xCoord >= LENGTH || yCoord < 0 || yCoord >= LENGTH) {
            return;
        }
        if (this.displayGrid[xCoord][yCoord] == 2) {
            return;
        } else if (this.displayGrid[xCoord][yCoord] == 0) {
            this.displayGrid[xCoord][yCoord] = 2;
        } else if (this.displayGrid[xCoord][yCoord] == 1) {
            return;
        }
    }

    // Displays game over screen
    private void displayGameOver() {
        System.out.println("  <GAME OVER!>");
        for (int j = 0; j < LENGTH; j++) {
            System.out.print("|");
            for (int i = 0; i < LENGTH; i++) {
                if (this.displayGrid[i][j] == 0) {
                    if (this.grid[i][j]) {
                        System.out.print("X");
                    } else {
                        System.out.print("O");
                    }
                } else if (this.displayGrid[i][j] == 1) {
                    if (this.neighborGrid[i][j] == 0) {
                        System.out.print("_");
                    } else {
                        System.out.print(this.neighborGrid[i][j]);
                    }
                } else if (this.displayGrid[i][j] == 2) {
                    System.out.print("F");
                }
                System.out.print("|");
            }
            System.out.println();
        }
    }

    private MineSweeper() {
        this.fillGrid();
        this.getNeighbors();
        for (int i = 0; i < LENGTH; i++) {
            for (int j = 0;  j< LENGTH; j++) {
                this.displayGrid[i][j] = 0;
            }
        }
    }

    public static void main(String[] unused) {
        MineSweeper x = new MineSweeper();
        MineSweeper.printGrid(x.grid);
        x.displayGrid();
        x.flag(2,4);
        x.displayGrid();
        x.flag(5,7);
        x.displayGrid();
        x.displayGameOver();
    }
}
