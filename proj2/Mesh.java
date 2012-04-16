/* class Mesh
 * The main program -- construct the GUI and displays
 *
 * Doug DeCarlo
 */

import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Mesh extends JFrame
{
    // Swing OpenGL component classes
    static WorldView worldDraw;

    // Viewed shape
    static Shape shape;

    // Method to refresh entire display
    public static void refresh()
    {
        if (!SwingUtilities.isEventDispatchThread())
          return;

        worldDraw.display();
    }

    // Method to recompute shape
    public static void recompute()
    {
        if (!SwingUtilities.isEventDispatchThread())
          return;

        // Nothing to do in this case...
        // (primitive geometry is computed on the fly...)
    }

    // Construct GUI for a set of parameters
    public static void makeControls(Container controls,
                                    GridBagLayout controlLayout,
                                    GridBagConstraints controlCon,
                                    Vector dpList, Vector optList,
                                    String title)
    {
        Container spec = new Container();
        controlLayout.setConstraints(spec, controlCon);
        controls.add(spec);

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints con = new GridBagConstraints();

        spec.setLayout(layout);

        Iterator i = dpList.iterator();
        while (i.hasNext()) {
            DoubleParameter dp = (DoubleParameter)(i.next());

            // Make slider and text field
            JSlider val = new JSlider(JSlider.HORIZONTAL);
            JTextField tval = new JTextField(5);

            dp.register(val);
            dp.register(tval);

            // Lay out label, slider, text field
            con.fill = GridBagConstraints.NONE;
            con.weightx = 0.0;
            con.weighty = 0.0;
            con.gridwidth = 1;

            JLabel name = new JLabel(dp.name);
            layout.setConstraints(name, con);
            spec.add(name);

            con.fill = GridBagConstraints.HORIZONTAL;
            con.weightx = 0.8;
            layout.setConstraints(val, con);
            spec.add(val);
            
            con.weightx = 0.2;
            con.gridwidth = GridBagConstraints.REMAINDER;
            layout.setConstraints(tval, con);
            spec.add(tval);
        }

        // Label column on bottom
        con.fill = GridBagConstraints.VERTICAL;
        con.anchor = GridBagConstraints.SOUTH;
        con.weightx = 0.0;
        con.weighty = 1.0;
        con.gridwidth = GridBagConstraints.REMAINDER;
        JLabel ltitle = new JLabel(title);
        layout.setConstraints(ltitle, con);
        spec.add(ltitle);

        // Display options
        con.anchor = GridBagConstraints.WEST;
        i = optList.iterator();
        while (i.hasNext()) {
            BooleanParameter bp = (BooleanParameter)(i.next());

            JCheckBox ck = new JCheckBox(bp.name);
            bp.register(ck);

            layout.setConstraints(ck, con);
            spec.add(ck);
        }
    }

    // Main program -- create and start GUI
    public Mesh(boolean debug)
    {
        // Create drawing area for shape
        worldDraw = new WorldView(this, shape, debug);
        worldDraw.setSize(500, 500);

        // Create menubar
        JMenuBar menubar = new JMenuBar();
        setJMenuBar(menubar);

        JMenu menu = new JMenu("File");
        menu.getPopupMenu().setLightWeightPopupEnabled(false);
        menubar.add(menu);

        // Exit when quit selected
        JMenuItem resetm = menu.add("Reset");
        resetm.addActionListener(
            new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    Parameter.blockAction(true);

                    // Reset all parameters
                    shape.reset();

                    Parameter.blockAction(false);

                    Parameter.onUserAction();
                }
            });


        // Exit when quit selected
        JMenuItem quitm = menu.add("Quit");
        quitm.addActionListener(
            new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    System.exit(0);
                }
            });
        
        // ------------------------------------------------------
        
        // Lay out main window
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints con = new GridBagConstraints();

        Container c = getContentPane();
        c.setLayout(layout);

        // World viewport
        con.gridwidth = 1;
        con.weightx = 0.2;
        con.weighty = 1.0;
        con.fill = GridBagConstraints.BOTH;
        con.insets  = new Insets(4,4,4,2);
        layout.setConstraints(worldDraw, con);
        c.add(worldDraw);

        // -- Parameter controls

        // Make container for controls
        Container cc = new Container();

        con.gridwidth = GridBagConstraints.REMAINDER;
        con.weightx = 0.2;
        con.weighty = 1.0;
        con.fill = GridBagConstraints.HORIZONTAL;
        
        layout.setConstraints(cc, con);
        c.add(cc);
        
        // Fill compartment
        GridBagLayout clayout = new GridBagLayout();
        cc.setLayout(clayout);
        GridBagConstraints ccon = new GridBagConstraints();

        ccon.weightx = 1.0;
        ccon.weighty = 1.0;
        ccon.anchor = GridBagConstraints.NORTH;
        ccon.insets = new Insets(10, 10, 0, 10);
        ccon.fill = GridBagConstraints.BOTH;
        ccon.gridwidth = GridBagConstraints.REMAINDER;
        makeControls(cc, clayout, ccon,
                     shape.getParams(), 
                     shape.getOptions(), 
                     "Object parameters");

        // ------------------------------------------------------

        // Exit when window closes
        addWindowListener(
            new WindowAdapter()
            {
                public void windowClosing(WindowEvent e)
                {
                    System.exit(0);
                }
            });

        // Placement of window on screen
        setLocation(100, 50);

        pack();
        setVisible(true);
    }
    
    public static void main(String args[])
    {
        boolean debug = true;

        // Parse command-line arguments
        try {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-help")) {
                    System.out.println("Usage: java Mesh <filename>");
                    System.out.println("   or: java Mesh <shape> [uSize vSize]");
                    System.out.println(" where <shape> is one of:");
                    System.out.println(" -ellipsoid  -torus -dini");
                    System.exit(0);
                } else if (args[i].equals("-nodebug")) {
                    debug = false;
                } else if (args[i].charAt(0) == '-') {
                    // Primitive
                    String primName = args[i].substring(1);
    
                    int uSize = 24, vSize = 24;
    
                    // Check for u/v
                    if (args.length >= i+3) {
                        uSize = (new Integer(args[i+1])).intValue();
                        vSize = (new Integer(args[i+2])).intValue();
                        i += 2;
                    }
    
                    // Create primitive
                    if (primName.equals("ellipsoid")) {
                        shape = new Ellipsoid(uSize, vSize);
                    } else if (primName.equals("torus")) {
                        shape = new Torus(uSize, vSize);
                    } else if(primName.equals("dini")) {
                     	shape = new dini(uSize,vSize);
                    } else {
                        throw new Exception("Unknown primitive: " + primName);
                    }
                } else {
                    // Filename
                    shape = new PolyMesh(args[i]);
                }
            }
            if (shape == null)
              throw new Exception("No shape specified.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }

        // Create main window
        try {
            Mesh m = new Mesh(debug);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
