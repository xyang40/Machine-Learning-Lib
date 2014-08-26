
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.text.html.HTMLDocument.Iterator;

import cs475.ClassificationLabel;
import cs475.FeatureVector;
import cs475.Instance;
import cs475.Label;
import cs475.Predictor;
import cs475.RegressionLabel;

public class EnsembleFeatureBaggingPredictor extends Predictor implements
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

	public EnsembleFeatureBaggingPredictor(int k, double r, int i) {
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

		HashSet<Integer> featureindexSet = new HashSet<Integer>();
		for (Instance instance : instances) {
			FeatureVector fv = instance.getFeatureVector();
			int[] keys = fv.getKeys();

			for (int k : keys) {
				Integer I = new Integer(k);
				featureindexSet.add(I);
			}
		}

		for (int index_ens = 0; index_ens < k_ensemble; index_ens++) {

			LinkedList<Instance> subinstances = new LinkedList<Instance>();

			for (Instance instance : instances) {

				FeatureVector tempfv = null;
				try {
					tempfv = (FeatureVector) instance.getFeatureVector()
							.clone();
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				Label templabel = null;
				try {
					templabel = (Label) instance.getLabel().clone();
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				// Instance tempinstance = new Instance(tempfv, templabel);

				// FeatureVector fv = tempinstance.getFeatureVector();
				double[] values = tempfv.getValues();
				int[] keys = tempfv.getKeys();
				Integer[] cover_keys = new Integer[keys.length];
				for (int y = 0; y < keys.length; y++) {
					cover_keys[y] = new Integer(keys[y]);
				}
				List<Integer> list = Arrays.asList(cover_keys);
				
				LinkedList<Integer> keylist = new LinkedList<Integer>();
				LinkedList<Double> vallist = new LinkedList<Double>();

				for (Integer featureindex : featureindexSet) {
					int foundindex = list.indexOf(featureindex);

					// Care
					if (foundindex != -1) {
						if (featureindex.intValue() % k_ensemble == index_ens) {
							//values[foundindex] = 0;
							keylist.add(new Integer(featureindex));
							vallist.add(new Double(values[foundindex]));
						}
					}
				}
				
				Integer[] keylistasarray = keylist.toArray(new Integer[0]);
				int[] keylistasarrayprim = new int[keylistasarray.length];
				Double[] vallistasarray = vallist.toArray(new Double[0]);
				double[] vallistasarrayprim = new double[vallistasarray.length];
				for (int i=0;i<keylistasarray.length;i++){
					keylistasarrayprim[i]=keylistasarray[i].intValue();
				}
				
				for (int i=0;i<keylistasarray.length;i++){
					vallistasarrayprim[i]=vallistasarray[i].doubleValue();
				}

                tempfv.setKeys(keylistasarrayprim);
				tempfv.setValues(vallistasarrayprim);
				//tempinstance.setFeatureVector(fv);
				subinstances.add(new Instance(tempfv,templabel ));
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
				ClassificationLabel rlabel = (ClassificationLabel) instance
						.getLabel();
				double reallabel = rlabel.getLabel();

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
			// System.out.println(k+" index "+mu[k]+" "+rlabel.getLabel());
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

	public double[] getMu() {
		return mu;
	}

	public void setMu(double[] mu) {
		this.mu = mu;
	}

	public PerceptronPredictor[] getEnsemble() {
		return ensemble;
	}

	public void setEnsemble(PerceptronPredictor[] ensemble) {
		this.ensemble = ensemble;
	}

}
