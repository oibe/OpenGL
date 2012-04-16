/* class Cone
 * Ray-object intersections for a cone
 *
 * Doug DeCarlo
 */
import java.io.*;
import javax.vecmath.*;
import java.text.ParseException;
import java.lang.reflect.*;

class Cone extends Shape
{
    public static String keyword = "cone";

    /** constructor from tokenizer */
    public Cone(StreamTokenizer tokenizer)
	throws ParseException, IOException, NoSuchMethodException,
	       ClassNotFoundException,IllegalAccessException,
	       InvocationTargetException
	       
    {
	super(tokenizer);
	read(tokenizer);
    }

    // -----------------------------------------------------------------------

    /** computes the ray intersection point of a cone with base radius 1
     *  on the z axis from z=0 to z=1, with the tip at z=0 (and base at z=1)
     */
    boolean hit(Ray r, ISect intersection, boolean all, double minT)
    {
	ISect icone = new ISect(), icap1 = new ISect();
	boolean hit_cone, hit_cap1;

	hit_cone = hitConeCap(r, icone, all, minT);
	hit_cap1 = hitCap1(r, icap1, all, minT);

	if (hit_cone && (!hit_cap1 || icone.t < icap1.t)) {
	    intersection.set(icone);
	} else if (hit_cap1 && (!hit_cone || icap1.t < icone.t)) {
	    intersection.set(icap1);
	} else {
	    return false;
	}

	intersection.setHitObject(this);

	return true;

    }

    private boolean hitConeCap(Ray r, ISect intersection, boolean all,
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

 	a = rd.x * rd.x + rd.y * rd.y - rd.z * rd.z;
	b = rd.x * rp.x + rd.y * rp.y - rd.z * rp.z;
	c = rp.x * rp.x + rp.y * rp.y - rp.z * rp.z;

	if (Math.abs(a) < epsilon) {
	    /* One intersection point */
	    t1 = -0.5*c / b;
	    z1 = rp.z + t1 * rd.z;

	    if (t1 < minT || z1 < 0 || z1 > 1)
	      return false;

	    intersection.t = t1;
	} else {
	    d = b*b - a*c;

	    if (d < 0.0)
	      return false;
	    d = Math.sqrt(d);

	    t1 = (-b + d) / a;
	    t2 = (-b - d) / a;

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
	}

	if (all) {
	    Point3d ipoint = intersection.getHitPoint();
	    Vector3d inorm = intersection.getNormal();

	    ipoint.scale(intersection.t, rd);
	    ipoint.add(rp);

	    Vector3d ipv = new Vector3d(ipoint);

	    inorm.cross(ipv, new Vector3d(0,0,1));
	    inorm.cross(ipv, inorm);
	    inorm.normalize();

	    uvConeCap(intersection);
	}

	intersection.t /= dlen;

	return true;
    }

    private void uvConeCap(ISect intersection)
    {
	Point3d ipoint = intersection.getHitPoint();

	intersection.setV(ipoint.z);

	double val = ipoint.x / ipoint.z;

	if (val > 1.0) {
	    intersection.setU(0.0);
	} else if (val < -1.0) {
	    intersection.setU(0.5);
	} else {
	    intersection.setU(Math.acos(val) / (2*Math.PI));
	}

	if (ipoint.y < 0)
	    intersection.setU(1 - intersection.getU());

	intersection.getDpDu().set(-ipoint.y, ipoint.x, 0);
	intersection.getDpDu().normalize();

	intersection.getDpDv().set(ipoint);
	intersection.getDpDv().normalize();
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
