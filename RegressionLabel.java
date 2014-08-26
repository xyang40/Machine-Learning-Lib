

import java.io.Serializable;

public class RegressionLabel extends Label implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private double label;

	public RegressionLabel(double label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return Double.toString(label);
	}

	public double getLabel() {
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
		return (int) label;
	}


}
