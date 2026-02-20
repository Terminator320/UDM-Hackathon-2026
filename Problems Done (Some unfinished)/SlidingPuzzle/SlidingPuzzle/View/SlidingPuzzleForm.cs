using System;
using System.Drawing;
using System.Windows.Forms;
using SlidingPuzzle.Controller;

namespace SlidingPuzzle.View
{
    public partial class SlidingPuzzleForm : Form
    {
        private const int TileSize = 70;
        private Button[,] buttons = new Button[6, 6];
        private PuzzleController controller = new PuzzleController();

        private Timer gameTimer = new Timer();
        private Timer solveTimer = new Timer();
        private int secondsElapsed = 0;

        private Label lblTime = new Label();
        private Label lblMoves = new Label();

        public SlidingPuzzleForm()
        {
            CreateUI();
            RefreshGrid();
        }

        private void CreateUI()
        {
            this.Text = "Sliding Puzzle 6x6";
            this.ClientSize = new Size(600, 550);

            // Tiles
            for (int r = 0; r < 6; r++)
            {
                for (int c = 0; c < 6; c++)
                {
                    Button btn = new Button
                    {
                        Size = new Size(TileSize, TileSize),
                        Location = new Point(c * TileSize + 10, r * TileSize + 10),
                        Tag = (r, c)
                    };
                    btn.Click += Tile_Click;
                    buttons[r, c] = btn;
                    Controls.Add(btn);
                }
            }

            // Reset
            Button btnReset = new Button { Text = "Reset", Location = new Point(10, 440) };
            btnReset.Click += (s, e) =>
            {
                StopTimer();
                solveTimer.Stop();
                controller.ResetGame();
                secondsElapsed = 0;
                RefreshGrid();
            };

            // Shuffle
            Button btnShuffle = new Button { Text = "Shuffle", Location = new Point(100, 440) };
            btnShuffle.Click += (s, e) =>
            {
                controller.ShuffleGame();
                secondsElapsed = 0;
                StartTimer();
                RefreshGrid();
            };

            // Solve
            Button btnSolve = new Button { Text = "Solve", Location = new Point(200, 440) };
            btnSolve.Click += (s, e) =>
            {
                foreach (var btn in buttons) btn.Enabled = false;
                controller.ComputeSolution();
                solveTimer.Start();
            };

            Controls.Add(btnReset);
            Controls.Add(btnShuffle);
            Controls.Add(btnSolve);

            // Labels
            lblTime.Location = new Point(350, 440);
            lblMoves.Location = new Point(350, 470);
            Controls.Add(lblTime);
            Controls.Add(lblMoves);

            // Timer
            gameTimer.Interval = 1000;
            gameTimer.Tick += (s, e) =>
            {
                secondsElapsed++;
                UpdateLabels();
            };

            solveTimer.Interval = 50; // animation speed
            solveTimer.Tick += SolveTimer_Tick;
        }

        private void Tile_Click(object sender, EventArgs e)
        {
            var (r, c) = ((int, int))((Button)sender).Tag;
            if (controller.TryMove(r, c))
            {
                RefreshGrid();
                if (controller.Board.IsSolved())
                {
                    StopTimer();
                    MessageBox.Show($"🎉 Puzzle solved in {secondsElapsed} seconds and {controller.MoveCount} moves!");
                }
            }
        }

        private void RefreshGrid()
        {
            for (int r = 0; r < 6; r++)
            {
                for (int c = 0; c < 6; c++)
                {
                    int val = controller.Board.Grid[r, c];
                    Button btn = buttons[r, c];
                    btn.Text = val == 0 ? "" : val.ToString();
                    btn.Visible = val != 0;
                }
            }
            UpdateLabels();
        }

        private void UpdateLabels()
        {
            lblTime.Text = $"Time: {secondsElapsed}s";
            lblMoves.Text = $"Moves: {controller.MoveCount}";
        }

        private void StartTimer() => gameTimer.Start();
        private void StopTimer() => gameTimer.Stop();

        private void SolveTimer_Tick(object sender, EventArgs e)
        {
            bool moved = controller.SolveStep();
            RefreshGrid();

            if (!moved || controller.Board.IsSolved())
            {
                solveTimer.Stop();
                foreach (var btn in buttons) btn.Enabled = true;

                if (controller.Board.IsSolved())
                    MessageBox.Show($"🎉 Puzzle solved in {secondsElapsed} seconds and {controller.MoveCount} moves!");
            }
        }
    }
}
