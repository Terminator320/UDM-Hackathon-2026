using Animation.Model;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Windows.Forms;
using System.Collections.Generic;
using System.Linq;

namespace Animation.View
{
    public class CubePanelView : Panel
    {
        public Cube CubeModel { get; set; }

        public CubePanelView()
        {
            DoubleBuffered = true;
        }

        protected override void OnPaint(PaintEventArgs e)
        {
            if (CubeModel == null) return;

            Graphics g = e.Graphics;
            g.SmoothingMode = SmoothingMode.AntiAlias;
            g.Clear(Color.FromArgb(45, 45, 48));

            var (verts, depths) = CubeModel.GetTransformedData(Width, Height);

            // Calculate face depths and sort: DESCENDING (Max Z to Min Z)
            var sortedFaces = CubeModel.Faces
                .Select((indices, index) => new {
                    Index = index,
                    Depth = indices.Average(i => depths[i])
                })
                .OrderByDescending(f => f.Depth)
                .ToList();

            foreach (var face in sortedFaces)
            {
                PointF[] facePoints = CubeModel.Faces[face.Index].Select(i => verts[i]).ToArray();
                Color faceColor = CubeModel.FaceColors[face.Index];

                using (var brush = new SolidBrush(faceColor))
                // Using a pen of the SAME color fixes the "seams" between faces
                using (var pen = new Pen(faceColor, 1.0f))
                {
                    g.FillPolygon(brush, facePoints);
                    g.DrawPolygon(pen, facePoints);
                }

                // Optional: Draw a thin black wireframe overlay for definition
                using (var wireframe = new Pen(Color.FromArgb(50, 0, 0, 0), 1f))
                {
                    g.DrawPolygon(wireframe, facePoints);
                }
            }
        }
    }
}