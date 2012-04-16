/* class IllumProgram
 * Specification of vertex and fragment programs for programmable
 * shading
 */

import javax.media.opengl.GL;

public class IllumProgram extends SlangProgram {
    protected BooleanParameter glslOn, phongModel, toonShading;
    private DoubleParameter toonLow, toonHigh;

    public IllumProgram(String name, Shape shape)
    {
        super(name, shape, "illum.vp", "illum.fp");

        // For enabling GLSL
        glslOn = addOption(new BooleanParameter("GLSL Illumination", 
                                                false, 1));
        // For enabling GLSL phong shading
        phongModel = addOption(new BooleanParameter("GLSL Phong model", 
                                                    false, 1));
        // For enabling GLSL toon shading
        toonShading = addOption(new BooleanParameter("GLSL Toon shading",
                                                     false,1));

        // Toon shading parameters
        toonHigh = addParameter(new DoubleParameter("Toon High", 0.7, 0, 1,1));
        toonLow = addParameter(new DoubleParameter("Toon Low", 0.5, 0, 1, 1));
    }

    // Send (uniform) values to fragment program
    protected void bindUniform(GL gl)
    {
        int v;

        v = gl.glGetUniformLocationARB(program, "phong");
        gl.glUniform1iARB(v, phongModel.value ? 1 : 0);

        v = gl.glGetUniformLocationARB(program, "toon");
        gl.glUniform1iARB(v, toonShading.value ? 1 : 0);

        v = gl.glGetUniformLocationARB(program, "toonLow");
        gl.glUniform1fARB(v, (float)toonLow.value);

        v = gl.glGetUniformLocationARB(program, "toonHigh");
        gl.glUniform1fARB(v, (float)toonHigh.value);

        // Bind values for shape
        super.bindUniform(gl);
    }
}
