/* class WorldView
 * The OpenGL drawing component of the interface
 * for the "world" viewport
 *
 * CS 428   Doug DeCarlo
 */

import java.awt.*;
import java.nio.IntBuffer;

import javax.media.opengl.*;

public class WorldView extends SimpleGLCanvas
{
    ViewVolume viewVol;
    Cube cube;

    public WorldView(Window parent, boolean debug)
    {
        super(parent);
        debugging = debug;
        
        viewVol = new ViewVolume();
        cube = new Cube();
    }

    // Method for initializing OpenGL (called once at the beginning)
    public void init(GL gl)
    {
        // Set background color to black
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Turn on visibility test
        gl.glEnable(GL.GL_DEPTH_TEST);
    }

    // ------------------------------------------------------------

    // Method for handling window resizing
    public void projection(GL gl)
    {
        // Set drawing area
        gl.glViewport(0, 0, width, height);

        double mult =2;
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity ();               
         double aspect = (double)width/(double)height;
         double l = -1;
         double b = -1;
         double r = 1;
         double t =1 ;
        	if(aspect > 1){
        		l*=aspect;
        		r*=aspect;
        		Camera.setCurrentView(l,r,b,t);
			 gl.glFrustum (l*mult ,r*mult, b*mult, t*mult, 2.0, 80.0);        	
       		}
        	else{
        		b/=aspect;
        		t/=aspect;
			Camera.setCurrentView(l,r,b,t);
			gl.glFrustum (l*mult ,r*mult, b*mult, t*mult, 2.0, 80.0);   
		}

        gl.glMatrixMode (GL.GL_MODELVIEW);
    }

