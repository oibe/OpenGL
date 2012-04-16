/* class PolygonAccess
 *
 * Abstract class that implements the common aspects of any polygon
 *  - the array of vertices
 *  - the related accessors for those vertices
 */

public abstract class PolygonAccess implements Polygon
{
    // Vertices in the polygon
    private Vertex vert[];

    // Constructor when specifying polygon of a particular size
    //  (the vertices need to be filled in, all initially are null)
    public PolygonAccess(int num)
    {
        vert = new Vertex[num];
    }

    // Constructor when specifying polygon given a list of vertices
    public PolygonAccess(Vertex[] polyVert)
    {
        vert = polyVert;
    }
    
    //-------------------------------------------------------------------
    
    // Accessor for number of vertices
    public int size() 
    {
        return vert.length;
    }
    
    // Vertex accessors (MOD size)
    public Vertex getVertex(int index)
    {
        index %= vert.length;
        if (index < 0) index += vert.length;
        return vert[index];
    }
    public void setVertex(int index, Vertex v)
    {
        index %= vert.length;
        if (index < 0) index += vert.length;
        vert[index] = v;
    }
}
