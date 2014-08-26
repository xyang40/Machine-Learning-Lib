

import java.util.List;

public class AccuracyEvaluator extends Evaluator {

	@Override
	public double evaluate(List<Instance> instances, Predictor predictor) {

		double correct_number = 0;

		for (Instance instance : instances) {

			if (instance.getLabel() instanceof ClassificationLabel) {

				ClassificationLabel label = (ClassificationLabel) instance
						.getLabel();
				ClassificationLabel prediction = (ClassificationLabel) predictor
						.predict(instance);

				if (instance.getLabel() != null) {

					if (label.equals(prediction)) {
						correct_number++;
					}
				}
			} else if (instance.getLabel() instanceof RegressionLabel) {

				RegressionLabel label = (RegressionLabel) instance.getLabel();
				RegressionLabel prediction = (RegressionLabel) predictor
						.predict(instance);

				correct_number += Math.abs(prediction.getLabel()
						- label.getLabel());
			}
		}

		System.out.println(correct_number / instances.size());
		return correct_number / instances.size();
	}
}
