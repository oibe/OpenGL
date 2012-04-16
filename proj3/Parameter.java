/* class Parameter
 * Abstract class for storing a parameter value
 * These values are reflected internally, as well as in the GUI and must
 * be presented and updated consistently
 *
 * Doug DeCarlo
 */

import java.util.*;

import javax.swing.JComponent;

public abstract class Parameter
{
    // Name of parameter
    String name;

    // User interface components that display this value
    public Vector<JComponent> views;

    // Level of effect of this parameter
    int dirtyLevel;

    // Global indicator for interface status
    //  - 0 indicates all is well
    //  - 1 indicated a redraw is needed due to values being changed
    //  - 2 indicates a recomputation is needed
    static int dirty = 2;

    // Indicates whether a block action is in progress
    private static boolean blockActionOn = false;

    // ----

    // Default constructor
    public Parameter(String paramName, int dirtyLevelVal)
    {
        name = paramName;
        
        dirtyLevel = dirtyLevelVal;

        // Create empty interface component list
        views = new Vector<JComponent>();
    }

    // Abstract method for resetting value
    public abstract void reset();

    // Accessor for blockActionOn
    public static boolean blockAction(boolean on)
    {
        boolean old = blockActionOn;

        blockActionOn = on;

        return old;
    }

    // Method to call for each user action performed -- if any changes
    // have been made, it updates the display
    public static void onUserAction()
    {
        // Overlook user action if block action is on
        if (blockActionOn)
          return;

        // Update GUI if values are changed (dirty)
        switch (dirty) {
          case 2:
            Main.recompute();
          case 1:
            Main.refresh();
            break;
        }
        dirty = 0;
    }
}
