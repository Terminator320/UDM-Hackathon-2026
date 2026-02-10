package org.diro.dirosnake.system;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsWorld;
import org.diro.dirosnake.EntityTypes;


import java.util.ArrayList;

public class SnakePhysics {

    // Member variables
    private final PhysicsWorld physicsWorld;
    private final ArrayList<SnakeCell> snake;

    public SnakePhysics(ArrayList<SnakeCell> snake, PhysicsWorld physicsWorld) {
        this.physicsWorld = physicsWorld;
        this.snake = snake;
    }

    public void init() {
        collisionHandler();
    }

    private void collisionHandler() {
        //TODO: Coder la détection des collisions avec les données ici.
        physicsWorld.addCollisionHandler(new CollisionHandler(EntityTypes.HEAD, EntityTypes.DATA) {

            protected void onCollisionBegin(Entity head, Entity data) {
                data.removeFromWorld();
                FXGL.getAudioPlayer().playSound(FXGL.getAssetLoader().loadSound("eating.mp3"));
                SnakeLogic.expendSnake(snake);
                SnakeLogic.spawnData(snake);
            }
        });
        // [NOTE DU D.I.R.O.]
        // Bien que possible, vous ne devez pas faire la détection des collisions entre le
        // S.N.A.K.E. et son corps ou les côtés de l'écran ici.


    }
}