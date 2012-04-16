

/* class Scene
 * Methods to describe, process and display the scene which contains
 * rocks, trees and critters.
 *
 * Doug DeCarlo
 */

import java.util.*;
import java.text.*;

import javax.media.opengl.GL;
import javax.vecmath.*;

import com.sun.opengl.util.GLUT;

public class Scene
{
    // Parameters for specifying V; the 3D view
    private Vector<DoubleParameter> params;
    private DoubleParameter tH, tV, tZ, rAlt, rAzim;

    // Parameters for display options
    private Vector<BooleanParameter> options;
    private BooleanParameter drawTime;
    public  BooleanParameter drawAnimation, drawBugView, marioMode,marioModePlanar,dimen;

    // ------------

    // Elements of the scene
    Vector<Critter> critters;
    Vector<Obstacle> obstacles;
    
    // Main character in scene (a reference to a bug stored in critters) */
    Bug mainBug;
    Bug Predator;
    Bug[] bugList;
    
    Point3d goal;
    double timeElap;
    // Random number generator
    Random rgen;

    // Clock reading at last computation
    double computeClock = 0;

    // Seed for random number generator
    long seed;

    // Rendering quality flag
    boolean nice;

    // Speed multiplier for clock (1.0 is default)
    double clockSpeed;

    // File prefix used for file dumping (null if not dumping images)
    String dumpPrefix;

    // Center of the world
    Point3d origin = new Point3d(0,0,0);

    //-----------------------------------------------------------------------

    // Default constructor for scene
    public Scene(long seedVal, boolean niceVal, double clockSpeedVal, 
                 String dumpPrefixVal)
    {
        seed = seedVal;
        nice = niceVal;
	clockSpeed = clockSpeedVal;
        dumpPrefix = dumpPrefixVal;

        // Allocate parameters
        params = new Vector<DoubleParameter>();
        options = new Vector<BooleanParameter>();

        tH = addParameter(new DoubleParameter("Left/Right", 0, -10, 10, 1));
        tV = addParameter(new DoubleParameter("Down/Up",   -2, -10, 10, 1));
        tZ = addParameter(new DoubleParameter("Out/In",     0, -30, 20, 1));

        rAlt  = addParameter(new DoubleParameter("Altitude", 15, -15, 80, 1));
        rAzim = addParameter(new DoubleParameter("Azimuth", 0, -180, 180, 1));

        drawAnimation = addOption(new BooleanParameter("Animation", 
                                                       false, 2));
        drawTime      = addOption(new BooleanParameter("Show time",
                                                       dumpPrefix == null, 
                                                       1));
        drawBugView   = addOption(new BooleanParameter("Bug camera view", 
                                                       false, 1));
       
        marioMode   = addOption(new BooleanParameter("2-D Meets 3-D!!!", 
                                                       false, 1));
                                                       
        marioModePlanar   = addOption(new BooleanParameter("2-D Planar Mode!!!", 
                                                       false, 1));
        
       	dimen  = addOption(new BooleanParameter("Expanding Universe", 
                                                       false, 1));
        
        timeElap = 3000;
        build();
    }

    // ----------------------------------------------------------------------

    // Keep track of list of all scene parameters/drawing options
    public DoubleParameter addParameter(DoubleParameter p)
    {
        params.add(p);
        return p;
    }
    public BooleanParameter addOption(BooleanParameter p)
    {
        options.add(p);
        return p;
    }

    // Accessors for parameters/options
    public Vector getParams()
    {
        return params;
    }
    public Vector getOptions()
    {
        return options;
    }

    // Reset all shape parameters to default values
    public void reset()
    {
        Iterator i;

        i = params.iterator(); 
        while (i.hasNext()) {
            ((Parameter)i.next()).reset();
        }

        i = options.iterator(); 
        while (i.hasNext()) {
            ((Parameter)i.next()).reset();
        }
    }

    // -----------------------------------------------------------------
    // -- Clock stuff

    // Starting time of program, and time of latest pause
    public long startTime, pauseTime;

    // Flag for determining if clock always reports 1/30 second
    // intervals each time it is polled
    public boolean frameByFrameClock = false;

