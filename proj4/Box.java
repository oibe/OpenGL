/* class Box
 * Ray-object intersections for a box
 *
 * Doug DeCarlo
 */
import java.io.*;
import javax.vecmath.*;
import java.text.ParseException;
import java.lang.reflect.*;

class Box extends Shape
{
    public static String keyword = "box";

    public Box( StreamTokenizer tokenizer )
	throws ParseException, IOException, NoSuchMethodException,
	       ClassNotFoundException,IllegalAccessException,
	       InvocationTargetException
    {
	super(tokenizer);
	read(tokenizer);
    }

    // -----------------------------------------------------------------------

    /** computes the ray intersection point of a cube centered on the
     *  origin with side length 2 (same bounds as the sphere)
     */
    boolean hit(Ray r, ISect intersection, boolean all, double minT)
    {
	double rayLength = r.getDirection().length();
	r.getDirection().normalize();
	minT *= rayLength;
	boolean objHit = false;
       
	double[] rayPos = new double[3];
	r.getPoint().get( rayPos);
	double[] rayDir = new double[3];
	r.getDirection().get( rayDir);
       
	double t1, t2;
       
	for (int i = 0; i < rayDir.length; i++) {
	    int i1 = (i+1) % 3;
	    int i2 = (i+2) % 3;
	    double left = -1.0;
	    double right = 1.0;
	    double dir = rayDir[i];
	    double pos = rayPos[i];
	   
	    if (dir != 0.0) {
		t1 = (left - pos) / dir;
		if (t1 >= minT && (!objHit || t1 < intersection.t)) {
		    Point3d iPoint = new Point3d((Tuple3d)r.getDirection());
		    iPoint.scaleAdd(t1, r.getPoint());
		   
		    double[] temp = new double[3];
		    iPoint.get(temp);
		    if (!(temp[i1] < left || temp[i1] > right ||
			  temp[i2] < left || temp[i2] > right)) {
		       
			objHit = true;
			intersection.t = t1;
			intersection.setHitPoint(iPoint);
			if (all) {
			    intersection.getNormal().set(0,0,0);
			    double[] nVector = new double[3];
			    intersection.getNormal().get(nVector);
			    nVector[i] = -1.0;
			    intersection.getNormal().set(nVector);
			    uvPlane(intersection, i1, i2);
			}
		    }
		}
		t2 = (right - pos) / dir;
		if (t2 >= minT && (!objHit || t2 < intersection.t)) {
		    Point3d iPoint = new Point3d((Tuple3d)r.getDirection());
		    iPoint.scaleAdd(t2, r.getPoint());
		    double[] temp = new double[3];
		    iPoint.get(temp);
		    if (!(temp[i1] < left || temp[i1] > right ||
			  temp[i2] < left || temp[i2] > right)) {
			objHit = true;
			intersection.setHitPoint(iPoint);
			intersection.t = t2;
			if (all) {
			    intersection.getNormal().set(0,0,0);
			    double[] nVector = new double[3];
			    intersection.getNormal().get(nVector);
			    nVector[i] = 1.0;
			    intersection.getNormal().set(nVector);
			    uvPlane(intersection, i1, i2);
			}
		    }
		}
	    }
	}

	if (objHit) {
	    intersection.t /= rayLength;
	    intersection.setHitObject(this);
	}
	return objHit;
    }
    
    private void uvPlane(ISect intersection, int x, int y)
    {
	double[] ipoint = new double[3];
	intersection.getHitPoint().get(ipoint);
	
	intersection.setU((ipoint[x]+1.0)/2);
	intersection.setV((ipoint[y]+1.0)/2);
	
	intersection.getDpDu().set(x == 0 ? 1 : 0,
				   x == 1 ? 1 : 0,
				   x == 2 ? 1 : 0);
	intersection.getDpDv().set(y == 0 ? 1 : 0,
				   y == 1 ? 1 : 0,
				   y == 2 ? 1 : 0);
    }
}
