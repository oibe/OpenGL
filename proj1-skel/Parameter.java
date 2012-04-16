/* class Parameter
 * Abstract class for storing a parameter value
 * These values are reflected internally, as well as in the GUI and must
 * be presented and updated consistently
 *
 * Doug DeCarlo
 */

import java.util.*;
import javax.swing.*;

class Parameter
{
    // Name of parameter
    String name;

    // User interface components that display this value
    public Vector<JComponent> views;

    // Global flag for interface redrawing status    
    // (indicates a redraw is needed due to values being changed)
    static Boolean dirty = Boolean.TRUE;

    // Indicates whether a block action is in progress
    private static boolean blockActionOn = false;

    // ----

    // Default constructor
    public Parameter(String paramName)
    {
        name = paramName;

        // Create empty interface component list
        views = new Vector<JComponent>();
    }

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
        if (dirty.booleanValue()) {
            dirty = Boolean.FALSE;

            View.refresh();
        }
    }
}
