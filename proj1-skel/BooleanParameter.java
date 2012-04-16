/* class BooleanParameter
 * Class for holding a boolean value that is kept in sync 
 * with the user interface (perhaps in multiple locations, such
 * as in both a checkbox or a menu checkbox)
 * 
 * First, create an instance of this class.  Then, upon the creation
 * of each interface component, register() it.
 *
 * Doug DeCarlo
 */

import java.util.*;
import java.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

class BooleanParameter extends Parameter 
    implements ActionListener
{ 
    // Value
    boolean value;

    // Default value
    private boolean def;

    // ------

    // Constructor
    public BooleanParameter(String name, boolean defVal)
    {
        super(name);

        value = def = defVal;
    }

    // Specify interface component as a listener
    public void register(JCheckBox v)
    {
        views.add(v);
        v.addActionListener(this);
        update((Object)v);
    }
    public void register(JCheckBoxMenuItem v)
    {
        views.add(v);
        v.addActionListener(this);
        update((Object)v);
    }

    // ------

    // Respond to an event from an interface component
    public void respond(Object v) 
    {
        String vcn = v.getClass().getName();

        if (vcn.equals("javax.swing.JCheckBox")) {
            value = ((JCheckBox)v).isSelected();
        } else if (vcn.equals("javax.swing.JCheckBoxMenuItem")) {
            value = ((JCheckBoxMenuItem)v).isSelected();
        } else {
            return;
        }

        updateAll();
    }

    // Listener abstract methods for responding to interface events
    public void actionPerformed(ActionEvent e)
    {
        boolean old = Parameter.blockAction(true);

        respond(e.getSource());

        Parameter.blockAction(old);
        Parameter.onUserAction();
    }

    // Reset value to its default and update interface
    public void reset()
    {
        value = def;

        updateAll();
    }

    // ------

    // Update a single interface component
    public void update(Object v)
    {
        String vcn = v.getClass().getName();

        if (vcn.equals("javax.swing.JCheckBox")) {
            ((JCheckBox)v).setSelected(value);
        } else if (vcn.equals("javax.swing.JCheckBoxMenuItem")) {
            ((JCheckBoxMenuItem)v).setSelected(value);
        } else {
            return;
        }

        dirty = Boolean.TRUE;
    }

    // Update the entire interface
    public void updateAll() 
    {
        Iterator i = views.iterator();

        while (i.hasNext()) {
            update(i.next());
        }
    }
}
