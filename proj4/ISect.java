/* class ISect
 * Represents the information to describe a ray-object intersection
 *
 * Doug DeCarlo
 */
import javax.vecmath.*;

public class ISect
{
    // reference to the intersected object (null if none)
    Shape hitObject = null;

    // intersection t value (measures distance from ray origin along ray
    // if ray direction normalized)
    double t = 0.0;

    // point of intersection
    Point3d hitPoint = new Point3d();

    // surface normal
    Vector3d normal  = new Vector3d();

    // texture coordinates (u,v)
    double u = 0.0;
    double v = 0.0;

    // surface derivatives along U anv V
    Vector3d dPdU = new Vector3d();
    Vector3d dPdV = new Vector3d();

    //------------------------------------------------------------------------
    // accessors

    double    getT()         { return t; }
    Shape     getHitObject() { return hitObject; }
    Point3d   getHitPoint()  { return hitPoint; }
    Vector3d  getNormal()    { return normal; }
    double    getU()         { return u; }
    double    getV()         { return v; }
    Vector3d  getDpDu()      { return dPdU; }
    Vector3d  getDpDv()      { return dPdV; }
    
    void setT(double newT)           { t = newT; }
    void setHitObject(Shape obj)     { hitObject = obj; }
    void setHitPoint (Point3d point) { hitPoint.set(point); }
    void setU(double newU)           { u = newU; }
    void setV(double newV)           { v = newV; }

    // Copy intersection
    void set(ISect other)
    {
        hitObject = other.hitObject;
        t = other.t;

        hitPoint.set(other.hitPoint);
        normal.set(other.normal);

        u = other.u;
        v = other.v;

        dPdU.set(other.dPdU);
        dPdV.set(other.dPdV);
    }

    public String toString()
    {
        String Result = new String();
        Result += "ISect : \n";
        Result += "t     : "+t + "\n";
        Result += "hitObj: "+hitObject+ "\n";
        Result += "hitP  : "+hitPoint+ "\n";
	
        return Result;
    }
}
