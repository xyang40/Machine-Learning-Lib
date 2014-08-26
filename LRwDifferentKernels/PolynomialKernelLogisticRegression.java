

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cs475.ClassificationLabel;
import cs475.FeatureVector;
import cs475.Instance;
import cs475.Label;
import cs475.Predictor;

public class PolynomialKernelLogisticRegression extends Predictor implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private double gradient_ascent_learning_rate;
	private int gradient_ascent_training_iterations;
	private double polynomial_kernel_exponent;

	private HashMap<Integer, Double> alpha_index_map;
	private HashMap<Integer, Instance> support_vector_map;

	public PolynomialKernelLogisticRegression(double rate, int iterations,
			double exponent) {
		this.gradient_ascent_learning_rate = rate;
		this.gradient_ascent_training_iterations = iterations;
		this.polynomial_kernel_exponent = exponent;

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
			double[] values_outer = fv_outer.getValues();

			for (int j = 0; j < matrix[i].length; j++) {
				Instance inner = instances.get(j + i);
				FeatureVector fv_inner = inner.getFeatureVector();
				int[] keys_inner = fv_inner.getKeys();
				Integer[] cover_keys_inner = new Integer[keys_inner.length];
				for (int y = 0; y < keys_inner.length; y++) {
					cover_keys_inner[y] = new Integer(keys_inner[y]);
				}
				double[] values_inner = fv_inner.getValues();
				List<Integer> list = Arrays.asList(cover_keys_inner);

				double kernel = 0;
				for (int k = 0; k < keys_outer.length; k++) {
					int index_outer = keys_outer[k];
					

					int foundindex = list.indexOf(new Integer(index_outer));

					if (foundindex !=-1) {
						kernel += values_outer[k] * values_inner[foundindex];
					} else {
						kernel += 0;
					}
				}

				matrix[i][j] = Math.pow(1 + kernel,
						this.polynomial_kernel_exponent);
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

					gradient+= (labelvalue-1+link(-gradient_inner[i]))*kernel;
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
			double[] cur_values = cur_fv.getValues();
			List<Integer> list = Arrays.asList(cover_cur_keys);

			double kernel = 0;

			for (int j = 0; j < keys.length; j++) {
				int test_inst_index = keys[j];
					

				int foundindex = list.indexOf(new Integer(test_inst_index));

				if (foundindex !=-1) {
					kernel += values[j] * cur_values[foundindex];
				} else {
					kernel += 0;
				}
			}

			sum += alpha *Math.pow(1+kernel, this.polynomial_kernel_exponent) ;
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

	public double getPolynomial_kernel_exponent() {
		return polynomial_kernel_exponent;
	}

	public void setPolynomial_kernel_exponent(double polynomial_kernel_exponent) {
		this.polynomial_kernel_exponent = polynomial_kernel_exponent;
	}

}
