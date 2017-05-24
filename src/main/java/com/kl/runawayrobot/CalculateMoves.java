package com.kl.runawayrobot;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.kl.runawayrobot.CalculateMoves.Move.NOT_CHECKED;
import static com.kl.runawayrobot.CalculateMoves.Move.SUCCEEDED;

/**
 * Created by Kev on 19/05/2017.
 */
public class CalculateMoves {
    AtomicInteger numberOfPossibleRemaining;
    AtomicInteger prevNumberOfPossibleRemainingPrinted;
    Integer minNumOfMoves;
    Integer maxNumOfMoves;
    Board board;
    Set<String> allPossibleMoves = new HashSet<>();
    ConcurrentHashMap<String,Move> availableMoves = new ConcurrentHashMap<>();

    List<Point> pointsToCheck = new ArrayList<>();

    TestMoves test;
    private boolean stopThreads = false;
    public String result = null;

    public enum Move {
        NOT_CHECKED, FAILED, SUCCEEDED;
    }

    public CalculateMoves(Integer minNumOfMoves,Integer maxNumOfMoves, Board board, Board testingBoard) throws IOException, InterruptedException {
        this.minNumOfMoves = minNumOfMoves;
        this.maxNumOfMoves = maxNumOfMoves;
        this.board = board;
        this.test = new TestMoves(testingBoard);
        this.numberOfPossibleRemaining = new AtomicInteger(0);
        generatePointsToCheck();
        try {
            checkGeneratedPoints();
        } catch (PuzzleSolvedException e) {
            this.result = e.getMessage();
        }
    }

