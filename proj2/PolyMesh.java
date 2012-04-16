/* class Polymesh
 * Class for representing a polygon mesh shape
 *
 * Doug DeCarlo
 */


import javax.vecmath.*;

import java.io.*;
import java.util.*;

public class PolyMesh extends Shape
{
    // Constructor
    public PolyMesh(String filename)
    {
	super(filename);

        // Read polygon mesh in from file
        read(filename);

        // Compute area-weighted polygon and vertex normal vectors
        computeAllNormals();
    }

    // Vertex specification in a polygon mesh
    private class VertexPM implements Vertex
    {
        // 3D location of vertex
        private Point3d point;
        // Normal vector at vertex (averaged from nearby polygons)
        private Vector3d normal;
        
        // Constructor
        public VertexPM()
        {
            point  = new Point3d();
            normal = new Vector3d();
        }

        //---------------------------------------------------------------------

        // Accessors
        public Point3d getPoint() { return point; }
        public Vector3d getNormal() { return normal; }
        
        private void setPoint(Point3d newPoint) { point = newPoint; }
        private void setNormal(Vector3d newNormal) { normal = newNormal; }
    }

    // Polygon specification in a polygon mesh
    public class PolygonPM extends PolygonAccess
    {
        // Polygon normal vector
        private Vector3d normal;

        // Constructor when specifying polygon of a particular size
        //  (the vertices need to be filled in, all initially are null)
        private PolygonPM(int num)
        {
            super(num);
            normal = new Vector3d();
        }
        
        // --------------------------------------------------------------------
        
        // Accessor for polygon normal
        public Vector3d getNormal()
        { 
            return normal; 
        }
        
        // Compute unnormalized normal from vertices of polygon
        // using Newell's method
        public void computeNormal()
        {
            // Zero polygon normal
            normal.set(0,0,0);
            
            for (int i = 0; i < size(); i++) {
                // Vertex i and i+1 (modulo size)
                Point3d p =  getVertex(i).getPoint();
                Point3d pn = getVertex(i+1).getPoint();

                // Newell's method
                normal.x += (p.y - pn.y) * (p.z + pn.z);
                normal.y += (p.z - pn.z) * (p.x + pn.x);
                normal.z += (p.x - pn.x) * (p.y + pn.y);
            }
            // Don't normalize the final result
            // ...need area weighting for later
        }
    }

    // Compute all polygon and vertex normal vectors
    private void computeAllNormals()
    {
    	// ....
    	for(int i = 0; i < polygons.length;i++){
    		Polygon poly = polygons[i];
    		poly.computeNormal();
    		for(int j = 0; j < poly.size();j++){
    			poly.getVertex(j).getNormal().x += poly.getNormal().x;
    			poly.getVertex(j).getNormal().y += poly.getNormal().y;
    			poly.getVertex(j).getNormal().z += poly.getNormal().z;
    		}
    		poly.getNormal().normalize();
    	}
    	for(int z = 0; z < vertices.length;z++){
    		vertices[z].getNormal().normalize();
    	}
    }
    
    // --------------------------------------------------------------------

    // Rescale object so it fits in viewpoint
    private void rescale()
    {
        if (vertices.length == 0)
          return;

        // Find the 3D bounding box
        Point3d bbmin = new Point3d(vertices[0].getPoint());
        Point3d bbmax = new Point3d(vertices[0].getPoint());
        for (int i = 1; i < vertices.length; i++) {
            Point3d point = vertices[i].getPoint();

            bbmin.x = Math.min(bbmin.x, point.x);
            bbmin.y = Math.min(bbmin.y, point.y);
            bbmin.z = Math.min(bbmin.z, point.z);

            bbmax.x = Math.max(bbmax.x, point.x);
            bbmax.y = Math.max(bbmax.y, point.y);
            bbmax.z = Math.max(bbmax.z, point.z);
        }

        // Center of the bounding box
        Point3d center = new Point3d();
        center.interpolate(bbmin, bbmax, 0.5);

        // Diagonal of the bounding box
        Vector3d diag = new Vector3d();
        diag.sub(bbmax, bbmin);
        
        // Maximum dimension of bounding box
        double maxBBox = Math.max(Math.max(diag.x, diag.y), diag.z);

        // Transform resulting vertices
        for (int i = 0; i < vertices.length; i++) {
            // Translate center of box to origin
            vertices[i].getPoint().sub(center);

            // Scale to fit bounding box in viewpoint, with slack
            vertices[i].getPoint().scale(3.2/maxBBox);
        }
    }

