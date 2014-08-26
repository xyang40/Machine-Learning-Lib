

import java.io.Serializable;

public class ClassificationLabel extends Label implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int label;

	public ClassificationLabel(int label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return Integer.toString(label);
	}

	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}

	public boolean equals(Object cl) {

		return (cl instanceof ClassificationLabel)
				&& (this.getLabel() == ((ClassificationLabel) cl).getLabel());

	}

	public int hashCode() {
		return label;
	}

}
