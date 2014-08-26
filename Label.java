
import java.io.Serializable;

public abstract class Label implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	public abstract String toString();

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
