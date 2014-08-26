

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;

import cs475.ClassificationLabel;
import cs475.FeatureVector;
import cs475.Instance;
import cs475.Label;
import cs475.Predictor;

public class NaiveBayesPredictor extends Predictor implements Serializable {

	private double lambda;

	private double y1Prob;
	private double y2Prob;

	private double[] featuremean;

	// LSB probabilities
	private double[] featureProbGivenY1;
	private double[] featureProbGivenY2;

	private HashSet<Integer> featureindexSet;
	private int[] globalkeys;

	public NaiveBayesPredictor(double l) {
		this.setLambda(l);
		featureindexSet = new HashSet<Integer>();
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void train(List<Instance> instances) {
		// TODO Auto-generated method stub
		double y1count = 0;
		double y2count = 0;

		for (Instance instance : instances) {
			ClassificationLabel label = (ClassificationLabel) instance
					.getLabel();

			if (label.equals(new ClassificationLabel(0))) {
				y1count++;
			} else if (label.equals(new ClassificationLabel(1))) {
				y2count++;
			} else {
				System.out.println("Unexpected Label?");
			}
		}

		this.setY1Prob((y1count + lambda) / (instances.size() + 2 * lambda));
		this.setY2Prob(1 - y1Prob);

		for (Instance instance : instances) {

			FeatureVector fv = instance.getFeatureVector();
			int[] keys = fv.getKeys();

			for (int k : keys) {
				Integer I = new Integer(k);
				featureindexSet.add(I);
			}
		}

		int size = featureindexSet.size();

		this.setFeaturemean(new double[size]);
		this.setFeatureProbGivenY1(new double[size]);
		this.setFeatureProbGivenY2(new double[size]);
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

		for (int i = 0; i < globalkeys.length; i++) {
			double sum = 0;
			int featureindex = globalkeys[i];
			int counter = 0;

			for (Instance instance : instances) {
				FeatureVector fv = instance.getFeatureVector();
				int[] keys = fv.getKeys();
				double[] values = fv.getValues();

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
					sum = sum + 0;
				} else {
					sum = sum + values[locatedindex];
					counter++;
				}
			}

			 featuremean[i] = sum / instances.size();
			//if (Math.abs(sum - counter) < 0.000001) {
			//	featuremean[i] = 666.1;// set arbitrarily to
										// accommodate for binary
										// features
		//	} else {
				//featuremean[i] = sum / counter;// all other continuous features
												// follow
		//	}
		}

		for (int i = 0; i < globalkeys.length; i++) {
			double LSBcountunderY1 = 0;
			double LSBcountunderY2 = 0;

			double existentY1count = 0;
			double existentY2count = 0;

			int featureindex = globalkeys[i];

			for (Instance instance : instances) {

				ClassificationLabel label = (ClassificationLabel) instance
						.getLabel();
				FeatureVector fv = instance.getFeatureVector();
				int[] keys = fv.getKeys();
				double[] values = fv.getValues();

				if (label.equals(new ClassificationLabel(0))) {

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
						// if (0 <= featuremean[i]) {
						//if (featuremean[i] == 666.1) {
						//	LSBcountunderY1++;
						//}
						if (0 <= featuremean[i]) {
							LSBcountunderY1++;
						}
					} else {
						existentY1count++;

						//if (featuremean[i] != 666.1) {
							if (values[locatedindex] <= featuremean[i]) {
								LSBcountunderY1++;
							}
					//	} else {
					//		if (values[locatedindex] == 0) {
						//		LSBcountunderY1++;
						//	}
						//}
					}

				} else if (label.equals(new ClassificationLabel(1))) {

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
						// if (0 <= featuremean[i]) {
					//	if (featuremean[i] == 666.1) {
					//		LSBcountunderY2++;
						//}
						if (0 <= featuremean[i]) {
							LSBcountunderY2++;
						}
					} else {
						existentY2count++;

					//	if (featuremean[i] != 666.1) {
							if (values[locatedindex] <= featuremean[i]) {
								LSBcountunderY2++;
							}
					//	} else {
						//	if (values[locatedindex] == 0.0) {
						//		LSBcountunderY2++;
					//		}
					//	}
					}
				} else {

					System.out.println("Exceptional Circumstances");
				}

				/*
				 * this.featureProbGivenY1[i] = (LSBcountunderY1 + lambda) /
				 * (y1count + 2 * lambda); this.featureProbGivenY2[i] =
				 * (LSBcountunderY2 + lambda) / (y2count + 2 * lambda);
				 */
				
				//if (featuremean[i] == 666.1) {
					this.featureProbGivenY1[i] = (LSBcountunderY1 + lambda)
							/ (y1count + 2 * lambda);
					this.featureProbGivenY2[i] = (LSBcountunderY2 + lambda)
							/ (y2count + 2 * lambda);
				//} else {*/
					//this.featureProbGivenY1[i] = (LSBcountunderY1 + lambda)
				//			/ (existentY1count + 2 * lambda);
				//	this.featureProbGivenY2[i] = (LSBcountunderY2 + lambda)
					//		/ (existentY2count + 2 * lambda);
			//	}
			}
		}

	}

	@Override
	public Label predict(Instance instance) {

		FeatureVector fv = instance.getFeatureVector();
		int[] keys = fv.getKeys();
		double[] values = fv.getValues();

		double posteriorY1Prob = Math.log(this.y1Prob) / Math.log(2);
		double posteriorY2Prob = Math.log(this.y2Prob) / Math.log(2);

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
				
				//  if (0 <= featuremean[i]) { posteriorY1Prob +=
				//  Math.log(featureProbGivenY1[i]) / Math.log(2);
				//  posteriorY2Prob += Math.log(featureProbGivenY2[i]) /
				//  Math.log(2); } else { posteriorY1Prob += Math.log((1 -
				//  featureProbGivenY1[i])) / Math.log(2); posteriorY2Prob +=
				//  Math.log((1 - featureProbGivenY2[i])) / Math.log(2); }
				
				
			} else {
				//if (featuremean[i] != 666.1) {
					if (values[locatedindex] <= featuremean[i]) {
						posteriorY1Prob += Math.log(featureProbGivenY1[i])
								/ Math.log(2);
						posteriorY2Prob += Math.log(featureProbGivenY2[i])
								/ Math.log(2);
					} else {
						posteriorY1Prob += Math
								.log((1 - featureProbGivenY1[i])) / Math.log(2);
						posteriorY2Prob += Math
								.log((1 - featureProbGivenY2[i])) / Math.log(2);
					}
			//	} else {
				//	if (values[locatedindex] ==0.0) {
				//		posteriorY1Prob += Math.log(featureProbGivenY1[i])
				//				/ Math.log(2);
				//		posteriorY2Prob += Math.log(featureProbGivenY2[i])
				//				/ Math.log(2);
				//	} else {
				//		posteriorY1Prob += Math
				//				.log((1 - featureProbGivenY1[i])) / Math.log(2);
				//		posteriorY2Prob += Math
				//				.log((1 - featureProbGivenY2[i])) / Math.log(2);
				//	}

				//}
			}
		}

		Label label = null;

		if (posteriorY1Prob < posteriorY2Prob) {
			label = new ClassificationLabel(1);
		} else if (posteriorY1Prob > posteriorY2Prob) {
			label = new ClassificationLabel(0);
		} else {
			label = new ClassificationLabel(1);
		}

		return label;

	}

	public double getLambda() {
		return lambda;
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

	public double[] getFeaturemean() {
		return featuremean;
	}

	public void setFeaturemean(double[] featureSplit) {
		this.featuremean = featureSplit;
	}

	public HashSet<Integer> getFeatureindexSet() {
		return this.featureindexSet;
	}

	public void setFeatureindexSet(HashSet<Integer> set) {
		this.featureindexSet = set;
	}

	public int[] getGlobalkeys() {
		return globalkeys;
	}

	public void setGlobalkeys(int[] globalkeys) {
		this.globalkeys = globalkeys;
	}

	public double[] getFeatureProbGivenY1() {
		return featureProbGivenY1;
	}

	public void setFeatureProbGivenY1(double[] featureProbGivenY1) {
		this.featureProbGivenY1 = featureProbGivenY1;
	}

	public double[] getFeatureProbGivenY2() {
		return featureProbGivenY2;
	}

	public void setFeatureProbGivenY2(double[] featureProbGivenY2) {
		this.featureProbGivenY2 = featureProbGivenY2;
	}

	public double getY1Prob() {
		return y1Prob;
	}

	public void setY1Prob(double y1Prob) {
		this.y1Prob = y1Prob;
	}

	public double getY2Prob() {
		return y2Prob;
	}

	public void setY2Prob(double y2Prob) {
		this.y2Prob = y2Prob;
	}
}
