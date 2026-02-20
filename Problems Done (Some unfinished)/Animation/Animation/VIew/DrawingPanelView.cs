using Animation.model;
using System.Drawing;
using System.Windows.Forms;

namespace Animation.VIew
{
    public class DrawingPanelView : Panel
    {
        public Square SquareModel { get; set; }

        public DrawingPanelView()
        {
            this.DoubleBuffered = true;
            this.Paint += DrawingPanelView_Paint;
        }

        private void DrawingPanelView_Paint(object sender, PaintEventArgs e)
        {
            Graphics g = e.Graphics;
            g.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;

            g.Clear(Color.White);

            if (SquareModel != null)
            {
                // Fill if color exists
                if (SquareModel.FillColor.HasValue)
                {
                    using (Brush fillBrush = new SolidBrush(SquareModel.FillColor.Value))
                    {
                        g.FillRectangle(fillBrush, SquareModel.X, SquareModel.Y, SquareModel.Size, SquareModel.Size);
                    }
                }

                // Draw edges
                g.DrawRectangle(Pens.Black, SquareModel.X, SquareModel.Y, SquareModel.Size, SquareModel.Size);
            }
        }
    }
}
