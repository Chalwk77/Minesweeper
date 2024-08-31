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
    private boolean flagged;

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

    public void flagCell(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return;
        }
        board[row][col].setFlagged(true);
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

    public void revealCell(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return;
        }
        Cell cell = board[row][col];
        if (cell.isMine()) {
            state = BoardState.LOST;
            return;
        }
        if (cell.isRevealed()) {
            return;
        }
        cell.setRevealed(true);
        if (cell.isEmpty()) {
            revealEmptyCell(row, col);
        }
        revealed++;
        if (revealed == totalCells - mineCount) {
            state = BoardState.WON;
        }
    }


    public BoardState getState() {
        return state;
    }

    public void setState(BoardState state) {
        this.state = state;
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

    public void revealAllMines() {
        for (Cell[] row : board) {
            for (Cell cell : row) {
                if (cell.isMine()) {
                    cell.setRevealed(true);
                }
            }
        }
    }

    public String buildBoardString() {
        StringBuilder sb = new StringBuilder();

        // Header row with column indices
        sb.append("```\n");
        sb.append("  ");
        for (int j = 0; j < cols; j++) {
            sb.append(j + " ");
        }
        sb.append("\n");

        // Rows with cell contents (include 🚩 for flagged cells)
        for (int i = 0; i < rows; i++) {
            sb.append(i + " ");
            for (int j = 0; j < cols; j++) {
                if (board[i][j].isRevealed()) {
                    if (board[i][j].isMine()) {
                        sb.append("* ");
                    } else {
                        sb.append(board[i][j].getHint() + " ");
                    }
                } else if (board[i][j].isFlagged()) {
                    sb.append("🚩 ");
                } else {
                    sb.append(". ");
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

        public void setMine(boolean mine) {
            this.mine = mine;
        }

        public boolean isEmpty() {
            return !this.mine && !this.revealed;
        }

        public boolean isMine() {
            return mine;
        }

        public boolean isFlagged() {
            return flagged;
        }

        public void setFlagged(boolean flagged) {
            this.flagged = flagged;
        }

        public void setRevealed(boolean revealed) {
            this.revealed = revealed;
        }

        public boolean isRevealed() {
            return revealed;
        }

        public int getHint() {
            return hint;
        }

        public void setHint(int hint) {
            this.hint = hint;
        }
    }
}