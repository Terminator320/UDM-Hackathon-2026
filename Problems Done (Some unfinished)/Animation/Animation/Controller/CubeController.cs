using Animation.Model;
using Animation.View;
using System;
using System.Windows.Forms;

namespace Animation.Controller
{
    public class CubeController
    {
        private readonly CubePanelView view;
        private readonly Cube cube;
        private readonly Timer timer;

        public CubeController(CubePanelView view)
        {
            this.view = view;
            cube = new Cube(180);

            // Set initial rotation for better 3D view
            cube.AngleX = 20;  // Initial tilt
            cube.AngleY = -30; // Initial rotation

            view.CubeModel = cube;

            timer = new Timer();
            timer.Interval = 16; // ~60 FPS (1000ms / 60 ≈ 16ms)
            timer.Tick += Timer_Tick;
        }

        private void Timer_Tick(object sender, EventArgs e)
        {
            // Rotate around X axis (HORIZONTAL axis rotation)
            // This makes the cube spin like the diagram shows
            cube.RotateX(1.5f); // Smooth rotation speed
            view.Invalidate();
        }

        public void Start() => timer.Start();
        public void Stop() => timer.Stop();
    }
}