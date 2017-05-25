package com.kl.runawayrobot;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class CalculateMoves {
    Integer minNumOfMoves;
    Integer maxNumOfMoves;
    Board board;
    List<Point> pointsToCheck = new ArrayList<>();

    public String result = null;

    public CalculateMoves(Integer minNumOfMoves,Integer maxNumOfMoves, Board board) throws IOException, PuzzleNotSolvedException {
        this.minNumOfMoves = minNumOfMoves;
        this.maxNumOfMoves = maxNumOfMoves;
        this.board = board;
        generatePointsToCheck();
        checkGeneratedPoints();
    }

    private void checkGeneratedPoints() throws IOException, PuzzleNotSolvedException {
        Integer index = 0;
        for (Point point : pointsToCheck) {
            index++;
            System.out.write(("\rTesting sub board " + index.toString() + " of " + String.valueOf(pointsToCheck.size()) + "...").getBytes());
            generateMovesForPoint(point);
        }
        if (result == null) {
            throw new PuzzleNotSolvedException("Unable to find solution");
        }
    }

    private void generatePointsToCheck() {
        for (int i=minNumOfMoves; i<=maxNumOfMoves;i++) {
            for (int j=0;j<=i;j++) {
                if (board.board[j][i-j] == 1) {
                    if (isPointValidWhenLooped(new Point(j,i-j))) {
                        pointsToCheck.add(new Point(j, i - j));
                    }
                }
            }
        }
    }

    private boolean isPointValidWhenLooped(Point point) {
        while (point.getX() < board.board.length && point.getY() < board.board[0].length) {
            point.setLocation(point.getX()*2, point.getY()*2);
            if (point.getX() < board.board.length && point.getY() < board.board[0].length) {
                if (board.board[(int)point.getX()][(int)point.getY()] != 1) {
                    return false;
                }
            }
        }
        return true;
    }

    private void generateMovesForPoint(Point endPoint) {
        generateMovesForPoint("", endPoint, 0, 0, getStackedSubBoard(endPoint));
    }

    private void generateMovesForPoint(String move, Point endPoint, int Rcount, int Dcount, Integer[][] subBoard) {
        if (subBoard[Rcount][Dcount] == 1 && result == null) {
            if (Dcount < endPoint.getY()) {
                generateMovesForPoint(move + "D", endPoint, Rcount, Dcount + 1, subBoard);
            }
            if (Rcount < endPoint.getX()) {
                generateMovesForPoint(move + "R", endPoint, Rcount + 1, Dcount, subBoard);
            }
            if (Rcount == endPoint.getX() && Dcount == endPoint.getY()) {
                result = move;
            }
        }
    }

    private Integer[][] getStackedSubBoard(Point endPoint) {
        Map<Integer[][], Integer> subBoards = new HashMap<>();
        int looped = 0;
        while (endPoint.getX()*(looped) < board.board.length && endPoint.getY()*(looped) < board.board[0].length) {
            Integer[][] subBoard = new Integer[(int)endPoint.getX()+1][(int)endPoint.getY()+1];
            int countGoodSpots =0;
            for (int y = 0; y <= endPoint.getY(); y++) {
                for (int x = 0; x <= endPoint.getX(); x++) {
                    try {
                        subBoard[x][y] = board.board[x + ((int) endPoint.getX() * looped)][y + ((int) endPoint.getY() * looped)];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        break;
                    }
                    if (subBoard[x][y] == 1) {countGoodSpots++;}
                }
            }
            looped++;
            subBoards.put(subBoard, countGoodSpots);
        }
        Integer[][] stackedSubBoard = new Integer[(int)endPoint.getX()+1][(int)endPoint.getY()+1];

        subBoards.entrySet().stream().forEach(entry -> {
            Integer[][] boardToStack = entry.getKey();
            for (int y = 0; y < boardToStack[0].length; y++) {
                for (int x = 0; x < boardToStack.length; x++) {
                    if (boardToStack[x][y] != null && boardToStack[x][y] != 1) {
                        stackedSubBoard[x][y] = 3;
                    } else if (stackedSubBoard[x][y] == null){
                        stackedSubBoard[x][y] = 0;
                    }
                }
            }
        });

        BoardPreProcessor boardPreProcessor = new BoardPreProcessor();
        boardPreProcessor.preProcessBoard(stackedSubBoard);

        return stackedSubBoard;
    }

    protected class PuzzleNotSolvedException extends Exception {
        public PuzzleNotSolvedException() { super(); }
        public PuzzleNotSolvedException(String message) { super(message); }
    }
}