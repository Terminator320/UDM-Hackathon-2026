using Animation.Controller;
using Animation.View;
using System.Drawing;
using System.Windows.Forms;

namespace Animation
{
    public class CarForm : Form
    {
        private readonly CarPanelView carPanel;
        private readonly Button btnStart;
        private readonly CarController controller;

        public CarForm()
        {
            Text = "Car Animation (MVC)";
            Size = new Size(550, 420);

            carPanel = new CarPanelView
            {
                Location = new Point(20, 20),
                Size = new Size(500, 300),
                BorderStyle = BorderStyle.FixedSingle
            };

            btnStart = new Button
            {
                Text = "Start Car",
                Location = new Point(220, 330),
                Size = new Size(100, 30)
            };

            controller = new CarController(carPanel);
            btnStart.Click += (s, e) => controller.Start();

            Controls.Add(carPanel);
            Controls.Add(btnStart);
        }
    }
}
