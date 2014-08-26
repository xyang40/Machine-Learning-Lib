
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import cs475.ClassificationLabel;
import cs475.Instance;
import cs475.Label;
import cs475.Predictor;
import cs475.RegressionLabel;

public class EnsembleInstanceBaggingPredictor extends Predictor implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int k_ensemble;

	private double ensemble_learning_rate;

	private int ensemble_training_iterations;

	private double[] mu;

	private PerceptronPredictor[] ensemble;

	public EnsembleInstanceBaggingPredictor(int k, double r, int i) {
		this.setK_ensemble(k);
		this.setEnsemble_learning_rate(r);
		this.setEnsemble_training_iterations(i);
		this.ensemble = new PerceptronPredictor[k];
		for (int z = 0; z < ensemble.length; z++) {
			ensemble[z] = new PerceptronPredictor();
		}
		this.setMu(new double[k]);
	}

	@Override
	public void train(List<Instance> instances) {

		for (int index_ens = 0; index_ens < this.k_ensemble; index_ens++) {
			List<Instance> subinstances = new LinkedList<Instance>();
			for (int index = 0; index < instances.size(); index++) {
				if (index % k_ensemble != index_ens) {
					subinstances.add(instances.get(index));
				}
			}
			ensemble[index_ens].train(subinstances);
		}

		int iteration = 1;
		while (iteration <= this.ensemble_training_iterations) {
			for (Instance instance : instances) {
				double sum = 0;
				for (int k = 0; k < this.k_ensemble; k++) {
					RegressionLabel rlabel = (RegressionLabel) ensemble[k]
							.predict(instance);
					sum += mu[k] * (rlabel.getLabel());
				}
				int predicted = -100;
				if (sum >= 0) {
					predicted = 1;
				} else {
					predicted = 0;
				}
				ClassificationLabel rlabel = (ClassificationLabel) instance.getLabel();
				int reallabel = rlabel.getLabel();

				if (reallabel != predicted) {
					if (reallabel == 1) {
						for (int k = 0; k < this.k_ensemble; k++) {
							RegressionLabel reglabel = (RegressionLabel) ensemble[k]
									.predict(instance);
							mu[k] += this.ensemble_learning_rate
									* (reglabel.getLabel());
						}
					} else if (reallabel == 0) {
						for (int k = 0; k < this.k_ensemble; k++) {
							RegressionLabel reglabel = (RegressionLabel) ensemble[k]
									.predict(instance);
							mu[k] -= this.ensemble_learning_rate
									* (reglabel.getLabel());
						}
					} else {
						System.out.println("Impossible Scenario");
					}
				}
			}

			iteration++;
		}

	}

	@Override
	public Label predict(Instance instance) {
		double sum = 0;
		for (int k = 0; k < this.k_ensemble; k++) {
			RegressionLabel rlabel = (RegressionLabel) ensemble[k]
					.predict(instance);
			sum += mu[k] * (rlabel.getLabel());
		}
		int predicted = -100;
		if (sum >= 0) {
			predicted = 1;
		} else {
			predicted = 0;
		}
		Label rlabel = new ClassificationLabel(predicted);
		return rlabel;
	}

	public int getK_ensemble() {
		return k_ensemble;
	}

	public void setK_ensemble(int k_ensemble) {
		this.k_ensemble = k_ensemble;
	}

	public double getEnsemble_learning_rate() {
		return ensemble_learning_rate;
	}

	public void setEnsemble_learning_rate(double ensemble_learning_rate) {
		this.ensemble_learning_rate = ensemble_learning_rate;
	}

	public int getEnsemble_training_iterations() {
		return ensemble_training_iterations;
	}

	public void setEnsemble_training_iterations(int ensemble_training_iterations) {
		this.ensemble_training_iterations = ensemble_training_iterations;
	}

	public PerceptronPredictor[] getEnsemble() {
		return ensemble;
	}

	public void setEnsemble(PerceptronPredictor[] ensemble) {
		this.ensemble = ensemble;
	}

	public double[] getMu() {
		return mu;
	}

	public void setMu(double[] mu) {
		this.mu = mu;
	}

}