 // Method for drawing the contents of the window
    public void draw(GL gl)
    {
        // Clear the window
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
       
        projection(gl);
        transformation(gl);
       
        gl.glPushMatrix();
        	double l,b,r,t;
        	CameraView.inverse_transformation(gl);
        	if(World.isNormalized()){  
              l = b = -1;   r = t = 1;
              gl.glTranslated(Camera.tx(),Camera.ty(),Camera.tz());
              gl.glRotated(Camera.roll(), 0.0f, 0.0f, 1.0f);
              gl.glRotated(Camera.yaw(), 0.0f, 1.0f, 0.0f);
              gl.glRotated(Camera.pitch(), 1.0f, 0.0f, 0.0f);
           		if(Camera.isPerspective() == true){
                    gl.glFrustum (l ,r, b, t, Camera.near(), Camera.far());   
                }else{
                    gl.glOrtho(l, r, b, t, Camera.near(), Camera.far()) ;
                }
        	}
        	//gl.glClipPlane(GL.GL_CLIP_PLANE0,eqn);
        	
        	viewVol.draw(gl);
        	if(World.isClipping()){
        		viewVol.placeClipPlanes(gl);  
        		viewVol.enablePlanes(gl);		
        	}
        	
        gl.glPopMatrix();
        	 
        gl.glPushMatrix();
       	//wv^-1pvm
       		if(World.isNormalized()){
       			CameraView.inverse_transformation(gl);
       			l = b = -1;   r = t = 1;
       			gl.glTranslated(Camera.tx(),Camera.ty(),Camera.tz());
       	        gl.glRotated(Camera.roll(), 0.0f, 0.0f, 1.0f);
       	        gl.glRotated(Camera.yaw(), 0.0f, 1.0f, 0.0f);
       	        gl.glRotated(Camera.pitch(), 1.0f, 0.0f, 0.0f);
       			if(Camera.isPerspective() == true){
       				gl.glFrustum (l ,r, b, t,Camera.near(), Camera.far());   
       			}else{
       				gl.glOrtho(l, r, b, t,Camera.near(), Camera.far()) ;
       			}
       			gl.glTranslated(Camera.tx(),Camera.ty(),Camera.tz());
       	        gl.glRotated(Camera.roll(), 0.0f, 0.0f, 1.0f);
       	        gl.glRotated(Camera.yaw(), 0.0f, 1.0f, 0.0f);
       	        gl.glRotated(Camera.pitch(), 1.0f, 0.0f, 0.0f);
       		}	
        	cube.transform(gl);
        	cube.draw(gl, true);
       gl.glPopMatrix();
        	
       gl.glPushMatrix();
       		CameraView.inverse_transformation(gl);
         	if(World.isNormalized()){	 
         		l = b = -1;   r = t = 1;
         		gl.glTranslated(Camera.tx(),Camera.ty(),Camera.tz());
                gl.glRotated(Camera.roll(), 0.0f, 0.0f, 1.0f);
                gl.glRotated(Camera.yaw(), 0.0f, 1.0f, 0.0f);
                gl.glRotated(Camera.pitch(), 1.0f, 0.0f, 0.0f);
         		if(Camera.isPerspective() == true){
         			gl.glFrustum (l ,r, b, t,Camera.near(), Camera.far());   
         		}else{
         			gl.glOrtho(l, r, b, t,Camera.near(),Camera.far()) ;
         		}
         	}
            viewVol.placeClipPlanesReverse(gl);
            viewVol.disablePlanes(gl);
        gl.glPopMatrix();
        
        
      if(World.isClipping()){
    	  gl.glPushMatrix();
    	  	if(World.isNormalized()){
    	  		CameraView.inverse_transformation(gl);
    	  		l = b = -1;   r = t = 1;
    	  		gl.glTranslated(Camera.tx(),Camera.ty(),Camera.tz());
    	        gl.glRotated(Camera.roll(), 0.0f, 0.0f, 1.0f);
    	        gl.glRotated(Camera.yaw(), 0.0f, 1.0f, 0.0f);
    	        gl.glRotated(Camera.pitch(), 1.0f, 0.0f, 0.0f);
    	  		if(Camera.isPerspective()){
    			  gl.glFrustum (l ,r, b, t,Camera.near(), Camera.far());   
    	  		}else{
    	  			gl.glOrtho(l, r, b, t,Camera.near(), Camera.far()) ;
    	  		}
    	  		gl.glTranslated(Camera.tx(),Camera.ty(),Camera.tz());
    	        gl.glRotated(Camera.roll(), 0.0f, 0.0f, 1.0f);
    	        gl.glRotated(Camera.yaw(), 0.0f, 1.0f, 0.0f);
    	        gl.glRotated(Camera.pitch(), 1.0f, 0.0f, 0.0f);
    	  	}
   	
    	  	cube.transform(gl);
   		
    	  	gl.glEnable(GL.GL_CLIP_PLANE0);
    	  	cube.draw(gl, false);
    	  	gl.glDisable(GL.GL_CLIP_PLANE0);
        
    	  	gl.glEnable(GL.GL_CLIP_PLANE1);
    	  	cube.draw(gl, false);
    	  	gl.glDisable(GL.GL_CLIP_PLANE1);
    
    	  	gl.glEnable(GL.GL_CLIP_PLANE2);
    	  	cube.draw(gl, false);
    	  	gl.glDisable(GL.GL_CLIP_PLANE2);

    	  	gl.glEnable(GL.GL_CLIP_PLANE3);
    	  	cube.draw(gl, false);
    		gl.glDisable(GL.GL_CLIP_PLANE3);

    		gl.glEnable(GL.GL_CLIP_PLANE4);
    		cube.draw(gl, false);
    		gl.glDisable(GL.GL_CLIP_PLANE4);

    		gl.glEnable(GL.GL_CLIP_PLANE5);
    		cube.draw(gl, false);
    		gl.glDisable(GL.GL_CLIP_PLANE5);
   
    	gl.glPopMatrix();
     }
 }

    // Apply the (world) viewing transformation (W)
    void transformation(GL gl)
    {
    	gl.glLoadIdentity();
        gl.glTranslated(World.tx(),World.ty(),World.tz());
        gl.glRotated(World.roll(), 0.0, 0.0, 1.0);
        gl.glRotated(World.yaw(), 0.0, 1.0, 0.0);
        gl.glRotated(World.pitch(), 1.0, 0.0, 0.0);
    }
}
