using Animation.Model;
using System.Drawing;
using System.Windows.Forms;

namespace Animation.View
{
    public class SmileyPanelView : Panel
    {
        public Smiley SmileyModel { get; set; }

        public SmileyPanelView()
        {
            this.DoubleBuffered = true; 
            this.Paint += SmileyPanelView_Paint;
        }

        private void SmileyPanelView_Paint(object sender, PaintEventArgs e)
        {
            Graphics g = e.Graphics;
            g.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;
            g.Clear(Color.White);

            if (SmileyModel == null) return;

            // Save current transform
            var oldTransform = g.Transform;

            // Rotate around center of smiley
            g.TranslateTransform(SmileyModel.X, SmileyModel.Y);
            g.RotateTransform(SmileyModel.RotationAngle);
            g.TranslateTransform(-SmileyModel.X, -SmileyModel.Y);

            int r = SmileyModel.Radius;

            // Face
            g.FillEllipse(Brushes.Yellow, SmileyModel.X - r, SmileyModel.Y - r, 2 * r, 2 * r);
            g.DrawEllipse(Pens.Black, SmileyModel.X - r, SmileyModel.Y - r, 2 * r, 2 * r);

            // Eyes
            int eyeOffsetX = r / 2;
            int eyeOffsetY = r / 2;
            int eyeSize = r / 5;
            g.FillEllipse(Brushes.Black, SmileyModel.X - eyeOffsetX - eyeSize / 2, SmileyModel.Y - eyeOffsetY - eyeSize / 2, eyeSize, eyeSize);
            g.FillEllipse(Brushes.Black, SmileyModel.X + eyeOffsetX - eyeSize / 2, SmileyModel.Y - eyeOffsetY - eyeSize / 2, eyeSize, eyeSize);

            // Mouth
            g.DrawArc(Pens.Black, SmileyModel.X - r / 2, SmileyModel.Y - r / 4, r, r / 2, 0, 180);

            // Restore transform
            g.Transform = oldTransform;
        }
    }
}
