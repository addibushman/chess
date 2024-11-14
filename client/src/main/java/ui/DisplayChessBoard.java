package ui;

import model.GameData;

public class DisplayChessBoard {

    public static void displayChessBoard() {
        String[][] board = {
                {"R", "N", "B", "K", "Q", "B", "N", "R"},
                {"P", "P", "P", "P", "P", "P", "P", "P"},
                {" ", " ", " ", " ", " ", " ", " ", " "},
                {" ", " ", " ", " ", " ", " ", " ", " "},
                {" ", " ", " ", " ", " ", " ", " ", " "},
                {" ", " ", " ", " ", " ", " ", " ", " "},
                {"p", "p", "p", "p", "p", "p", "p", "p"},
                {"r", "n", "b", "k", "q", "b", "n", "r"}
        };

        System.out.println("White's Perspective:");
        printBoard(board);
        System.out.println("\nBlack's Perspective:");
        printBoard(flipBoard(board));
    }

    private static void printBoard(String[][] board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String square = board[i][j];
                if ((i + j) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + square + EscapeSequences.RESET_BG_COLOR + " ");
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY + square + EscapeSequences.RESET_BG_COLOR + " ");
                }
            }
            System.out.println();
        }
    }

    private static String[][] flipBoard(String[][] board) {
        String[][] flippedBoard = new String[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                flippedBoard[i][j] = board[7 - i][7 - j];
            }
        }
        return flippedBoard;
    }

}

