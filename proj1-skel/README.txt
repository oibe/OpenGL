Notes for project 1: Viewing
=============================================================================

To compile:

  - type "make"

To run:

  - type "java View" or "make go"

To run with OpenGL debugging on:

  - type "java View -debug"

=============================================================================

Classes to be modified
----------------------

CameraView.java
WorldView.java
  - The main OpenGL part of the program for each of the display windows
    (the camera and world viewports)

Cube.java
  - This class is used to draw a cube on the screen.

ViewVolume.java
  - This class will be used to draw a view volume on the screen

Other important classes
-----------------------

World.java
Camera.java
Obj.java
  - These three classes hold the specifiation for the world, camera and
    object coordinate systems and displays

SimpleGLCanvas.java
  - the parent class for WorldView and CameraView, this class is used to
    create and respond to a Swing/AWT OpenGL container

Classes you probably won't look at
----------------------------------

View.java
  - The main program -- builds the user interface and starts it up

Parameter.java
BooleanParameter.java
DoubleParameter.java
  - These classes are used to ensure that the parameters displayed on
    the user interface, and the internal values are all in correspondence

=============================================================================
