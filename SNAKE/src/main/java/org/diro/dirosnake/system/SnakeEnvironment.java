package org.diro.dirosnake.system;

import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.shape.Rectangle;
import org.diro.dirosnake.Constants;
import org.diro.dirosnake.EntityTypes;

/** [NOTE DU D.I.R.O.]
 * Cette classe représente se qui sera affiché derrière le S.N.A.K.E.
 */
public class SnakeEnvironment {

    public void createBackground() {
        //TODO: Coder l'environnement ici.
        int cols = Constants.WIDTH / Constants.GRID_SCALE;
        int rows = Constants.HEIGHT / Constants.GRID_SCALE;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                boolean even = (x + y) % 2 == 0;
                Rectangle tile = new Rectangle(
                        Constants.GRID_SCALE,
                        Constants.GRID_SCALE,
                        even ? Constants.TILE_1 : Constants.TILE_2
                );

                FXGL.entityBuilder()
                        .type(EntityTypes.NONE)
                        .at(x * Constants.GRID_SCALE, y * Constants.GRID_SCALE)
                        .view(tile)
                        .buildAndAttach();
            }
        }
    }
}