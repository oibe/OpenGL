/* class Torus
 * Class which defined uv-parameterized torus shape
 *
 * Doug DeCarlo
 */

import javax.media.opengl.GL;
import javax.vecmath.*;

public class dini extends UVShape
{
    //Torus parameters: inner/outer radii


    public dini(int uSizeVal, int vSizeVal)
    {
        super("dini", uSizeVal, vSizeVal, false,
              0, 4*Math.PI, 0, 2);
	
   
        // Use my own vertex program
        slProgram.vShaderFile = "dini.vp";
    }
    
    // Let the graphics processor know the halfSum and halfDif
    protected void bindUniform(GL gl)
    {
      
    }
    public static double f(double u){
    	return 20*Math.pow(u,3)-65*Math.PI*Math.pow(u,2)+50*Math.pow(Math.PI,2)*u-16*Math.pow(Math.PI,3);
    }
    public static double g(double u){
    	return Math.sqrt(    (8*Math.pow(Math.cos(2*u),2))-(Math.cos(2*u)*(24*Math.pow(Math.cos(u),3)-8*Math.cos(u)+15))+(6*Math.pow(Math.cos(u),4)*(1-(3*Math.pow(Math.sin(u),2))))+17);
    }
    // Compute geometry of vertices in torus using inRad and outRad
    public void evalPosition(double u, double v, Point3d p)
    {
       double a = 1;
       double b = .2;
        /*p.set((((Math.sqrt(2)*f(u)*Math.cos(u)*Math.cos(v)*(3*Math.pow(Math.cos(u),2)-1)-2*Math.cos(2*u))/(80*Math.pow(Math.PI,3)*g(u)))-((3*Math.cos(u)-3)/4)),
		-1*((f(u)*Math.sin(v))/(60*Math.pow(Math.PI,3))),
		-1*(Math.sqrt(2)*f(u)*Math.sin(u)*Math.cos(v))/(15*Math.pow(Math.PI,3)*g(u))+((Math.sin(u)*Math.pow(Math.cos(u),2)+Math.sin(u))/4)-(Math.sin(u)*Math.cos(u)/2));*/
		p.set(a*Math.cos(u)*Math.sin(v),Math.cos(v)+Math.log(Math.tan(v/2))+b*u,a*Math.sin(u)*Math.sin(v));
		
    }

    // Compute normal vector
    public void evalNormal(double u, double v, Vector3d n)
    {
        // ...   (placeholder)
        double a = 1;
        double b =.2;
	n.set(a*Math.cos(u)*Math.cos(v)+.2*Math.sin(u),Math.sin(v),a*Math.sin(u)*Math.cos(v)-.2*Math.cos(u));
	n.normalize();
    }
}