    // Frame number (for frame-by-frame clock)
    private int frameNumber = 0;

    // Get current frame number
    public int getFrameNumber()
    {
	return frameNumber;
    }

    // Go to next frame number
    public void incrementFrameNumber()
    {
	frameNumber++;
    }

    // Make clock frame-by-frame (each frame has 1/30 second duration)
    public void setFrameByFrameClock()
    {
	frameByFrameClock = true;
    }

    // Record starting time of program and frame number
    public void resetClock()
    {
	startTime = System.currentTimeMillis();
	pauseTime = startTime;

        computeClock = 0;

	frameNumber = 0;
    }

    // Pause clock (time doesn't elapse)
    public void pauseClock(boolean stop)
    {
	long now = System.currentTimeMillis();

	if (stop) {
	    pauseTime = now;
	} else {
	    startTime += now - pauseTime;
	}
    }

    // Return current time (in seconds) since program started
    // (or if frameByFrameClock is true, then return time of current
    // frame number)
    public double readClock()
    {
	if (frameByFrameClock) {
	    return frameNumber * (1/30.0f);
	} else {
	    long elapsed;
	    
	    if (drawAnimation.value) {
		// Time during animation
		elapsed = System.currentTimeMillis() - startTime;
	    } else {
		// Time during pause
		elapsed = pauseTime - startTime;
	    }
	    
	    return elapsed / 1000.0f;
	}
    }

    // ----------------------------------------------------------------------

    public Point3d getRandomPoint(Vector<Obstacle> obstacles,int obRadius, int newRadius)
    {
    	//Variable Declarations
    	double dist;		
    	boolean check = false;
    	Point3d obPoint;
    	Point3d newPoint;
	int count = 0;
	
    	while(true)//forever
    	{
    		check = false;	//reinitialize check to false
    		newPoint = new Point3d(rgen.nextGaussian()*4,rgen.nextGaussian()*4,0); //grab a random point in the radius of your circle
    		for(int j = 0; j < obstacles.size();j++)//for each obstacle in list
    		{
    			obPoint = obstacles.get(j).getLocation();//get the obstacles point
    			dist = Math.sqrt(Math.pow(obPoint.x-newPoint.x,2)+Math.pow(obPoint.y-newPoint.y,2));//find the distance between the obstacles point and the new Point
    			if(dist < (obRadius+newRadius))//if the new point intersects the radius of any obstacles then set check to true and break;
    			{
    				check = true;
    				break;
    			}
    		}
    		
    		if(check == false)  //if check == false nothing intersected with the radius so this point is safe to return
    		{
    			return newPoint;
    		}
    		
    		if(count++ == 20) // if 20 iterations have gone by then just give up
    		{
    			return null;
    		}
    	}
    
    }
    
