package com.kl.runawayrobot;


import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Board {

    Integer[][] board;
    Point position = new Point(0,0);
    final String EDGE = "|";
    final String JOIN = "+";
    List<Point> bombs = new ArrayList<>();

    /*
    0 = Not used
    1 = Good path
    2 = Probably bad
    3 = Bomb
    */


    public Board(String fvTerrainString, Integer fvBoardX, Integer fvBoardY, Boolean print) {
        this.position.setLocation(0,0);
        this.board = new Integer[fvBoardX][fvBoardY];
        for (int y = 0; y < fvBoardY; y++) {
            String row = fvTerrainString.substring(y * fvBoardX, (y * fvBoardX) + fvBoardX);
            int x = 0;
            for (Character character : row.toCharArray()) {
                if (character.equals('.')) {
                    this.board[x][y] = 0;
                } else {
                    this.board[x][y] = 3;
                    bombs.add(new Point(x,y));
                }
                x++;
            }
        }

        if (print) {
            System.out.println("Unprocessed Board:");
            printBoard(false);
        }

        BoardPreProcessor boardPreProcessor = new BoardPreProcessor();
        boardPreProcessor.preProcessBoard(board);

        if (print) {
            System.out.println("Processed Board:");
            printBoard(false);
        }

    }

    public void resetPosition() {
        this.position.setLocation(0,0);
    }

    public void moveDown() {
        this.position.setLocation(this.position.getX(),this.position.getY()+1);
    }

    public void moveRight() {
        this.position.setLocation(this.position.getX()+1,this.position.getY());
    }

    public Boolean canIMoveDown() {
        return canIMoveDown(position);
    }

    public Boolean canIMoveDown(Point point) {
        if (point.getY() == board[0].length-1) {
            return true;
        } else if (board[(int)point.getX()][(int)point.getY() + 1] < 2) {
            return true;
        }
        return false;
    }

    public Boolean canIMoveRight() {
        return canIMoveRight(position);
    }

    public Boolean canIMoveRight(Point point) {
        if (point.getX() == board.length-1) {
            return true;
        } else if (board[(int)point.getX() + 1][(int)point.getY()] < 2) {
            return true;
        }
        return false;
    }

    public void printBoard(boolean includeBorders) {
        BoardPrinter boardPrinter = new BoardPrinter(this);
        boardPrinter.printBoard(includeBorders, false);
    }
}
