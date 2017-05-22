package com.kl.runawayrobot;

import java.util.List;
import java.util.Set;

/**
 * Created by Kev on 20/05/2017.
 */
public class TestMoves {

    private final Board board;

    public TestMoves(Board board) {
        this.board = board;
    }

    public Boolean testMove(String move) {
        System.out.print ("\nTesting " + move + "...");
        board.resetPosition();
        try {
            while (board.position.getX() < board.board.length && board.position.getY() < board.board[0].length) {
                for (char moveItem : move.toCharArray()) {
                    if (board.position.getX() == board.board.length || board.position.getY() == board.board[0].length) {
                        break;
                    }
                    switch (moveItem) {
                        case 'D':
                            if (board.canIMoveDown()) {
                                board.moveDown();
                            } else {
                                throw new Exception();
                            }
                            break;
                        case 'R':
                            if (board.canIMoveRight()) {
                                board.moveRight();
                            } else {
                                throw new Exception();
                            }
                            break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Failed");
            return false;
        }
        System.out.println("Success!");
        return true;
    }

    public String testMoves(Set<String> moves) {
        for (String move: moves) {
            if (testMove(move)) {
                return move;
            }
        }
        return null;
    }
}