    // Build the contents of the scene
    // (no OpenGL calls are allowed in here, as it hasn't been
    //  initialized yet)
    public void build()
    {
    	Point3d loc;
    	computeFPS(0);

    	// Make ranBug[] bugListdom number generator
    	if (seed == -1) {
    		seed = System.currentTimeMillis() % 10000;
    		System.out.println("Seed value: " + seed);
    	}
    	rgen = new Random(seed);

    	// Create empty scene
    	obstacles = new Vector<Obstacle>();
    	critters = new Vector<Critter>();

    	/********************************
	***MY BUILD CHANGES START HERE***
	*********************************/
	
	int Recursion = 0;
	int extraRock = 0;
	int extraTree = 0;
	
	if(nice){
		Recursion = 2;
		extraRock = 4;
		extraTree = 3;
	}
	
	
	Point3d p = new Point3d(rgen.nextGaussian()*4,rgen.nextGaussian()*4,0);
    	obstacles.addElement(new Tree(rgen, 5, 3, 3.0f, 1.0f,p.x, p.y));//For getRandomPoint to work their needs to be at least one obstacle, so the loop will run.
	
	/*BUGS*/
	p = getRandomPoint(obstacles,2, 2);
	if(p != null)
	{
		mainBug = new Bug(rgen, 0.6f,  p.x, p.y,  0.1f, 0.0f,false);
		critters.addElement(mainBug);
			
		bugList = new Bug[3];
        	
       		bugList[0] = new Bug(rgen,0.3f,mainBug.getLocation().x-2.5,mainBug.getLocation().y-1,0.1f,0.0f,false);
        	bugList[1] = new Bug(rgen,0.3f,mainBug.getLocation().x-2.5,mainBug.getLocation().y,0.1f,0.0f,false);
        	bugList[2] = new Bug(rgen,0.3f,mainBug.getLocation().x-2.5,mainBug.getLocation().y+1,0.1f,0.0f,false);
        
       		for(int b = 0; b < bugList.length;b++)
       		{
       			critters.addElement(bugList[b]);
       		}		
       		
	}
    	/*
    	*PREDATOR PLACEMENT
    	*/
    		p = getRandomPoint(obstacles,3, 3);
		if(p != null)
		{
        		Predator = new Bug(rgen,0.7f, p.x,p.y,0.1f,0.0f,true);
        		critters.addElement(Predator);
        	}
    	
    	/*
    	*TREE PLACEMENT
    	*/
	for(int i = 0; i < (1+extraTree);i++)
	{
		p = getRandomPoint(obstacles,4, 4);
		if(p != null)
		{
			obstacles.addElement(new Tree(rgen, 4+Recursion, 3, 3.0f, 1.0f,p.x, p.y));
		}	
	}
        
	/*
	*ROCK PLACEMENT
	*/
	for(int i = 0; i < (2+extraRock);i++)
	{
		p = getRandomPoint(obstacles,2, 2);
		if(p != null)
		{
			obstacles.addElement(new Rock(rgen, 3+Recursion,  p.x, p.y, 1));
		}
	}
        	
        	
        goal = new Point3d(rgen.nextGaussian()*3,rgen.nextGaussian()*3,0);

        // ---------------
        // Reset computation clock
        computeClock = 0;
    }

    // Perform computation for critter movement so they are updated to
    // the current time
    public void process()
    {
        // Get current time
	double t = readClock() * clockSpeed;
	double dTime = t - computeClock;
	double dtMax = 1/50.0f;

	// Set current time on display
	computeClock = t;

        // Only process if time has elapsed
        if (dTime <= 0)
          return;


	// ---------------

	// Compute accelerations, then integrate (using Critter methods)

        // This part advances the simulation forward by dTime seconds, but
        // using steps that are no larger than dtMax (this means it takes
        // more than one step when dTime > dtMax -- the number of steps
        // you need is stored in numSteps).

        // *** placeholder value
        //int numSteps = 1;
	int numSteps = 1+(int)(dTime/dtMax);
	
        // Here is the rough structure of what you'll need
        //
        // do steps times
        //   - reset acceleration
        //   - compute acceleration (adding up accelerations from attractions,
        //     repulsions, drag, ...)
        //   - integrate (by dTime/numSteps)
        // end
        //
        // ...
	for(int i = 0; i < numSteps;i++){
		mainBug.accelReset();
		Predator.accelReset();
		if(timeElap >= 8){
			timeElap = 0;
			goal.set(rgen.nextGaussian()*5,rgen.nextGaussian()*5,0);
		}
			timeElap+=dTime;
		
		
		for(int l = 0; l < obstacles.size();l++){
			mainBug.accelAttract(obstacles.get(l).getLocation(), -3.0, -200.0);
			Predator.accelAttract(obstacles.get(l).getLocation(), -3.0, -250.0);
			
			Predator.accelDrag(-2.0);
			mainBug.accelDrag(-2.0);
		}
		
		for(int z = 0; z < bugList.length;z++){
			bugList[z].accelReset();
			bugList[z].accelAttract(mainBug.getLocation(),6.0,10);
			bugList[z].accelAttract(mainBug.getLocation(),-4.0,-5);
			
			for(int n = 0; n < obstacles.size();n++){
				bugList[z].accelAttract(obstacles.get(n).getLocation(), -4.0, -200.0);
				
			}
			
			for(int x = 0; x < bugList.length;x++){
				if(x != z){
					bugList[z].accelAttract(bugList[x].getLocation(),-2,-9);
				}
			}
			bugList[z].integrate(dTime/numSteps);
			bugList[z].keyframe(bugList[z].vel.length()*dTime);
		}
		mainBug.accelAttract(goal, 2.0, 3.0);
		mainBug.integrate(dTime/numSteps);
		
		Predator.accelAttract(mainBug.getLocation(),7.0,3.0);
		Predator.accelAttract(mainBug.getLocation(),-6.0,-250.0);
		Predator.integrate(dTime/numSteps);
	}
	// Keyframe motion for each critter
	mainBug.keyframe(mainBug.vel.length()*dTime);
	Predator.keyframe(Predator.vel.length()*dTime);
           // ...
 }

