/* class Vertex
 *
 * Abstract representation for a mesh vertex -- a 3D point and normal vector 
 * (averaged from adjacent polygons)
 *
 * Instantiations of this interface are in UVShape and PolyMesh
 *
 * Doug DeCarlo
 */

import javax.vecmath.*;

public interface Vertex
{
    // Location of a vertex
    public abstract Point3d getPoint();

    // Normal of a vertex (for a PolyMesh, this is averaged from polygons
    // that contain this vertex)
    public abstract Vector3d getNormal();
}
