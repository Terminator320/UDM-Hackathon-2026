using Animation.Model;
using System.Drawing;
using System.Windows.Forms;

namespace Animation.View
{
    public class CarPanelView : Panel
    {
        public Car CarModel { get; set; }

        private readonly Rectangle trackRect = new Rectangle(50, 150, 400, 100);

        public CarPanelView()
        {
            DoubleBuffered = true;
            Paint += CarPanelView_Paint;
        }

        public Rectangle GetTrackRect() => trackRect;

        private void CarPanelView_Paint(object sender, PaintEventArgs e)
        {
            Graphics g = e.Graphics;
            g.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;
            g.Clear(Color.White);

            // Track
            g.FillRectangle(Brushes.LightGray, trackRect);
            g.DrawRectangle(Pens.Black, trackRect);

            if (CarModel == null) return;

            // Clip car inside track
            g.SetClip(trackRect);

            // Car body
            g.FillRectangle(
                Brushes.Blue,
                CarModel.X,
                CarModel.Y,
                CarModel.Width,
                CarModel.Height
            );

            int wheelRadius = CarModel.Height / 4;
            int wheelY = CarModel.Y + CarModel.Height;

            DrawWheel(g, CarModel.X + 10, wheelY, wheelRadius, CarModel.WheelRotation);
            DrawWheel(g, CarModel.X + CarModel.Width - 10, wheelY, wheelRadius, CarModel.WheelRotation);

            g.ResetClip();
        }

        private void DrawWheel(Graphics g, int cx, int cy, int r, float rotation)
        {
            g.FillEllipse(Brushes.Black, cx - r, cy - r, r * 2, r * 2);

            var oldTransform = g.Transform;
            g.TranslateTransform(cx, cy);
            g.RotateTransform(rotation);

            int spokes = 6;
            for (int i = 0; i < spokes; i++)
            {
                g.RotateTransform(360f / spokes);
                g.DrawLine(Pens.White, 0, 0, r, 0);
            }

            g.Transform = oldTransform;
        }
    }
}
