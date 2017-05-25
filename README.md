Hacker.org Runaway Robot Challenge
===================

Java application written to solve the Runaway Robot challenge found at http://www.hacker.org/runaway

How It Works
------------
If you found this looking for a solution but don't want to spoil the fun...don't read any further!

 - Download the level from hacker.org site, parse html and get game parameters
 - Create a 2d integer array from this and preprocess it to eliminate all impossible moves.
 - Find the possible "end points" based on the minimum and maximum number of moves (eg. if you have minimum 5 moves then (5,0), (4,1), (3,2), (2,3), (1,4) and (0,5) are all potential end points.
 - Check if the end points are at a bomb site.
 - Repeat this check for all multiples of these points, until reaching the edge of the board.
 - Get a "sub board" from the origin to each valid point and stack multiple sub boards on top of each other (eg (0,0) -> (5,5) and (5,5) -> (10,10) to the maximum number of multiple needed to reach the edge of the board.
 - Preprocess the result.
 - Loop through stacked sub boards to find one which has a valid path.
 - Return this path to hacker.org and get the next level.
