
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import cs475.ClassificationLabel;
import cs475.FeatureVector;
import cs475.Instance;
import cs475.Label;
import cs475.Predictor;

public class WinnowPredictor extends Predictor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private double[] weight;
	private int[] globalkeys;
	private double thickness;
	private double rate;
	private double iterations;
	private double threshold;

	public WinnowPredictor(double thickness, double online_learning_rate,
			int online_learning_iterations) {
		this.setThickness(thickness);
		this.setRate(online_learning_rate);
		this.setIterations(online_learning_iterations);
	}

	@Override
	public void train(List<Instance> instances) {
		this.setThreshold(instances.size() / 2);

		HashSet<Integer> featureindexSet = new HashSet<Integer>();

		for (Instance instance : instances) {
			FeatureVector fv = instance.getFeatureVector();
			int[] keys = fv.getKeys();

			for (int k : keys) {
				Integer I = new Integer(k);
				featureindexSet.add(I);
			}
		}

		int size = featureindexSet.size();
		this.setWeight(new double[size]);
		Arrays.fill(this.weight, 1);
		
		this.setGlobalkeys(new int[size]);

		Integer[] tempglobalkeys = new Integer[size];

		int chronicleindex = 0;
		for (Integer i : featureindexSet) {
			tempglobalkeys[chronicleindex] = i;
			chronicleindex++;
		}

		for (int i = 0; i < tempglobalkeys.length; i++) {
			this.globalkeys[i] = tempglobalkeys[i].intValue();
		}

		int iteration = 1;
		while (iteration <= this.iterations) {

			for (Instance instance : instances) {
				double product = 0;

				FeatureVector fv = instance.getFeatureVector();
				int[] keys = fv.getKeys();
				double[] values = fv.getValues();

				for (int i = 0; i < globalkeys.length; i++) {
					int featureindex = globalkeys[i];
					int locatedindex = -1;
					boolean found = false;

					for (int k = 0; k < keys.length; k++) {
						if (keys[k] == featureindex) {
							locatedindex = k;
							found = true;
							break;
						}
					}

					if (found == false) {
						product += 0;
					} else {
						product += weight[i] * values[locatedindex];
					}
				}

				int predicted = -100;

				if (product >= threshold + thickness) {
					predicted = 1;
				} else if (product <= threshold - thickness) {
					predicted = -1;
				} else {
					predicted = 0;
				}
				
			//	System.out.println(predicted);

				ClassificationLabel truelabel = (ClassificationLabel) instance
						.getLabel();
				int label = truelabel.getLabel();
				if (label == 0) {
					label = -1;
				}

				if ((label != predicted) || (predicted == 0)) {

					for (int i = 0; i < weight.length; i++) {
						int featureindex = globalkeys[i];

						int locatedindex = -1;
						boolean found = false;

						for (int k = 0; k < keys.length; k++) {
							if (keys[k] == featureindex) {
								locatedindex = k;
								found = true;
								break;
							}
						}

						if (found == false) {
							//weight[i] = weight[i] * Math.pow(rate, label);
							 
							//if (weight[i] >= Math.pow(10, 6)) {
							//	weight[i] = Math.pow(10, 6);	
							//}
						} else if(found==true) {
							if (values[locatedindex] >= 0) {
								weight[i] = weight[i] * Math.pow(rate, label);
							} else {
								weight[i] = weight[i] * Math.pow(rate, -label);
							}

							if (weight[i] >= Math.pow(10, 6)) {
								
								weight[i] = Math.pow(10, 6);
								
							}
							
						}
					}
				}
			}

			iteration++;
		}
	}

	@Override
	public Label predict(Instance instance) {

		double product = 0;

		FeatureVector fv = instance.getFeatureVector();
		int[] keys = fv.getKeys();
		double[] values = fv.getValues();

		for (int i = 0; i < globalkeys.length; i++) {
			int featureindex = globalkeys[i];
			int locatedindex = -1;
			boolean found = false;

			for (int k = 0; k < keys.length; k++) {
				if (keys[k] == featureindex) {
					locatedindex = k;
					found = true;
					break;
				}
			}

			if (found == false) {
				product += 0;
			} else {
				product += weight[i] * values[locatedindex];
			}
		}

		int predicted = -100;

	//	if (product >= threshold + thickness) {
		if (product >= threshold ) {
			predicted = 1;
		} else {
			predicted = 0;
		}
		
		Label label = new ClassificationLabel(predicted);
		//System.out.println(label);

		return label;
	}

	public double[] getWeight() {
		return weight;
	}

	public void setWeight(double[] weight) {
		this.weight = weight;
	}

	public int[] getGlobalkeys() {
		return globalkeys;
	}

	public void setGlobalkeys(int[] globalkeys) {
		this.globalkeys = globalkeys;
	}

	public double getThickness() {
		return thickness;
	}

	public void setThickness(double thickness) {
		this.thickness = thickness;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public double getIterations() {
		return iterations;
	}

	public void setIterations(double iterations) {
		this.iterations = iterations;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

}
