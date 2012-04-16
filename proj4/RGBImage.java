/* class RGBImage
 * Class that holds a grid of pixels (an image)
 *
 * Doug DeCarlo
 */
import java.io.*;
import javax.vecmath.*;

class RGBImage
{
    Vector3d[][] data;
    int width  = 0;
    int height = 0;
    int depth  = 3;
    // width * height * depth
    int length = 0;

    /** Constructors */
    public RGBImage(String fileName)
           throws IOException, FileNotFoundException, SecurityException
    {
	read(fileName);
    }
    
    public RGBImage(int newWidth, int newHeight)
    {
       width  = newWidth;
       height = newHeight;

       // R,G,B
       depth  = 3;

       // length of the file to be written
       length = width * height * depth;

       data = new Vector3d[width][height];
       for (int i=0; i < data.length; i++)
           for (int j=0; j < data[i].length; j++)
               data[i][j] = new Vector3d();
    }

    //-----------------------------------------------------------------------

    public int getWidth()  { return width; }
    public int getHeight() { return height; }

    /** Get value of pixel (i,j) */
    public Vector3d getPixel(int i, int j)
    {
	return data[i][j];
    }

    /** Set value of pixel (i,j) */
    public void setPixel(int i, int j, Vector3d newVal)
    {
	data[i][j].set(newVal);

	adjustColor(data[i][j]);
    }

    /** Make sure pixel value is in correct range */
    private void adjustColor(Vector3d color)
    {
	// Gamma convert to match display (for better contrast)
	double gamma = 2.4;
	color.x = Math.pow(color.x, 1.0/gamma);
	color.y = Math.pow(color.y, 1.0/gamma);
	color.z = Math.pow(color.z, 1.0/gamma);

	// clamp RGB values to [0,1]
	color.clamp(0.0, 1.0);
    }

    /** Determine the pixel value at a location of the images by bi-linear
     *  interpolation of the neighboring 4 pixels
     * (u and v are both in the range [0,1])
     */
    public Vector3d getSubPixel(double u, double v)
    {
        double x = u * (width-1), y = v * (height-1);
        
        int x0 = (int)x, y0 = (int)y;
        double alphaX = x - x0, alphaY = y - y0;
	
        Vector3d p = new Vector3d(), p0 = new Vector3d();

        // Add up weighted average of 4 neighbors
        p0.scale((1-alphaX) * (1-alphaY), getPixel(x0  , y0));     p.add(p0);
        p0.scale(  (alphaX) * (1-alphaY), getPixel(x0+1, y0));     p.add(p0);
        p0.scale((1-alphaX) *   (alphaY), getPixel(x0  , y0+1));   p.add(p0);
        p0.scale(  (alphaX) *   (alphaY), getPixel(x0+1, y0+1));   p.add(p0);
        
        return p;
    }
    
    //-----------------------------------------------------------------------
    // Image file I/O

    /** Read the data using PPM format (text or binary) */
    public void read(String filename)
    {
	try {
	    FileInputStream is = new FileInputStream(filename);
	    LineInputStream in = new LineInputStream(is);
	    
	    // read header
	    boolean raw;
	    
	    String magic = getsPPM(in);
	    if (magic.equals("P6"))
	      raw = true;
	    else if (magic.equals("P3"))
	      raw = false;
	    else 
	      throw new IOException("Not a PPM file");
	    
	    depth = 3;
	    
	    String size = getsPPM(in);
	    width = Integer.parseInt(size.substring(0,size.indexOf(' ')));
	    height = Integer.parseInt(size.substring(size.indexOf(' ')+1));
	    
	    data = new Vector3d[width][height];
	    
	    String max = getsPPM(in);
	    if (Integer.parseInt(max) != 255) {
		throw new IOException("Not a PPM file");
	    }
	    
	    if (raw) {
		// read raw data
		byte brow[] = new byte[width*3];
		for (int i=0; i < height; i++) {
		    if (in.readdata(brow) != width*3)
		      throw new IOException("File read error");
		    for (int j=0, k=0; j < width*3; j += 3) {
			int r = brow[j], g = brow[j+1], b = brow[j+2];
			r = r < 0 ? r+256 : r;
			g = g < 0 ? g+256 : g;
			b = b < 0 ? b+256 : b;

			data[k++][height-i-1] = 
			  new Vector3d(r/255.0, g/255.0, b/255.0);
		    }
		}
	    } else {
		// read ascii data
		for (int i=0; i < height; i++) {
		    for(int j=0; j < width; j++) {
			int r = Integer.parseInt(in.getw());
			int g = Integer.parseInt(in.getw());
			int b = Integer.parseInt(in.getw());

			data[j][height-i-1] = 
			  new Vector3d(r/255.0, g/255.0, b/255.0);
		    }
		}
	    }
	    in.close();

	} catch (IOException e) {
	    System.out.println(e);
	    System.exit(-1);
	}
    }

    // Read one line from PPM format image data, ignoring comments
    private String getsPPM(LineInputStream in)
    {
	String line;
	do {
	    try { 
		line = in.gets();
	    } catch(IOException e) {
		return "";
	    }
	} while(line.charAt(0) == '#');

	return line;
    }

    /** Write the data using PPM format (binary) */
    public void write(String filename)
    {
	String header = new String("P6" + '\n' +
				   width + " " + height + '\n' +
				   "255" + '\n');
	// Space for image data
	byte[] dataOut = new byte[length];
	
	try {
	    FileOutputStream stream = new FileOutputStream(filename);

	    // Write file header
	    stream.write(header.getBytes());

	    // Collect image bytes
	    int pos = 0;
	    for (int j=0; j < data[0].length; j++) {
		for (int i=0; i < data.length; i++) {
		    Vector3d pixel = getPixel(i,height-j-1);

		    dataOut[pos++] = (byte)(255*pixel.x);
		    dataOut[pos++] = (byte)(255*pixel.y);
		    dataOut[pos++] = (byte)(255*pixel.z);
		}
	    }

	    // Write image contents
	    stream.write(dataOut, 0, dataOut.length);

	    stream.close();
	} catch (IOException e) {
	    System.out.println(e);
	    System.exit(-1);
	}
    }
}
