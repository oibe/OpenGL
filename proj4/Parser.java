/* class Parser
 * Reads in the input file format
 *
 * Doug DeCarlo
 */
import java.io.*;
import java.text.ParseException;
import java.lang.reflect.*;
import javax.vecmath.*;

/** Implements a parser for the input file format */
class Parser
{
    private static Class[] objectTypes;
    private static Scene   mainScene;

    public Parser(Scene s)
           throws ClassNotFoundException
    {
       mainScene = s;
       // initialize the array of object types
       objectTypes = new Class[8];

       // to add the actual types once they're created
       objectTypes[0] = Class.forName("Material");
       objectTypes[1] = Class.forName("Light");
       objectTypes[2] = Class.forName("Sphere");
       objectTypes[3] = Class.forName("Cylinder");
       objectTypes[4] = Class.forName("Cone");
       objectTypes[5] = Class.forName("Box");
       objectTypes[6] = Class.forName("Camera");
    }

    //------------------------------------------------------------------------
    /** reads the file indicated by fileName */
    public void readFile( String fileName )
                throws ParseException, NoSuchFieldException,
                       InvocationTargetException, ClassNotFoundException,
                       NoSuchMethodException, InstantiationException,
                       IllegalAccessException
    {

       try
       {
          FileReader input = new FileReader(fileName);

          // create the tokenizer and set its parameters
          StreamTokenizer tokenizer = new StreamTokenizer(input);
          tokenizer.commentChar('#');
          tokenizer.slashSlashComments(false);
          tokenizer.slashStarComments(false);
          tokenizer.eolIsSignificant(false);
          // don't skip these characters
          tokenizer.ordinaryChar('/');
          tokenizer.ordinaryChar('{');
          tokenizer.ordinaryChar('}');
          // skip commas
          tokenizer.whitespaceChars(',', ',');
          // read the file
          while (tokenizer.nextToken()!=StreamTokenizer.TT_EOF){
              switch (tokenizer.ttype) {
                 case StreamTokenizer.TT_WORD   :
                      // this is a new line
                      parseLine(tokenizer);
                      break;
                 case StreamTokenizer.TT_EOL    :
                      // do nothing
                      break;
                 default                        :
                      ignoreLine(tokenizer);

              }
          }
          // done. Close the file
          input.close();
       }
       catch (FileNotFoundException E)
       {
          System.err.println("Specified file does not exist. --- " + fileName);
          System.err.println(fileName);
          throw new RuntimeException();
       }
       catch (SecurityException E)
       {
          System.err.print("Insufficient priviliges on the file. --- ");
          System.err.println(fileName);
          throw new RuntimeException();
       }
       catch (IOException E)
       {
          System.err.println("I/O error. Aborting...");
          throw new RuntimeException();
       }
    }// end of readFile


    /** reads the current token and, if it is valid, calls the appropriate
        object constructor
    */
    private void parseLine( StreamTokenizer tokenizer )
                 throws ParseException, NoSuchFieldException,
                        InvocationTargetException, ClassNotFoundException,
                        NoSuchMethodException, InstantiationException,
                        IllegalAccessException, IOException
    {
       try
       {
          if ( tokenizer.ttype == StreamTokenizer.TT_WORD ) {
             int i;
             for ( i=0; i<objectTypes.length; i++)
                 if (objectTypes[i]!=null){
                   // check for the value of the "keyword" field
                   Field keyword = objectTypes[i].getDeclaredField("keyword");
                   java.lang.String s = new java.lang.String();
                   s=(String)keyword.get(null);

                   // if the keyword mathches the current token
                   if (s.compareTo(tokenizer.sval)==0) {
                      // create a new instance of the class
                      Class[] list = {Class.forName("java.io.StreamTokenizer")};
                      Constructor c = objectTypes[i].getConstructor(list);

                      Object[] listOfParameters = {tokenizer};
                      Object newObject = c.newInstance(listOfParameters);

                      mainScene.addObject((RaytracerObject)newObject);

		      //((RaytracerObject)newObject).print(System.out);

                      // exit the loop
                      break;
                   }
                 }
             if (i>=objectTypes.length) {
		 // the keyword did not match any object type.
		 // It must be a scene command
		 SceneCommand sc = new SceneCommand(tokenizer, mainScene);
             }
          }
          else
          throw new ParseException("Unknown object type.", tokenizer.lineno());
       }
       catch (NoSuchFieldException E)
       {
          System.err.print(tokenizer.sval);
          System.err.println("does not have the \"keyword\" field.");
          throw E;
       }
       catch (InvocationTargetException E)
       {
          System.err.print("Could not instanciate class for ");
          System.err.println(tokenizer.sval + " object ");
          throw E;
       }
    }



