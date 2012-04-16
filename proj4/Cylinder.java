/* class Cylinder
 * Ray-object intersections for a cylinder
 *
 * Doug DeCarlo
 */
import java.io.*;
import javax.vecmath.*;
import java.text.ParseException;
import java.lang.*;
import java.lang.reflect.*;

class Cylinder extends Shape
{
    public static String keyword = "cylinder";

    /** constructor from tokenizer */
    public Cylinder(StreamTokenizer tokenizer)
	throws ParseException, IOException, NoSuchMethodException,
	       ClassNotFoundException,IllegalAccessException,
	       InvocationTargetException
	       
    {
	super(tokenizer);
	read(tokenizer);
    }

    // -----------------------------------------------------------------------

    /** computes the ray intersection point of a cylinder of radius 1
     *  on the z axis from z=0 to z=1
     */
    boolean hit(Ray r, ISect intersection, boolean all, double minT)
    {
	ISect itube = new ISect(), icap0 = new ISect(), icap1 = new ISect();
	boolean hit_tube, hit_cap0, hit_cap1;

	hit_tube = hitTube(r, itube, all, minT);
	hit_cap0 = hitCap0(r, icap0, all, minT);
	hit_cap1 = hitCap1(r, icap1, all, minT);

	if (hit_tube && 
	    (!hit_cap0 || itube.t < icap0.t) && 
	    (!hit_cap1 || itube.t < icap1.t)) {
	    intersection.set(itube);
	} else if (hit_cap0 && 
		   (!hit_tube || icap0.t < itube.t) && 
		   (!hit_cap1 || icap0.t < icap1.t)) {
	    intersection.set(icap0);
	} else if (hit_cap1 && 
		   (!hit_tube || icap1.t < itube.t) && 
		   (!hit_cap0 || icap1.t < icap0.t)) {
	    intersection.set(icap1);
	} else {
	    return false;
	}

	intersection.setHitObject(this);

	return true;
    }

    private boolean hitTube(Ray r, ISect intersection, boolean all,
			    double minT)
    {
	double dlen, a, b, c, d, t1, t2, z1, z2;
	boolean t1bad, t2bad;
	double epsilon = 1e-6;

	Point3d rp = r.getPoint();
	Vector3d rd = new Vector3d(r.getDirection());
	dlen = rd.length();
	rd.normalize();

	minT *= dlen;

	a = rd.x * rd.x + rd.y * rd.y;
	if (a < epsilon * epsilon)
	  return false;

	b = rd.x * rp.x + rd.y * rp.y;
	c = rp.x * rp.x + rp.y * rp.y - 1;
	d = b*b - a*c;

	if (d < 0.0)
	  return false;
	d = Math.sqrt(d);

	t1 = (-b + d) / a;
	t2 = (-b - d) / a;

	if (t1 < minT && t2 < minT)
	  return false;

	z1 = rp.z + t1 * rd.z;
	z2 = rp.z + t2 * rd.z;

	t1bad = (t1 < minT || z1 < 0.0 || z1 > 1.0);
	t2bad = (t2 < minT || z2 < 0.0 || z2 > 1.0);

	if (t1bad && t2bad) {
	    return false;
	} else if (t1bad) {
	    intersection.t = t2;
	} else if (t2bad) {
	    intersection.t = t1;
	} else {
	    intersection.t = (t1 < t2) ? t1 : t2;
	}

	if (all) {
	    Point3d ipoint = intersection.getHitPoint();
	    Vector3d inorm = intersection.getNormal();

	    ipoint.scale(intersection.t, rd);
	    ipoint.add(rp);

	    inorm.set(ipoint.x, ipoint.y, 0);
	    inorm.normalize();

	    uvTube(intersection);
	}

	intersection.t /= dlen;

	return true;
    }

    private void uvTube(ISect intersection)
    {
	Point3d ipoint = intersection.getHitPoint();

	intersection.setV(ipoint.z);

	if (ipoint.x > 1.0) {
	    intersection.setU(0.0);
	} else if (ipoint.x < -1.0) {
	    intersection.setU(0.5);
	} else {
	    intersection.setU(Math.acos(ipoint.x) / (2*Math.PI));
	}
	    
	if (ipoint.y < 0)
	    intersection.setU(1 - intersection.getU());

	intersection.getDpDu().set(-ipoint.y, ipoint.x, 0);
	intersection.getDpDu().normalize();

	intersection.getDpDv().set(0, 0, 1);
    }


    private boolean hitCap0(Ray r, ISect intersection, boolean all,
			    double minT)
    {
	double pz, dz, t, dlen;
	Point3d rp = r.getPoint();
	Vector3d rd = new Vector3d(r.getDirection());
	dlen = rd.length();
	rd.normalize();

	minT *= dlen;

	pz = rp.z;
	dz = rd.z;

	if (dz == 0.0)
	  return false;

	t = -pz / dz;

	if (t < minT)
	  return false;

	Point3d ipoint = intersection.getHitPoint();
	ipoint.scale(t, rd);
	ipoint.add(rp);

	if (ipoint.x * ipoint.x + ipoint.y * ipoint.y <= 1.0) {
	    intersection.t = t / dlen;

	    if (all) {
		intersection.getNormal().set(0, 0, -1);
		
		uvPlane(intersection, 0, 1);
	    }
	} else {
	    return false;
	}

	return true;
    }

    private boolean hitCap1(Ray r, ISect intersection, boolean all, 
			    double minT)
    {
	double pz, dz, t, dlen;
	Point3d rp = r.getPoint();
	Vector3d rd = new Vector3d(r.getDirection());
	dlen = rd.length();
	rd.normalize();

	minT *= dlen;

	pz = rp.z;
	dz = rd.z;

	if (dz == 0.0)
	  return false;

	t = (1 - pz) / dz;

	if (t < minT)
	  return false;

	Point3d ipoint = intersection.getHitPoint();
	ipoint.scale(t, rd);
	ipoint.add(rp);

	if (ipoint.x * ipoint.x + ipoint.y * ipoint.y <= 1.0) {
	    intersection.t = t / dlen;

	    if (all) {
		intersection.getNormal().set(0, 0, 1);
		
		uvPlane(intersection, 0, 1);
	    }
	} else {
	    return false;
	}

	return true;
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
