package org.diro.dirosnake;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import org.diro.dirosnake.menu.SnakeSceneFactory;
import org.diro.dirosnake.system.*;

import java.util.ArrayList;

public class Launcher extends GameApplication {
    private final ArrayList<SnakeCell> snake = new ArrayList<>();
    SnakePhysics physics;
    SnakeInput inputs;
    SnakeEnvironment environment = new SnakeEnvironment();
    private com.almasb.fxgl.audio.Music gameMusic;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(Constants.WIDTH);
        settings.setHeight(Constants.HEIGHT);
        settings.setTitle("S.N.A.K.E");
        settings.setVersion("1.0.0");
        settings.setFontUI("MGS.ttf");
        settings.setMainMenuEnabled(true);
        settings.setSceneFactory(new SnakeSceneFactory());
    }

    @Override
    protected void initGame() {
        environment.createBackground();
        SnakeLogic.initGame(snake);
        SnakeLogic.spawnData(snake);
        TimeHelper.resetTime();

        gameMusic = FXGL.getAssetLoader().loadMusic("game.mp3");
        FXGL.getAudioPlayer().loopMusic(gameMusic);

    }

    @Override
    protected void initPhysics() {
        physics = new SnakePhysics(snake, FXGL.getPhysicsWorld());
        physics.init();
    }

    @Override
    protected void initInput() {
        inputs = new SnakeInput(snake, FXGL.getInput());
        inputs.init();
    }

    @Override
    protected void onUpdate(double tpf) {

        if (GameState.isGameOver && !GameState.restartGame) {
            FXGL.getGameController().gotoGameMenu();
        }

        if (GameState.restartGame) {
            FXGL.getGameController().startNewGame();
            snake.clear();
            GameState.resetGameStates();
        }

        double now = FXGL.getGameTimer().getNow();
        if (now - TimeHelper.getStartTime() >= 1.0 / Constants.SNAKE_SPEED) {
            TimeHelper.resetTime();

            Directions dir = snake.isEmpty() ? Directions.RIGHT : snake.get(0).direction;
            SnakeLogic.moveSnake(snake, dir);
        }

        if (GameState.isGameOver && !GameState.restartGame) {
            if (gameMusic != null) FXGL.getAudioPlayer().stopMusic(gameMusic);
            FXGL.getGameController().gotoGameMenu();
        }

        if (GameState.restartGame) {
            if (gameMusic != null) FXGL.getAudioPlayer().stopMusic(gameMusic);
            FXGL.getGameController().startNewGame();
            snake.clear();
            GameState.resetGameStates();
        }

        //TODO: Faire bouger le S.N.A.K.E automatiquement ici.

    }

    public static void main(String[] args) {
        launch(args);
    }
}