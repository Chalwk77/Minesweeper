/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */

package com.chalwk.game;

import java.util.Random;

public class Board {

    private static final double MINE_DENSITY = 0.15;
    private final Cell[][] board;
    private final int rows;
    private final int cols;
    private final int totalCells;
    private BoardState state;
    private int revealed;
    private int mineCount;

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.board = new Cell[rows][cols];
        this.totalCells = rows * cols;
        this.revealed = 0;
        this.state = BoardState.ONGOING;
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = new Cell();
            }
        }

        this.placeMines();
        this.calculateHints();
    }

    public void placeMines() {
        Random random = new Random();
        int numMines = (int) (rows * cols * MINE_DENSITY);
        int minesPlaced = 0;
        while (minesPlaced <= numMines) {
            int row = random.nextInt(rows);
            int col = random.nextInt(cols);
            if (!board[row][col].isMine()) {
                board[row][col].setMine(true);
                minesPlaced++;
            }
        }
        this.mineCount = numMines;
    }

    public void flagCell(int row, int col, boolean flagged) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return;
        }
        board[row][col].setFlagged(flagged);
    }

    private int countAdjacentMines(int row, int col) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
                if (newRow < 0 || newRow >= rows || newCol < 0 || newCol >= cols) {
                    continue;
                }
                if (board[newRow][newCol].isMine()) {
                    count++;
                }
            }
        }
        return count;
    }

    public void calculateHints() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!board[i][j].isMine()) {
                    int hint = countAdjacentMines(i, j);
                    board[i][j].setHint(hint);
                }
            }
        }
    }

    private boolean isValidCoordinate(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public void revealCell(int row, int col) {
        Cell cell = board[row][col];
        if (cell.isMine()) {
            state = BoardState.LOST;
            revealAllMines();
            return;
        }
        if (cell.isRevealed()) {
            return;
        }
        cell.setRevealed(true);
        revealed++;
        if (cell.isEmpty()) {
            revealEmptyCell(row, col);
        } else if (cell.getHint() == 0) {
            revealEmptyCell(row, col);
        }

        // Update neighboring cells' reveal status if they are not mines
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if ((i == 0 && j == 0) || !isValidCoordinate(row + i, col + j)) {
                    continue;
                }
                Cell neighbor = board[row + i][col + j];
                if (!neighbor.isMine()) {
                    neighbor.setRevealed(true);
                }
            }
        }

        if (revealed == totalCells - mineCount) {
            state = BoardState.WON;
            revealAllMines();
        }
    }

    private void revealEmptyCell(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return;
        }
        Cell cell = board[row][col];
        if (!cell.isEmpty()) {
            return;
        }
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                revealCell(row + i, col + j);
            }
        }
    }

    public void revealAllMines() {
        for (Cell[] row : board) {
            for (Cell cell : row) {
                if (cell.isMine()) {
                    cell.setRevealed(true);
                }
            }
        }
    }

    public BoardState getState() {
        return state;
    }

    public boolean isGameWon() {
        return state == BoardState.WON;
    }

    public boolean isGameLost(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return false;
        }
        Cell cell = board[row][col];
        return cell.isMine() && cell.isRevealed();
    }

    public String buildBoardString() {
        StringBuilder sb = new StringBuilder();

        sb.append("```\n");
        sb.append("   ");
        for (int j = 0; j < cols; j++) {
            sb.append(j).append("   ");
        }
        sb.append("\n");

        for (int i = 0; i < rows; i++) {
            sb.append(i).append(" ");
            for (int j = 0; j < cols; j++) {
                Cell cell = board[i][j];

                if (cell.isRevealed()) {
                    if (cell.isMine()) {
                        sb.append("[*] "); // this represents a mine
                    } else if (cell.getHint() == 0) {
                        sb.append("[ ] "); // this represents a cell with no adjacent mines
                    } else {
                        sb.append("[").append(cell.getHint()).append("] "); // hint (number of adjacent mines)
                    }
                } else if (cell.isFlagged()) {
                    sb.append("[?] "); // this represents a flag
                } else {
                    sb.append("[.] "); // this represents an unrevealed empty cell
                }
            }
            sb.append("\n");
        }

        sb.append("```");
        return sb.toString();
    }


    public static class Cell {

        private boolean mine;
        private boolean flagged;
        private boolean revealed;
        private int hint;

        public boolean isEmpty() {
            return !this.mine && !this.revealed;
        }

        public boolean isMine() {
            return mine;
        }

        public void setMine(boolean mine) {
            this.mine = mine;
        }

        public boolean isFlagged() {
            return flagged;
        }

        public void setFlagged(boolean flagged) {
            this.flagged = flagged;
        }

        public boolean isRevealed() {
            return revealed;
        }

        public void setRevealed(boolean revealed) {
            this.revealed = revealed;
        }

        public int getHint() {
            return hint;
        }

        public void setHint(int hint) {
            this.hint = hint;
        }
    }
}