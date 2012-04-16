/* class Shape
 * Abstract class for geometric objects in the scene
 *
 * Doug DeCarlo
 */
import java.io.*;
import java.util.*;
import javax.vecmath.*;
import java.text.ParseException;
import java.lang.reflect.*;

abstract class Shape extends RaytracerObject
{
    // stores the name of the material associatef with this object
    String materialName = new String();

    // a reference to the associated material (this may be set after the
    // object is created (allowing to specify materials after the object
    // has been created )
    Material materialRef = null;

    // Object transformation M
    Matrix4d M;
    // Inverse of M
    Matrix4d MInverse;
    // Transpose Inverse of M (just the 3x3 part is transposed)
    Matrix4d MTInverse;

    // Hierarchical object management
    VectorHierarchy<Shape> parent = null;
    VectorHierarchy<Shape> children = new VectorHierarchy<Shape>(this);

    //------------------------------------------------------------------------

    /** enforcing the presence of this constructor */
    public Shape(StreamTokenizer tokenizer)
           throws ParseException, IOException, NoSuchMethodException,
                  ClassNotFoundException,IllegalAccessException,
                  InvocationTargetException
    {
	super(tokenizer);

	M = new Matrix4d();
	MInverse = new Matrix4d();
	MTInverse = new Matrix4d();

	addSpec("material", "setMaterialName",
		materialName.getClass().getName());
    }

    //------------------------------------------------------------------------

    /** computes the intersection of the ray with the scene and
     *  returns true if this object was hit, false if not
     *
     * If an object is hit, the intersection holds the details of the
     * hit point that has the smallest t value above minT
     *
     * If computeAllFields is false, only the t value is computed (no
     * point, normal, texture coordinates)
     */
    abstract boolean hit(Ray r, ISect intersection,
			 boolean computeAllFields, double minT);

    // -----------------------------------------------------------------------

    public String   getMaterialName() { return materialName; }
    public Material getMaterialRef () { return materialRef; }

    public Matrix4d getMatrix()       { return M; }
    public Matrix4d getInvMatrix()    { return MInverse; }
    public Matrix4d getInvTMatrix()   { return MTInverse; }

    public void setMaterialName(String newName) { materialName = newName; }
    public void setMaterialRef(Material newRef) { materialRef = newRef; }

    /** set the object transformation, and compute inverse */
    public void setMatrix(Matrix4d mat)
    {
	M.set(mat);

	// Compute inverse
	MInverse.invert(M);

	// Compute inverse transpose
	Matrix3d invRS = new Matrix3d();
	MInverse.getRotationScale(invRS);
	invRS.transpose();

	Vector3d translation = new Vector3d();
	MInverse.get(translation);
	MTInverse.set(invRS, translation, 1.0);
    }

    public void print(PrintStream out)
    {
	super.print(out);
	
	out.println("Material : " + materialName);
	out.println("M        : " + M);
	out.println("Minv     : " + MInverse);
	out.println("MTinv    : " + MTInverse);
    }
}
