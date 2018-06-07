package com.swehorison.neuralnetwork.tictactoe;

public class TicTacToe {

    private Board board;

    public TicTacToe() {
        board = new Board();
    }

    public static void main(String[] args) {
        TicTacToe ticTacToe = new TicTacToe();
        System.out.println(ticTacToe.placeTick(0, 0, 1));
        System.out.println(ticTacToe.placeTick(1, 0, 1));
        System.out.println(ticTacToe.placeTick(2, 0, 1));


    }

    public Board getBoard() {
        return board;
    }

    public int placeTick(int x, int y, int playerId) {
        this.board.placeTick(x, y, playerId);

        return checkWin();
    }

    private int checkWin() {
        if (checkPlayerOne()) {
            return 0;
        } else if (checkPlayerTwo()) {
            return 1;
        }

        return -1;
    }

    private boolean checkPlayerTwo() {
        // Left-right
        if (board.getBoard()[0][0] == 1 && board.getBoard()[1][0] == 1 && board.getBoard()[2][0] == 1) {
            return true;
        }

        if (board.getBoard()[0][1] == 1 && board.getBoard()[1][1] == 1 && board.getBoard()[2][1] == 1) {
            return true;
        }

        if (board.getBoard()[0][2] == 1 && board.getBoard()[1][2] == 1 && board.getBoard()[2][2] == 1) {
            return true;
        }


        // top-bottom
        if (board.getBoard()[0][0] == 1 && board.getBoard()[0][1] == 1 && board.getBoard()[0][2] == 1) {
            return true;
        }

        if (board.getBoard()[1][0] == 1 && board.getBoard()[1][1] == 1 && board.getBoard()[1][2] == 1) {
            return true;
        }

        if (board.getBoard()[2][0] == 1 && board.getBoard()[2][1] == 1 && board.getBoard()[2][2] == 1) {
            return true;
        }


        // Diagonal
        if (board.getBoard()[0][0] == 1 && board.getBoard()[1][1] == 1 && board.getBoard()[2][2] == 1) {
            return true;
        }

        if (board.getBoard()[2][0] == 1 && board.getBoard()[1][1] == 1 && board.getBoard()[0][2] == 1) {
            return true;
        }
        return false;
    }

    private boolean checkPlayerOne() {
        // Left-right
        if (board.getBoard()[0][0] == 0 && board.getBoard()[1][0] == 0 && board.getBoard()[2][0] == 0) {
            return true;
        }

        if (board.getBoard()[0][1] == 0 && board.getBoard()[1][1] == 0 && board.getBoard()[2][1] == 0) {
            return true;
        }

        if (board.getBoard()[0][2] == 0 && board.getBoard()[1][2] == 0 && board.getBoard()[2][2] == 0) {
            return true;
        }


        // top-bottom
        if (board.getBoard()[0][0] == 0 && board.getBoard()[0][1] == 0 && board.getBoard()[0][2] == 0) {
            return true;
        }

        if (board.getBoard()[1][0] == 0 && board.getBoard()[1][1] == 0 && board.getBoard()[1][2] == 0) {
            return true;
        }

        if (board.getBoard()[2][0] == 0 && board.getBoard()[2][1] == 0 && board.getBoard()[2][2] == 0) {
            return true;
        }


        // Diagonal
        if (board.getBoard()[0][0] == 0 && board.getBoard()[1][1] == 0 && board.getBoard()[2][2] == 0) {
            return true;
        }

        if (board.getBoard()[2][0] == 0 && board.getBoard()[1][1] == 0 && board.getBoard()[0][2] == 0) {
            return true;
        }
        return false;
    }

    public boolean isPlaceAvailable(int placeX, int placeY) {
        return board.getBoard()[placeX][placeY] == -1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("----------------\n");
        sb.append("| " + board.getBoard()[0][0] + " | " + board.getBoard()[0][1] + " | " + board.getBoard()[0][2] + " | \n");
        sb.append("| " + board.getBoard()[1][0] + " | " + board.getBoard()[1][1] + " | " + board.getBoard()[1][2] + " | \n");
        sb.append("| " + board.getBoard()[2][0] + " | " + board.getBoard()[2][1] + " | " + board.getBoard()[2][2] + " | \n");
        sb.append("----------------");
        return sb.toString();
    }

    public class Board {
        private int[][] board = new int[][]{
                {-1, -1, -1},
                {-1, -1, -1},
                {-1, -1, -1}
        };

        public void placeTick(int x, int y, int playerId) {
            if (this.board[x][y] == -1) {
                this.board[x][y] = playerId;
            } else {
                throw new IllegalArgumentException("Already occupied");
            }
        }

        public int[][] getBoard() {
            return board;
        }
    }
}
