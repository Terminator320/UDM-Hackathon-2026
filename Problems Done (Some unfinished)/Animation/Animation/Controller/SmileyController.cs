using Animation.Model;
using Animation.View;
using System;
using System.Windows.Forms;

namespace Animation.Controller
{
    public class SmileyController
    {
        private SmileyPanelView view;
        private Smiley smiley;
        private Timer timer;

        public SmileyController(SmileyPanelView view)
        {
            this.view = view;

            int centerX = view.Width / 2;
            int centerY = view.Height / 2;
            int radius = Math.Min(view.Width, view.Height) / 4;

            smiley = new Smiley(centerX, centerY, radius);
            view.SmileyModel = smiley;

            timer = new Timer();
            timer.Interval = 1000 / 24; // 24 FPS
            timer.Tick += Timer_Tick;
        }

        private void Timer_Tick(object sender, EventArgs e)
        {
            smiley.RotationAngle += 5; // degrees per frame
            if (smiley.RotationAngle >= 360) smiley.RotationAngle -= 360;
            view.Invalidate();
        }

        public void Start()
        {
            timer.Start();
        }

        public void Stop()
        {
            timer.Stop();
        }
    }
}