    // Draw scene
    public void draw(GL gl, GLUT glut)
    {
    	// Light position
       	float lt_posit[] = { 15, 3, 10, 0 };

        // Ground plane (for clipping)
        double ground[]  = { 0.0, 0.0, 1.0, 0.0 };
	
        // Do computation if animating
        if (drawAnimation.value) {
            process();
        }
	
        // ------------------------------------------------------------
	
        // Initialize materials
        materialSetup(gl);
	
        // Specify V for scene
        gl.glLoadIdentity();
        

        transformation(gl);
	
	if(dimen.value){
		marioMode.value =true;
	}
        
        // Position light wrt camera
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, lt_posit, 0);
        gl.glEnable(GL.GL_LIGHTING);
	
        // Draw ground plane (a circle at z=0 of radius 15)
        gl.glColor3d(0.4, 0.6, 0.35);
        gl.glBegin(GL.GL_POLYGON);
        gl.glNormal3d(0, 0, 1);
        int ncirc = 200;
        for (int i = 0; i < ncirc; i++) {
            double theta = 2*Math.PI * i / ncirc;
            gl.glVertex3d(15*Math.cos(theta), 15*Math.sin(theta), 0);
        }
        gl.glEnd();
        
        /******************************
	******MARIO MODE PLANAR!*****
	******************************/
	if(marioModePlanar.value){
		double a = 0;
    		double b = 1; 
    		double d = -.01;
    		double c = 0;
    		float[] lt_posit_o = {0,10,0,0};
		gl.glDisable(GL.GL_LIGHTING);
    			
    			double[] shade = 
    			{
    				b*lt_posit_o[1]+c*lt_posit_o[2],-a*lt_posit_o[1],-a*lt_posit_o[2],0,
    				-b*lt_posit_o[0],a*lt_posit_o[0]+c*lt_posit_o[2],-b*lt_posit_o[2],0,
    				-c*lt_posit_o[0],-c*lt_posit_o[1],a*lt_posit_o[0]+b*lt_posit_o[1],0,
    				-d*lt_posit_o[0],-d*lt_posit_o[1],-d*lt_posit_o[2],a*lt_posit_o[0]+b*lt_posit_o[1]+c*lt_posit_o[2]
    			};
    			gl.glMultMatrixd(shade, 0);
    		
	}
	if(marioMode.value){
		/******************************
		******MARIO MODE!!!!*****
		******************************/
		double a = 0;
    		double b = 1; 
    		double d = -.01;
    		double c = 0;
    		float[] lt_posit_o = {0,10,0,0};
    		 // Draw critters
        	for (int i = 0; i < critters.size(); i++) {
    			gl.glDisable(GL.GL_LIGHTING);
    			double yValue = ((Critter)(critters.elementAt(i))).getLocation().y;
    			double[] shade = 
    			{
    				b*lt_posit_o[1]+c*lt_posit_o[2],-a*lt_posit_o[1],-a*lt_posit_o[2],0,
    				-b*lt_posit_o[0],a*lt_posit_o[0]+c*lt_posit_o[2]+yValue,-b*lt_posit_o[2],0,
    				-c*lt_posit_o[0],-c*lt_posit_o[1],a*lt_posit_o[0]+b*lt_posit_o[1],0,
    				-d*lt_posit_o[0],-d*lt_posit_o[1],-d*lt_posit_o[2],a*lt_posit_o[0]+b*lt_posit_o[1]+c*lt_posit_o[2]
    			};
    			if(!dimen.value){
        			gl.glPushMatrix();
        		}
        			gl.glMultMatrixd(shade, 0);
            			((Critter)(critters.elementAt(i))).draw(gl);
            		if(!dimen.value){
            			gl.glPopMatrix();
            		}
        	}
	}
	else
	{
		for (int i = 0; i < critters.size(); i++) {
			((Critter)(critters.elementAt(i))).draw(gl);
		}
	}
        // Clip below ground (so rocks don't peek below ground)
        gl.glClipPlane(GL.GL_CLIP_PLANE0, ground, 0);

        
        // **** Once you get the rock working, enable this -- it can be
        //      difficult to debug the rock when this is on, as you can only
        //      see the top of it -- this way you'll see the entire rock if
        //      you peek below the ground plane...
        //gl.glEnable(GL.GL_CLIP_PLANE0);
	
