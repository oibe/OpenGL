/* abstract class SlangProgram
 * The framework for a GLSL program
 * 
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.GLException;

public abstract class SlangProgram
{
    private static int programCount;
    
    static
    {
        programCount = 0;
    }

    /** The name of this program */
    protected String name;
    /** The unique id number of this program */
    protected int number;
    /** Path to vertex shader file */
    protected String vShaderFile;
    /** Path to fragment shader file */
    protected String fShaderFile;
    /** OpenGL program object */
    protected int program;
    /** The shape in this program */
    protected Shape shape;

    /** The shading program is ready to use */
    protected boolean ready;
    /** The shading program could never be ready since something goes wrong */
    protected boolean never_ready;
    
    /**
     * @param name
     * @param shape
     * @param vShaderFile 
     * @param fShaderFile 
     */
    public SlangProgram(String name, Shape shape,
                        String vShaderFile, String fShaderFile)
    {
        number = programCount;
        ++programCount;

        program = 0;
        ready = false;
        never_ready = false;

        this.name = name;
        this.vShaderFile = vShaderFile;
        this.fShaderFile = fShaderFile;
        this.shape = shape;
    }

    // Keep track of list of all shape parameters/drawing options
    public DoubleParameter addParameter(DoubleParameter p)
    {
        shape.getParams().add(p);
        return p;
    }
    public BooleanParameter addOption(BooleanParameter p)
    {
        shape.getOptions().add(p);
        return p;
    }

    /**
     * @param gl
     */
    public static void checkSystemExt(GL gl)
    {
        // Print the graphics card information
        System.out.println("Renderer:" + gl.glGetString(GL.GL_RENDERER));
        System.out.println("Vendor:" + gl.glGetString(GL.GL_VENDOR));
        System.out.println("Version:" + gl.glGetString(GL.GL_VERSION));

        // Check support for ARB extension
        if (!gl.isExtensionAvailable("GL_ARB_vertex_shader") && 
            !gl.isExtensionAvailable("GL_ARB_fragment_shader") ) {
            System.out.println("ARB_vertex_shader is not supported.");
            System.out.println("ARB_fragment_shader is not supported.");
            throw new GLException("GLSL not suppoted");
        } else if (gl.isExtensionAvailable("GL_ARB_vertex_shader") &&
                   gl.isExtensionAvailable("GL_ARB_fragment_shader") ) {
            System.out.println("ARB_vertex_shader extension supported!");
            System.out.println("ARB_fragment_shader extension supported!");
        }
    }

    public boolean Ready()
    {
        return ready;
    }
    
    
    /**
     * @param gl
     * @param shaderFile 
     * @param shaderType
     */
    public int buildShader(GL gl, String shaderFile, int shaderType)
        throws GLException
    {
        int shader = gl.glCreateShaderObjectARB(shaderType);

        // Read shader files.
        String shaderSource = null;
        try {
            shaderSource = loadTextFile(shaderFile);
        } catch(IOException e) {
            throw new GLException("Couldn't load " + shaderFile);
        }

        // Read shaders.
        gl.glShaderSourceARB(shader, 1, new String[] {shaderSource},
                            new int[]{-1}, 0);
        // Comile shaders.
        gl.glCompileShaderARB(shader);

        int[] status = new int[1];

        // Error check.
        gl.glGetObjectParameterivARB(shader, GL.GL_OBJECT_COMPILE_STATUS_ARB, 
                                     status, 0);
        System.out.println("Compile: " + shaderFile + "...");
        System.out.println(getObjectLog(gl, shader));
        if (status[0] == GL.GL_FALSE) {
            throw new GLException("Failed to compile " + shaderFile);
        }

        return shader;
    }

    /**
     * @param gl
     */
    public final void init(GL gl)
    {
	if (never_ready) {
	    // Dont bother
	    return;
	}

	try {
            checkSystemExt(gl);

            int vShader, fShader;

            // Create program and attach shader objects.
            program = gl.glCreateProgramObjectARB();

            if (vShaderFile != null) {
                vShader = buildShader(gl, vShaderFile, GL.GL_VERTEX_SHADER);
                gl.glAttachObjectARB(program, vShader);
            }
            if (fShaderFile != null) {
                fShader = buildShader(gl, fShaderFile, GL.GL_FRAGMENT_SHADER);
                gl.glAttachObjectARB(program, fShader);
            }

            // Link.

            int[] status = new int[1];
    
            gl.glLinkProgramARB(program);
            gl.glGetObjectParameterivARB(program, 
                                         GL.GL_OBJECT_LINK_STATUS_ARB, 
                                         status, 0);
            System.out.println("Link: " + name + "...");
            System.out.println(getObjectLog(gl, program));
            if (status[0] == GL.GL_FALSE) {
                throw new GLException("Failed to link " + name);
            }
    
            gl.glValidateProgramARB(program);
        } catch (Exception e) {
            System.out.println(e.getMessage() + 
                               ", program runs with fixed pipeline only");
            // don't bother next time
            never_ready = true;
            return;
        }

        ready = true;
    }

    /**
     * Binds the uniform variables used by this program.
     * @param gl
     */
    protected void bindUniform(GL gl)
    {
        // bind shape specific uniform variables
        shape.bindUniform(gl);
    }

    /**
     * Toggling using this program for rendering. 
     * 
     * @param gl
     * @param enable
     *			true: using this shader
     *			Uses fixed shading functionality.
     */
    public final void enable(GL gl, boolean enable)
    {
	if (never_ready) {
	    // on a machine that does not have shading program support
	    // just return
	    return;
	}
 
    	if (enable) {
	    if (!Ready())
		init(gl);
	    if (Ready()) {
		gl.glUseProgramObjectARB(program);
		bindUniform(gl);
	    }
    	} else {
	    if (Ready())
		gl.glUseProgramObjectARB(0);
    	}
    }

    /**
     * This helper method reads the specified text file into a
     * <code>String</code>, where the lines in the file are separated by "\n"
     * in the returned string.
     *
     * @param filename
     *        the name of the file to be loaded
     * @return the contents of the file as a single <code>String</code>
     * @throws IOException
     *         if the file cannot be loaded.
     */
    private String loadTextFile(String filename) throws IOException
    {
        String text = "";
        String line;

        BufferedReader file = new BufferedReader(new FileReader(filename));
        line = file.readLine();

        while (line != null)
        {
            text += line + "\n";
            line = file.readLine();
        }

        return text;
    }

    /**
     * Gets the Slang log for the specified shader or program object.
     * 
     * @param gl
     *        the OpenGL context
     * @param loggedObjectID
     *        the identifier of the shader object or program object whose 
     *        log is to be fetched
     * @return the fetched log (or an empty string if the log is empty).
     */
    protected final String getObjectLog(GL gl, int loggedObjectID)
    {
        int[] result = new int[1];
        gl.glGetObjectParameterivARB(
                loggedObjectID, GL.GL_OBJECT_INFO_LOG_LENGTH_ARB, result, 0);
        int logLength = result[0];
        if (logLength == 0)
        {
            return "";
        }
        byte[] message = new byte[logLength];
        gl.glGetInfoLogARB(loggedObjectID, logLength, result, 0, message, 0);
        String log = new String(message).substring(0, logLength - 1);
        return log;
    }
    
    /**
     * @return The program number.
     */
    public final int getNumber()
    {
        return number;
    }

    /**
     * @return The program name.
     */
    public final String getName()
    {

        return name;
    }
    
    @Override
    public String toString()
    {
        return getName() + ": " + getNumber();
    }
}
