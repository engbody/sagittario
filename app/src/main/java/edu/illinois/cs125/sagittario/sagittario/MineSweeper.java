package edu.illinois.cs125.sagittario.sagittario;

import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class MineSweeper {

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

    // Displays the Bomb Grid or Neighbor Grid
    //     - Created for debugging
    private static void printGrid(final boolean[][] grid) {
        System.out.println();
        for (int j = 0; j < grid.length; j++) {
            System.out.print("|");
            for (int i = 0; i < grid.length; i++) {
                char val = (grid[i][j]) ? 'X' : 'O';
                System.out.print(val + "|");
            }
            System.out.println();
        }
    }

    private static void printGrid(final int[][] grid) {
        System.out.println();
        for (int j = 0; j < grid.length; j++) {
            System.out.print("|");
            for (int i = 0; i < grid.length; i++) {
                if (grid[i][j] == 9) {
                    System.out.print("X|");
                } else {
                    System.out.print(grid[i][j] + "|");
                }
            }
            System.out.println();
        }
    }

    // displays what the player would see
    private void displayGrid() {
        System.out.println("  <DISPLAY GRID>");
        System.out.println(" 0 1 2 3 4 5 6 7");
        for (int j = 0; j < this.fieldSize; j++) {
            System.out.print("|");
            for (int i = 0; i < this.fieldSize; i++) {
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
            System.out.print(" " + j);
            System.out.println();
        }
    }

    // flags a tile
    private void flag(final int xCoord, final int yCoord) {
        if(outOfBounds(xCoord, yCoord)){
            return;
        }
        if (this.displayGrid[xCoord][yCoord] == FLAGGED) {
            this.displayGrid[xCoord][yCoord] = COVERED;
        } else if (this.displayGrid[xCoord][yCoord] == COVERED) {
            this.displayGrid[xCoord][yCoord] = FLAGGED;
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
                this.displayGameOver();
                return;
            }
            this.reveal(xCoord, yCoord, true);
        } else if (this.displayGrid[xCoord][yCoord] == UNCOVERED) {
            return;
        }
    }

    /**
     * this.displayGrid[xCoord][yCoord] = 1;
     * int a;
     * int b;
     * a = xCoord + 1;
     * b = yCoord;
     * if (a < this.fieldSize && b < this.fieldSize && a >= 0 && b >= 0 && this.neighborGrid[a][b] == 0 ) {
     * this.reveal(a, b);
     * }
     * a = xCoord - 1;
     * b = yCoord;
     * if (a < this.fieldSize && b < this.fieldSize && a >= 0 && b >= 0 && this.neighborGrid[a][b] == 0 ) {
     * this.reveal(a, b);
     * }
     * a = xCoord;
     * b = yCoord + 1;
     * if (a < this.fieldSize && b < this.fieldSize && a >= 0 && b >= 0 && this.neighborGrid[a][b] == 0 ) {
     * this.reveal(a, b);
     * }
     * a = xCoord;
     * b = yCoord - 1;
     * if (a < this.fieldSize && b < this.fieldSize && a >= 0 && b >= 0 && this.neighborGrid[a][b] == 0 ) {
     * this.reveal(a, b);
     * }
     **/
    // Reveals all inter-connected 0 spaces;
    public void reveal(final int xCoord, final int yCoord) {
        System.out.println("Running at coords (" + xCoord + "," + yCoord + ")");
        if (outOfBounds(xCoord, yCoord)) {
            return;
        }
        if (!this.grid[xCoord][yCoord] && this.displayGrid[xCoord][yCoord] == 0) {
            this.displayGrid[xCoord][yCoord] = 1;
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
        System.out.println("FIRST RUN! :)");
        if (outOfBounds(xCoord, yCoord)) {
            return;
        }
        this.displayGrid[xCoord][yCoord] = 1;
        this.reveal(xCoord + 1, yCoord);
        this.reveal(xCoord - 1, yCoord);
        this.reveal(xCoord, yCoord + 1);
        this.reveal(xCoord, yCoord - 1);
    }

    // Displays game over screen
    private void displayGameOver() {
        this.gameOver = true;
        System.out.println("  <GAME OVER!>");
        for (int j = 0; j < this.fieldSize; j++) {
            System.out.print("|");
            for (int i = 0; i < this.fieldSize; i++) {
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

    public static void main(String[] unused) {
        MineSweeper x = new MineSweeper(8, 10);
        while (!x.gameOver) {
            Scanner reader = new Scanner(System.in); // Reading from System.in
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
            x.displayGrid();
            System.out.print("Enter \"flag\" or \"choose\": ");
            String input = reader.nextLine();
            if (input.equals("flag")) {
                System.out.println("Coordinates to Flag");
                System.out.print("x: ");
                int a = reader.nextInt(); // Scans the next token of the input as an int.
                System.out.print("y: ");
                int b = reader.nextInt();
                x.flag(a, b);
            } else if (input.equals("choose")) {
                System.out.println("Coordinates to Reveal");
                System.out.print("x: ");
                int a = reader.nextInt(); // Scans the next token of the input as an int.
                System.out.print("y: ");
                int b = reader.nextInt();
                x.choose(a, b);
            } else if (input.equals("game over")) {
                for (int i = 0; i < 50; i++) {
                    System.out.println();
                }
                x.displayGameOver();
            } else if (input.equals("reveal")) {
                for (int i = 0; i < 50; i++) {
                    System.out.println();
                }
                System.out.println("> Displaying Neighbors");
                MineSweeper.printGrid(x.neighborGrid);
                System.out.print("Type any char to continue... ");
                input = reader.next();
            } else if (input.equals("help")) {
                System.out.println("");
                System.out.println(">COMMANDS:");
                System.out.println("help: Get list of commands");
                System.out.println("reveal: Uncover all tiles");
                System.out.println("game over: End the game");
                System.out.print("Type any char to continue... ");
                input = reader.next();
            }
        }
    }
}
