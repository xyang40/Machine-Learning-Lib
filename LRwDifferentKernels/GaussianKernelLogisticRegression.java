
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import cs475.ClassificationLabel;
import cs475.FeatureVector;
import cs475.Instance;
import cs475.Label;
import cs475.Predictor;

public class GaussianKernelLogisticRegression extends Predictor implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private double gradient_ascent_learning_rate;
	private int gradient_ascent_training_iterations;
	private double gaussian_kernel_sigma;

	private HashMap<Integer, Double> alpha_index_map;
	private HashMap<Integer, Instance> support_vector_map;

	public GaussianKernelLogisticRegression(double rate, int iterations,
			double sigma) {
		this.setGradient_ascent_learning_rate(rate);
		this.setGradient_ascent_training_iterations(iterations);
		this.setGaussian_kernel_sigma(sigma);

		this.alpha_index_map = new HashMap<Integer, Double>();
		this.support_vector_map = new HashMap<Integer, Instance>();
	}

	public double link(double number) {
		return 1 / (1 + Math.pow(Math.E, -number));
	}

	@Override
	public void train(List<Instance> instances) {

		int size = instances.size();

		double[][] matrix = new double[size][];

		for (int i = 0; i < size; i++) {
			matrix[i] = new double[size - i];
		}

		for (int i = 0; i < size; i++) {
			Instance outer = instances.get(i);
			FeatureVector fv_outer = outer.getFeatureVector();
			int[] keys_outer = fv_outer.getKeys();
			Integer[] cover_keys_outer = new Integer[keys_outer.length];
			for (int y = 0; y < keys_outer.length; y++) {
				cover_keys_outer[y] = new Integer(keys_outer[y]);
			}
			List<Integer> list_outer = Arrays.asList(cover_keys_outer);
			HashSet<Integer> indexset1 = new HashSet<Integer>(list_outer);

			double[] values_outer = fv_outer.getValues();

			for (int j = 0; j < matrix[i].length; j++) {
				Instance inner = instances.get(j + i);
				FeatureVector fv_inner = inner.getFeatureVector();
				int[] keys_inner = fv_inner.getKeys();
				Integer[] cover_keys_inner = new Integer[keys_inner.length];
				for (int y = 0; y < keys_inner.length; y++) {
					cover_keys_inner[y] = new Integer(keys_inner[y]);
				}
				List<Integer> list_inner = Arrays.asList(cover_keys_inner);
				HashSet<Integer> indexset = new HashSet<Integer>(list_inner);

				double[] values_inner = fv_inner.getValues();

				indexset.addAll(indexset1);

				double kernel = 0;

				for (Integer featureind : indexset) {

					int featureindex = featureind.intValue();

					int foundindex_outer = list_outer.indexOf(new Integer(
							featureindex));
					int foundindex_inner = list_inner.indexOf(new Integer(
							featureindex));

					if (foundindex_outer != -1 && foundindex_inner == -1) {
						kernel += Math.pow(values_outer[foundindex_outer], 2);
					} else if (foundindex_outer != -1 && foundindex_inner != -1) {
						kernel += Math.pow(values_outer[foundindex_outer]
								- values_inner[foundindex_inner], 2);
					} else if (foundindex_outer == -1 && foundindex_inner != -1) {
						kernel += Math.pow(values_inner[foundindex_inner], 2);
					} else if (foundindex_outer == -1 && foundindex_inner == -1) {
						// System.out.println("aaa Impossible Scenario");
					}
				}

				matrix[i][j] = Math
						.pow(Math.E,
								-kernel
										/ (2 * this.gaussian_kernel_sigma * this.gaussian_kernel_sigma));
			}
		}

		double[] alpha = new double[size];
		double[] beta = new double[size];

		int iteration = 1;
		while (iteration <= this.gradient_ascent_training_iterations) {
			double[] gradient_inner = new double[size];
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					if (j < i) {
						gradient_inner[i] += alpha[j] * matrix[j][i - j];
					} else {
						gradient_inner[i] += alpha[j] * matrix[i][j - i];
					}
				}
			}

			for (int alpha_index = 0; alpha_index < size; alpha_index++) {

				double gradient = 0;

				for (int i = 0; i < size; i++) {

					Instance instance = instances.get(i);
					ClassificationLabel label = (ClassificationLabel) instance
							.getLabel();
					int labelvalue = label.getLabel();

					double kernel = 0;
					if (alpha_index < i) {
						kernel = matrix[alpha_index][i - alpha_index];
					} else {
						kernel = matrix[i][alpha_index - i];
					}

					gradient += (labelvalue - 1 + link(-gradient_inner[i]))
							* kernel;
				}

				beta[alpha_index] += gradient_ascent_learning_rate * gradient;
			}

			for (int z = 0; z < size; z++) {
				alpha[z] = beta[z];
			}

			iteration++;
		}

		for (int alpha_index = 0; alpha_index < size; alpha_index++) {
			if (alpha[alpha_index] != 0) {
				alpha_index_map.put(alpha_index, alpha[alpha_index]);
				support_vector_map.put(alpha_index, instances.get(alpha_index));
			}
		}
	}

	@Override
	public Label predict(Instance instance) {

		double sum = 0;
		FeatureVector fv = instance.getFeatureVector();
		int[] keys = fv.getKeys();
		Integer[] cover_keys = new Integer[keys.length];
		for (int y = 0; y < keys.length; y++) {
			cover_keys[y] = new Integer(keys[y]);
		}
		List<Integer> list = Arrays.asList(cover_keys);
		HashSet<Integer> indexset = new HashSet<Integer>(list);

		double[] values = fv.getValues();

		for (int index : alpha_index_map.keySet()) {

			double alpha = alpha_index_map.get(index);

			Instance cur_instance = support_vector_map.get(index);

			FeatureVector cur_fv = cur_instance.getFeatureVector();
			int[] cur_keys = cur_fv.getKeys();
			Integer[] cover_cur_keys = new Integer[cur_keys.length];
			for (int y = 0; y < cur_keys.length; y++) {
				cover_cur_keys[y] = new Integer(cur_keys[y]);
			}
			List<Integer> list_cur = Arrays.asList(cover_cur_keys);
			HashSet<Integer> indexset1 = new HashSet<Integer>(list_cur);

			indexset1.addAll(indexset);
			double[] cur_values = cur_fv.getValues();

			double kernel = 0;

			for (int featureindex : indexset1) {

				int foundindex = list.indexOf(new Integer(featureindex));
				int foundindex_cur = list_cur
						.indexOf(new Integer(featureindex));

				if (foundindex != -1 && foundindex_cur == -1) {
					kernel += Math.pow(values[foundindex], 2);
				} else if (foundindex != -1 && foundindex_cur != -1) {
					kernel += Math.pow(values[foundindex]
							- cur_values[foundindex_cur], 2);
				} else if (foundindex == -1 && foundindex_cur != -1) {
					kernel += Math.pow(cur_values[foundindex_cur], 2);
				} else if (foundindex == -1 && foundindex_cur == -1) {
					// System.out.println("Impossible Scenario");
				}
			}

			sum += alpha
					* Math.pow(
							Math.E,
							-kernel
									/ (2 * this.gaussian_kernel_sigma * this.gaussian_kernel_sigma));
		}

		Label label = null;
		if (link(sum) >= 0.5) {
			label = new ClassificationLabel(1);
		} else {
			label = new ClassificationLabel(0);
		}

		return label;
	}

	public double getGradient_ascent_learning_rate() {
		return gradient_ascent_learning_rate;
	}

	public void setGradient_ascent_learning_rate(
			double gradient_ascent_learning_rate) {
		this.gradient_ascent_learning_rate = gradient_ascent_learning_rate;
	}

	public int getGradient_ascent_training_iterations() {
		return gradient_ascent_training_iterations;
	}

	public void setGradient_ascent_training_iterations(
			int gradient_ascent_training_iterations) {
		this.gradient_ascent_training_iterations = gradient_ascent_training_iterations;
	}

	public double getGaussian_kernel_sigma() {
		return gaussian_kernel_sigma;
	}

	public void setGaussian_kernel_sigma(double gaussian_kernel_sigma) {
		this.gaussian_kernel_sigma = gaussian_kernel_sigma;
	}

}
