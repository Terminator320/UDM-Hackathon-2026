using System;
using System.Collections.Generic;

namespace SlidingPuzzle.Model
{
    public class PuzzleBoard
    {
        public const int Size = 6;
        public int[,] Grid { get; private set; }
        public int EmptyRow { get; private set; }
        public int EmptyCol { get; private set; }

        public void SetEmptyPosition(int row, int col)
        {
            EmptyRow = row;
            EmptyCol = col;
        }

        public PuzzleBoard()
        {
            Grid = new int[Size, Size];
            Reset();
        }

        public void Reset()
        {
            int value = 1;
            for (int r = 0; r < Size; r++)
            {
                for (int c = 0; c < Size; c++)
                {
                    if (r == Size - 1 && c == Size - 1)
                    {
                        Grid[r, c] = 0;
                        EmptyRow = r;
                        EmptyCol = c;
                    }
                    else
                        Grid[r, c] = value++;
                }
            }
        }

        public bool MoveTile(int row, int col)
        {
            if (Math.Abs(row - EmptyRow) + Math.Abs(col - EmptyCol) != 1)
                return false;

            Grid[EmptyRow, EmptyCol] = Grid[row, col];
            Grid[row, col] = 0;
            EmptyRow = row;
            EmptyCol = col;
            return true;
        }

        public bool IsSolved()
        {
            int expected = 1;
            for (int r = 0; r < Size; r++)
            {
                for (int c = 0; c < Size; c++)
                {
                    if (r == Size - 1 && c == Size - 1)
                        return Grid[r, c] == 0;
                    if (Grid[r, c] != expected++)
                        return false;
                }
            }
            return true;
        }

        public void Shuffle(int moves = 300)
        {
            Random rnd = new Random();
            for (int i = 0; i < moves; i++)
            {
                var neighbors = GetMovableTiles();
                var (r, c) = neighbors[rnd.Next(neighbors.Count)];
                MoveTile(r, c);
            }
        }

        public List<(int, int)> GetMovableTiles()
        {
            var list = new List<(int, int)>();
            if (EmptyRow > 0) list.Add((EmptyRow - 1, EmptyCol));
            if (EmptyRow < Size - 1) list.Add((EmptyRow + 1, EmptyCol));
            if (EmptyCol > 0) list.Add((EmptyRow, EmptyCol - 1));
            if (EmptyCol < Size - 1) list.Add((EmptyRow, EmptyCol + 1));
            return list;
        }

        public int[,] CloneGrid()
        {
            int[,] clone = new int[Size, Size];
            Array.Copy(Grid, clone, Grid.Length);
            return clone;
        }

        public void SwapTiles(int r1, int c1, int r2, int c2)
        {
            int temp = Grid[r1, c1];
            Grid[r1, c1] = Grid[r2, c2];
            Grid[r2, c2] = temp;
        }
    }
}