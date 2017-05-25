package com.kl.runawayrobot;

public class BoardPrinter {

    private Integer[][] board;

    public BoardPrinter(Board boardToPrint) {
        this.board = boardToPrint.board;
    }

    public BoardPrinter(Integer[][] board) {
        this.board = board;
    }

    public BoardPrinter() {}

    public void printBoard(Integer[][] board, boolean isSubBoard) {
        this.board = board;
        printBoard(false, isSubBoard);
    }

    public void printBoard(boolean includeBorders, boolean isSubBoard) {
        System.out.println();
        for (int col = 0; col < this.board[0].length; col++) {
            if (includeBorders) {printRowBorder();}
            for (Integer[] row : this.board) {
                if (isSubBoard && row[col] != null) {
                    switch (row[col]) {
                        case 1: //good path
                            printCell("\033[32m+\033[0m", includeBorders);
                            break;
                        default:
                            printCell("\033[31mx\033[0m", includeBorders);
                            break;
                    }
                } else {
                    if (row[col] != null) {
                        switch (row[col]) {
                            case 0: //not checked
                                printCell("o", includeBorders);
                                break;
                            case 1: //good path
                                printCell("\033[32m+\033[0m", includeBorders);
                                break;
                            case 2: //probably bad
                                printCell("\033[33m-\033[0m", includeBorders);
                                break;
                            case 3:
                                printCell("\033[31mx\033[0m", includeBorders);
                                break;
                        }
                    }
                }
            }
            if (includeBorders) {System.out.println("|");} else { System.out.println(); }
        }
        if (includeBorders) {printRowBorder();}
    }

    public void printCell(String cellContents, boolean includeBorders) {
        if (includeBorders) {
            System.out.print("| " + cellContents + " ");
        } else {
            System.out.print(cellContents);
        }


    }

    public void printRowBorder() {
        for (int i = 0; i < this.board.length; i++) {
            System.out.print("+---");
            if (i == this.board.length - 1) {
                System.out.println("+");
            }
        }
    }


}
