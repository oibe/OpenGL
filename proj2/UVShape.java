
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/* class UVShape
 * Abstract class which is extended to build parameterized shapes such
 * as ellipsoid or torus
 *
 * A regular UV grid is created for all shapes
 *
 * Doug DeCarlo
 */
public abstract class UVShape extends Shape
{
    // Resolution of (u,v) grid (number of vertices in u and v
    // directions on shape)
    int uSize, vSize;

    // Domain of (u,v) grid
    double uMin, uMax, vMin, vMax;

    // If true, then at uMin and uMax, domain is collapsed to a point
    // (like for a sphere) -- triangles will be produced in that case
    boolean uClosed;

    // Constructor: allocate space for vertex/polygons
    public UVShape(String name, int uSizeVal, int vSizeVal, boolean uClosedVal,
                   double uMinVal, double uMaxVal,
                   double vMinVal, double vMaxVal)
    {
        super(name);

        uSize = uSizeVal;
        vSize = vSizeVal;

        uClosed = uClosedVal;

        uMin = uMinVal;
        uMax = uMaxVal;

        vMin = vMinVal;
        vMax = vMaxVal;

        // -- Allocate a UV grid
        // Create a uSize X vSize grid of vertices
    	vertices = new Vertex[uSize * vSize];
        // Create polygons that span this grid
    	polygons = new Polygon[(uSize-1) * (vSize-1)];

        // -- Create a UV grid and polygon normals
        buildUVGrid();

        // Compute normal UV center locations for each polygon
        for (int i = 0; i < polygons.length; i++) {
            if (polygons[i] != null) {
                polygons[i].computeNormal();
            }
        }
    }

    // Vertex specification in a uv-parameterized shape
    private class VertexUV implements Vertex
    {
        // u,v coordinates of vertex: geometry is computed on the fly
        private double u, v;

        // Constructor for (u,v) vertex
        private VertexUV(double uVal, double vVal)
        {
            u = uVal;
            v = vVal;
        }

        // Accessors for uv coordinates
        private double getU() { return u; }
        private double getV() { return v; }

        // --------------------------------------------------------------------

        // Accessor for vertex location
        //  - normally, computes equation on the fly from uv
        //  - for GLSL, just returns (u,v,0)
        public Point3d getPoint()
        {
            if (useGLSL()) {
                // Return (u,v) in the point when using programmable
                // hardware (the vertex shader computes the position
                // using this)
                return new Point3d(u, v, 0);
            } else {
                // Compute surface location on the fly
                Point3d p = new Point3d();
                evalPosition(u, v, p);
                return p;
            }
        }

        // Accessor for vertex normal
        //  - normally, computes equation on the fly from uv
        //  - for GLSL, just returns (u,v,0)
        public Vector3d getNormal()
        {
            if (useGLSL()) {
                // Return (u,v) in the point when using programmable
                // hardware (the vertex shader computes the position
                // using this)
                return new Vector3d(u, v, 0);
            } else {
                // Compute normal on the fly
                Vector3d n = new Vector3d();
                evalNormal(u, v, n);
                return n;
            }
        }
    }

    // Polygon specification in a uv-parameterized shape
    private class PolygonUV extends PolygonAccess
    {
        // uv coordinates of center of polygon (for polygon normal)
        private double normalu, normalv;

        // Constructor when specifying polygon given a list of vertices
        private PolygonUV(Vertex[] polyVert)
        {
            super(polyVert);
            normalu = normalv = 0;
        }

        // --------------------------------------------------------------------

        // Accessor for polygon normal
        //  - normally, computes equation on the fly from uv
        //  - for GLSL, just returns (u,v,0) of the uv-center of polygon
        public Vector3d getNormal()
        { 
            if (useGLSL()) {
                // Return (u,v) in the center of polygon when using
                // programmable hardware (the vertex shader computes
                // the normal using this)
                return new Vector3d(normalu, normalv, 0);
            } else {
                // Compute normal on the fly
                Vector3d n = new Vector3d();
                evalNormal(normalu, normalv, n);
                return n;
            }
        }

        // Compute (u,v) of polygon normal (the mean of the uv's)
        public void computeNormal()
        {
            normalu = normalv = 0;
            
            // Find average of uv coordinates in polygon
            // (Note: this assumes that the grid is "open" and that
            //  2Pi is there, and it isn't wrapped around to 0...)
            for (int i = 0; i < size(); i++) {
                normalu += ((VertexUV)getVertex(i)).getU();
                normalv += ((VertexUV)getVertex(i)).getV();
            }
            normalu /= size();
            normalv /= size();
        }
    }

    // Abstract method for accessing the geometric properties
    // (a different evaluating scheme is specified for each uv primitive)
    public abstract void evalPosition(double u, double v, Point3d point);
    public abstract void evalNormal(double u, double v, Vector3d normal);

    // ---------------------------------------------------------------
    // Methods for building the uv grid

    // Methods for addressing individual vertices in the uv-grid by
    // indexed coordinate; this assumes the mesh was allocated as above
    // in the constructor

    // Set the vertex to v that corresponds to grid position (uIndex, vIndex)
    // (assuming there are vSize columns)
    public void setUVGridVertex(int uIndex, int vIndex, Vertex v)
    {
        int pos = uIndex * vSize + vIndex;

        vertices[pos] = v;
    }

    // Get the vertex that corresponds to grid position (uIndex, vIndex)
    // (assuming there are vSize columns)
    public Vertex getUVGridVertex(int uIndex, int vIndex)
    {
        int pos = uIndex * vSize + vIndex;

        return vertices[pos];
    }

    // Build a grid of vertices that spans [uMin,uMax] x [vMin,vMax]
    // and connect them by polygons (quads)
    // (Note: polygons at the poles are triangles if uClosed is true,
    //  because otherwise the silhouettes break on buggy graphics cards)
    public void buildUVGrid()
    {
    	// Build vertices
    	for (int i = 0; i < uSize; i++) {
    	    for (int j = 0; j < vSize; j++) {
                double u = uMin + (uMax - uMin) * i / (uSize - 1);
                double v = vMin + (vMax - vMin) * j / (vSize - 1);

                // Add new vertex
                setUVGridVertex(i, j, new VertexUV(u, v));
    	    }
    	}

    	// Build polygons
    	for (int i = 0; i < uSize-1; i++) {
    	    for (int j = 0; j < vSize-1; j++) {
                // Check if at poles
                boolean tri = uClosed && (j == 0 || j == vSize-2);
                // Allocate polygon vertices
    		Vertex[] verts = new Vertex[tri ? 3 : 4];

                int v = 0;
    		verts[v++] = getUVGridVertex(i,j);
                if (!uClosed || j != 0)
                  verts[v++] = getUVGridVertex(i+1,j);
                if (!uClosed || j != vSize-2)
                  verts[v++] = getUVGridVertex(i+1,j+1);
    		verts[v++] = getUVGridVertex(i,j+1);
    
    		addPolygon(new PolygonUV(verts));
    	    }
    	}
    }
}
