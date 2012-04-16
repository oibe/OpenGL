/* class Ray
 * Represents a ray
 *
 * Doug DeCarlo
 */
import javax.vecmath.*;
import java.io.*;

class Ray
{
    // Ray origin (p)
    Point3d origin = new Point3d();

    // Ray direction (d)
    Vector3d direction = new Vector3d();

    // ---------------------------------------------------------------------

    /** Constructors */
    public Ray()
    {
    }

    public Ray(Point3d newOrigin, Vector3d newDirection)
    {
	origin = new Point3d(newOrigin);
	direction = new Vector3d(newDirection);
    }

    public Ray(Ray original)
    {
	origin = new Point3d(original.origin);
	direction = new Vector3d(original.direction);
    }

    // ---------------------------------------------------------------------
    // accessors

    public Point3d  getPoint()     { return origin; }
    public Vector3d getDirection() { return direction; }

    public void setOrigin(Point3d  newValue)    { origin.set(newValue); }
    public void setDirection(Vector3d newValue) { direction.set(newValue); }

    public String toString()
    {
	String result = new String("Ray : \n");
	result += "Point : " + origin + "\n";
	result += "Direction : " + direction + "\n";
	
	return result;
    }
}
