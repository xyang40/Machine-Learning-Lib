
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import cs475.FeatureVector;
import cs475.Instance;
import cs475.Label;
import cs475.Predictor;
import cs475.RegressionLabel;

public class DistanceWeightedKNNPredictor extends Predictor implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int knn;
	private List<Instance> instances;

	public DistanceWeightedKNNPredictor(int knn) {
		this.knn = knn;
		instances = null;
	}

	@Override
	public void train(List<Instance> instances) {
		// TODO Auto-generated method stub
		this.setInstances(instances);
	}

	@Override
	public Label predict(Instance instance) {

		LinkedList<Double> K = new LinkedList<Double>();
		LinkedList<Double> L = new LinkedList<Double>();
		int index = 0;

		FeatureVector fv = instance.getFeatureVector();

		int[] keys = fv.getKeys();
		Integer[] aug_keys = new Integer[keys.length];
		for (int y = 0; y < keys.length; y++) {
			aug_keys[y] = new Integer(keys[y]);
		}

		List<Integer> list = Arrays.asList(aug_keys);

		double[] values = fv.getValues();

		for (Instance inst : this.instances) {
			RegressionLabel label = (RegressionLabel) inst.getLabel();
			double distance = 0;

			FeatureVector fv_in = inst.getFeatureVector();
			int[] keys_in = fv_in.getKeys();
			double[] values_in = fv_in.getValues();

			for (int i = 0; i < keys_in.length; i++) {
				Integer key_in = new Integer(keys_in[i]);
				double value_in = values_in[i];

				int foundindex = list.indexOf(key_in);

				if (foundindex != -1) {
					distance += Math.pow(values[foundindex] - value_in, 2);
				} else {
					distance += Math.pow(value_in, 2);
				}
			}

			Double D = new Double(Math.sqrt(distance));
			//Double D = new Double( distance);

			K.add(index, D);
			L.add(index, new Double(label.getLabel()));
			index++;
		}

		double lambdasum = 0;
		int ini = 1;
		LinkedList<Double> tempK = new LinkedList<Double>();
		tempK.addAll(K);

		while (ini <= knn) {
			Double min = Double.MAX_VALUE;
			for (Double d : tempK) {
				if (d < min) {
					min = d;
				}
			}
			int foundindex = tempK.indexOf(min);
			if (foundindex != -1) {
				lambdasum += 1 / (1 + tempK.get(foundindex));
				tempK.set(foundindex, Double.MAX_VALUE);
			}
			ini++;
		}

		double labelsum = 0;
		int ini2 = 1;
		while (ini2 <= this.knn) {
			Double min = Double.MAX_VALUE;
			for (Double d : K) {
				if (d < min) {
					min = d;
				}
			}
			int foundindex = K.indexOf(min);
			if (foundindex != -1) {

				labelsum += ((1 / (1 + K.get(foundindex))) / lambdasum)
						* L.get(foundindex);
				// System.out.println(((1 / (1 + K.get(foundindex))) /
				// lambdasum)+" "+L.get(foundindex));
				K.set(foundindex, Double.MAX_VALUE);
			}
			ini2++;
		}

		Label label = new RegressionLabel(labelsum);

		return label;
	}

	public int getKnn() {
		return knn;
	}

	public void setKnn(int knn) {
		this.knn = knn;
	}

	public List<Instance> getInstances() {
		return instances;
	}

	public void setInstances(List<Instance> instances) {
		this.instances = instances;
	}

}
