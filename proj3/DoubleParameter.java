/* class DoubleParameter
 * Class for holding a double value that is kept in sync 
 * with the user interface (perhaps in multiple locations, such
 * as in both a textbox and a slider)
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

class DoubleParameter extends Parameter 
    implements ActionListener, ChangeListener
{ 
    // Value
    double value;

    // Default value
    private double def;
    // Value range
    private double min, max;

    // Number formatting
    DecimalFormat numFormat;

    final int sliderRange = 2000;

    // ------

    // Constructor with default format (and initial value, min and max),
    // and dirty level
    public DoubleParameter(String name, double defVal, 
                           double minVal, double maxVal,
                           int dirtyLevelVal)
    {
        this(name, defVal, minVal, maxVal, dirtyLevelVal, "0.0");
    }

    // Constructor (initial value, min and max, format) and dirty level
    public DoubleParameter(String name, double defVal, 
                           double minVal, double maxVal, int dirtyLevelVal,
                           String formatSpec)
    {
        super(name, dirtyLevelVal);

        value = def = defVal;
        min = minVal;
        max = maxVal;

        // Make sure default is within range
        if (def < min)
          def = min;
        if (def > max)
          def = max;

        numFormat = new DecimalFormat(formatSpec);
    }

    // Specify interface component as a listener
    public void register(JTextField v)
    {
        views.add(v);
        v.addActionListener(this);
        update((Object)v);
    }
    public void register(JSlider v)
    {
        views.add(v);
        v.addChangeListener(this);
        update((Object)v);
    }

    // ------

    // Respond to an event from an interface component
    public void respond(Object v) 
    {
        String vcn = v.getClass().getName();

        if (vcn.equals("javax.swing.JTextField")) {
            try {
                value = Double.parseDouble(((JTextField)v).getText());
            } catch (NumberFormatException e) {
                // Leave value unchanged
            }
        } else if (vcn.equals("javax.swing.JSlider")) {
            int ivalue = ((JSlider)v).getValue();

            value = min + ivalue * (max - min) / sliderRange;
        } else {
            return;
        }

        // Constrain to valid range
        if (value < min)
          value = min;
        if (value > max)
          value = max;

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
    public void stateChanged(ChangeEvent e)
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
        
        if (vcn.equals("javax.swing.JTextField")) {
            ((JTextField)v).setText(numFormat.format(value));
        } else if (vcn.equals("javax.swing.JSlider")) {
            int ivalue = (int)Math.round((value - min) * sliderRange /
                                         (max - min));
            
            ((JSlider)v).setMinimum(0);
            ((JSlider)v).setMaximum(sliderRange);
            ((JSlider)v).setValue(ivalue);
        } else {
            return;
        }

        // Mark as dirty at appropriate level
        if (dirty < dirtyLevel)
          dirty = dirtyLevel;
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
