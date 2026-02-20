using System;
using System.Drawing;

namespace Animation.Model
{
    public class Cube
    {
        public class Vertex3D
        {
            public float X, Y, Z;
            public Vertex3D(float x, float y, float z) { X = x; Y = y; Z = z; }
        }

        public Vertex3D[] Vertices3D { get; private set; }
        public int Size { get; private set; }
        public float AngleX { get; set; }
        public float AngleY { get; set; }

        public int[][] Faces { get; private set; } = new int[][]
        {
            new int[]{0,1,2,3}, // front
            new int[]{5,4,7,6}, // back
            new int[]{4,0,3,7}, // left
            new int[]{1,5,6,2}, // right
            new int[]{4,5,1,0}, // bottom
            new int[]{3,2,6,7}  // top
        };

        public Color[] FaceColors { get; private set; } = new Color[]
        {
            Color.FromArgb(230, 60, 60),    // red
            Color.FromArgb(60, 120, 230),   // blue
            Color.FromArgb(255, 140, 60),   // orange
            Color.FromArgb(60, 200, 60),    // green
            Color.FromArgb(255, 200, 60),   // yellow
            Color.FromArgb(240, 240, 240)    // white
        };

        public Cube(int size)
        {
            Size = size;
            Vertices3D = new Vertex3D[8];
            InitializeVertices();
        }

        private void InitializeVertices()
        {
            float s = Size / 2f;
            Vertices3D[0] = new Vertex3D(-s, -s, -s);
            Vertices3D[1] = new Vertex3D(s, -s, -s);
            Vertices3D[2] = new Vertex3D(s, s, -s);
            Vertices3D[3] = new Vertex3D(-s, s, -s);
            Vertices3D[4] = new Vertex3D(-s, -s, s);
            Vertices3D[5] = new Vertex3D(s, -s, s);
            Vertices3D[6] = new Vertex3D(s, s, s);
            Vertices3D[7] = new Vertex3D(-s, s, s);
        }

        public (PointF[] points, float[] depths) GetTransformedData(int width, int height)
        {
            PointF[] projected = new PointF[8];
            float[] depths = new float[8];

            float radX = AngleX * (float)(Math.PI / 180.0);
            float radY = AngleY * (float)(Math.PI / 180.0);
            float sinX = (float)Math.Sin(radX), cosX = (float)Math.Cos(radX);
            float sinY = (float)Math.Sin(radY), cosY = (float)Math.Cos(radY);

            for (int i = 0; i < 8; i++)
            {
                // Rotate Y
                float x1 = Vertices3D[i].X * cosY - Vertices3D[i].Z * sinY;
                float z1 = Vertices3D[i].X * sinY + Vertices3D[i].Z * cosY;
                // Rotate X
                float y2 = Vertices3D[i].Y * cosX - z1 * sinX;
                float z2 = Vertices3D[i].Y * sinX + z1 * cosX;

                float factor = 600f / (600f + z2);
                projected[i] = new PointF(width / 2f + x1 * factor, height / 2f - y2 * factor);
                depths[i] = z2; // Store Z for sorting
            }
            return (projected, depths);
        }

        public void RotateX(float delta) => AngleX = (AngleX + delta) % 360;
        public void RotateY(float delta) => AngleY = (AngleY + delta) % 360;
    }
}