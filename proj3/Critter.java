/* class Critter
 * This abstract class implements methods for keeping track of the position,
 * velocity and acceleration of a critter (such as a bug), for integrating
 * these quantities over time, and for computing accelerations that give
 * the bug wandering behavior
 *
 * Doug DeCarlo
 */

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.vecmath.*;
import java.util.*;

abstract class Critter
{
    // Position, velocity, acceleration
    Point3d pos;
    Vector3d vel, acc;

    // Total distance traveled (used for keyframing)
    double dist;

    // Random number generator
    Random rgen;

    // ---------------------------------------------------------------

    // Constructor
    public Critter(Random randomGen)
    {
	pos = new Point3d();
	vel = new Vector3d();
	acc = new Vector3d();

	dist = 0;

	rgen = randomGen;
    }

    // Method to draw critter
    abstract void draw(GL gl);

    // Method to do keyframe animation
    abstract void keyframe(double t);

    // ---------------------------------------------------------------

    // Return location of critter
    public Point3d getLocation()
    {
	return pos;
    }

    // Method to integrate acc to get updated vel and pos
    // (assumes acc is already computed)
    // Also, compute the distance traveled
    //
    public void integrate(double dt)
    {
	// Euler integration
	// ...
	vel.x+=acc.x*dt;
	vel.y+=acc.y*dt;
	vel.z+=acc.z*dt;

	if(vel.length() > 2){
		vel.normalize();
		vel.x*=2;
		vel.y*=2;
		vel.z*=2;
	}
	
	// Update distance
	// ...
	
	pos.x+=vel.x*dt;
	pos.y+=vel.y*dt;
	pos.z+=pos.z*dt;
	
	return;
    }

    // Accessor for total distance traveled by bug
    public double distTraveled()
    {
	return dist;
    }

    // ---------------------------------------------------------------

    // Reset acceleration to zero
    public void accelReset()
    {
    	acc.set(0,0,0);
    }

    // Add in viscous drag (assume mass of 1):  a += -k v   (k > 0)
    public void accelDrag(double k)
    {
        // Add viscous drag to acceleration acc
        //...
        Vector3d temp = new Vector3d(k*vel.x,k*vel.y,k*vel.z);
	acc.x+=temp.x;
	acc.y*=temp.y;
	acc.z*=temp.z;
	return;
    }

    // Add in attraction acceleration:  a+= direction * (k*dist^exp)
    // (negative values of k produce repulsion)
    public void accelAttract(Point3d p, double k, double exp)
    {
        //...
        Point3d myPos = getLocation();
        Vector3d d = new Vector3d(p.x-myPos.x,p.y-myPos.y,p.z-myPos.z);
        double length_d = d.length();
        d.normalize();
        length_d = Math.pow(length_d,exp);
        
        d.x*=(k*length_d);
        d.y*=(k*length_d);
        d.z*=(k*length_d);
        
        acc.x += d.x;
        acc.y += d.y;
        acc.z += d.z;
        
        return;
    }

    // ...   (add more methods like those above when you need them)
}
