/* class TreePart
 * Class for representing a subtree, describing the size of the part at
 * the transformation to get to this subtree from the parent, the
 * current tree node (length and width) and whether this is a leaf node
 *
 * Doug DeCarlo
 */

import java.util.*;

import javax.media.opengl.GL;
import javax.vecmath.*;

class TreePart
{
    // Transformation for this branch/leaf (relative to its parent)

    // ...
    // ... specify angles and translations to say how parts of the
    // ... tree are positions with respect to each other
    // ...

    // Leaf or trunk
    boolean leaf;

    // Size of part
    int ran;
    double length, width;
    double rand_offset = 0;
    int level;
    // Children
    	TreePart[] parts;
	static double pos[] = { 15, 3, 10, 0 };
    // ---------------------------------------------------------------
	
	public static void construct(TreePart[] pieces,Random rgen, int depth, int numBranch, double partLen, double partWid,int ran)
	{
		if(depth == 0)
		{
			return;
		}
		double scale = 2.0/3.0;
		for(int i = 0; i < numBranch;i++)
		{
			if(depth == 5){
				scale = 4.0/5.0;
			}
			
			pieces[i] = new TreePart(rgen,depth-1,numBranch,partLen*scale,partWid*(1.0/2.0),ran);
			pieces[i].rand_offset = ((rgen.nextGaussian()+1)/2.0)*(1.5/4.0)*pieces[i].length;
		}
		
	}
	
    // Constructor: recursively construct a treepart of a particular depth,
    // with specified branching factor, dimensions and transformation
    public TreePart(Random rgen,int depth, int numBranch,double partLen, double partWid,int ran)
    {
	this.length = partLen;
        this.width = partWid;
       
   	level = depth;
       	this.ran = ran;
        
        if(depth == 0)
        {
        	this.leaf = true;
        }
        else
        {
        	this.leaf = false;
        }
        
        numBranch = (int) (((rgen.nextGaussian()*3+6)%2)+3);
	this.parts = new TreePart[numBranch];
	construct(this.parts,rgen,depth,numBranch,partLen,partWid,this.ran);
	
    }
    
    public static void drawLeaves(GL gl,TreePart t, char check,boolean shadow)
    {
    	gl.glDisable(GL.GL_LIGHTING);
    	
    
	
    	if(check == 'w'){
    		gl.glColor3d(0.0,0.0,0.0);
    		gl.glEnable(gl.GL_POLYGON_OFFSET_FILL);
    		gl.glPolygonOffset(1,1);
    		
    		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
    		
    	}
    	else
    	{
    	
    		if(!shadow)
    		{	
    			switch(t.ran)
    			{
           			case 0:gl.glColor3d(1.0,0.2,0.0); break;
           			case 1: gl.glColor3d(1.0,1.0,0.0); break;
           			case 2: gl.glColor3d(0.8,0.0,0.0); break;
           			case 3: gl.glColor3d(0.0,1.0,0.0); break;
    			}
           	}
           	else
           	{
           		gl.glColor3d(0.0,0.0,0.0);
           	}
    		
    	}
    	
    	/*gl.glEnable(GL.GL_LIGHTING);
	
	Vector3d nor = new Vector3d(pos[0],pos[1],pos[2]-(t.length+.35));
	nor.normalize();
	System.out.println("nor.x "+nor.x+" nor.y "+nor.y+" nor.z "+nor.z);
    	gl.glNormal3d(nor.x, nor.y, nor.z);
    	*/
    	gl.glBegin(GL.GL_POLYGON);
    		gl.glVertex3d(0.0,0.0,t.length);
    		gl.glVertex3d(0.25,0.0,t.length+.25);
    		gl.glVertex3d(.05,0.0,t.length+.15);
    		gl.glVertex3d(.18,0.0,t.length+.45);
    		gl.glVertex3d(.05,0.0,t.length+.35);
    		gl.glVertex3d(0.0,0.0,t.length+.65);
    		gl.glVertex3d(-.05,0.0,t.length+.35);
    		gl.glVertex3d(-.18,0.0,t.length+.45);
    		gl.glVertex3d(-.05,0.0,t.length+.15);
    		gl.glVertex3d(-0.25,0.0,t.length+.25);
    	gl.glEnd();
    		
    		
  
    	
    		//gl.glColor3d(.54,.27,.075);
    	
    	if(check == 'w')
    	{
        	gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
        }
        gl.glEnable(GL.GL_LIGHTING);
       
    }
    
    public static void treeShadow(GL gl,int level ,TreePart tree, int count)
    {	
    	double a = 0;
    	double b = 0; 
    	double d = -.01;
    	double c = 1;
    	gl.glColor3d(0.0,0.0,0.0);
    	gl.glDisable(GL.GL_LIGHTING);
    	double[] shade = 
    	{
    		b*pos[1]+c*pos[2],-a*pos[1],-a*pos[2],0,
    		-b*pos[0],a*pos[0]+c*pos[2],-b*pos[2],0,
    		-c*pos[0],-c*pos[1],a*pos[0]+b*pos[1],0,
    		-d*pos[0],-d*pos[1],-d*pos[2],a*pos[0]+b*pos[1]+c*pos[2]
    	};
    	
    	gl.glMultMatrixd(shade, 0);
    	tree.draw(gl,level,tree,count,true);
    }
    
    // Recursively draw a tree component
    //  - place the component using transformation for this subtree
    //  - draw leaf (if this is a leaf node)
    //  - draw subtree (if this is an interior node)
    //    (draw this component, recursively draw children)
    public void draw(GL gl,int level,TreePart tree,int count,boolean shadow)
    {	
		
		
		if(!shadow)
    		{
    			gl.glColor3d(.54,.27,.075);
    		}
    		else
    		{
    			gl.glColor3d(0,0,0);
    		}
		
		if (tree.leaf) {
            // Draw leaf
            		if(!shadow){
            		
			drawLeaves(gl,tree,'n',false);    
			drawLeaves(gl,tree,'w',false);
		}
		else{
			drawLeaves(gl,tree,'n',true);    
			
		}
			return;
    		
          
	} else { 
            // Draw branch
	if(level == 0){

    		return;
    	}
            // ... (transformation for cylinder)
            	if(count == 0){
            		gl.glPushMatrix();
          			gl.glScaled(tree.width,tree.width,tree.length);
				Objs.cylinder(gl);
			gl.glPopMatrix();
		}
		
		for(int i = 0; i < tree.parts.length;i++){
		
			gl.glPushMatrix();
				//System.out.println("rand "+tree.parts[i].rand_offset);
				gl.glTranslated(0,0,(1.0/2.0)*tree.parts[i].length+tree.parts[i].rand_offset);
				gl.glRotated((360/tree.parts.length)*i,0,0,1);
				if(level > 3){
				gl.glRotated(7*level,0,1,0);
				}
				else{
				gl.glRotated(35,0,1,0);
				}
					gl.glPushMatrix();
						gl.glScaled(tree.parts[i].width,tree.parts[i].width,tree.parts[i].length);
							
						Objs.cylinder(gl);
						
					gl.glPopMatrix();
					
				draw(gl,level-1,tree.parts[i],count+1,shadow);
				gl.glPopMatrix();
			
		}
		
	    // Recursively draw children
            // ...
            
            
	}

	
    }
}
