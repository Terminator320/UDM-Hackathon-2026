using Animation.Model;
using Animation.View;
using System;
using System.Windows.Forms;

namespace Animation.Controller
{
    public class CarController
    {
        private readonly CarPanelView view;
        private readonly Car car;
        private readonly Timer timer;
        private int elapsedMs;

        public CarController(CarPanelView view)
        {
            this.view = view;

            car = new Car(50, 200, 60, 30);
            view.CarModel = car;

            timer = new Timer
            {
                Interval = 1000 / 24 // 24 FPS
            };
            timer.Tick += Timer_Tick;
        }

        private void Timer_Tick(object sender, EventArgs e)
        {
            // Accelerate for 5 seconds
            if (elapsedMs < 5000)
                car.Speed += 0.5f;

            car.X += (int)car.Speed;

            // Rotate wheels based on speed
            car.WheelRotation += car.Speed * 2f;
            if (car.WheelRotation >= 360)
                car.WheelRotation -= 360;

            // Remove when fully past track
            if (car.X >= view.GetTrackRect().Right)
            {
                timer.Stop();
                view.CarModel = null;
            }

            elapsedMs += timer.Interval;
            view.Invalidate();
        }

        public void Start()
        {
            elapsedMs = 0;
            car.X = 50;
            car.Speed = 0;
            car.WheelRotation = 0;
            view.CarModel = car;
            timer.Start();
        }
    }
}
