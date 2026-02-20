using Animation.Controller;
using Animation.View;
using System;
using System.Drawing;
using System.Windows.Forms;

namespace Animation
{
    public class SmileyForm: Form
    {
        private SmileyPanelView smileyPanel;
        private Button btnStart;

        private SmileyController controller;

        public SmileyForm()
        {
            this.Text = "Spinning Smiley MVC";
            this.Size = new Size(500, 500);

            // Smiley panel
            smileyPanel = new SmileyPanelView
            {
                Location = new Point(50, 50),
                Size = new Size(400, 400),
                BorderStyle = BorderStyle.FixedSingle
            };
            this.Controls.Add(smileyPanel);

            // Start button
            btnStart = new Button
            {
                Text = "Start Spinning",
                Location = new Point(200, 10),
                Size = new Size(100, 30)
            };
            btnStart.Click += BtnStart_Click;
            this.Controls.Add(btnStart);

            // Controller
            controller = new SmileyController(smileyPanel);
        }

        private void BtnStart_Click(object sender, EventArgs e)
        {
            controller.Start();
        }
    }
}
