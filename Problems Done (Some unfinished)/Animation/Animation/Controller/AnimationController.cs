using System.Windows.Forms;

namespace Animation.Controller
{
    public class AnimationController
    {
        private Timer animationTimer;

        public bool Animation1Started { get; private set; } = false;

        public AnimationController()
        {
            animationTimer = new Timer();
            animationTimer.Interval = 1000 / 24; // 24 FPS
        }

        // Start Animation 1 (outline square, infinite)
        public void StartAnimation1()
        {
            Animation1Started = true;
            animationTimer.Start();
        }

        // Start Animation 2 or 3 (fill square for 5 sec)
        public void StartAnimationWithDuration(Timer timer)
        {
            timer.Start(); // timer will handle the 5-second removal
        }

        public Timer GetAnimationTimer()
        {
            return animationTimer;
        }
    }
}
