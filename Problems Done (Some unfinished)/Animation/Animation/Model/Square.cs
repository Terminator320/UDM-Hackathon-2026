using System.Drawing;

namespace Animation.model
{
    public class Square
    {
        public int X { get; set; }
        public int Y { get; set; }
        public int Size { get; set; }
        public Color? FillColor { get; set; } 

        public Square(int x, int y, int size, Color? fillColor = null)
        {
            X = x;
            Y = y;
            Size = size;
            FillColor = fillColor;
        }
    }
}