    private void checkGeneratedPoints() throws InterruptedException, PuzzleSolvedException, IOException {
        /*int THREAD_COUNT = 8;
        ThreadPoolExecutor pool = new ThreadPoolExecutor(THREAD_COUNT, THREAD_COUNT, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

        pointsToCheck.forEach(point -> pool.execute(new Runnable() {
            public void run() {
                generateMovesForPoint(point);
                try {
                    removeImpossibleMoves();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }));
        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);*/
        for (Point point : pointsToCheck) {
            generateMovesForPoint(point);
            this.numberOfPossibleRemaining.set(availableMoves.size());
            this.prevNumberOfPossibleRemainingPrinted = new AtomicInteger(numberOfPossibleRemaining.get());
            stopThreads = false;
            //if (availableMoves.size() > 200000) {removeImpossibleMoves();}
            /*System.out.println();
            System.out.print("Testing " + numberOfPossibleRemaining.toString() + " moves...");
            String result = testRemainingPossible();
            if (result != null) {
                System.out.println("Success: " + result);
                throw new PuzzleSolvedException(result);
            } else {
                System.out.println("Failed");
            }*/
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
        generateMovesForPoint("", endPoint, 0, 0, getSubBoard(endPoint));
    }

    private void generateMovesForPoint(String move, Point endPoint, int Rcount, int Dcount, Integer[][] subBoard) {
        if (subBoard[Rcount][Dcount] == 1) {
            if (Dcount < endPoint.getY()) {
                generateMovesForPoint(move + "D", endPoint, Rcount, Dcount + 1, subBoard);
            }
            if (Rcount < endPoint.getX()) {
                generateMovesForPoint(move + "R", endPoint, Rcount + 1, Dcount, subBoard);
            }
            if (Rcount == endPoint.getX() && Dcount == endPoint.getY()) {
                this.availableMoves.put(move, NOT_CHECKED);
            }
        }
    }

    private Integer[][] getSubBoard(Point endPoint) {
        Map<Integer[][], Integer> subBoards = new HashMap<>();
        int looped = 0;
        while (endPoint.getX()*(looped+1) < board.board.length && endPoint.getY()*(looped+1) < board.board[0].length) {
            Integer[][] subBoard = new Integer[(int)endPoint.getX()+1][(int)endPoint.getY()+1];
            int countGoodSpots =0;
            for (int y = 0; y <= endPoint.getY(); y++) {
                for (int x = 0; x <= endPoint.getX(); x++) {
                    subBoard[x][y] = board.board[x+((int)endPoint.getX()*looped)][y+((int)endPoint.getY()*looped)];
                    if (subBoard[x][y] == 1) {countGoodSpots++;}
                }
            }
            looped++;
            subBoards.put(subBoard, countGoodSpots);
        }
        int min = Collections.min(subBoards.values());
        Optional<Map.Entry<Integer[][], Integer>> bestBoard = subBoards.entrySet().stream().filter(entry -> entry.getValue() == min).findFirst();
        return bestBoard.get().getKey();
    }


    private void generateAvailableMoves(String startString, Integer Rcount, Integer Dcount) {
        for (int i = minNumOfMoves;i<=maxNumOfMoves;i++) {
            if (startString.length() != maxNumOfMoves) {
                if (board.board[Rcount][Dcount] == 1) {
                    if (startString.length() + 1 >= minNumOfMoves) {
                        this.availableMoves.put(startString + "D", NOT_CHECKED);
                        this.availableMoves.put(startString + "R", NOT_CHECKED);
                        numberOfPossibleRemaining.addAndGet(2);
                    }
                    generateAvailableMoves(startString + "D", Rcount, Dcount + 1);
                    generateAvailableMoves(startString + "R", Rcount + 1, Dcount);
                } /*else {
                    if (startString.length()+1 >= minNumOfMoves) {

                        this.availableMoves.put(startString + "D", FAILED);
                        this.availableMoves.put(startString + "R", FAILED);
                    }
                }*/
            }
        }
    }

    private void removeImpossibleMoves() throws IOException, InterruptedException {
        ThreadPoolExecutor pool = new ThreadPoolExecutor(8, 8, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        int threadCount = board.bombs.size();

        for (int i=0;i<threadCount;i++) {
            Point bomb = board.bombs.get(i);
            pool.execute(new Runnable() { public void run() {
                StringBuilder sb = new StringBuilder();
                for (int j=0;j<bomb.getY();j++) {
                    sb.append("D");
                }
                for (int j=0;j<bomb.getX();j++) {
                    sb.append("R");
                }
                setMovesFailed(sb.toString());
            }});
        }
        pool.shutdown();
        pool.awaitTermination(30,TimeUnit.SECONDS);
    }

    public String calculatePossible(String moves, Boolean testing) {
        if (board.position.getX() < board.board.length && board.position.getY() < board.board[0].length && moves.length() < maxNumOfMoves & notChecked(moves)) {
            try {
                String result = notChecked(moves+"D") ? checkNextMove(moves, "") : checkNextMove(moves, "D");
                moves += result;
                if (moves.length() >= minNumOfMoves) {
                    setMoveSucceeded(moves);
                    allPossibleMoves.add(moves);
                    if (testing) {
                        if (test.testMove(moves)) {
                            return "PASSED-" + moves;
                        }
                    }
                }
                moves = calculatePossible(moves, testing);
            } catch (NoFurtherMovesException e) {
                return moves;
            }
            return moves;
        } else {
            return moves;
        }
    }

    private void setMovesFailed(String moveToFind) {

        for (Iterator<Map.Entry<String, Move>> it = availableMoves.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, Move> entry = it.next();
            if (!stopThreads) {
                char[] a = moveToFind.toCharArray();
                String temp = entry.getKey();
                String loopedMove = temp;
                for (double i = 0; i < Math.ceil((double) moveToFind.length() / temp.length()); i++) {
                    loopedMove = loopedMove + temp;
                }

                char[] b = loopedMove.substring(0, moveToFind.length()).toCharArray();

                Arrays.sort(a);
                Arrays.sort(b);

                if (Arrays.equals(a, b)) {
                    it.remove();
                    numberOfPossibleRemaining.decrementAndGet();
                    try {
                        if (numberOfPossibleRemaining.get() < (prevNumberOfPossibleRemainingPrinted.get() - 1000)) {
                            System.out.write(("\rRemaining Possible Moves: " + String.valueOf(numberOfPossibleRemaining)).getBytes());
                            prevNumberOfPossibleRemainingPrinted.set(numberOfPossibleRemaining.get());
                        }
                        if (numberOfPossibleRemaining.get() < 500000) {
                            System.out.write(("\rRemaining Possible Moves: " + String.valueOf(numberOfPossibleRemaining)).getBytes());
                            prevNumberOfPossibleRemainingPrinted.set(numberOfPossibleRemaining.get());
                            stopThreads = true;
                        }
                    } catch (IOException e) {}
                    //entry.setValue(Move.FAILED);
                }
            } else {
                break;
            }
        }
    }

    private boolean notChecked(String moveToFind) {
        return availableMoves.entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith(moveToFind))
                .anyMatch(entry -> entry.getValue() == Move.NOT_CHECKED);
    }

    private void setMoveSucceeded(String moveToFind) {
        availableMoves.put(moveToFind, SUCCEEDED);
    }

    public String checkNextMove(String currentMove, String alreadyTried) throws NoFurtherMovesException {
        if (alreadyTried.equals("")) {
            if (board.canIMoveDown()) {
                board.moveDown();
                return "D";
            } else {
                setMovesFailed(currentMove + "D");
            }
        }

        if (alreadyTried.equals("") || alreadyTried.equals("D")) {
            if (board.canIMoveRight()){
                board.moveRight();
                return "R";
            } else {
                setMovesFailed(currentMove + "R");
            }
        }

        throw new NoFurtherMovesException("Unable to move further");
    }

    public Set<String> calculateAllPossible() {
        System.out.println("Looking for possible combinations...");
        String result = "";
        while (!notChecked("")) {
            board.resetPosition();
            result = calculatePossible("", false);
        }
        return allPossibleMoves;
    }

    public String calculateAllPossibleAndTest() {
        if (availableMoves.entrySet()
                .stream()
                .filter(entry -> entry.getValue() == Move.NOT_CHECKED).count() < 100) {
            return testRemainingPossible();
        }
        System.out.println("Looking for possible combinations...");
        String result = "";
        while (!result.startsWith("PASSED")) {
            board.resetPosition();
            result = calculatePossible("", true);
        }
        return result.split("PASSED-")[1];
    }

    public String testRemainingPossible() {
        Iterator it = availableMoves.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Move> moveToTest = (Map.Entry)it.next();
            if (test.testMove(moveToTest.getKey())) {
                return moveToTest.getKey();
            } else {
                it.remove();
            }
        }
        return null;
    }

    public class NoFurtherMovesException extends Exception {
        public NoFurtherMovesException() { super(); }
        public NoFurtherMovesException(String message) { super(message); }
    }

    private class PuzzleSolvedException extends Exception {
        public PuzzleSolvedException() { super(); }
        public PuzzleSolvedException(String message) { super(message); }
    }
}