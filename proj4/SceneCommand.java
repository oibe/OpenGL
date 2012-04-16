/* class SceneCommand
 * Implements the commands that specify the scene configuration
 * (matrix stack stuff, primarily)
 *
 * Doug DeCarlo
 */
import java.io.*;
import javax.vecmath.*;
import java.text.*;
import java.lang.reflect.*;
import java.util.*;

/** implements the reader for a scene file command
 */
class SceneCommand
{
    String[] names = { "push", "pop", "identity",
		       "translate", "rotate", "scale",
		       "up", "down" };

    /** constructor. All kinds of operations will be treated as regular
        parameters, setting some flags about the data.
    */
    public SceneCommand(StreamTokenizer tokenizer, Scene s)
           throws ParseException,  IOException, InvocationTargetException,
                  ClassNotFoundException, IllegalAccessException,
                  NoSuchMethodException
    {
	if (tokenizer.ttype!=StreamTokenizer.TT_WORD)
	    throw new ParseException("Name expected instead of " + tokenizer,
				     tokenizer.lineno());
	Class[] paramList = {tokenizer.getClass(), s.getClass()};
	Object[] params = {tokenizer, s };
	
	int i;
	for (i=0; i<names.length; i++) {
	    if (names[i].compareTo(tokenizer.sval)==0) {
		// check for '{'
		tokenizer.nextToken();
		if ( tokenizer.ttype!='{' )
		    throw new ParseException("No \"{\" found",
					     tokenizer.lineno());
		
		// call the appropriate method
		Method m = getClass().getMethod(names[i],paramList);
		m.invoke(this,params);
		
		// check for '}'
		tokenizer.nextToken();
		if ( tokenizer.ttype!='}' )
		    throw new ParseException("No \"}\" found",
					     tokenizer.lineno());
		break;
	    }
	}

	if (i>=names.length)
	    throw new ParseException("Unknows token "+tokenizer,
				     tokenizer.lineno());
    }

    //-----------------------------------------------------------------------
    // Transformations

    /** emulates glPushMatrix */
    public void push(StreamTokenizer tokenizer, Scene s)
    {
	// nothing to read from the tokenizer
	s.getMStack().push(null);
    }
    
    /** emulates glPopMatrix */
    public void pop(StreamTokenizer tokenizer, Scene s)
    {
	// nothing to read from tokenizer
	s.getMStack().pop();
    }

    /** emulates glLoadIdentity */
    public void identity(StreamTokenizer tokenizer, Scene s)
    {
	// nothing to read from tokenizer
	s.getMStack().peek().setIdentity();
    }
    
    /** emulates glTranslate */
    public void translate( StreamTokenizer tokenizer, Scene s)
	throws ParseException, IOException
    {
        // read the vector
        Vector3d newTranslation = Parser.readVector3d(tokenizer);
	
        // set translation matrix
        Matrix4d m = new Matrix4d();
        m.setIdentity();
        m.setTranslation(newTranslation);
        s.getMStack().push(m);
    }

    /** emulates glRotate */
    public void rotate( StreamTokenizer tokenizer, Scene s )
	throws ParseException, IOException
    {
        // read the angle, then axis
        double angle = Parser.readDouble(tokenizer).doubleValue();
        Vector3d v   = Parser.readVector3d(tokenizer);
	
        Matrix4d m = new Matrix4d();
        m.setIdentity();
        m.setRotation(new AxisAngle4d(v, angle * Math.PI / 180));
        s.getMStack().push(m);
    }

    /** emulates glScale */
    public void scale( StreamTokenizer tokenizer, Scene s)
	throws ParseException, IOException
    {
        // read the vector
        Vector3d newScale = Parser.readVector3d(tokenizer);
	
        // set the scale matrix
        Matrix4d m = new Matrix4d();
        m.setIdentity();
        m.setElement(0, 0, newScale.x);
        m.setElement(1, 1, newScale.y);
        m.setElement(2, 2, newScale.z);
        s.getMStack().push(m);
    }

    // -----------------------------------------------------------------------
    // Hierarchy

    /** Move up in hierarchy */
    public void up(StreamTokenizer tokenizer, Scene s)
        throws ParseException
    {
	if (!s.hierarchyOn)
	  return;

	if (s.currentLevel == s.objects) {
	    throw new ParseException("Hierarchy underflow",
				     tokenizer.lineno());
	} else {
	    // Move up in hierarchy
	    s.currentLevel = s.currentLevel.parent.parent; //((Shape)s.currentLevel.firstElement()).parent;
	}
    }

    /** Move down in hierarchy */
    public void down(StreamTokenizer tokenizer, Scene s)
        throws ParseException
    {
	if (!s.hierarchyOn)
	  return;

	if (s.currentLevel.isEmpty()) {
	    throw new ParseException("Cannot use 'down' without creating" +
				     " a child object first",
				     tokenizer.lineno());
	} else {
	    // Move down in hierarchy (into last child)
	    s.currentLevel = ((Shape)s.currentLevel.lastElement()).children;
	}
    }
}
