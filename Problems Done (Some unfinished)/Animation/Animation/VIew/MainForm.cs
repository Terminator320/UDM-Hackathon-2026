using Animation.Controller;
using Animation.model;
using Animation.VIew;
using System;
using System.Drawing;
using System.Windows.Forms;

namespace Animation
{
    public partial class MainForm : Form
    {
        private DrawingPanelView drawingPanel;
        private Button btnAnimation1;
        private Button btnAnimation2;
        private Button btnAnimation3;
        private AnimationController controller;

        public MainForm()
        {
            this.Text = "Animation Challenge";
            this.Size = new Size(600, 500);

            controller = new AnimationController();

            // Drawing panel
            drawingPanel = new DrawingPanelView
            {
                Location = new Point(50, 50),
                Size = new Size(400, 400),
                BorderStyle = BorderStyle.FixedSingle
            };
            this.Controls.Add(drawingPanel);

            // Buttons
            btnAnimation1 = new Button { Text = "Animation 1", Location = new Point(470, 50), Size = new Size(100, 30) };
            btnAnimation2 = new Button { Text = "Animation 2", Location = new Point(470, 100), Size = new Size(100, 30) };
            btnAnimation3 = new Button { Text = "Animation 3", Location = new Point(470, 150), Size = new Size(100, 30) };

            this.Controls.Add(btnAnimation1);
            this.Controls.Add(btnAnimation2);
            this.Controls.Add(btnAnimation3);

            btnAnimation1.Click += BtnAnimation1_Click;
            btnAnimation2.Click += BtnAnimation2_Click;
            btnAnimation3.Click += BtnAnimation3_Click;
        }

        private void BtnAnimation1_Click(object sender, EventArgs e)
        {
            int size = 200;
            int x = (drawingPanel.Width - size) / 2;
            int y = (drawingPanel.Height - size) / 2;

            // Create square with no fill
            drawingPanel.SquareModel = new Square(x, y, size);

            // Create a timer to repaint 24 FPS indefinitely
            Timer animTimer = controller.GetAnimationTimer();
            animTimer.Tick += (s, ev) => drawingPanel.Invalidate();
            controller.StartAnimation1();
        }

        private void BtnAnimation2_Click(object sender, EventArgs e)
        {
            if (!controller.Animation1Started)
            {
                MessageBox.Show("You must start Animation 1 first!");
                return;
            }

            if (drawingPanel.SquareModel != null)
            {
                drawingPanel.SquareModel.FillColor = Color.LightGreen;
            }
            drawingPanel.Invalidate();

            // Remove fill after 5 seconds
            Timer removeTimer = new Timer();
            removeTimer.Interval = 5000;
            removeTimer.Tick += (s, ev) =>
            {
                if (drawingPanel.SquareModel != null)
                {
                    drawingPanel.SquareModel.FillColor = null;
                    drawingPanel.Invalidate();
                }
                removeTimer.Stop();
            };
            controller.StartAnimationWithDuration(removeTimer);
        }

        private void BtnAnimation3_Click(object sender, EventArgs e)
        {
            if (!controller.Animation1Started)
            {
                MessageBox.Show("You must start Animation 1 first!");
                return;
            }

            if (drawingPanel.SquareModel != null)
            {
                drawingPanel.SquareModel.FillColor = Color.Red;
            }
            drawingPanel.Invalidate();

            // Remove fill after 5 seconds
            Timer removeTimer = new Timer();
            removeTimer.Interval = 5000;
            removeTimer.Tick += (s, ev) =>
            {
                if (drawingPanel.SquareModel != null)
                {
                    drawingPanel.SquareModel.FillColor = null;
                    drawingPanel.Invalidate();
                }
                removeTimer.Stop();
            };
            controller.StartAnimationWithDuration(removeTimer);
        }
    }
}
