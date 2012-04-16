Notes for Project 2: Polygon Meshes
=============================================================================

To compile:

  - type "make"

  (Note: the fragment/vertex programs are comp at run-time)

To run:

  - type "java Mesh <filename>" or "java Mesh <primitive> [ uSize vSize ]"
    where <primitive> is one of:
        -ellipsoid  -torus
    and []'s are option

    Examples:   java Mesh -ellipsoid
                java Mesh -torus 32 32
                Java Mesh obj/cow.obj

By default, OpenGL debugging is on.  If for some reason you want to turn it
off, specify the -nodebug option.

=============================================================================

----------------------
Classes to be modified
----------------------

Shape.java
  - Abstract class used to define a shape defined using a polygon mesh; 
    it performs many mesh computations and draws them using OpenGL
    Methods to complete:
        draw()
        drawPolygons()
        drawWireframe()

PolyMesh.java
  - Class used to define polygon meshes that are read from a file.
    Methods to complete:
        computeAllNormals()

Ellipsoid.java
Torus.java (extra credit)
  - Classes which implement specific parameteric primitives
    Methods to complete in each:
        evalPosition()
        evalNormal()

------------------------
Vertex/Fragment Programs
------------------------
illum.vp
  - basic vertex shader that sends position, normal, and current color to
    the fragment shader (complete)

illum.fp
  - complete main() and toonShade

ellipsoid.vp
  - complete ellipsoidPosition(), ellipsoidNormal() and the top of main()

torus.vp (extra credit)
  - complete torusPosition(), torusNormal() and the top of main()

-----------------------
Other important classes
-----------------------

Vertex.java
  - Interface for representing vertices in a mesh

Polygon.java
  - Interface for representing polygons in a mesh

PolygonAccess.java
  - An abstract class that implements Polygon that stores the vertices
    in a polygon

UVShape.java
  - An abstract class for representing polygon meshes produced from
    parameteric shapes using a uv grid

PolyMesh.java
  - The class used to represent polygon meshes read in from a file
    (the file must be a Wavefront OBJ file)

* Here is the class hierarchy for these classes:

             Vertex                           Polygon
             /    \                              |
            /      \                       PolygonAccess
           /        \                       /          \
      VertexPM     VertexUV           PolygonPM      PolygonUV
    (in PolyMesh) (in UVShape)      (in PolyMesh)   (in UVShape)

WorldView.java
  - The main OpenGL part of the program which defines the material and
    light used to draw the polygons, applies V and draws the shape

Mesh.java
  - The main program -- builds the user interface and starts it up;
    the command line arguments are handled here

----------------------------------
Classes you probably won't look at
----------------------------------

Parameter.java
BooleanParameter.java
DoubleParameter.java
  - These classes are used to ensure that the parameters displayed on
    the user interface, and the internal values are all in correspondence

SimpleGLCanvas.java
  - the parent class for WorldView, this class is used to create and
    respond to a Swing/AWT OpenGL container

SlangProgram.java
  - handles all the GLSL stuff behind the scenes -- yuk!

IllumProgram.java
  - loads in the illumination vertex and fragment programs

=============================================================================
