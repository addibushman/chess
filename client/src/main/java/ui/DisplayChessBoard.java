package ui;

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

        System.out.println("Black's Perspective:");
        printBoardWhite(board);

        System.out.println("\nWhite's Perspective:");
        printBoardBlack(board);
    }

    private static void printBoardWhite(String[][] board) {
        System.out.print("   ");
        for (char c = 'h'; c >= 'a'; c--) {
            System.out.print(c + " ");
        }
        System.out.println();

        for (int i = 0; i < 8; i++) {
            System.out.print((i + 1) + "  ");

            for (int j = 0; j < 8; j++) {
                String square = board[i][j];
                if ((i + j) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + square +
                            EscapeSequences.RESET_BG_COLOR + " ");
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY + square +
                            EscapeSequences.RESET_BG_COLOR + " ");
                }
            }
            System.out.println("  " + (i + 1));
        }

        System.out.print("   ");
        for (char c = 'h'; c >= 'a'; c--) {
            System.out.print(c + " ");
        }
        System.out.println();
    }

    private static void printBoardBlack(String[][] board) {
        System.out.print("   ");
        for (char c = 'a'; c <= 'h'; c++) {
            System.out.print(c + " ");
        }
        System.out.println();

        for (int i = 0; i < 8; i++) {
            System.out.print((8 - i) + "  ");

            for (int j = 0; j < 8; j++) {
                String square = board[7 - i][7 - j];

                if (Character.isLowerCase(square.charAt(0))) {
                    square = square.toLowerCase();
                } else {
                    square = square.toUpperCase();
                }

                if ((i + j) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + square +
                            EscapeSequences.RESET_BG_COLOR + " ");
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY + square +
                            EscapeSequences.RESET_BG_COLOR + " ");
                }
            }

            System.out.println("  " + (8 - i));
        }

        System.out.print("   ");
        for (char c = 'a'; c <= 'h'; c++) {
            System.out.print(c + " ");
        }
        System.out.println();
    }
}
