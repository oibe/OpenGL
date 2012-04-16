/* class Tools
 * This class implements some functions for computations involved in the
 * ray tracer
 *
 * Doug DeCarlo
 */
import javax.vecmath.*;

class Tools
{
    /** Termwise-multiply each component of target by p
     *  (useful for scaling light by Ka, Kd, Ks, ..., for instance)
     */
    public static void termwiseMul3d(Vector3d target, Vector3d p)
    {
	target.x *= p.x;
	target.y *= p.y;
	target.z *= p.z;
    }

    /** Reflect the incident vector around the normal vector
     *
     *  target = 2 * (normal . incident) * normal - incident
     */
    public static void reflect(Vector3d target, Vector3d incident,
			       Vector3d normal)
    {
	double s = 2 * normal.dot(incident);

	target.set(normal);
	target.scale(s);
	target.sub(incident);
	target.normalize();
    }

    /* Refract incoming light vector in using normal n, and the index of
     * refraction of the material being exited (t1) and entered (t2).
     * If there is not total internal reflection, compute the refracted ray
     * in target and return true.
     * (Note: the incoming ray direction in must be pointing *at* the surface,
     * partly in the opposite direction as n)
     */
    public static boolean refract(Vector3d target, Vector3d in, Vector3d n,
                                  double t1, double t2)
    {
	double eta = t1 / t2;

	/* cos(theta1) */
	double cosT1 = -in.dot(n);

	/* cos^2(theta2) */
	double cos2T2 = 1 - eta * eta * (1- cosT1 * cosT1);

	/* Total internal reflection */
	if (cos2T2 < 0.0) return false;
	
	Vector3d temp = new Vector3d();
	temp.scale(eta * cosT1 - Math.sqrt(cos2T2), n);
	
	target.scale(eta, in);
	target.add(temp);
	return true;
    }
}
