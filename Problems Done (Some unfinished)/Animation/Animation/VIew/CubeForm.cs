using Animation.Controller;
using Animation.View;
using System.Drawing;
using System.Windows.Forms;

namespace Animation
{
    public class CubeForm : Form
    {
        private CubePanelView cubePanel;
        private Button btnStart;
        private Button btnStop;
        private CubeController controller;

        public CubeForm()
        {
            Text = "3D Cube Animation - MVC";
            Size = new Size(600, 600);
            BackColor = Color.FromArgb(30, 30, 30);

            cubePanel = new CubePanelView
            {
                Location = new Point(20, 20),
                Size = new Size(540, 480),
                BorderStyle = BorderStyle.FixedSingle
            };

            btnStart = new Button
            {
                Text = "Start Animation",
                Location = new Point(180, 520),
                Size = new Size(120, 35),
                BackColor = Color.FromArgb(0, 120, 215),
                ForeColor = Color.White,
                FlatStyle = FlatStyle.Flat
            };
            btnStart.FlatAppearance.BorderSize = 0;

            btnStop = new Button
            {
                Text = "Stop Animation",
                Location = new Point(310, 520),
                Size = new Size(120, 35),
                BackColor = Color.FromArgb(180, 50, 50),
                ForeColor = Color.White,
                FlatStyle = FlatStyle.Flat,
                Enabled = false
            };
            btnStop.FlatAppearance.BorderSize = 0;

            controller = new CubeController(cubePanel);

            btnStart.Click += (s, e) =>
            {
                controller.Start();
                btnStart.Enabled = false;
                btnStop.Enabled = true;
            };

            btnStop.Click += (s, e) =>
            {
                controller.Stop();
                btnStart.Enabled = true;
                btnStop.Enabled = false;
            };

            Controls.Add(cubePanel);
            Controls.Add(btnStart);
            Controls.Add(btnStop);
        }
    }
}