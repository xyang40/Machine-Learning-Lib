
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cs475.ClassificationLabel;
import cs475.FeatureVector;
import cs475.Instance;
import cs475.Label;
import cs475.Predictor;

public class DT extends Predictor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Tree treemodel;

	private ClassificationLabel majorityLabel;

	private int max_depth;

	public DT(int depth) {
		// the way of initialization
		this.treemodel = new Tree();
		majorityLabel = new ClassificationLabel(-9999);// the default value for
														// debug use
		max_depth = depth;
	}

	private ClassificationLabel getGlobalMajorityLabel(List<Instance> instances) {

		ClassificationLabel result = new ClassificationLabel(-9999);

		HashSet<ClassificationLabel> labelset = new HashSet<ClassificationLabel>();
		for (Instance instance : instances) {
			ClassificationLabel cl = (ClassificationLabel) instance.getLabel();
			labelset.add(cl);
		}

		int globalcount = -9999;
		for (ClassificationLabel label : labelset) {
			int count = 0;
			for (Instance instance : instances) {
				if (label.equals((ClassificationLabel) (instance.getLabel()))) {
					count++;
				}
			}
			if (count >= globalcount) {
				if (count == globalcount) {
					if (label.getLabel() < result.getLabel()) {
						result = label;
					}
				} else {
					result = label;
				}
				globalcount = count;
			}
		}
		return result;
	}

	// pick the feature to split using maximal information gain benchmark

	@SuppressWarnings("unused")
	public wrappedTree featurePick(List<Instance> instances,
			HashSet<Integer> instanceindexSet, HashSet<Integer> usedFeatures) {

		// the parameter instanceindexset is the hashset of all instance indices
		// being considered

		HashSet<Integer> featureindexSet = new HashSet<Integer>();
		// featureindexSet will store all the feature indices contained in these
		// instances

		for (Integer Index : instanceindexSet) {

			int index = Index.intValue();
			Instance instance = instances.get(index);
			FeatureVector fv = instance.getFeatureVector();
			int[] keys = fv.getKeys();

			for (int k : keys) {
				Integer I = new Integer(k);
				if (!usedFeatures.contains((Integer) I)) {
					featureindexSet.add(I);
				}
			}
		}

		BigDecimal leastconditionalentropy = new BigDecimal(9999);
		int feature_of_choice = -1;// default, since feature -1 does not exist
		double benchmark = -9999;

		HashSet<Integer> chosenleftindicesSet = null;
		HashSet<Integer> chosenrightindicesSet = null;

		for (Integer Index : featureindexSet) {

			int featureindex = Index.intValue();

			double featurevaluesum = 0;
			HashMap<ClassificationLabel, Integer> infmap = new HashMap<ClassificationLabel, Integer>();
			HashMap<ClassificationLabel, Integer> supmap = new HashMap<ClassificationLabel, Integer>();

			for (Integer i : instanceindexSet) {
				int instanceindex = i.intValue();
				Instance instance = instances.get(instanceindex);
				FeatureVector fv = instance.getFeatureVector();

				int[] keys = fv.getKeys();
				double[] values = fv.getValues();

				for (int k = 0; k < keys.length; k++) {

					if (keys[k] == featureindex) {

						featurevaluesum += values[k];
					}
				}
			}
			double featurevaluemean = featurevaluesum / instanceindexSet.size();
			// the feature comparison benchmark value obtained
			// (<= to) left oder (>) to right

			HashSet<Integer> leftindicesSet = new HashSet<Integer>();
			HashSet<Integer> rightindicesSet = new HashSet<Integer>();

			for (Integer i : instanceindexSet) {

				int instanceindex = i.intValue();
				Instance instance = instances.get(instanceindex);
				FeatureVector fv = instance.getFeatureVector();

				int[] keys = fv.getKeys();
				double[] values = fv.getValues();

				boolean existent = false;

				int theindex = -1;

				for (int k = 0; k < keys.length; k++) {
					if (keys[k] == featureindex) {
						existent = true;
						theindex = k;
						break;
					}
				}

				ClassificationLabel label = (ClassificationLabel) (instance
						.getLabel());

				if (existent == true) {
					if (values[theindex] <= featurevaluemean) {
						leftindicesSet.add(i);
						if (infmap.containsKey(label)) {
							infmap.put(label, infmap.get(label) + 1);
						} else {
							infmap.put(label, 1);
						}

					} else {
						rightindicesSet.add(i);
						if (supmap.containsKey(label)) {
							supmap.put(label, supmap.get(label) + 1);
						} else {
							supmap.put(label, 1);
						}
					}
				} else {
					if (0 <= featurevaluemean) {
						leftindicesSet.add(i);
						if (infmap.containsKey(label)) {
							infmap.put(label, infmap.get(label) + 1);
						} else {
							infmap.put(label, 1);
						}
					} else {
						rightindicesSet.add(i);
						if (supmap.containsKey(label)) {
							supmap.put(label, supmap.get(label) + 1);
						} else {
							supmap.put(label, 1);
						}
					}
				}
			}

			Collection<Integer> infc = infmap.values();
			BigDecimal infcount = new BigDecimal(0);
			for (Integer i : infc) {
				infcount = infcount.add(new BigDecimal(Integer.toString(i)));
			}
			BigDecimal supcount = new BigDecimal(instanceindexSet.size())
					.subtract(infcount);

			BigDecimal infp = infcount.divide(
					new BigDecimal(instanceindexSet.size()), 20,
					RoundingMode.HALF_UP);
			BigDecimal supp = new BigDecimal(1).subtract(infp);

			// for infmap
			java.util.Iterator<Entry<ClassificationLabel, Integer>> infiterator = infmap
					.entrySet().iterator();
			BigDecimal infentropy = new BigDecimal(0);
			while (infiterator.hasNext()) {
				Map.Entry<ClassificationLabel, Integer> entry = (Map.Entry<ClassificationLabel, Integer>) infiterator
						.next();
				ClassificationLabel label = entry.getKey();
				Integer Occurrences = entry.getValue();
				BigDecimal occurrences = new BigDecimal(
						Integer.toString(Occurrences));

				BigDecimal probability = occurrences.divide(infcount, 20,
						RoundingMode.HALF_UP);

				BigDecimal logprobability = new BigDecimal(Math.log(occurrences
						.divide(infcount, 20, RoundingMode.HALF_UP)
						.doubleValue())).divide(new BigDecimal(Math.log(2)),
						20, RoundingMode.HALF_UP);
				infentropy = infentropy.add(probability
						.multiply(logprobability).multiply(new BigDecimal(-1)));
			}

			// for supmap
			java.util.Iterator<Entry<ClassificationLabel, Integer>> supiterator = supmap
					.entrySet().iterator();
			BigDecimal supentropy = new BigDecimal(0);
			while (supiterator.hasNext()) {
				Map.Entry<ClassificationLabel, Integer> entry = (Map.Entry<ClassificationLabel, Integer>) supiterator
						.next();
				ClassificationLabel label = entry.getKey();
				Integer Occurrences = entry.getValue();
				BigDecimal occurrences = new BigDecimal(
						Integer.toString(Occurrences));

				BigDecimal probability = occurrences.divide(supcount, 20,
						RoundingMode.HALF_UP);
				BigDecimal logprobability = new BigDecimal(Math.log(occurrences
						.divide(supcount, 20, RoundingMode.HALF_UP)
						.doubleValue())).divide(new BigDecimal(Math.log(2)),
						20, RoundingMode.HALF_UP);

				supentropy = supentropy.add(probability
						.multiply(logprobability).multiply(new BigDecimal(-1)));

			}

			BigDecimal conditionalentropy = infp.multiply(infentropy).add(
					supp.multiply(supentropy));

			if (conditionalentropy.compareTo(leastconditionalentropy) != 1) {
				
				if (conditionalentropy.compareTo(leastconditionalentropy) == 0) {
					if (featureindex < feature_of_choice) {
						leastconditionalentropy = conditionalentropy;
						feature_of_choice = featureindex;
						benchmark = featurevaluemean;
						chosenleftindicesSet = leftindicesSet;
						chosenrightindicesSet = rightindicesSet;
					}
				} else {
					leastconditionalentropy = conditionalentropy;
					feature_of_choice = featureindex;
					benchmark = featurevaluemean;
					chosenleftindicesSet = leftindicesSet;
					chosenrightindicesSet = rightindicesSet;
				}
			}
		}

		wrappedTree wtree = new wrappedTree(feature_of_choice, benchmark,
				chosenleftindicesSet, chosenrightindicesSet);

		return wtree;
	}

	private boolean NoExamples(List<Instance> instances,
			HashSet<Integer> instanceindexSet) {
		boolean result = false;
		if (instanceindexSet.size() == 0) {
			result = true;
		}
		return result;
	}

	private boolean AllLabelsAgree(List<Instance> instances,
			HashSet<Integer> instanceindexSet) {
		boolean result = false;// by default

		HashSet<ClassificationLabel> labeltest = new HashSet<ClassificationLabel>();

		for (Integer i : instanceindexSet) {
			int index = i.intValue();
			Instance instance = instances.get(index);
			ClassificationLabel cl = (ClassificationLabel) instance.getLabel();
			labeltest.add(cl);
		}

		if (labeltest.size() == 1) {
			result = true;
		}

		return result;
	}

	private boolean NoFurtherSplits(List<Instance> instances,
			HashSet<Integer> instanceindexSet, HashSet<Integer> usedFeatures) {
		boolean result = true;

		HashSet<Integer> featureindexSet = new HashSet<Integer>();

		for (Integer Index : instanceindexSet) {
			int index = Index.intValue();
			Instance instance = instances.get(index);

			FeatureVector fv = instance.getFeatureVector();

			int[] keys = fv.getKeys();

			for (int k : keys) {
				Integer I = new Integer(k);

				if (!usedFeatures.contains((Integer) I)) {
					featureindexSet.add(I);
				}
			}
		}

		if (featureindexSet.size() == 0) {

			result = true;

		} else {

			for (Integer featureIndex : featureindexSet) {

				HashSet<Double> valueset = new HashSet<Double>();

				int featureindex = featureIndex.intValue();

				for (Integer instanceIndex : instanceindexSet) {
					int instanceindex = instanceIndex.intValue();
					Instance instance = instances.get(instanceindex);
					FeatureVector fv = instance.getFeatureVector();
					int[] keys = fv.getKeys();
					double[] values = fv.getValues();
					boolean existent = false;
					Double featureValue = new Double(9999);
					int k = -1;
					for (k = 0; k < keys.length; k++) {
						if (keys[k] == featureindex) {
							existent = true;
							break;
						}
					}

					if (existent == true) {
						featureValue = new Double(values[k]);
						valueset.add(featureValue);
					} else {
						featureValue = new Double(9999);
						valueset.add(featureValue);
					}
				}

				if (valueset.size() != 1) {
					result = false;
					break;
					// which means there is at least one feature we can split on
				}
			}
		}
		return result;
	}

	public Tree buildTree(List<Instance> instances,
			HashSet<Integer> instanceindexSet, int depth,
			HashSet<Integer> usedFeatures) {
		// return a decision tree built successfully
		// split the data into halves and decide boundaries
		if (depth == this.getMax_depth()) {

			ClassificationLabel result = new ClassificationLabel(-9999);

			HashSet<ClassificationLabel> labelset = new HashSet<ClassificationLabel>();

			for (Integer I : instanceindexSet) {
				int index = I.intValue();
				Instance instance = instances.get(index);
				ClassificationLabel cl = (ClassificationLabel) instance
						.getLabel();
				labelset.add(cl);
			}

			int globalcount = -9999;
			for (ClassificationLabel label : labelset) {
				int count = 0;
				for (Integer I : instanceindexSet) {
					int index = I.intValue();
					Instance instance = instances.get(index);
					if (label
							.equals((ClassificationLabel) (instance.getLabel()))) {
						count++;
					}
				}
				if (count >= globalcount) {

					if (count == globalcount) {
						if (label.getLabel() < result.getLabel()) {
							result = label;
						}

					} else {
						result = label;
					}
					globalcount = count;
				}
			}

			return new Tree(result);

		} else {

			if (NoExamples(instances, instanceindexSet)
					|| AllLabelsAgree(instances, instanceindexSet)
					|| NoFurtherSplits(instances, instanceindexSet,
							usedFeatures)) {

				if (NoExamples(instances, instanceindexSet)) {

					return new Tree(this.majorityLabel);
				}
				if (AllLabelsAgree(instances, instanceindexSet)) {

					ClassificationLabel commonlabel = null;
					int index = -1;
					for (Integer I : instanceindexSet) {
						index = I.intValue();
						break;
					}
					Instance instance = instances.get(index);
					commonlabel = (ClassificationLabel) instance.getLabel();

					return new Tree(commonlabel);
				}
				if (NoFurtherSplits(instances, instanceindexSet, usedFeatures)) {

					ClassificationLabel result = new ClassificationLabel(-9999);

					HashSet<ClassificationLabel> labelset = new HashSet<ClassificationLabel>();

					for (Integer I : instanceindexSet) {
						int index = I.intValue();
						Instance instance = instances.get(index);
						ClassificationLabel cl = (ClassificationLabel) instance
								.getLabel();
						labelset.add(cl);
					}

					int globalcount = -9999;
					for (ClassificationLabel label : labelset) {
						int count = 0;
						for (Integer I : instanceindexSet) {
							int index = I.intValue();
							Instance instance = instances.get(index);
							if (label.equals((ClassificationLabel) (instance
									.getLabel()))) {
								count++;
							}
						}

						if (count >= globalcount) {

							if (count == globalcount) {
								if (label.getLabel() < result.getLabel()) {
									result.setLabel(label.getLabel());
								}
							} else {
								result.setLabel(label.getLabel());
							}
							globalcount = count;
						}
					}

					return new Tree(result);
				} else {
					System.out.println("Impossible Scenario");
					return new Tree();
				}

			} else {

				Tree tree = new Tree();
				wrappedTree wtree = featurePick(instances, instanceindexSet,
						usedFeatures);

				tree.setKey(wtree.getKey());
				tree.setValue(wtree.getValue());

				HashSet<Integer> leftindicesSet = new HashSet<Integer>();
				leftindicesSet.addAll(wtree.getLeft());
				HashSet<Integer> rightindicesSet = new HashSet<Integer>();
				rightindicesSet.addAll(wtree.getRight());

				Integer usedFeature = new Integer(wtree.getKey());

				usedFeatures.add(usedFeature);

				HashSet<Integer> usedFeaturesOne = new HashSet<Integer>();
				usedFeaturesOne.addAll(usedFeatures);
				HashSet<Integer> usedFeaturesTwo = new HashSet<Integer>();
				usedFeaturesTwo.addAll(usedFeatures);

				int depthOne = depth + 1;
				int depthTwo = depth + 1;

				tree.setLeft(buildTree(instances, leftindicesSet, depthOne,
						usedFeaturesOne));

				tree.setRight(buildTree(instances, rightindicesSet, depthTwo,
						usedFeaturesTwo));

				return tree;
			}
		}
	}

	@Override
	public void train(List<Instance> instances) {
		// Goal: get a decision model built into our Tree treemodel
		this.majorityLabel = getGlobalMajorityLabel(instances);


		HashSet<Integer> instanceindexSet = new HashSet<Integer>();
		for (int index = 0; index < instances.size(); index++) {
			Integer Index = new Integer(index);
			instanceindexSet.add(Index);
		}

		int initial_depth = 0;

		HashSet<Integer> usedFeatures = new HashSet<Integer>();

		this.setTreemodel(buildTree(instances, instanceindexSet, initial_depth,
				usedFeatures));
	}

	@Override
	public Label predict(Instance instance) {
		// Goal: let each instance go through the Tree treemodel and make a
		// prediction
		Tree tree = this.treemodel;
		ClassificationLabel cl = stepThrough(tree, instance);
		Label label = cl;
		return label;
	}

	public ClassificationLabel stepThrough(Tree tree, Instance instance) {
		if (tree.getLabel() != null) {

			return tree.getLabel();

		} else {
			int featureindex = tree.getKey();
			double benchmarkvalue = tree.getValue();

			FeatureVector fv = instance.getFeatureVector();
			int[] keys = fv.getKeys();
			double[] values = fv.getValues();

			boolean existent = false;
			int k = -1;

			for (k = 0; k < keys.length; k++) {
				if (keys[k] == featureindex) {
					// if this instance contains the feature
					existent = true;
					break;
				}
			}

			if (existent == true) {
				if (values[k] <= benchmarkvalue) {
					return stepThrough(tree.getLeft(), instance);
				} else {
					return stepThrough(tree.getRight(), instance);
				}
			} else {
				if (0 <= benchmarkvalue) {
					return stepThrough(tree.getLeft(), instance);
				} else {
					return stepThrough(tree.getRight(), instance);
				}
			}
		}
	}

	public Tree getTreemodel() {
		return treemodel;
	}

	public void setTreemodel(Tree treemodel) {
		this.treemodel = treemodel;
	}

	public int getMax_depth() {
		return max_depth;
	}

	public void setMax_depth(int max_depth) {
		this.max_depth = max_depth;
	}

}
