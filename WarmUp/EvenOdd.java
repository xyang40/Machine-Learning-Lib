import java.io.Serializable;
import java.util.List;

import cs475.ClassificationLabel;
import cs475.FeatureVector;
import cs475.Instance;
import cs475.Label;
import cs475.Predictor;

public class EvenOdd extends Predictor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int errand;

	public EvenOdd() {
		errand = -1;
	}

	@Override
	public void train(List<Instance> instances) {
		//doing nothing
		this.setErrand(-1);
	}

	@Override
	public Label predict(Instance instance) {
		double odd_sum = 0;
		double even_sum = 0;

		FeatureVector fv = instance.getFeatureVector();
		int[] keys = fv.getKeys();
		double[] values = fv.getValues();

		for (int i = 0; i < fv.getSize(); i++) {

			if (keys[i] % 2 == 1) {
				odd_sum += values[i];
			} else {
				even_sum += values[i];
			}
		}

		ClassificationLabel cl = null;
		if (even_sum >= odd_sum) {
			cl = new ClassificationLabel(1);
		} else {
			cl = new ClassificationLabel(0);
		}

		Label label = cl;
		return label;
	}

	public int getErrand() {
		return errand;
	}

	public void setErrand(int errand) {
		this.errand = errand;
	}

}
