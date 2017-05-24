package com.kl.runawayrobot;

/**
 * Created by Kev on 24/05/2017.
 */
public class BoardPreProcessor {

    private Integer[][] board;

    public BoardPreProcessor() {

    }

    public Integer[][] preProcessBoard(Integer[][] board) {
        this.board = board;
        preProcessBoard(0,0);
        return board;
    }

    private boolean preProcessBoard(int x, int y) {
        if (x == board.length || y == board[0].length) {
            return true;
        } else if (board[x][y] == 1) {
            return true;
        } else if (board[x][y] > 1) {
            return false;
        } else {
            boolean canIGoDown, canIGoRight;
            board[x][y] = 1;
            canIGoRight = preProcessBoard(x+1,y);
            canIGoDown = preProcessBoard(x,y+1);
            if (!canIGoRight && !canIGoDown) {
                board[x][y] = 2;
                return false;
            }
            return true;
        }
    }
}
