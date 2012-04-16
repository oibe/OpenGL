/* class Camera
 * Camera parameters (static storage)
 *
 * Doug DeCarlo
 */

class Camera
{
    private static DoubleParameter tx, ty, tz, roll, pitch, yaw, near, far;
    private static double left, right, bottom, top;
    private static DoubleParameter param[];
    private static BooleanParameter perspective;

    // Create parameters
    public static void init()
    {
        param = new DoubleParameter[8];
        
        tx    = param[0] = new DoubleParameter("Tx",     0, -40, 40);
        ty    = param[1] = new DoubleParameter("Ty",     0, -40, 40);
        tz    = param[2] = new DoubleParameter("Tz",    -5, -40, 40);
        pitch = param[3] = new DoubleParameter("Pitch",  0, -180, 180);
        yaw   = param[4] = new DoubleParameter("Yaw",    0, -180, 180);
        roll  = param[5] = new DoubleParameter("Roll",   0, -180, 180);
        near  = param[6] = new DoubleParameter("Near",   2,  0.2, 20);
        far   = param[7] = new DoubleParameter("Far",   20,  1, 100);

        perspective = new BooleanParameter("Perspective", true);
    }
    
    // Set viewing frustum extents on the near plane
    // (screen coordinates in the camera view)
    public static void setCurrentView(double l, double r, double b, double t)
    {
        left = l;
        right = r;
        bottom = b;
        top = t;
    }

    // Reset all camera parameters
    public static void reset()
    {
        for (int i = 0; i < param.length; i++)
          param[i].reset();

        perspective.reset();
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

    // Accessors for frustum extents in depth
    public static double near()   { return near.value; }
    public static double far()    { return far.value;  }

    // Accessors for frustum extents on the near plane
    public static double left()   { return left;   }
    public static double right()  { return right;  }
    public static double top()    { return top;    }
    public static double bottom() { return bottom; }

    // Accessor for the list of parameters
    public static DoubleParameter[] params()
    {
        return param;
    }

    // Accessor for perspective boolean parameter
    public static BooleanParameter perspective()
    {
        return perspective;
    }

    // Accessor for perspective camera option
    public static boolean isPerspective()
    {
        return perspective.value;
    }
}
