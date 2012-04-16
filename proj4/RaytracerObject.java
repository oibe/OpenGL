/* class RaytracerObject
 * Abstract class for objects that specify the scene (camera, light,
 * material, shapes, ...)
 *
 * Doug DeCarlo
 */
import java.io.*;
import javax.vecmath.*;
import java.text.*;
import java.lang.reflect.*;
import java.util.*;

abstract class RaytracerObject
{
   /** the name of the object. Default value : an empty string */
   String name="";

   /** parameters of the object */
   Vector<ParamSpec> paramSpecs;

   /** this constructor enforces the creation of an object from a file */
   protected RaytracerObject(StreamTokenizer tokenizer)
             throws NoSuchMethodException, SecurityException,
                    ClassNotFoundException
   {
      // nothing to do here
      paramSpecs = new Vector<ParamSpec>();
      addSpec("name", "setName", "java.lang.String");
   }
   //------------------------------------------------------------------------
   // print to a stream
   public void print(PrintStream out)
   {
      // only the name is known so far
      out.println(getClass().getName() + " : " +name);
   }
   //------------------------------------------------------------------------
   public void read( StreamTokenizer tokenizer )
               throws ParseException,  IOException, InvocationTargetException,
               ClassNotFoundException, IllegalAccessException
   {
      // read the open brace
      tokenizer.nextToken();
      if ( tokenizer.ttype!='{' )
         throw new ParseException("No \"{\" found", tokenizer.lineno());

      // then read the content
      boolean Stop = false;
      while (!Stop) {
         if (tokenizer.nextToken()==StreamTokenizer.TT_WORD) {
            String paramName = tokenizer.sval;
            // search for the parameter in the list
            int i;
            for (i=0; i<paramSpecs.size(); i++)
                if ( ((ParamSpec)paramSpecs.elementAt(i)).is(paramName))
                   break;

            if (i<paramSpecs.size()) {
               // skip the "="
               tokenizer.nextToken();
               ParamSpec temp = (ParamSpec)(paramSpecs.elementAt(i));
               temp.read(tokenizer, this);
            }
         }
         else if ( tokenizer.ttype=='}' )
                 Stop=true;
         else throw new ParseException("Name expected", tokenizer.lineno());
      }
   }
   //------------------------------------------------------------------------
   /** returns a reference to an accessor method
       @param methodName the name of the method (e.g. "getName")

       It is required that the accessor takes no parameters.
   */
   protected Method getAccessor(String methodName, Class[] paramType)
             throws NoSuchMethodException, SecurityException
   {
      try
      {
          return getClass().getMethod(methodName, paramType);
      }
      catch (NoSuchMethodException E)
      {
         System.err.print("Accessor " + methodName + " ");
         for (int i=0; i<paramType.length; i++)
             System.err.print(paramType[i].getName() + " ");
         System.err.println(" not found in class "+getClass().getName());
         throw E;
      }
   }

   /** creates a new ParamSpec and adds it to the list of specs */
   protected void addSpec( String kWord, String accessorName, String paramType)
             throws NoSuchMethodException, SecurityException,
                    ClassNotFoundException
   {
      Class[] c = new Class[1];
      c[0]      = Class.forName(paramType);
      Method m  = getAccessor(accessorName, c);

      ParamSpec nameSpec = new ParamSpec(kWord, c[0], m, null);
      paramSpecs.addElement(nameSpec);
   }

   /** creates a new ParamSpec and adds it to the list of specs */
   protected void addSpecSpecial(String kWord, String accessorName,
		                 String readerName)
             throws NoSuchMethodException, SecurityException,
                    ClassNotFoundException
   {
      Class[] c  = new Class[1];
      c[0]       = Class.forName("java.util.Vector");
      Method m   = getAccessor(accessorName, c);

      Class[] ct = new Class[1];
      ct[0]      = Class.forName("java.io.StreamTokenizer");
      Method r   = getAccessor(readerName, ct);
      
      ParamSpec nameSpec = new ParamSpec(kWord, c[0], m, r);
      paramSpecs.addElement(nameSpec);
   }

   //------------------------------------------------------------------------
   public String getName() { return name; }
   public void   setName( String newName ) { name = newName; };

}// end of class RaytracerObject
