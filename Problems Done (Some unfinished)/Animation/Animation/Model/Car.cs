namespace Animation.Model
{
    public class Car
    {
        public int X { get; set; }
        public int Y { get; set; }
        public int Width { get; set; }
        public int Height { get; set; }
        public float Speed { get; set; }
        public float WheelRotation { get; set; }

        public Car(int x, int y, int width, int height)
        {
            X = x;
            Y = y;
            Width = width;
            Height = height;
            Speed = 0;
            WheelRotation = 0;
        }
    }
}
