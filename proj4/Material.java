/* class Material
 * Description of a material
 *
 * Doug DeCarlo
 */
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import javax.vecmath.*;
import java.text.ParseException;

class Material extends RaytracerObject
{
    final public static String keyword = "material";
    
    // Ka (R, G, B)
    Vector3d ambient     = new Vector3d();

    // Kd (R, G, B)
    Vector3d diffuse     = new Vector3d();

    // Ks (R, G, B)
    Vector3d specular    = new Vector3d();

    // Kt (R, G, B)
    Vector3d transparent = new Vector3d();

    // Index of refraction
    double index       = 1.0;

    // Shininess (exponent)
    double shininess   = 1.0;
    
    // Texture image specification
    String textureFileName     = null;
    RGBImage textureImage      = null;

    // Texture repeating factors
    double textureScaleU       = 1.0;
    double textureScaleV       = 1.0;

    // Checker specification
    Vector3d checkerColor1     = null;
    Vector3d checkerColor2     = null;

    // ----------------------------------------------------------------------

    /** Steps for adding a custom material:
     *   - add class variables for specifying material
     *   - write a setXXXSpec() and readXXXSpec() method, and
     *     pass it, along with the material name XXX to addSpecSpecial()
     *     in the Material(tokenizer) constructor
     *   - add a check (using ||) in hasTexture()
     *   - put in another check in getTextureColor() that will call
     *     getXXXColor(u,v)
     *   - if it needs an image, add that to the setup() method
     *   - add variables and methods 
     */

    /** constructor for default material */
    public Material(String matName)
           throws ParseException, IOException, NoSuchMethodException,
                  ClassNotFoundException,IllegalAccessException,
                  InvocationTargetException
    {
	super(null);

	name = matName;
	ambient = new Vector3d(0.2, 0.2, 0.2);
	diffuse = new Vector3d(0.8, 0.8, 0.8);
    }

    /** constructor that reads the content of the object from the tokenizer */
    public Material(StreamTokenizer tokenizer)
           throws ParseException, IOException, NoSuchMethodException,
                  ClassNotFoundException,IllegalAccessException,
                  InvocationTargetException
    {
	super(tokenizer);

	// add the parameters
	addSpec("ka", "setKa", ambient.getClass().getName());
	addSpec("kd", "setKd", diffuse.getClass().getName());
	addSpec("ks", "setKs", specular.getClass().getName());
	addSpec("kt", "setKt", transparent.getClass().getName());
	addSpec("index", "setIndex", "java.lang.Double");
	addSpec("shiny", "setShiny", "java.lang.Double");

	addSpecSpecial("texture", "setTextureSpec", "readTextureSpec");
	addSpecSpecial("checker", "setCheckerSpec", "readCheckerSpec");

	// read the content of this object
	read(tokenizer);
    }

    /** Set up materials (read in textures) */
    public void setup(boolean verbose)
    {
        // read the texture file
        if (textureFileName != null) {
            try {
		if (verbose)
		  System.out.println("Loading texture " + textureFileName);

                textureImage = new RGBImage(textureFileName);
            } catch (IOException E) {
                System.err.println("Error while reading file " + 
				   textureFileName);
                textureImage = null;
                textureFileName = null;
            }
	}
    }

    // accessors
    public Vector3d getKa()      { return ambient; }
    public Vector3d getKd()      { return diffuse; }
    public Vector3d getKs()      { return specular; }
    public Vector3d getKt()      { return transparent; }
    public double   getShiny()   { return shininess; }
    public double   getIndex()   { return index; }
    
    public void setKa(Vector3d a)    { ambient     = a; }
    public void setKd(Vector3d d)    { diffuse     = d; }
    public void setKs(Vector3d s)    { specular    = s; }
    public void setKt(Vector3d t)    { transparent = t; }
    public void setShiny(Double s)   { shininess   = s.doubleValue(); }
    public void setIndex(Double r)   { index       = r.doubleValue(); }
    public void setTextureFileName(String s) { textureFileName = s; }

    //------------------------------------------------------------------------

    // properties
    public boolean isMatte()    { return specular.equals(new Vector3d()); }
    public boolean isOpaque()   { return transparent.equals(new Vector3d()); }

    //------------------------------------------------------------------------

    /** Check if any valid texture is present */
    public boolean hasTexture()
    {
	return textureImage != null || checkerColor1 != null;
    }

    /** returns the texture color corresponding to the u, v coordinates */
    public Vector3d getTextureColor(double u, double v)
    {
	if (checkerColor1 != null) {
	    return getCheckerColor(u,v);
	} else if (textureImage != null) {
	    return getTextureImageColor(u,v);
	} else {
	    // Illegal texture specification
	    System.err.println("Unknown texture specification");
	    return null;
	}
    }

    //------------------------------------------------------------------------
    // Texture image

    // Parser
    public static Vector readTextureSpec(StreamTokenizer tokenizer)
        throws ParseException, IOException
    {
	Vector<Object> v = new Vector<Object>();

	// texture = filename uScale vScale
	v.addElement(Parser.readString(tokenizer));
	v.addElement(Parser.readDouble(tokenizer));
	v.addElement(Parser.readDouble(tokenizer));

	return v;
    }
    // Setter
    public void setTextureSpec(Vector v)
    {
	textureFileName = (String)v.elementAt(0);
	textureScaleU   = ((Double)v.elementAt(1)).doubleValue();
	textureScaleV   = ((Double)v.elementAt(2)).doubleValue();
    }

    /** returns the image color corresponding to the u, v coordinates */
    public Vector3d getTextureImageColor(double u, double v)
    {
	return textureImage.getSubPixel((u * textureScaleU) % 1,
					(v * textureScaleV) % 1);
    }

    //------------------------------------------------------------------------
    // Procedural checker texture

    public static Vector readCheckerSpec(StreamTokenizer tokenizer)
        throws ParseException, IOException
    {
	Vector<Object> v = new Vector<Object>();

	// checker = color1 color2 uScale vScale
	v.addElement(Parser.readVector3d(tokenizer));
	v.addElement(Parser.readVector3d(tokenizer));
	v.addElement(Parser.readDouble(tokenizer));
	v.addElement(Parser.readDouble(tokenizer));

	return v;
    }
    public void setCheckerSpec(Vector v)
    {
	checkerColor1   = (Vector3d)v.elementAt(0);
	checkerColor2   = (Vector3d)v.elementAt(1);
	textureScaleU   = ((Double)v.elementAt(2)).doubleValue();
	textureScaleV   = ((Double)v.elementAt(3)).doubleValue();
    }
    public Vector3d getCheckerColor(double u, double v)
    {
	double scu = textureScaleU, scv = textureScaleV;

	if ((int)(Math.floor(u * scu) + Math.floor(v * scv)) % 2 == 0) {
	    return checkerColor1;
	} else {
	    return checkerColor2;
	}
    }

    //------------------------------------------------------------------------

    public void print(PrintStream out)
    {
	super.print(out);

	out.println("Ambient     : " + getKa());
	out.println("Diffuse     : " + getKd());
	out.println("Specular    : " + getKs());
	out.println("Transparent : " + getKt());
	out.println("Shininess   : " + getShiny());
	out.println("Refraction  : " + getIndex());
    }
}
