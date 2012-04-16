/* class Shape
 * Class for object hierarchies
 *
 * Peter Borosan
 */

import java.util.*;

public class VectorHierarchy<E> extends Vector<E> {
	// pointer to parent
	public E parent = null;
	
	public VectorHierarchy() { }
	public VectorHierarchy(E p) { parent = p; }
}
