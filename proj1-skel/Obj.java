/* class Obj
 * Object parameters (static storage)
 *
 * Doug DeCarlo
 */

class Obj
{
    private static DoubleParameter tx, ty, tz, rx, ry, rz, scale;
    private static DoubleParameter param[];

    // Create parameters
    public static void init()
    {
        param = new DoubleParameter[7];
        
        tx    = param[0] = new DoubleParameter("Tx",     0, -40, 40);
        ty    = param[1] = new DoubleParameter("Ty",     0, -40, 40);
        tz    = param[2] = new DoubleParameter("Tz",     0, -40, 40);
        rx    = param[3] = new DoubleParameter("Rx",     0, -180, 180);
        ry    = param[4] = new DoubleParameter("Ry",     0, -180, 180);
        rz    = param[5] = new DoubleParameter("Rz",     0, -180, 180);
        scale = param[6] = new DoubleParameter("Scale",  1,  0.2, 20);
    }
    
    // Reset all camera parameters
    public static void reset()
    {
        for (int i = 0; i < param.length; i++)
          param[i].reset();
    }

    // ---------

    // Accessors for translation
    public static double tx() { return tx.value; }
    public static double ty() { return ty.value; }
    public static double tz() { return tz.value; }

    // Accessors for rotation
    public static double rx() { return rx.value; }
    public static double ry() { return ry.value; }
    public static double rz() { return rz.value; }

    // Accessors for scale
    public static double scale() { return scale.value; }

    // Accessor for the list of parameters
    public static DoubleParameter[] params()
    {
        return param;
    }
}