      	/******************************
	******MARIO MODE!!!!**********
	******************************/
        if(marioMode.value){
        	for (int i = 0; i < obstacles.size(); i++) {
        		double a = 0;
    			double b = 1; 
    			double d = -.01;
    			double c = 0;
    			float[] lt_posit_o = {0,10,0,0};
    			 // Draw critters
        			
    				gl.glDisable(GL.GL_LIGHTING);
    				double yValue = ((Obstacle)(obstacles.elementAt(i))).getLocation().y;
    				double[] shade = 
    				{
    					b*lt_posit_o[1]+c*lt_posit_o[2],-a*lt_posit_o[1],-a*lt_posit_o[2],0,
    					-b*lt_posit_o[0],a*lt_posit_o[0]+c*lt_posit_o[2]+yValue,-b*lt_posit_o[2],0,
    					-c*lt_posit_o[0],-c*lt_posit_o[1],a*lt_posit_o[0]+b*lt_posit_o[1],0,
    					-d*lt_posit_o[0],-d*lt_posit_o[1],-d*lt_posit_o[2],a*lt_posit_o[0]+b*lt_posit_o[1]+c*lt_posit_o[2]
    				};
    				
    				if(!dimen.value){
        			gl.glPushMatrix();
        			}
        				gl.glMultMatrixd(shade, 0);
        	    			((Obstacle)(obstacles.elementAt(i))).draw(gl);
        	    		if(!dimen.value){
        	    			gl.glPopMatrix();
        	    		}
        	}
        
        }
        else{
          for (int u= 0; u < obstacles.size(); u++) {
        	((Obstacle)(obstacles.elementAt(u))).draw(gl);
        	}
        }
        gl.glDisable(GL.GL_CLIP_PLANE0);
	
