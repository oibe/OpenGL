/* class Light
 * Description of a light source
 *
 * Doug DeCarlo
 */
import java.io.*;
import java.text.ParseException;
import java.lang.reflect.*;
import javax.vecmath.*;

class Light extends RaytracerObject
{
    final public static String keyword = "light";
    
    /** parameters of the light source */

    // Location of light (one of these is always null), depending on
    // whether the light is directional light or not
    Point3d  position    = new Point3d();
    Vector3d direction   = null;

    // Light color (R, G, B)
    Vector3d color       = new Vector3d(1.0, 1.0, 1.0);

    // Light attenuation (Kc, Kl, Kq); default has no attenuation
    //    - given distance D from a light (not directional)
    //    - atten factor is 1/(Kc + Kl * D + Kq * D^2)
    //    - ambient light is not attenuated
    Vector3d attenuation = new Vector3d(1.0, 0.0, 0.0);

    //------------------------------------------------------------------------
	
    /** constructor that reads the content of the object from the tokenizer */
    public Light(StreamTokenizer tokenizer)
        throws ParseException, IOException, NoSuchMethodException,
        ClassNotFoundException,IllegalAccessException,
        InvocationTargetException
    {
        super(tokenizer);

        // add the parameters
        addSpec("position",     "setPosition",
                position.getClass().getName());
        addSpec("direction",    "setDirection", 
                (new Vector3d()).getClass().getName());
        addSpec("color",        "setColor",
                color.getClass().getName());
        addSpec("attenuation",  "setAttenuation",
                attenuation.getClass().getName());

        // read the content of this object
        read(tokenizer);
    }

    /** transform light location given matrix m */
    public void transform(Matrix4d m)
    {
        if (isDirectional()) {
            m.transform(direction);
        } else {
            m.transform(position);
        }
    }

    //------------------------------------------------------------------------

    // accessors
    public Point3d  getPosition()    { return position;    }
    public Vector3d getDirection()   { return direction;   }
    public Vector3d getColor()       { return color;       }
    public Vector3d getAttenuation() { return attenuation; }

    public void setPosition(Point3d p)
    {
        if (position != null)
          position.set(p);
        else
          position = new Point3d(p);

        direction = null;
    }

    public void setDirection(Vector3d d)
    {
        if (direction != null)
          direction.set(d);
        else
          direction = new Vector3d(d);
	
        // Direction is always normalized
        direction.normalize();
	
        position = null;
    }

    public void setColor(Vector3d c)        { color = c; }
    public void setAttenuation (Vector3d a) { attenuation = a; }

    /** For determining whether light is directional or position-based */
    public boolean isDirectional() { return direction != null; }

    /** For printing light specification */
    public void print(PrintStream out)
    {
        super.print(out);

        if (direction != null)
          out.println("Direction   : " + direction   );
        if (position != null)
          out.println("Position    : " + position   );
        out.println("Color       : " + color       );
        out.println("Attenuation : " + attenuation );
    }

    //------------------------------------------------------------------------

	public double a(Vector3d Li)
	{
		double length = Li.length();
		Vector3d atten = getAttenuation();
		double factor = 1.0/(atten.x+atten.y*length+atten.z*length*length);
		return factor;
	}

    /** compute the resulting color at an intersection point for
     *  _this_ light, which has been tinted (from shadowing), and given
     *  the ray that led to the intersection (which can be traced back
     *  to the camera location)
     *
     * The computation does the following:
     *  - computes ambient, diffuse and specular (Phong model) illumination
     *  - uses material color (Ka, Kd, Ks) and texture
     *  - handles both directional and (attenuated) point light sources
     *    (which are affected by shadows using 'tint')
     *
     * The 'tint' is used to specify the amount of the light that is being
     * let through to the intersection point.  An exposed light has a tint
     * of (1,1,1) and an occluded light has a tint of (0,0,0) -- intermediate
     * values can result from intervening transparent objects.
     * The tint does not affect the ambient light.
     */
    Vector3d compute(ISect intersection, Vector3d tint, Ray r)
    {
        // Material for this object
        Material mat = intersection.getHitObject().getMaterialRef();
        Vector3d Li = null;
        Point3d pos = getPosition();
        if(pos == null)
        {
        	Li = getDirection();
        }
        else
        {
		Li = new Vector3d(getPosition());
		Li.sub(intersection.getHitPoint());
	}
		
		Li.normalize();
		
        // Compute color
        	double shine = mat.getShiny();
        	//first light term
		Vector3d firstTerm = new Vector3d(color);
		Tools.termwiseMul3d(firstTerm, mat.getKa());
		if(mat.hasTexture())
		{
			Tools.termwiseMul3d(firstTerm, mat.getTextureColor(intersection.getU(), intersection.getV()));
		}
		
		//second light term
		Vector3d secondTerm = new Vector3d(color);
		
			if(pos == null)
			{
				secondTerm.scale(a(Li));
			}
			
		
		Tools.termwiseMul3d(secondTerm, tint);
		Tools.termwiseMul3d(secondTerm, mat.getKd());
		if(mat.hasTexture())
		{
			Tools.termwiseMul3d(secondTerm, mat.getTextureColor(intersection.getU(), intersection.getV()));
		}
		
		
		Vector3d norm = intersection.getNormal();
		norm.normalize();
		double nDotl = norm.dot(Li);
		secondTerm.scale(Math.max(0,nDotl));
		
		//third light term
		Vector3d thirdTerm = null;
			
		Vector3d result = new Vector3d();
		result.add(firstTerm,secondTerm);
		if(nDotl >= 0)
		{
			Vector3d Ri = new Vector3d();
			Vector3d v = new Vector3d(r.getDirection());
			v.normalize();
			v.scale(-1.0);
			Tools.reflect(Ri,Li,norm);
			Ri.normalize();
			double rDotv = Ri.dot(v);
			thirdTerm = new Vector3d(color);
			
			if(pos == null)
			{
				thirdTerm.scale(a(Li));
			}
			
			Tools.termwiseMul3d(thirdTerm, tint);
			Tools.termwiseMul3d(thirdTerm, mat.getKs());
			double specular = Math.pow(Math.max(0,rDotv),shine);
			thirdTerm.scale(specular);
			result.add(thirdTerm);
		}
		//System.out.println("x "+ result.x+" y "+result.y+" z "+ result.z);
		return result;
    }
}
