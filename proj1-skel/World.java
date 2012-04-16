/* class World
 * World camera parameters (static storage)
 *
 * Doug DeCarlo
 */

class World
{
    private static DoubleParameter tx, ty, tz, roll, pitch, yaw;
    private static DoubleParameter param[];
    private static BooleanParameter clipped, normalized;

    // Create parameters
    public static void init()
    {
        param = new DoubleParameter[6];
        
        tx    = param[0] = new DoubleParameter("Tx",     0, -40, 40);
        ty    = param[1] = new DoubleParameter("Ty",     0, -40, 40);
        tz    = param[2] = new DoubleParameter("Tz",   -10, -40, 40);
        pitch = param[3] = new DoubleParameter("Pitch",  0, -180, 180);
        yaw   = param[4] = new DoubleParameter("Yaw",    0, -180, 180);
        roll  = param[5] = new DoubleParameter("Roll",   0, -180, 180);

        clipped = new BooleanParameter("Clipped", false);
        normalized = new BooleanParameter("Normalized", false);
    }

    // Reset all camera parameters
    public static void reset()
    {
        for (int i = 0; i < param.length; i++)
          param[i].reset();

        clipped.reset();
        normalized.reset();
    }

    // ---------
    
    // Accessors for translation
    public static double tx()     { return tx.value; }
    public static double ty()     { return ty.value; }
    public static double tz()     { return tz.value; }

    // Accessors for rotation
    public static double pitch()  { return pitch.value; }
    public static double yaw()    { return yaw.value;   }
    public static double roll()   { return roll.value;  }

    // Accessor for the list of parameters
    public static DoubleParameter[] params()
    {
        return param;
    }

    // Accessor for clipped boolean parameter
    public static BooleanParameter clipped()
    {
        return clipped;
    }

    // Accessor for clipping option
    public static boolean isClipping()
    {
        return clipped.value;
    }

    // Accessor for normalized boolean parameter
    public static BooleanParameter normalized()
    {
        return normalized;
    }

    // Accessor for clipping option
    public static boolean isNormalized()
    {
        return normalized.value;
    }
}
