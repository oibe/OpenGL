

/* class Rock
 * Represents a rock using a rectangular grid; given a particular level
 * of subdivision l, the rock will be a 2^l+1 X 2^l+1 height field
 * The rock is drawn a little below the surface to get a rough edge.
 *
 * Doug DeCarlo
 */

import java.util.*;

import javax.media.opengl.GL;
import javax.vecmath.*;

class Rock implements Obstacle
{
    // Location of rock
    private double xpos, ypos, scale;

    // -- Rock mesh: a height-field of rsize X rsize vertices
    int rsize;
    // Height field: z values
    private double[][] height;
    // Whether height value has been set (locked) already
    private boolean[][] locked;

    // Random number generator
    Random rgen;

    // ---------------------------------------------------------------

    public Rock(Random randGen, int level, 
		double xPosition, double yPosition, double scaling)
    {
        // Grid size of (2^level + 1)
        rsize = (1 << level) + 1;

        // Height field -- initially all zeros
        height = new double[rsize][rsize];
        locked = new boolean[rsize][rsize];
 
        rgen = randGen;

	// Set rock position in the world
	xpos = xPosition;
	ypos = yPosition;
	scale = scaling;

	compute();
    }

    // ----------------------------------------------------------------
    // Obstacle methods

    // Get rock location (as a scene element)
    public Point3d getLocation()
    {
	return new Point3d(xpos, ypos, 0);
    }

    // Draw rock in scene
    public void draw(GL gl)
    {
	gl.glPushMatrix();
	gl.glTranslated(xpos, ypos, -0.15);
	gl.glScaled(scale, scale, scale);

        // Draw polygon grid of rock as quad-strips
        //gl.glColor3d(0.6, 0.6, 0.6); gray is so last season
        gl.glColor3d(.85,.62,.13);

        // (Create these outside the loops, to avoid excessive memory
        // management in case Java compiler is too dumb...)
        Point3d p = new Point3d();
        Vector3d n = new Vector3d();

        for (int i = 0; i < rsize-1; i++) {
            gl.glBegin(GL.GL_QUAD_STRIP);
            for (int j = 0; j < rsize; j++) {
                getRockPoint(i, j, p);
                getRockNormal(i, j, n);
                gl.glNormal3d(n.x, n.y, n.z);
                gl.glVertex3d(p.x, p.y, p.z);
                
                getRockPoint(i+1, j, p);
                getRockNormal(i+1, j, n);
                gl.glNormal3d(n.x, n.y, n.z);
                gl.glVertex3d(p.x, p.y, p.z);
            }
            gl.glEnd();
        }

        // Make GC easy
        p = null;
        n = null;
    
	gl.glPopMatrix();
    }
    
    // ---------------------------------------------------------------

    // Point (i,j) on the rock -- point p gets filled in
    public void getRockPoint(int i, int j, Point3d p)
    {
        // Rock (x,y) locations are on the grid [-0.5, 0.5] x [-0.5, 0.5]
        p.x = (double)i / (rsize-1) - 0.5;
        p.y = (double)j / (rsize-1) - 0.5;
        // Rock z comes from height field
        p.z = height[i][j];
    }

    // Normal vector (i,j) on the rock -- vector n gets filled in
    public void getRockNormal(int i, int j, Vector3d n)
    {
        // This is the formula for a normal vector of a height field with
        // regularly spaced x and y values (assuming rock is zero on
        // its borders and outside of it too)

        // X component is zleft - zright (respecting boundaries)
        n.x = height[(i == 0) ? i : i-1][j] - 
              height[(i == rsize-1) ? i : i+1][j];

        // Y component is zbottom - ztop (respecting boundaries)
        n.y = height[i][(j == 0) ? j : j-1] - 
              height[i][(j == rsize-1) ? j : j+1];

        // Z component is twice the separation
        n.z = 2 / (rsize-1);

        n.normalize();
    }

    // ---------------------------------------------------------------

    // Compute the geometry of the rock
    // (called when the rock is created)
    public void compute()
    {
	// Initialize mesh
	for (int i = 0; i < rsize; i++) {
            for (int j = 0; j < rsize; j++) {
                height[i][j] = 0;

                // Lock sides...
                locked[i][j] = (i == 0 || i == rsize-1 ||
                                j == 0 || j == rsize-1);
            }
	}
	
	double start = .5 + (rgen.nextGaussian()+3)*.05;
        // Raise the middle point and lock it there
        height[rsize/2][rsize/2] = start;
        locked[rsize/2][rsize/2] = true;
	
	height[0][0] = height[0][rsize-1] = height[rsize-1][0] = height[rsize-1][rsize-1] = 0.0;
	locked[0][0] = locked[0][rsize-1] = locked[rsize-1][0] = locked[rsize-1][rsize-1] = true;
	
	
        // Recursively compute fractal structure
	computeFractal(0,0,rsize,0);
	
    }
    
    
    private void computeFractal(int x, int y, int sidelength,int count)
    {
    	Random r = new Random();
  	double val = ((r.nextGaussian()+1)/2)/Math.pow(2,count)+.375/Math.pow(2,count);

  	if(sidelength == 2){
  		return;
  	}
  	
  	if(locked[x][y+(sidelength/2)] == false){
  		height[x][y+(sidelength/2)]=((height[x][y]+height[x][y+sidelength-1])/2)+val;
  		locked[x][y+(sidelength/2)]=true;
  	}
  	if(locked[x+(sidelength/2)][y] == false){
  		height[x+(sidelength/2)][y]=((height[x][y]+height[x+sidelength-1][y])/2)+val;
  		locked[x+(sidelength/2)][y]=true;
  	}
  	if(locked[x+(sidelength/2)][y+sidelength-1] == false){
  		height[x+(sidelength/2)][y+sidelength-1]=((height[x][y+sidelength-1]+height[x+sidelength-1][y+sidelength-1])/2)+val;
  		locked[x+(sidelength/2)][y+sidelength-1]=true;
  	}
	if(locked[x+sidelength-1][y+(sidelength/2)] == false){
  		height[x+sidelength-1][y+(sidelength/2)]=((height[x+sidelength-1][y]+height[x+sidelength-1][y+sidelength-1])/2)+val;
  		locked[x+sidelength-1][y+(sidelength/2)]=true;
  	}
  	
  	if(locked[x+(sidelength/2)][y+sidelength/2] == false){
  		height[x+sidelength/2][y+sidelength/2] = (height[x][y+(sidelength/2)]+height[x+(sidelength/2)][y]+height[x+(sidelength/2)][y+sidelength-1]+height[x+sidelength-1][y+(sidelength/2)])/4;
  		locked[x+sidelength/2][y+sidelength/2] = true;
  	}
  	
  	computeFractal(x, y, (sidelength/2)+1, count+1);
  	computeFractal(x, y+(sidelength/2), (sidelength/2)+1,count+1);
  	computeFractal(x+(sidelength/2), y+(sidelength/2), (sidelength/2)+1,count+1);
  	computeFractal(x+(sidelength/2), y, (sidelength/2)+1,count+1);
    }
}
