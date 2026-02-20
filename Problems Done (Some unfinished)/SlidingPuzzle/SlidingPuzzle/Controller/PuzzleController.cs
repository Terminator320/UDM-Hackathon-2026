using System;
using System.Collections.Generic;
using System.Linq;
using SlidingPuzzle.Model;

namespace SlidingPuzzle.Controller
{
    public class PuzzleController
    {
        public PuzzleBoard Board { get; private set; }
        public int MoveCount { get; private set; }

        private Queue<(int r, int c)> solutionMoves = new Queue<(int r, int c)>();

        public PuzzleController()
        {
            Board = new PuzzleBoard();
        }

        public bool TryMove(int row, int col)
        {
            bool moved = Board.MoveTile(row, col);
            if (moved) MoveCount++;
            return moved;
        }

        public void ResetGame()
        {
            Board.Reset();
            MoveCount = 0;
            solutionMoves.Clear();
        }

        public void ShuffleGame()
        {
            Board.Reset();
            Board.Shuffle();
            MoveCount = 0;
            solutionMoves.Clear();
        }

        // Simple greedy solver - moves tiles toward their goal positions
        public void ComputeSolution()
        {
            solutionMoves.Clear();

            // Create working copy
            int[,] grid = Board.CloneGrid();
            int emptyR = Board.EmptyRow;
            int emptyC = Board.EmptyCol;

            // Try to solve using greedy approach with limited iterations
            int maxMoves = 5000;
            int moveCount = 0;

            while (!IsSolved(grid) && moveCount < maxMoves)
            {
                moveCount++;

                // Find the best move - one that reduces total distance
                var possibleMoves = GetPossibleMoves(emptyR, emptyC);

                if (possibleMoves.Count == 0)
                    break;

                // Calculate distance for each possible move
                int bestDistance = int.MaxValue;
                (int r, int c) bestMove = possibleMoves[0];

                foreach (var move in possibleMoves)
                {
                    // Try this move temporarily
                    int[,] tempGrid = CloneGrid(grid);
                    int tempEmptyR = emptyR;
                    int tempEmptyC = emptyC;

                    tempGrid[tempEmptyR, tempEmptyC] = tempGrid[move.r, move.c];
                    tempGrid[move.r, move.c] = 0;

                    int distance = CalculateTotalDistance(tempGrid);

                    if (distance < bestDistance)
                    {
                        bestDistance = distance;
                        bestMove = move;
                    }
                }

                // Make the best move
                solutionMoves.Enqueue(bestMove);
                grid[emptyR, emptyC] = grid[bestMove.r, bestMove.c];
                grid[bestMove.r, bestMove.c] = 0;
                emptyR = bestMove.r;
                emptyC = bestMove.c;
            }
        }

        private bool IsSolved(int[,] grid)
        {
            int expected = 1;
            for (int r = 0; r < 6; r++)
            {
                for (int c = 0; c < 6; c++)
                {
                    if (r == 5 && c == 5)
                        return grid[r, c] == 0;
                    if (grid[r, c] != expected++)
                        return false;
                }
            }
            return true;
        }

        private List<(int r, int c)> GetPossibleMoves(int emptyR, int emptyC)
        {
            var moves = new List<(int r, int c)>();

            if (emptyR > 0) moves.Add((emptyR - 1, emptyC));
            if (emptyR < 5) moves.Add((emptyR + 1, emptyC));
            if (emptyC > 0) moves.Add((emptyR, emptyC - 1));
            if (emptyC < 5) moves.Add((emptyR, emptyC + 1));

            return moves;
        }

        private int CalculateTotalDistance(int[,] grid)
        {
            int totalDistance = 0;

            for (int r = 0; r < 6; r++)
            {
                for (int c = 0; c < 6; c++)
                {
                    int value = grid[r, c];
                    if (value != 0)
                    {
                        int goalR = (value - 1) / 6;
                        int goalC = (value - 1) % 6;
                        totalDistance += Math.Abs(r - goalR) + Math.Abs(c - goalC);
                    }
                }
            }

            return totalDistance;
        }

        private int[,] CloneGrid(int[,] grid)
        {
            int[,] clone = new int[6, 6];
            Array.Copy(grid, clone, grid.Length);
            return clone;
        }

        public bool SolveStep()
        {
            if (solutionMoves.Count == 0)
                return false;

            var (r, c) = solutionMoves.Dequeue();
            bool moved = Board.MoveTile(r, c);
            if (moved)
                MoveCount++;

            return solutionMoves.Count > 0;
        }
    }
}