import java.io.Serializable;
import java.util.List;

import cs475.ClassificationLabel;
import cs475.Instance;
import cs475.Label;
import cs475.Predictor;

public class Majority extends Predictor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int majority_label;

	public Majority() {
		majority_label = -1;
	}

	@Override
	public void train(List<Instance> instances) {
		int one = 0;
		int nil = 0;
		for (Instance instance : instances) {
			Label label = instance.getLabel();
			if (label instanceof ClassificationLabel) {
				if (((ClassificationLabel) label).getLabel() == 1) {
					one++;
				} else if (((ClassificationLabel) label).getLabel() == 0) {
					nil++;
				} else {
					System.err.println("Unexpected Label");
				}
			} else {
				System.err
						.println("This should be the case of RegressionLabel");
			}
		}

		if (one > nil) {
			this.setMajority_label(1);
		}
		if (one < nil) {
			this.setMajority_label(0);
		}
		if (one == nil) {
			double rand = Math.random();
			if (rand >= 0.5) {
				this.setMajority_label(1);
			} else {
				this.setMajority_label(0);
			}
		}
	}

	@Override
	public Label predict(Instance instance) {
		ClassificationLabel cl = new ClassificationLabel(
				this.getMajority_label());
		Label label = cl;
		return label;
	}

	public int getMajority_label() {
		return majority_label;
	}

	public void setMajority_label(int majority_label) {
		this.majority_label = majority_label;
	}

}
