import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Pong extends JPanel implements ActionListener, KeyListener {

    // Window size
    static final int WIDTH = 800;
    static final int HEIGHT = 500;

    // Paddle settings
    int paddleWidth = 10;
    int paddleHeight = 80;
    int leftPaddleY = HEIGHT / 2 - paddleHeight / 2;
    int rightPaddleY = HEIGHT / 2 - paddleHeight / 2;
    int paddleSpeed = 6;

    // Ball settings
    int ballSize = 15;
    int ballX = WIDTH / 2 - ballSize / 2;
    int ballY = HEIGHT / 2 - ballSize / 2;
    int ballXSpeed = 4;
    int ballYSpeed = 4;

    // Scores
    int leftScore = 0;
    int rightScore = 0;

    // Input flags
    boolean wPressed, sPressed, upPressed, downPressed;

    Timer timer;

    public Pong() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        timer = new Timer(16, this); // ~60 FPS
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.WHITE);

        // Middle line
        for (int i = 0; i < HEIGHT; i += 20) {
            g.fillRect(WIDTH / 2 - 1, i, 2, 10);
        }

        // Paddles
        g.fillRect(20, leftPaddleY, paddleWidth, paddleHeight);
        g.fillRect(WIDTH - 30, rightPaddleY, paddleWidth, paddleHeight);

        // Ball
        g.fillOval(ballX, ballY, ballSize, ballSize);

        // Scores
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString(String.valueOf(leftScore), WIDTH / 2 - 60, 40);
        g.drawString(String.valueOf(rightScore), WIDTH / 2 + 40, 40);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Paddle movement
        if (wPressed && leftPaddleY > 0) leftPaddleY -= paddleSpeed;
        if (sPressed && leftPaddleY < HEIGHT - paddleHeight) leftPaddleY += paddleSpeed;
        if (upPressed && rightPaddleY > 0) rightPaddleY -= paddleSpeed;
        if (downPressed && rightPaddleY < HEIGHT - paddleHeight) rightPaddleY += paddleSpeed;

        // Ball movement
        ballX += ballXSpeed;
        ballY += ballYSpeed;

        // Wall collision
        if (ballY <= 0 || ballY >= HEIGHT - ballSize) {
            ballYSpeed *= -1;
        }

        // Paddle collision
        if (ballX <= 30 &&
                ballY + ballSize >= leftPaddleY &&
                ballY <= leftPaddleY + paddleHeight) {
            ballXSpeed *= -1;
        }

        if (ballX + ballSize >= WIDTH - 30 &&
                ballY + ballSize >= rightPaddleY &&
                ballY <= rightPaddleY + paddleHeight) {
            ballXSpeed *= -1;
        }

        // Scoring
        if (ballX < 0) {
            rightScore++;
            resetBall();
        }

        if (ballX > WIDTH) {
            leftScore++;
            resetBall();
        }

        repaint();
    }

    void resetBall() {
        ballX = WIDTH / 2 - ballSize / 2;
        ballY = HEIGHT / 2 - ballSize / 2;
        ballXSpeed *= -1;
    }

    // Key handling
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) wPressed = true;
        if (e.getKeyCode() == KeyEvent.VK_S) sPressed = true;
        if (e.getKeyCode() == KeyEvent.VK_UP) upPressed = true;
        if (e.getKeyCode() == KeyEvent.VK_DOWN) downPressed = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) wPressed = false;
        if (e.getKeyCode() == KeyEvent.VK_S) sPressed = false;
        if (e.getKeyCode() == KeyEvent.VK_UP) upPressed = false;
        if (e.getKeyCode() == KeyEvent.VK_DOWN) downPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pong");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(new Pong());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}