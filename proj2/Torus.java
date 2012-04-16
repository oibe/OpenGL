/* class Torus
 * Class which defined uv-parameterized torus shape
 *
 * Doug DeCarlo
 */

import javax.media.opengl.GL;
import javax.vecmath.*;

public class Torus extends UVShape
{
    // Torus parameters: inner/outer radii
    private DoubleParameter inRad, outRad;

    public Torus(int uSizeVal, int vSizeVal)
    {
        super("Torus", uSizeVal, vSizeVal, false,
              0, 2*Math.PI, 0, 2*Math.PI);
	
        // Torus parameters: inner/outer radii
        inRad = addParameter(new DoubleParameter("Inner Radius", 
                                                 0.5, 0.05, 3, 2));
        outRad = addParameter(new DoubleParameter("Outer Radius",
                                                  1, 0.05, 3, 2));

        // Use my own vertex program
        slProgram.vShaderFile = "torus.vp";
    }
    
    // Let the graphics processor know the halfSum and halfDif
    protected void bindUniform(GL gl)
    {
        float outRad = (float)this.outRad.value;
        float inRad = (float)this.inRad.value;

        int v = gl.glGetUniformLocationARB(slProgram.program, "outRad");
        gl.glUniform1fARB(v, (float)outRad);

        v = gl.glGetUniformLocationARB(slProgram.program, "inRad");
        gl.glUniform1fARB(v, (float)inRad);
    }

    // Compute geometry of vertices in torus using inRad and outRad
    public void evalPosition(double u, double v, Point3d p)
    {
        double a = (outRad.value - inRad.value)/2;
        double c = inRad.value+a;
        p.set((c+a*Math.cos(v))*Math.cos(u), (c+a*Math.cos(v))*Math.sin(u), a*Math.sin(v));
    }

    // Compute normal vector
    public void evalNormal(double u, double v, Vector3d n)
    {
        // ...   (placeholder)
        n.set(Math.cos(u)*Math.cos(v),Math.cos(v)*Math.sin(u), Math.sin(v));
        n.normalize();
    }
}
