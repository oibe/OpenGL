/* class ParamSpec
 * Implements the structure of the signature of parameters
 * to be read from a file
 *
 * Doug DeCarlo
 */
import java.lang.reflect.*;
import java.io.*;
import java.text.ParseException;

class ParamSpec
{
    /** the keyword of the parameter (e.g. "ka")*/
    String keyword;
    
    /** the type of the object (e.g. "java.lang.String" */
    Class type;
    
    /** the accessor method */
    Method accessor;
    
    /** the reader method (null if a predefined parser type) */
    Method specialReader;
    
    //------------------------------------------------------------------------
    public ParamSpec(String newKeyword, Class newType, Method newAccessor,
		     Method newReader)
        throws ClassNotFoundException
    {
	keyword  = newKeyword;
	type     = newType;
	accessor = newAccessor;
	specialReader = newReader;
    }

    //------------------------------------------------------------------------

    /** reads the parameter from the tokenizer and sets the appropriate value
     * in the target object
     */
    public void read( StreamTokenizer tokenizer, Object target )
         throws ClassNotFoundException, RuntimeException,
                InvocationTargetException, IOException,
                IllegalAccessException, ParseException
    {
	Object[] parameter = new Object[1];
	
	if (specialReader != null) {
	    parameter[0] = specialReader.invoke(target,
						new Object[] { tokenizer });
	} else if ( type.equals(Class.forName("java.lang.String")) )
	  parameter[0]=Parser.readString(tokenizer);
	else if ( type.equals(Class.forName("java.lang.Double")) )
	  parameter[0]=Parser.readDouble(tokenizer);
	else if ( type.equals(Class.forName("javax.vecmath.Vector2d")) )
	  parameter[0]=Parser.readVector2d(tokenizer);
	else if ( type.equals(Class.forName("javax.vecmath.Vector3d")) )
	  parameter[0]=Parser.readVector3d(tokenizer);
	else if ( type.equals(Class.forName("javax.vecmath.Point3d"))  )
	  parameter[0]=Parser.readPoint3d(tokenizer);
	else throw new RuntimeException("Type "+ type.getName()+
					" not supported by parser. Line " +
					tokenizer.lineno());
	
	accessor.invoke(target, parameter);
    }

    //------------------------------------------------------------------------
    public String getKeyword() { return keyword; }
    public boolean is( String kword ) { return keyword.compareTo(kword)==0; }
}
