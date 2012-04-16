/* class Objs
 * Methods to draw primitive objects (sphere, cylinder)
 *
 * Doug DeCarlo
 */

import java.util.*;

import javax.media.opengl.GL;
import javax.vecmath.*;

class Objs
{
    static int res;

    // Display list stuff
    private static boolean useDL = false;
    private static int cylDisplayList = -1;
    private static int sphDisplayList = -1;

    public static void initialize(GL gl, int res_, boolean useDL_)
    {
        res = res_;

        if (useDL_) {
            // Create display list ids
            sphDisplayList = gl.glGenLists(2);
            cylDisplayList = sphDisplayList + 1;

            // Define display lists
            gl.glNewList(sphDisplayList, GL.GL_COMPILE);
            sphere(gl);
            gl.glEndList();
            
            gl.glNewList(cylDisplayList, GL.GL_COMPILE);
            cylinder(gl);
            gl.glEndList();

            useDL = true;
        }
    }

    // Draw a sphere whose axis is along Z-axis with diameter 1 and
    // poles at z=0.5 and z=-0.5
    public static void sphere(GL gl)
    {
        // Use display list if defined
        if (useDL) {
            gl.glCallList(sphDisplayList);
            return;
        }

        int ures = res+1, vres = res-1;

        for (int vi = 0; vi < vres-1; vi++) {
            double v = Math.PI*vi/(vres-1) - Math.PI/2;
            double vn = Math.PI*(vi+1)/(vres-1) - Math.PI/2;

            gl.glBegin(GL.GL_QUAD_STRIP);
            for (int ui = 0; ui < ures; ui++) {
                double u = 2*Math.PI*ui/(ures-1);
                gl.glNormal3d(Math.cos(u)*Math.cos(v),
                              Math.sin(u)*Math.cos(v),
                              Math.sin(v));
                gl.glVertex3d(0.5*Math.cos(u)*Math.cos(v),
                              0.5*Math.sin(u)*Math.cos(v),
                              0.5*Math.sin(v));
                gl.glNormal3d(Math.cos(u)*Math.cos(vn),
                              Math.sin(u)*Math.cos(vn),
                              Math.sin(vn));
                gl.glVertex3d(0.5*Math.cos(u)*Math.cos(vn),
                              0.5*Math.sin(u)*Math.cos(vn),
                              0.5*Math.sin(vn));
            }
            gl.glEnd();
        }
    }

    // Draw a cylinder along Z-axis ranging from z=0 to z=1 that
    // has a diameter of 1
    public static void cylinder(GL gl)
    {
        // Use display list if defined
        if (useDL) {
            gl.glCallList(cylDisplayList);
            return;
        }

        int ures = res, vres = 2;

        // Bottom
        gl.glBegin(GL.GL_POLYGON);
        gl.glNormal3d(0, 0, -1);
        for (int i = 0; i < ures; i++) {
            double u = 2*Math.PI*i/(ures-1);
            gl.glVertex3d(0.5*Math.cos(u), 0.5*Math.sin(u), 0);
        }
        gl.glEnd();

        // Top
        gl.glBegin(GL.GL_POLYGON);
        gl.glNormal3d(0, 0, 1);
        for (int i = 0; i < ures; i++) {
            double u = 2*Math.PI*i/(ures-1);
            gl.glVertex3d(0.5*Math.cos(u), 0.5*Math.sin(u), 1);
        }
        gl.glEnd();

        // Tube
        for (int vi = 0; vi < vres-1; vi++) {
            double v = vi/(vres-1);
            double vn = (vi+1)/(vres-1);

            gl.glBegin(GL.GL_QUAD_STRIP);
            for (int ui = 0; ui < ures; ui++) {
                double u = 2*Math.PI*ui/(ures-1);
                gl.glNormal3d(Math.cos(u), Math.sin(u), 0);
                gl.glVertex3d(0.5*Math.cos(u), 0.5*Math.sin(u), v);

                gl.glNormal3d(Math.cos(u), Math.sin(u), 0);
                gl.glVertex3d(0.5*Math.cos(u), 0.5*Math.sin(u), vn);
            }
            gl.glEnd();
        }
    }
    
    public static void cone(GL gl)
    {
        // Use display list if defined
        if (useDL) {
            gl.glCallList(sphDisplayList);
            return;
        }

        int ures = res+1, vres = res-1;

        for (int vi = 0; vi < vres-1; vi++) {
            double v = Math.PI*vi/(vres-1) - Math.PI/2;
            double vn = Math.PI*(vi+1)/(vres-1) - Math.PI/2;

            gl.glBegin(GL.GL_QUAD_STRIP);
            for (int ui = 0; ui < ures; ui++) {
                double u = 2*Math.PI*ui/(ures-1);
                gl.glNormal3d(Math.cos(u)*Math.cos(v),
                              Math.sin(u)*Math.cos(v),
                              Math.sin(v));
                gl.glVertex3d(0.5*Math.cos(u)*Math.cos(v),
                              0.5*Math.sin(u)*Math.cos(v),
                              0.5*Math.sin(v));
                gl.glNormal3d(Math.cos(u)*Math.cos(vn),
                              Math.sin(u)*Math.cos(vn),
                              Math.sin(vn));
                gl.glVertex3d(0.5*Math.cos(u)*Math.cos(vn),
                              0.5*Math.sin(u)*Math.cos(vn),
                              0.5*Math.sin(vn));
            }
            gl.glEnd();
        }
    }
    
}