    // Read Wavefront OBJ file (only vertex and polygon information)
    private void read(String filename)
    {
        FileReader input;
        StreamTokenizer tokenizer = null;
        Vector<Vertex> vlist = new Vector<Vertex>();
        Vector<Vector> plist = new Vector<Vector>();

        try {
            input = new FileReader(filename);
            tokenizer = new StreamTokenizer(input);
            tokenizer.commentChar('#');
            tokenizer.commentChar('$');
            tokenizer.slashSlashComments(false);
            tokenizer.slashStarComments(false);
            tokenizer.eolIsSignificant(true);
            tokenizer.ordinaryChar('/');

            while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
                switch (tokenizer.ttype) {
                  case StreamTokenizer.TT_WORD:
                    // Next command
                    parseLine(tokenizer, vlist, plist);
                    break;
                  case StreamTokenizer.TT_EOL:
                    break;
                  default:
                    skipLine(tokenizer);
                }
            }

            input.close();

            // Create vertices
            vertices = new Vertex[vlist.size()];
            for (int i = 0; i < vlist.size(); i++) {
                vertices[i] = (Vertex)(vlist.elementAt(i));
            }

            // Create polygons
            polygons = new Polygon[plist.size()];
            for (int i = 0; i < plist.size(); i++) {
                Vector pi = (Vector)(plist.elementAt(i));

                polygons[i] = new PolygonPM(pi.size());

                for (int j = 0; j < pi.size(); j++) {
                    int pij = ((Integer)(pi.elementAt(j))).intValue();

                    polygons[i].setVertex(j, vertices[pij]);
                }
            }
        } catch (FileNotFoundException E) {
            System.err.println("File not found: " + filename);
            System.exit(1);
        } catch (SecurityException E) {
            System.err.println("Cannot access file: " + filename);
            System.exit(1);
        } catch (IOException E) {
            if (tokenizer != null) {
                System.err.println("Error reading file: " + filename +
                                   " [line " + tokenizer.lineno() + "]");
            } else {
                System.err.println("Error reading file: " + filename);
            }
            System.exit(1);
        }

        rescale();
    }

    // Process next command; accumulate results in v/plist
    private void parseLine(StreamTokenizer tokenizer,
                          Vector<Vertex> vlist, Vector<Vector> plist)
        throws IOException
    {
        if (tokenizer.sval.equals("v")) {
            // Vertex command
            VertexPM v = new VertexPM();

            v.setPoint(new Point3d(parseDouble(tokenizer),
                    parseDouble(tokenizer), parseDouble(tokenizer)));

            skipLine(tokenizer);

            vlist.add(v);
        } else if (tokenizer.sval.equals("f")) {
            // Face command
            plist.add(parsePolygon(tokenizer));
        } else {
            // Ignore other commands
            skipLine(tokenizer);
        }
    }

    // Read a single floating point value
    private double parseDouble(StreamTokenizer tokenizer)
        throws IOException
    {
        if (tokenizer.nextToken() == StreamTokenizer.TT_EOL ||
            tokenizer.ttype != StreamTokenizer.TT_NUMBER) {
            try {
                double d = Double.valueOf(tokenizer.sval).doubleValue();
                tokenizer.nval = d;
                return d;
             } catch (NumberFormatException nfe) {
                 System.out.print("Can not convert " + tokenizer.sval);
                throw new IOException();
             }
        }

        return tokenizer.nval;
    }

    // Read a polygon specification -- a list of integers
    private Vector parsePolygon(StreamTokenizer tokenizer)
        throws IOException
    {
        Vector<Integer> list = new Vector<Integer>();
        int slashes = 0;

        while (tokenizer.nextToken() != StreamTokenizer.TT_EOL) {
            switch(tokenizer.ttype) {
              case StreamTokenizer.TT_NUMBER: 
                switch (slashes) {
                  case 0:
                    list.add(new Integer((int)Math.round(tokenizer.nval)-1));
                    break;

                  case 2:
                    slashes = 0;
                    break;
                }
                break;

              case '/':
                slashes++;
                break;

              default:
                throw new IOException();
            }
        }
        if (slashes != 0)
          throw new IOException();

        return list;
    }

    // Skip the rest of a line
    private void skipLine(StreamTokenizer tokenizer)
        throws IOException
    {
        while (tokenizer.nextToken() != StreamTokenizer.TT_EOL);
    }
}
