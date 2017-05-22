package com.kl.runawayrobot;

/**
 * Created by Kev on 19/05/2017.
 */
public class BoardPrinter {

    private Integer[][] board;

    public BoardPrinter(Board boardToPrint) {
        this.board = boardToPrint.board;
    }

    public void printBoard() {
        System.out.println();
        for (int col = 0; col < this.board[0].length; col++) {
            printRowBorder();
            for (int row = 0; row < this.board.length; row++) {
                switch (board[row][col]) {
                    case 0: //not checked
                        printCell("o");
                        break;
                    case 1: //good path
                        printCell("\033[32m+\033[0m");
                        break;
                    case 2: //probably bad
                        printCell("\033[33m-\033[0m");
                        break;
                    case 3:
                        printCell("\033[31;1mx\033[0m");;
                        break;
                }

            }
            System.out.println("|");
        }
        printRowBorder();
    }

    public void printCell(String cellContents) {
        System.out.print("| " + cellContents + " ");


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
