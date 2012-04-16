/* class Sphere
 * Ray-object intersections for a sphere
 *
 * Doug DeCarlo
 */
import java.io.*;
import javax.vecmath.*;
import java.text.ParseException;
import java.lang.reflect.*;

class Sphere extends Shape
{
    public static String keyword = "sphere";

    /** constructor from tokenizer */
    public Sphere(StreamTokenizer tokenizer)
	throws ParseException, IOException, NoSuchMethodException,
	       ClassNotFoundException,IllegalAccessException,
	       InvocationTargetException
	       
    {
	super(tokenizer);
	read(tokenizer);
    }

    // -----------------------------------------------------------------------

    /** computes the ray intersection point of a sphere at the origin with
     *  radius 1
     */
    boolean hit(Ray r, ISect intersection, boolean all, double minT)
    {
    	
	double dirLength = r.getDirection().length();
	r.getDirection().normalize();
	minT *= dirLength;
	Vector3d v = new Vector3d(r.getPoint());

	double b   = -v.dot(r.getDirection());
	double d   = b*b - v.lengthSquared()+1;

	if (d<0.0) return false;

	d = Math.sqrt(d);
	double secondT = b + d;

	if (secondT <= minT) return false;

	double firstT  = b - d;

	intersection.t = firstT > minT ? firstT : secondT;
	intersection.setHitObject(this);
	if (all) {
	    Point3d hitPoint = intersection.getHitPoint();

	    hitPoint.set(r.getDirection());
	    hitPoint.scale(intersection.t);
	    hitPoint.add(r.getPoint());
	    
	    intersection.getNormal().set(hitPoint);
	    intersection.getNormal().normalize();
	    
	    uvSphere(intersection);
	}
	
	intersection.t /= dirLength;

	return true;
    }

    private void uvSphere(ISect intersection)
    {
	double phi, epsilon = 1e-6;

	Point3d ipoint = intersection.getHitPoint();
	
	if (ipoint.z > 1.0)
	    phi = Math.PI;
	else if (ipoint.z < -1)
	    phi = 0.0;
	else
	    phi = Math.acos(-ipoint.z);

	intersection.setV(phi/Math.PI);
	
	double absV = Math.abs(intersection.getV());
	if (absV < epsilon || absV > 1 - epsilon)
	    intersection.setU(0.0);
	else {
	    double theta = ipoint.x / Math.sin(phi);
	    if (theta > 1.0)
		theta = 0.0;
	    else if (theta < -1.0)
		theta = 0.5;
	    else
		theta = Math.acos(theta) / (2*Math.PI);
	    
	    if (ipoint.y > 0)
	      intersection.setU(theta);
	    else
	      intersection.setU(1 - theta);
	}
	
	intersection.getDpDu().set(-ipoint.y, ipoint.x, 0);
	intersection.getDpDu().normalize();
	
	intersection.getDpDv().cross(new Vector3d(ipoint),
				     intersection.getDpDu());
	intersection.getDpDv().normalize();
    }
}
