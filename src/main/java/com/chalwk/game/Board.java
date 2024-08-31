/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */

package com.chalwk.game;

import java.util.Random;

public class Board {

    private final Cell[][] board;
    private final int rows;
    private final int cols;

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.board = new Cell[rows][cols];
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
        int numMines = (int) (rows * cols * 0.15);
        int minesPlaced = 0;
        while (minesPlaced <= numMines) {
            int row = random.nextInt(rows);
            int col = random.nextInt(cols);
            if (!board[row][col].isMine()) {
                board[row][col].setMine(true);
                minesPlaced++;
            }
        }
    }

    public void calculateHints() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int hint = 0;
                if (board[i][j].isMine()) {
                    continue;
                }
                for (int r = i - 1; r <= i + 1; r++) {
                    for (int c = j - 1; c <= j + 1; c++) {
                        if (r >= 0 && r < rows && c >= 0 && c < cols && board[r][c].isMine()) {
                            hint++;
                        }
                    }
                }
                board[i][j].setHint(hint);
            }
        }
    }

    public void revealCell(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return;
        }
        board[row][col].setRevealed(true);
        if (board[row][col].getHint() == 0) {
            for (int r = row - 1; r <= row + 1; r++) {
                for (int c = col - 1; c <= col + 1; c++) {
                    if (r >= 0 && r < rows && c >= 0 && c < cols && !board[r][c].isRevealed()) {
                        revealCell(r, c);
                    }
                }
            }
        }
    }

    public boolean isGameWon() {
        int unrevealedCells = 0;
        for (Cell[] row : board) {
            for (Cell cell : row) {
                if (!cell.isMine() && !cell.isRevealed()) {
                    unrevealedCells++;
                }
            }
        }
        return unrevealedCells == 0;
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

        // Rows with cell contents
        for (int i = 0; i < rows; i++) {
            sb.append(i + " ");
            for (int j = 0; j < cols; j++) {
                if (board[i][j].isRevealed()) {
                    if (board[i][j].isMine()) {
                        sb.append("* ");
                    } else {
                        sb.append(board[i][j].getHint() + " ");
                    }
                } else {
                    sb.append(". ");
                }
            }
            sb.append("\n");
        }

        sb.append("```");
        return sb.toString();
    }

    public Cell getCell(int row, int col) {
        return board[row][col];
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public static class Cell {
        private boolean mine;
        private int hint;
        private boolean revealed;

        public Cell() {
            this.mine = false;
            this.hint = 0;
            this.revealed = false;
        }

        public boolean isMine() {
            return mine;
        }

        public void setMine(boolean mine) {
            this.mine = mine;
        }

        public int getHint() {
            return hint;
        }

        public void setHint(int hint) {
            this.hint = hint;
        }

        public boolean isRevealed() {
            return revealed;
        }

        public void setRevealed(boolean revealed) {
            this.revealed = revealed;
        }
    }
}