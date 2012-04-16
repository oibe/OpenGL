/* class WorldView
 * The OpenGL drawing component of the interface
 *
 * Doug DeCarlo
 */

import java.awt.Window;

import javax.media.opengl.GL;

public class WorldView extends SimpleGLCanvas
{
    Shape s;

    public WorldView(Window parent, Shape sh, boolean debug)
    {
        super(parent, debug);

        s = sh;
    }
    
    // ------------------------------------------------------------

    public void init(GL gl)
    {
        // --- OpenGL Initialization

    	// Clear background
        gl.glClearColor(0.9f, 0.9f, 0.8f, 1.0f);

        // Turn on Z buffer
        gl.glEnable(GL.GL_DEPTH_TEST);

        // Turn on Gouraud shaded polygons
        gl.glShadeModel(GL.GL_SMOOTH);

        // Scale normal appropriately when transforming
        gl.glEnable(GL.GL_RESCALE_NORMAL);
    }

    // Method for handling window resizing
    public void projection(GL gl, int width, int height)
    {
        gl.glViewport(0, 0, width, height);

        double aspect = (double)width / height;
        double l, r, b, t;
        // Move near plane closer, but don't widen field-of-view
        double zoom = 10;

        // Preserve aspect ratio
        if (aspect > 1) {
            r = aspect/zoom;
            t = 1/zoom;
        } else {
            r = 1/zoom;
            t = 1/(zoom*aspect);
        }
        // Window has (0,0) in center
        l = -r;
        b = -t;

        // Set the world projection
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glFrustum(l, r, b, t, 2/zoom, 500);
        gl.glMatrixMode(GL.GL_MODELVIEW);
    }

    // Method for drawing the contents of the window
    public void draw(GL gl)
    {
        // Clear the window and depth buffer
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

	// Reset world transformation
	gl.glLoadIdentity();

        // Apply V: move camera back so that object is visible
        gl.glTranslated(0, 0, -5);

	// Draw the shape
        s.draw(gl);
    }
}
