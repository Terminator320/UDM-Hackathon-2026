package org.diro.dirosnake.system;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import javafx.scene.shape.Rectangle;
import org.diro.dirosnake.Constants;
import org.diro.dirosnake.Directions;
import org.diro.dirosnake.EntityTypes;
import org.diro.dirosnake.GameState;

import java.util.ArrayList;
import java.util.Random;

public class SnakeLogic {

    /** [NOTE DU D.I.R.O.]
     * Puisque la variable "snake" est un ArrayList, les éléments sont passés par référence (pas exactement mais on peut
     * assumer que oui pour la suite du développement), ils sont donc modifiés directement dans les fonctions lorsque
     * la variable est passé en argument. Pour plus d'information, cliquer sur le lien suivant:
     * https://stackoverflow.com/questions/40480/is-java-pass-by-reference-or-pass-by-value?page=1&tab=scoredesc#tab-top
     */

    public static void expendSnake(ArrayList<SnakeCell> snake) {
        //TODO: Coder la fonction pour élargir le S.N.A.K.E. ici.

        if (snake.isEmpty()) {
            return;
        }

        SnakeCell tail = snake.get(snake.size() - 1);
        double x = tail.getEntity().getX();
        double y = tail.getEntity().getY();

        switch (tail.direction) {
            case UP:
                y += Constants.GRID_SCALE;
                break;
            case DOWN:
                y -= Constants.GRID_SCALE;
                break;
            case LEFT:
                x += Constants.GRID_SCALE;
                break;
            case RIGHT:
                x -= Constants.GRID_SCALE;
                break;
        }

        Entity body = FXGL.entityBuilder()
                .type(EntityTypes.BODY)
                .at(x, y)
                .viewWithBBox(new Rectangle(Constants.GRID_SCALE, Constants.GRID_SCALE, Constants.SNAKE_BODY_COLOR))
                .buildAndAttach();

        snake.add(new SnakeCell(body, tail.direction));

    }

    public static void moveSnake(ArrayList<SnakeCell> snake, Directions direction) {
        //TODO: Coder la fonction pour faire avancer le S.N.A.K.E. ici.

        /* [NOTE DU D.I.R.O.]
         * Ne pas oublier que chaque cellule du S.N.A.K.E. doit mettre à jour la direction.
         * Cella sera utile lorsqu'il faudra faire la fonction "expendSnake" et faire avancer
         * le S.N.A.K.E. automatiquement selon la direction à laquelle il fait face.
         */
        if (snake.isEmpty()) {
            return;
        }

        int size = snake.size();
        double[] oldX = new double[size];
        double[] oldY = new double[size];
        Directions[] oldDir = new Directions[size];

        for (int i = 0; i < size; i++) {
            oldX[i] = snake.get(i).getEntity().getX();
            oldY[i] = snake.get(i).getEntity().getY();
            oldDir[i] = snake.get(i).direction;
        }

        SnakeCell head = snake.get(0);
        head.direction = direction;

        double newX = oldX[0];
        double newY = oldY[0];

        switch (direction) {
            case UP:
                newY -= Constants.GRID_SCALE;
                break;
            case DOWN:
                newY += Constants.GRID_SCALE;
                break;
            case LEFT:
                newX -= Constants.GRID_SCALE;
                break;
            case RIGHT:
                newX += Constants.GRID_SCALE;
                break;
        }

        head.getEntity().setPosition(newX, newY);

        for (int i = 1; i < size; i++) {
            SnakeCell cell = snake.get(i);
            cell.getEntity().setPosition(oldX[i - 1], oldY[i - 1]);
            cell.direction = oldDir[i - 1];
        }

        snakeCollidesBody(snake);
        snakeCollidesScreenEdges(snake);

    }

    private static void snakeCollidesBody(ArrayList<SnakeCell> snake) {
        //TODO: Coder la détection des collisions entre le corps du S.N.A.K.E. et sa tête ici.
        if (snake.size() < 2) {
            return;
        }

        SnakeCell head = snake.get(0);
        double headX = head.getEntity().getX();
        double headY = head.getEntity().getY();

        for (int i = 1; i < snake.size(); i++) {
            SnakeCell cell = snake.get(i);
            if (cell.getEntity().getX() == headX && cell.getEntity().getY() == headY) {
                GameState.isGameOver = true;
                return;
            }
        }
    }

    private static void snakeCollidesScreenEdges(ArrayList<SnakeCell> snake) {
        //TODO: Coder la détection des collisions avec les côtés de l'écran ici.
        if (snake.isEmpty()) {
            return;
        }

        SnakeCell head = snake.get(0);
        double x = head.getEntity().getX();
        double y = head.getEntity().getY();

        if (x < 0 || y < 0 || x >= Constants.WIDTH || y >= Constants.HEIGHT) {
            GameState.isGameOver = true;
        }
    }

    public static void spawnData(ArrayList<SnakeCell> snake) {
        //TODO: Coder l'apparition des données ici.
        if (!FXGL.getGameWorld().getEntitiesByType(EntityTypes.DATA).isEmpty()) {
            return;
        }

        int cols = Constants.WIDTH / Constants.GRID_SCALE;
        int rows = Constants.HEIGHT / Constants.GRID_SCALE;

        Random random = new Random();
        int attempts = 0;
        int maxAttempts = cols * rows;

        while (attempts < maxAttempts) {
            double x = random.nextInt(cols) * Constants.GRID_SCALE;
            double y = random.nextInt(rows) * Constants.GRID_SCALE;

            boolean occupied = false;
            for (SnakeCell cell : snake) {
                if (cell.getEntity().getX() == x && cell.getEntity().getY() == y) {
                    occupied = true;
                    break;
                }
            }

            if (!occupied) {
                double imageSize = Constants.GRID_SCALE * 1.4;   // big image
                double hitSize   = Constants.GRID_SCALE * 0.6;   // small hitbox

                double imageOffset = (Constants.GRID_SCALE - imageSize) / 2.0;
                double hitOffset   = (Constants.GRID_SCALE - hitSize) / 2.0;

                var tex = FXGL.texture("coins.png", (int) imageSize, (int) imageSize);
                tex.setTranslateX(imageOffset);
                tex.setTranslateY(imageOffset);

                Entity data = FXGL.entityBuilder()
                        .type(EntityTypes.DATA)
                        .at(x, y)
                        .view(tex)
                        .with(new CollidableComponent(true))
                        .buildAndAttach();

                data.getBoundingBoxComponent().clearHitBoxes();
                data.getBoundingBoxComponent().addHitBox(
                        new com.almasb.fxgl.physics.HitBox(
                                "DATA",
                                new javafx.geometry.Point2D(hitOffset, hitOffset),
                                com.almasb.fxgl.physics.BoundingShape.box(hitSize, hitSize)
                        )
                );

                return;
            }

            attempts++;
        }
    }

    public static void initGame(ArrayList<SnakeCell> snake) {
        Entity head = FXGL.entityBuilder()
                .type(EntityTypes.HEAD)
                .at(Constants.GRID_SCALE * 5, Constants.GRID_SCALE * 8)
                .viewWithBBox(new Rectangle(Constants.GRID_SCALE, Constants.GRID_SCALE, Constants.SNAKE_HEAD_COLOR))
                .with(new CollidableComponent(true))
                .buildAndAttach();

        snake.add(new SnakeCell(head, Directions.RIGHT));
    }
}