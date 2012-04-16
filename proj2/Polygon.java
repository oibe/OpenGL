/* class Polygon
 *
 * Abstract representation of a mesh polygon -- a set of vertices and
 * a surface normal for the polygon
 *
 * Instantiations of this interface are in UVShape and PolyMesh
 *
 * Doug DeCarlo
 */

import javax.vecmath.*;

public interface Polygon
{
    // Number of vertices in a polygon
    public abstract int size();

    // Accessors for vertices in a polygon: for the actual vertices,
    // index ranges from 0 to size()
    // 
    // When accessing vertices outside this range, they indices "wrap
    // around".  So that getVertex(-1) is the same as getVertex(size()-1)
    // This is used to simplify your code by eliminating special
    // cases, by allowing you to do something like:
    // 
    //     v[0] = getVertex(i-1);
    //     v[1] = getVertex(i);
    //     v[2] = getVertex(i+1);
    //
    // without having to worry about boundaries or wrap around (such
    // as when i=0 or i=size()-1.
    // 
    // This still preserves the order of the vertices
    // (i.e. counter-clockwise)
    public abstract Vertex getVertex(int index);
    public abstract void setVertex(int index, Vertex v);

    // ----------------------------------------------------------------

    // Normal vector of polygon
    public abstract Vector3d getNormal();

    // Compute the normal vector of a polygon
    public abstract void computeNormal();
}
