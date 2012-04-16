/* class Ellipsoid
 * Class which defined uv-parameterized ellipsoid shape
 *
 * Doug DeCarlo
 */


import javax.media.opengl.GL;
import javax.vecmath.*;

public class Ellipsoid extends UVShape
{
    // Ellipsoid parameters: major axis lengths
    private DoubleParameter ax, ay, az;

    public Ellipsoid(int uSizeVal, int vSizeVal)
    {
    	super("Ellipsoid", uSizeVal, vSizeVal, true,
              0, 2*Math.PI, -Math.PI/2, Math.PI/2);

        // Ellipsoid parameters: axis lengths in x, y and z directions
        ax = addParameter(new DoubleParameter("Ax", 1, 0.05, 10, 2));
        ay = addParameter(new DoubleParameter("Ay", 1, 0.05, 10, 2));
        az = addParameter(new DoubleParameter("Az", 1, 0.05, 10, 2));

        // Use my own vertex program
        slProgram.vShaderFile = "ellipsoid.vp";
    }

    // Tell the graphics processor the ellipsoid parameters
    protected void bindUniform(GL gl)
    {
        float ax = (float)this.ax.value;
        float ay = (float)this.ay.value;
        float az = (float)this.az.value;
        
        int v;

        v = gl.glGetUniformLocationARB(slProgram.program, "ax");
        gl.glUniform1fARB(v, (float)ax);
        
        v = gl.glGetUniformLocationARB(slProgram.program, "ay");
        gl.glUniform1fARB(v, (float)ay);
        
        v = gl.glGetUniformLocationARB(slProgram.program, "az");
        gl.glUniform1fARB(v, (float)az);
    }

    // Compute geometry of vertices
    public void evalPosition(double u, double v, Point3d p)
    {
        p.set(ax.value*Math.cos(u)*Math.cos(v), ay.value*Math.cos(v)*Math.sin(u), az.value*Math.sin(v));
    }
    
    // Compute normal vector
    public void evalNormal(double u, double v, Vector3d n)
    {
        n.set(ay.value*az.value*Math.cos(u)*Math.cos(v), ax.value*az.value*Math.cos(v)*Math.sin(u), ax.value*ay.value*Math.sin(v));
        n.normalize();
    }
}
