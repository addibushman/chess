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

    private static String[][] initializeBoard(GameData gameData) {
        String[][] board = new String[8][8];


        board[0] = new String[] {EscapeSequences.WHITE_ROOK, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_BISHOP, EscapeSequences.WHITE_QUEEN, EscapeSequences.WHITE_KING, EscapeSequences.WHITE_BISHOP, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_ROOK};
        board[1] = new String[] {EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN};


        board[6] = new String[] {EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN};
        board[7] = new String[] {EscapeSequences.BLACK_ROOK, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_BISHOP, EscapeSequences.BLACK_QUEEN, EscapeSequences.BLACK_KING, EscapeSequences.BLACK_BISHOP, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_ROOK};

        for (int i = 2; i < 6; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = EscapeSequences.EMPTY;
            }
        }

        return board;
    }


    private static void printBoardBlackPerspective(String[][] board) {
        StringBuilder boardDisplay = new StringBuilder();

        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                String piece = board[i][j];
                String squareColor = (i + j) % 2 == 0 ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY;
                boardDisplay.append(squareColor).append(piece).append(EscapeSequences.RESET_BG_COLOR);
            }
            boardDisplay.append("\n");
        }

        System.out.println(boardDisplay.toString());
    }
}