    //------------------------------------------------------------------------
    /** reads a double value
        @param tokenizer the stream tokenizer which contains the data
        @return the double read from the tokenizer
    */
    public static Double readDouble(StreamTokenizer tokenizer)
           throws ParseException, IOException
    {
       tokenizer.nextToken();
       if (tokenizer.ttype != StreamTokenizer.TT_NUMBER)
          throw new ParseException(" Number expected "+tokenizer,
                                   tokenizer.lineno());
       return new Double(tokenizer.nval);
    }

    /** reads a Vector2d
        @param tokenizer the stream tokenizer which contains the data
        @return the double read from the tokenizer
    */
    public static Vector2d readVector2d( StreamTokenizer tokenizer )
           throws ParseException, IOException
    {
        double[] values = readTuple(tokenizer, 2);
        return new Vector2d(values);
    }


    /** reads a Vector3d
        @param tokenizer the stream tokenizer which contains the data
        @return the double read from the tokenizer
    */
    public static Vector3d readVector3d( StreamTokenizer tokenizer )
           throws ParseException, IOException
    {
        double[] values = readTuple(tokenizer, 3);
        return new Vector3d(values);
    }
    /** reads a 3d tuple and returns it as Point3d
    */
    public static Point3d readPoint3d( StreamTokenizer tokenizer )
           throws ParseException, IOException
    {
       double[] values = readTuple( tokenizer, 3);
       return new Point3d(values);
    }
    /** method that reads a specified size vector from the tokenizer
        @param tokenizer the StreamTokenizer to read from
        @param size the number of values to be read
    */
    private static double[] readTuple(StreamTokenizer tokenizer, int size)
            throws ParseException, IOException
    {
        tokenizer.nextToken();
        // check for "("
        if ( tokenizer.ttype != '(' )
           throw new ParseException(" \"(\" expected "+tokenizer,
                                    tokenizer.lineno());

        // read the meaningful data
        double values[] = new double[size];
        for (int i = 0; i < values.length; i++){
            tokenizer.nextToken();
            if (tokenizer.ttype != StreamTokenizer.TT_NUMBER)
               throw new ParseException(" Number expected "+tokenizer,
                                        tokenizer.lineno());
            values[i] = tokenizer.nval;
        }

        // check for ")"
        tokenizer.nextToken();
        if ( tokenizer.ttype != ')' )
           throw new ParseException(" \")\" expected "+tokenizer,
                                    tokenizer.lineno());

        return values;

    }


    /** reads a string from the tokenizer
        @param tokenizer the tokenizer to read from
    */
    public static String readString(StreamTokenizer tokenizer)
           throws ParseException, IOException
    {
       tokenizer.nextToken();
       if (tokenizer.ttype != StreamTokenizer.TT_WORD)
               throw new ParseException("String expected "+tokenizer,
                                        tokenizer.lineno());
       return tokenizer.sval;
    }



    //------------------------------------------------------------------------
    /** skips a line */
    public void ignoreLine(StreamTokenizer tokenizer)
                throws IOException
    {
       while (tokenizer.nextToken()!=StreamTokenizer.TT_EOL);
    }

}// end of class Parser
