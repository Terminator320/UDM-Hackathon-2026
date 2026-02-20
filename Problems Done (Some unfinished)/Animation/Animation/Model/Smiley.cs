using System.Drawing;

namespace Animation.Model
{
    public class Smiley
    {
        public int X { get; set; } // center X
        public int Y { get; set; } // center Y
        public int Radius { get; set; }
        public float RotationAngle { get; set; } // in degrees

        public Smiley(int x, int y, int radius)
        {
            X = x;
            Y = y;
            Radius = radius;
            RotationAngle = 0f;
        }
    }
}
