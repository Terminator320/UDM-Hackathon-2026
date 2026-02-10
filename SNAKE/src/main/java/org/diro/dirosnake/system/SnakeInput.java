package org.diro.dirosnake.system;

import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;
import org.diro.dirosnake.Directions;

import java.util.ArrayList;

public class SnakeInput {

    // Member variables
    private final Input inputManager;
    private final ArrayList<SnakeCell> snake;

    public SnakeInput(ArrayList<SnakeCell> snake, Input inputManager) {
        this.inputManager = inputManager;
        this.snake = snake;
    }

    public void init() {
        inputHandler();
    }

    private void inputHandler() {
        //TODO: Ajouter la capacité de faire bouger le S.N.A.K.E. avec les touches du clavier ici.
        inputManager.addAction(new UserAction("UpArrow") {
            @Override
            protected void onAction() {
                setDirection(Directions.UP);
            }
        }, KeyCode.UP);

        inputManager.addAction(new UserAction("UpW") {
            @Override
            protected void onAction() {
                setDirection(Directions.UP);
            }
        }, KeyCode.W);

        inputManager.addAction(new UserAction("DownArrow") {
            @Override
            protected void onAction() {
                setDirection(Directions.DOWN);
            }
        }, KeyCode.DOWN);

        inputManager.addAction(new UserAction("DownS") {
            @Override
            protected void onAction() {
                setDirection(Directions.DOWN);
            }
        }, KeyCode.S);

        inputManager.addAction(new UserAction("LeftArrow") {
            @Override
            protected void onAction() {
                setDirection(Directions.LEFT);
            }
        }, KeyCode.LEFT);

        inputManager.addAction(new UserAction("LeftA") {
            @Override
            protected void onAction() {
                setDirection(Directions.LEFT);
            }
        }, KeyCode.A);

        inputManager.addAction(new UserAction("RightArrow") {
            @Override
            protected void onAction() {
                setDirection(Directions.RIGHT);
            }
        }, KeyCode.RIGHT);

        inputManager.addAction(new UserAction("RightD") {
            @Override
            protected void onAction() {
                setDirection(Directions.RIGHT);
            }
        }, KeyCode.D);
    }

    private void setDirection(Directions newDir) {
        if (snake.isEmpty()) {
            return;
        }

        Directions current = snake.get(0).direction;

        boolean isOpposite =
                (current == Directions.UP && newDir == Directions.DOWN) ||
                        (current == Directions.DOWN && newDir == Directions.UP) ||
                        (current == Directions.LEFT && newDir == Directions.RIGHT) ||
                        (current == Directions.RIGHT && newDir == Directions.LEFT);

        if (!isOpposite) {
            snake.get(0).direction = newDir;
        }
    }

}