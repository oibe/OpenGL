/* class MatrixStack
 * Creates a stack suitable for 3D transformation matrices
 *
 * Doug DeCarlo
 */
import java.util.*;
import javax.vecmath.*;

class MatrixStack
{
    Stack<Matrix4d> s = new Stack<Matrix4d>();
    Matrix4d topMatrix = new Matrix4d();

    public MatrixStack()
    {
	topMatrix.setIdentity();
	s.push(topMatrix);
    }

    /** pushes a matrix on the stack. Multiplies the top matrix
       by the input matrix. If newMatrix is null, it will be
       considered as a "push" delimiter.

       Otherwise newTop = oldTop * newMatrix
    */
    public void push( Matrix4d newMatrix )
	throws EmptyStackException
    {
	Matrix4d temp;
       
	if (newMatrix == null) {
	    // it's a "push" delimiter
	    temp = new Matrix4d((Matrix4d)s.peek());
	    s.push(temp);
	} else {
	    // it's real matrix (don't push -- just right multiply)
	    temp = (Matrix4d)s.peek();
	    temp.mul(newMatrix);
	}
    }
    
    /** pops a matrix off the stack */
    public void pop()
	throws EmptyStackException
    {
	s.pop();
    }
    
    /** returns a reference to the top matrix */
    public Matrix4d peek()
	throws EmptyStackException
    {
	return (Matrix4d)s.peek();
    }
}
