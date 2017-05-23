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

        preProcessBoard(0,0);

        if (print) {
            System.out.println("Processed Board:");
            printBoard(false);
        }

        //removeBombsNotNeededToCheck();


    }

    private void removeBombsNotNeededToCheck() {
        boolean removeBomb = true,removeBombX1 = true, removeBombX2 = true,removeBombY1 = true, removeBombY2 = true;
        for (Iterator<Point> it = bombs.iterator(); it.hasNext();) {
            removeBomb = true;
            removeBombX1 = true;
            removeBombX2 = true;
            removeBombY1 = true;
            removeBombY2 = true;
            Point bomb = it.next();
            for (int x =0;x < bomb.getX();x++) {
                if (board[x][(int)bomb.getY()] == 1) {
                    removeBombX1 = false;
                    break;
                }
            }
            for (int x = (int)bomb.getX()+1;x < board.length;x++) {
                if (board[x][(int)bomb.getY()] == 1) {
                    removeBombX2 = false;
                    break;
                }
            }

            for (int y =0;y < bomb.getY();y++) {
                if (board[(int)bomb.getX()][y] == 1) {
                    removeBombY1 = false;
                    break;
                }
            }
            for (int y =(int)bomb.getY()+1;y < board[0].length;y++) {
                if (board[(int)bomb.getX()][y] == 1) {
                    removeBombY2 = false;
                    break;
                }
            }
            if ((!removeBombX1 && !removeBombX2) || (!removeBombY1 && !removeBombY2)) {
                it.remove();
                continue;
            }
        }

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
        if (position.getY() == board[0].length-1) {
            return true;
        } else if (board[(int)position.getX()][(int)position.getY() + 1] < 2) {
            return true;
        }
        return false;
    }

    public Boolean canIMoveRight() {
        if (position.getX() == board.length-1) {
            return true;
        } else if (board[(int)position.getX() + 1][(int)position.getY()] < 2) {
            return true;
        }
        return false;
    }

    public void printBoard(boolean includeBorders) {
        BoardPrinter boardPrinter = new BoardPrinter(this);
        boardPrinter.printBoard(includeBorders);
    }
}
