/* class Obstacle
 * Abstract class for obstacles in scene (trees, rocks), which provides
 * methods for drawing the objects and asking where they are
 *
 * Doug DeCarlo
 */

import javax.media.opengl.GL;
import javax.vecmath.*;

public interface Obstacle
{
    // Getter method for 3D position
    abstract Point3d getLocation();

    // Method to draw obstacle
    abstract void draw(GL gl);
}