        // Draw text on top of display showing time
        if (drawTime.value) {
            drawText(gl, glut, computeClock / clockSpeed);
        } else {
            numPrevT = 0;
        }
    }
    
    // Transformation of scene based on GUI values
    // (also transform scene so Z is up, X is forward)
    private void transformation(GL gl)
    {
	// Make X axis face forward, Y right, Z up
	// (map ZXY to XYZ)
	gl.glRotated(-90, 1, 0, 0);
    	gl.glRotated(-90, 0, 0, 1);
            
	if (drawBugView.value) {
	    // ---- "Bug cam" transformation (for mainBug)
		
		gl.glScaled(1.0/mainBug.scale,1.0/mainBug.scale,1.0/mainBug.scale);
		
		if(mainBug.vel.length() == 0)
		{
			gl.glRotated(-Math.toDegrees(Math.atan2(-mainBug.acc.y,-mainBug.acc.x)), 0, 0, 1);
		}
		else
		{
			gl.glRotated(-Math.toDegrees(Math.atan2(-mainBug.vel.y,-mainBug.vel.x)), 0, 0, 1);
		}
   		gl.glTranslated(-mainBug.pos.x, -mainBug.pos.y, -mainBug.pos.z-1);
   		
   		
	    // Move camera back so that scene is visible

	} else {
	    // ---- Ordinary scene transformation

	    // Move camera back so that scene is visible
	
	    gl.glTranslated(-20, 0, 0);
		//gl.glTranslated(mainBug.pos.x, mainBug.pos.y, mainBug.pos.z);
	    // Translate by Zoom/Horiz/Vert
	    gl.glTranslated(tZ.value, tH.value, tV.value);
	    
	    // Rotate by Alt/Azim
	    gl.glRotated(rAlt.value,  0, 1, 0);
	    gl.glRotated(rAzim.value, 0, 0, 1);
	}
    }

    // Define materials and lights
    private void materialSetup(GL gl)
    {
	float white[]  = {   1.0f,   1.0f,   1.0f, 1.0f };
	float black[]  = {   0.0f,   0.0f,   0.0f, 1.0f };
	float dim[]    = {   0.1f,   0.1f,   0.1f, 1.0f };
	
	// Set up material and light
	gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT,  dim, 0);
	gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE,  white, 0);
	gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR, dim, 0);
	gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL.GL_SHININESS, 5);

	// Set light color
 	gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, dim, 0);
 	gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, white, 0);
 	gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, black, 0);

	// Turn on light and lighting
	gl.glEnable(GL.GL_LIGHT0);
	gl.glEnable(GL.GL_LIGHTING);

	// Allow glColor() to affect current diffuse material
	gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);
	gl.glEnable(GL.GL_COLOR_MATERIAL);
    }

    // Draw text info on display
    private void drawText(GL gl, GLUT glut, double t)
    {
	String message;
	DecimalFormat twodigit = new DecimalFormat("00");

	// Put orthographic matrix on projection stack
	gl.glMatrixMode(GL.GL_PROJECTION);
	gl.glPushMatrix();
	gl.glLoadIdentity();
	gl.glOrtho(0, 1, 0, 1, -1, 1);
	gl.glMatrixMode(GL.GL_MODELVIEW);

	// Form text
	message = new String((int)t/60 + ":" + 
			     twodigit.format((int)t % 60) + "." +
			     twodigit.format((int)(100 * (t - (int)t))));

	// Add on frame rate to message if it has a valid value
	double fps = computeFPS(t);
	if (fps != 0) {
	    DecimalFormat fpsFormat = new DecimalFormat("0.0");

	    message = message + "  (" + fpsFormat.format(fps) + " fps)";

            fpsFormat = null;
	}

	gl.glDisable(GL.GL_LIGHTING);
	gl.glDisable(GL.GL_DEPTH_TEST);

	gl.glPushMatrix();
	gl.glLoadIdentity();

	// Draw text 
	gl.glColor3d(0.8, 0.2, 0.2);
	gl.glRasterPos2d(0.01, 0.01);
        glut.glutBitmapString(glut.BITMAP_HELVETICA_18, message);
        message = null;

	// Draw bug cam label 
	if (drawBugView.value) {
	    message = new String("BUG CAM");
	    gl.glRasterPos2d(0.45, 0.01);
  	    gl.glColor3d(1.0, 1.0, 1.0);
            glut.glutBitmapString(glut.BITMAP_HELVETICA_18, message);
            message = null;
	}

	gl.glPopMatrix();

	gl.glEnable(GL.GL_DEPTH_TEST);

	// Put back original viewing matrix
	gl.glMatrixMode(GL.GL_PROJECTION);
	gl.glPopMatrix();
	gl.glMatrixMode(GL.GL_MODELVIEW);
    }

    // ----------------------------------------------------------------------

    // Compute average frame rate (0.0 indicates not computed yet)
    private double[] prevT = new double[10];
    private int numPrevT = 0;

    private double computeFPS(double t)
    {
	// Restart average when animation stops
	if (t == 0 || !drawAnimation.value) {
	    numPrevT = 0;
	    return 0;
	}

	int which = numPrevT % prevT.length;
	double tdiff = t - prevT[which];

	prevT[which] = t;
	numPrevT++;

	// Only compute frame rate when valid
	if (numPrevT <= prevT.length || tdiff <= 0) {
	    return 0;
	}

	return prevT.length / tdiff;
    }
}
