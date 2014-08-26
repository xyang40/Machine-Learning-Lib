
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import cs475.ClassificationLabel;
import cs475.FeatureVector;
import cs475.Instance;
import cs475.Label;
import cs475.Predictor;

public class LambdaMeansPredictor extends Predictor implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private double lambda;
	private int iterations;
	private ArrayList<int[]> proto_keys;
	private ArrayList<double[]> proto_values;
	private int[] arch_keys;

	public LambdaMeansPredictor(double l, int i) {
		lambda = l;
		iterations = i;
		proto_keys = new ArrayList<int[]>();
		proto_values = new ArrayList<double[]>();
 
	}

	@Override
	public void train(List<Instance> instances) {

		HashSet<Integer> featureindexSet = new HashSet<Integer>();

		for (Instance instance : instances) {
			FeatureVector fv = instance.getFeatureVector();
			int[] keys = fv.getKeys();
			for (int k : keys) {
				Integer I = new Integer(k);
				featureindexSet.add(I);
			}
		}

		this.arch_keys = new int[featureindexSet.size()];

		int chronicleindex = 0;
		for (Integer i : featureindexSet) {
			arch_keys[chronicleindex] = i.intValue();
			chronicleindex++;
		}

		double[] arch_values = new double[arch_keys.length];

		for (int i = 0; i < arch_keys.length; i++) {
			Integer cur_key = new Integer(arch_keys[i]);

			for (int j = 0; j < instances.size(); j++) {
				Instance instance = instances.get(j);
				FeatureVector fv = instance.getFeatureVector();
				int[] keys = fv.getKeys();
				ArrayList<Integer> aug_keys = new ArrayList<Integer>();
				for (int y = 0; y < keys.length; y++) {
					aug_keys.add(y, new Integer(keys[y]));
				}
				double[] values = fv.getValues();

				int foundindex = aug_keys.indexOf(cur_key);
				if (foundindex != -1) {
					arch_values[i] += values[foundindex];
				} else {
					arch_values[i] += 0;
				}
			}
			arch_values[i] /= instances.size();
		}
		proto_keys.add(0, arch_keys);
		proto_values.add(0, arch_values);

		if (this.getLambda() == 0.0) {
			double sum = 0;
			for (Instance instance : instances) {
				double localsum = 0;
				FeatureVector fv = instance.getFeatureVector();
				int[] keys = fv.getKeys();
				ArrayList<Integer> aug_keys = new ArrayList<Integer>();
				for (int y = 0; y < keys.length; y++) {
					aug_keys.add(y, new Integer(keys[y]));
				}
				double[] values = fv.getValues();

				for (int i = 0; i < arch_keys.length; i++) {
					Integer cur_key = new Integer(arch_keys[i]);
					int foundindex = aug_keys.indexOf(cur_key);
					if (foundindex != -1) {
						localsum += Math.pow(values[foundindex]
								- arch_values[i], 2);
					} else {
						localsum += Math.pow(arch_values[i], 2);
					}
				}
				localsum = Math.sqrt(localsum);
				sum += localsum;
			}
			sum = sum / instances.size();
			this.setLambda(sum);
		} else {
			System.out.println("No Necessity to Reset Lambda");
		}

		System.out.println("lambda "+this.getLambda());
		int[] r = new int[instances.size()];

		int iteration = 1;
		while (iteration <=this.iterations) {
			// E
			for (int index = 0; index < instances.size(); index++) {
				Instance instance = instances.get(index);
				FeatureVector fv = instance.getFeatureVector();
				int[] keys = fv.getKeys();
				ArrayList<Integer> aug_keys = new ArrayList<Integer>();
				for (int y = 0; y < keys.length; y++) {
					aug_keys.add(y, new Integer(keys[y]));
				}
				double[] values = fv.getValues();

				double least_distance = Double.MAX_VALUE;

				for (int cluster_index = 0; cluster_index < proto_values.size(); cluster_index++) {
					double distance = 0;
					int[] cluster_keys = proto_keys.get(cluster_index);
					ArrayList<Integer> aug_cluster_keys = new ArrayList<Integer>();
					for (int y = 0; y < cluster_keys.length; y++) {
						aug_cluster_keys.add(y, new Integer(cluster_keys[y]));
					}
					double[] cluster_values = proto_values.get(cluster_index);

					for (int i = 0; i < aug_cluster_keys.size(); i++) {
						int foundindex = aug_keys.indexOf(aug_cluster_keys
								.get(i));
						if (foundindex != -1) {
							distance += Math.pow(cluster_values[i]
									- values[foundindex], 2);
						} else {
							distance += Math.pow(cluster_values[i], 2);
						}
					}
					for (int i = 0; i < keys.length; i++) {
						if (!aug_cluster_keys.contains(keys[i])) {
							distance += Math.pow(values[i], 2);
						}
					}
					distance = Math.sqrt(distance);
					if (distance < least_distance) {
						least_distance = distance;
						r[index] = cluster_index;
					}
				}

				if (least_distance > this.getLambda()) {
					r[index] = this.proto_keys.size();
					this.proto_keys.add(keys);
					this.proto_values.add(values);
				}
			}

			// M
			for (int cluster_index = 0; cluster_index < proto_values.size(); cluster_index++) {

				int counter = 0;
				HashSet<Integer> localfeatureindexSet = new HashSet<Integer>();

				for (int i = 0; i < instances.size(); i++) {
					if (r[i] == cluster_index) {
						counter++;
						Instance instance = instances.get(i);
						FeatureVector fv = instance.getFeatureVector();
						int[] keys = fv.getKeys();
						ArrayList<Integer> aug_keys = new ArrayList<Integer>();
						for (int y = 0; y < keys.length; y++) {
							aug_keys.add(y, new Integer(keys[y]));
						}

						for (Integer I : aug_keys) {
							localfeatureindexSet.add(I);
						}
					}
				}

				int[] localfeaturelist = new int[localfeatureindexSet.size()];
				double[] newvalues = new double[localfeaturelist.length];

				int helpindex = 0;
				for (Integer i : localfeatureindexSet) {
					localfeaturelist[helpindex] = i.intValue();
					helpindex++;
				}

				for (int i = 0; i < instances.size(); i++) {
					if (r[i] == cluster_index) {
						Instance instance = instances.get(i);
						FeatureVector fv = instance.getFeatureVector();
						int[] keys = fv.getKeys();
						ArrayList<Integer> aug_keys = new ArrayList<Integer>();
						for (int y = 0; y < keys.length; y++) {
							aug_keys.add(y, new Integer(keys[y]));
						}
						double[] values = fv.getValues();

						for (int j = 0; j < localfeaturelist.length; j++) {
							Integer I = new Integer(localfeaturelist[j]);
							int foundindex = aug_keys.indexOf(I);
							if (foundindex != -1) {
								newvalues[j] += values[foundindex];
							} else {
								newvalues[j] += 0.0;
							}
						}
					}
				}

				// if (counter != 0) {
				for (int i = 0; i < newvalues.length; i++) {
					newvalues[i] /= counter;
				}
				proto_keys.set(cluster_index, localfeaturelist);
				proto_values.set(cluster_index, newvalues);
				// } else {
				// System.out.println("Impossible Scenario");
				// }
			}
			iteration++;
		}
		for(int i=0;i<this.proto_keys.size();i++){
			System.out.println("cluster index "+i);
			for(int j=0;j<proto_values.get(i).length;j++){
				System.out.println("feature index "+j + "value "+proto_values.get(i)[j]);
			}
		}
	}

	@Override
	public Label predict(Instance instance) {
		Label label = null;
		int labelindex = -1;

		FeatureVector fv = instance.getFeatureVector();
		int[] keys = fv.getKeys();
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int y = 0; y < keys.length; y++) {
			list.add(y, new Integer(keys[y]));
		}
		double[] values = fv.getValues();

		double least_distance = Double.MAX_VALUE;
		for (int cluster_index = 0; cluster_index < proto_values.size(); cluster_index++) {
			double distance = 0;
			int[] cluster_keys = proto_keys.get(cluster_index);
			ArrayList<Integer> aug_cluster_keys = new ArrayList<Integer>();
			for (int y = 0; y < cluster_keys.length; y++) {
				aug_cluster_keys.add(y, new Integer(cluster_keys[y]));
			}
			double[] cluster_values = proto_values.get(cluster_index);

			for (int i = 0; i < cluster_keys.length; i++) {
				Integer cur_key = new Integer(cluster_keys[i]);
				int foundindex = list.indexOf(cur_key);
				if (foundindex != -1) {
					distance += Math.pow(
							cluster_values[i] - values[foundindex], 2);
				} else {
					distance += Math.pow(cluster_values[i], 2);
				}
			}
			for (int i = 0; i < keys.length; i++) {
				if (!aug_cluster_keys.contains(keys[i])) {
					distance += Math.pow(values[i], 2);
				}
			}

			distance = Math.sqrt(distance);
			if (distance < least_distance) {
				least_distance = distance;
				labelindex = cluster_index;
			}
		}
		label = new ClassificationLabel(labelindex);
		return label;
	}

	public double getLambda() {
		return lambda;
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

	public int getIterations() {
		return iterations;
	}

	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	public ArrayList<double[]> getMiu() {
		return proto_values;
	}

	public void setMiu(ArrayList<double[]> miu) {
		this.proto_values = miu;
	}

	public int[] getArch_keys() {
		return arch_keys;
	}

	public void setArch_keys(int[] arch_keys) {
		this.arch_keys = arch_keys;
	}

}
