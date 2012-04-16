/* class Trace
 * The main program
 *
 * Doug DeCarlo
 */
import java.io.PrintStream;
import java.text.ParseException;
import java.lang.reflect.*;

class Trace
{
    static boolean verbose = true;

    public static void main(String arguments[])
    {
        try {
            String inputFileName = "";
            String outputFileName = "out.ppm";

            int width  = 128;
            int height = 128;

            if (arguments.length < 1) {
                printUsage();
                System.exit(0);
            }

            for (int i=0; i < arguments.length; i++) {
                if (arguments[i].compareTo("-out")==0) {
                    outputFileName = arguments[++i];
                } else if (arguments[i].compareTo("-res")==0) {
                    width  = (new Integer(arguments[++i])).intValue();
                    height = (new Integer(arguments[++i])).intValue();
                } else if (arguments[i].compareTo("-quiet")==0) {
                    verbose = false;
                } else if (arguments[i].charAt(0) == '-') {
                    printUsage();
                    System.exit(0);
                } else {
                    inputFileName = arguments[i];
                }
            }

            try {
                Scene s = new Scene();
                Parser p = new Parser(s);
		
                // Parse scene file
                p.readFile(inputFileName);
		
                // Set up the scene
                s.setup();
		
                // Render the image
                RGBImage i = s.render(width, height, verbose);
		
                // Save the image
                i.write(outputFileName);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        } catch (ParseException e) {
            System.err.println(e.getMessage()+" in line "+e.getErrorOffset());
        } catch (Throwable e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
    
    private static void printUsage()
    {
        System.out.println("Usage: ");
        System.out.println("java Trace <fileName> [-res <width height>] "+
                           " [-out <outputFileName>] [-quiet] ");
    }
}
