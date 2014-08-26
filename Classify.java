

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import cs475.HW1.EvenOdd;
import cs475.HW1.Majority;
import cs475.HW2.DT;
import cs475.HW3.NaiveBayesPredictor;
import cs475.HW3.PerceptronPredictor;
import cs475.HW3.WinnowPredictor;
import cs475.HW4.GaussianKernelLogisticRegression;
import cs475.HW4.LinearKernelLogisticRegression;
import cs475.HW4.PolynomialKernelLogisticRegression;
import cs475.HW5.DistanceWeightedKNNPredictor;
import cs475.HW5.EnsembleFeatureBaggingPredictor;
import cs475.HW5.EnsembleInstanceBaggingPredictor;
import cs475.HW5.SimpleKNNPredictor;
import cs475.HW6.LambdaMeansPredictor;

public class Classify {
	static public LinkedList<Option> options = new LinkedList<Option>();

	public static void main(String[] args) throws IOException {
		// Parse the command line.
		String[] manditory_args = { "mode" };
		createCommandLineOptions();
		CommandLineUtilities.initCommandLineParameters(args, Classify.options,
				manditory_args);

		String mode = CommandLineUtilities.getOptionValue("mode");
		String data = CommandLineUtilities.getOptionValue("data");
		String predictions_file = CommandLineUtilities
				.getOptionValue("predictions_file");
		String algorithm = CommandLineUtilities.getOptionValue("algorithm");
		String model_file = CommandLineUtilities.getOptionValue("model_file");

		int max_decision_tree_depth = 4;
		if (CommandLineUtilities.hasArg("max_decision_tree_depth")) {
			max_decision_tree_depth = CommandLineUtilities
					.getOptionValueAsInt("max_decision_tree_depth");
		}

		double lambda = 1.0;
		if (CommandLineUtilities.hasArg("lambda"))
			lambda = CommandLineUtilities.getOptionValueAsFloat("lambda");

		double thickness = 0.0;
		if (CommandLineUtilities.hasArg("thickness"))
			thickness = CommandLineUtilities.getOptionValueAsFloat("thickness");

		double online_learning_rate = algorithm.equals("winnow") ? 2.0 : 1.0;
		if (CommandLineUtilities.hasArg("online_learning_rate"))
			online_learning_rate = CommandLineUtilities
					.getOptionValueAsFloat("online_learning_rate");

		int online_training_iterations = 1;
		if (CommandLineUtilities.hasArg("online_training_iterations"))
			online_training_iterations = CommandLineUtilities
					.getOptionValueAsInt("online_training_iterations");

		double polynomial_kernel_exponent = 2;
		if (CommandLineUtilities.hasArg("polynomial_kernel_exponent"))
			polynomial_kernel_exponent = CommandLineUtilities
					.getOptionValueAsFloat("polynomial_kernel_exponent");

		double gaussian_kernel_sigma = 1;
		if (CommandLineUtilities.hasArg("gaussian_kernel_sigma"))
			gaussian_kernel_sigma = CommandLineUtilities
					.getOptionValueAsFloat("gaussian_kernel_sigma");

		double gradient_ascent_learning_rate = 0.01;
		if (CommandLineUtilities.hasArg("gradient_ascent_learning_rate"))
			gradient_ascent_learning_rate = CommandLineUtilities
					.getOptionValueAsFloat("gradient_ascent_learning_rate");

		int gradient_ascent_training_iterations = 5;
		if (CommandLineUtilities.hasArg("gradient_ascent_training_iterations"))
			gradient_ascent_training_iterations = CommandLineUtilities
					.getOptionValueAsInt("gradient_ascent_training_iterations");

		int k_nn = 5;
		if (CommandLineUtilities.hasArg("k_nn"))
			k_nn = CommandLineUtilities.getOptionValueAsInt("k_nn");

		int k_ensemble = 5;
		if (CommandLineUtilities.hasArg("k_ensemble"))
			k_ensemble = CommandLineUtilities.getOptionValueAsInt("k_ensemble");

		double ensemble_learning_rate = 0.1;
		if (CommandLineUtilities.hasArg("ensemble_learning_rate"))
			ensemble_learning_rate = CommandLineUtilities
					.getOptionValueAsFloat("ensemble_learning_rate");

		int ensemble_training_iterations = 5;
		if (CommandLineUtilities.hasArg("ensemble_training_iterations"))
			ensemble_training_iterations = CommandLineUtilities
					.getOptionValueAsInt("ensemble_training_iterations");

		double cluster_lambda = 0.0;
		if (CommandLineUtilities.hasArg("cluster_lambda"))
			cluster_lambda = CommandLineUtilities
					.getOptionValueAsFloat("cluster_lambda");

		int clustering_training_iterations = 10;
		if (CommandLineUtilities.hasArg("clustering_training_iterations"))
			clustering_training_iterations = CommandLineUtilities
					.getOptionValueAsInt("clustering_training_iterations");

		if (mode.equalsIgnoreCase("train")) {
			if (data == null || algorithm == null || model_file == null) {
				System.out
						.println("Train requires the following arguments: data, algorithm, model_file");
				System.exit(0);
			}
			// Load the training data.
			DataReader data_reader = null;
			if (algorithm.equals("knn") || algorithm.equals("knn_distance")) {
				data_reader = new DataReader(data, false);

			} else {
				data_reader = new DataReader(data, true);
			}
			List<Instance> instances = data_reader.readData();
			data_reader.close();

			// Train the model.
			Predictor predictor = train(instances, algorithm,
					max_decision_tree_depth, lambda, thickness,
					online_learning_rate, online_training_iterations,
					polynomial_kernel_exponent, gaussian_kernel_sigma,
					gradient_ascent_learning_rate,
					gradient_ascent_training_iterations, k_nn, k_ensemble,
					ensemble_learning_rate, ensemble_training_iterations,
					cluster_lambda, clustering_training_iterations);

			saveObject(predictor, model_file);

		} else if (mode.equalsIgnoreCase("test")) {
			if (data == null || predictions_file == null || model_file == null) {
				System.out
						.println("Train requires the following arguments: data, predictions_file, model_file");
				System.exit(0);
			}

			// Load the test data.
			DataReader data_reader = null;

			if (algorithm.equals("knn") || algorithm.equals("knn_distance")) {
				data_reader = new DataReader(data, false);

			} else {
				data_reader = new DataReader(data, true);
			}

			List<Instance> instances = data_reader.readData();
			data_reader.close();

			// Load the model.
			Predictor predictor = (Predictor) loadObject(model_file);
			evaluateAndSavePredictions(predictor, instances, predictions_file);
		} else {
			System.out.println("Requires mode argument.");
		}

	}

	private static Predictor train(List<Instance> instances, String algorithm,
			int max_decision_tree_depth, double lambda, double thickness,
			double online_learning_rate, int online_learning_iterations,
			double polynomial_kernel_exponent, double gaussian_kernel_sigma,
			double gradient_ascent_learning_rate,
			int gradient_ascent_training_iterations, int k_nn, int k_ensemble,
			double ensemble_learning_rate, int ensemble_training_iterations,
			double cluster_lambda, int clustering_training_iterations) {

		Predictor predictor = null;
		Evaluator evaluator = new AccuracyEvaluator();

		if (algorithm.equals("majority")) {
			predictor = new Majority();
			predictor.train(instances);

		}
		if (algorithm.equals("even_odd")) {
			predictor = new EvenOdd();
			predictor.train(instances);

		}
		if (algorithm.equals("decision_tree")) {
			predictor = new DT(max_decision_tree_depth);
			predictor.train(instances);
		}
		if (algorithm.equals("naive_bayes")) {
			predictor = new NaiveBayesPredictor(lambda);
			predictor.train(instances);
		}
		if (algorithm.equals("perceptron")) {
			predictor = new PerceptronPredictor(thickness,
					online_learning_rate, online_learning_iterations);
			predictor.train(instances);
		}
		if (algorithm.equals("winnow")) {
			predictor = new WinnowPredictor(thickness, online_learning_rate,
					online_learning_iterations);
			predictor.train(instances);
		}
		if (algorithm.equals("logistic_regression_linear_kernel")) {
			predictor = new LinearKernelLogisticRegression(
					gradient_ascent_learning_rate,
					gradient_ascent_training_iterations);
			predictor.train(instances);
		}
		if (algorithm.equals("logistic_regression_polynomial_kernel")) {
			predictor = new PolynomialKernelLogisticRegression(
					gradient_ascent_learning_rate,
					gradient_ascent_training_iterations,
					polynomial_kernel_exponent);
			predictor.train(instances);
		}
		if (algorithm.equals("logistic_regression_gaussian_kernel")) {
			predictor = new GaussianKernelLogisticRegression(
					gradient_ascent_learning_rate,
					gradient_ascent_training_iterations, gaussian_kernel_sigma);
			predictor.train(instances);
		}
		if (algorithm.equals("knn")) {
			predictor = new SimpleKNNPredictor(k_nn);
			predictor.train(instances);
		}
		if (algorithm.equals("knn_distance")) {
			predictor = new DistanceWeightedKNNPredictor(k_nn);
			predictor.train(instances);
		}
		if (algorithm.equals("instance_bagging")) {
			predictor = new EnsembleInstanceBaggingPredictor(k_ensemble,
					ensemble_learning_rate, ensemble_training_iterations);
			predictor.train(instances);
		}
		if (algorithm.equals("feature_bagging")) {
			predictor = new EnsembleFeatureBaggingPredictor(k_ensemble,
					ensemble_learning_rate, ensemble_training_iterations);
			predictor.train(instances);
		}
		if (algorithm.equals("lambda_means")) {

			predictor = new LambdaMeansPredictor(cluster_lambda,
					clustering_training_iterations);

			predictor.train(instances);
		}

		evaluator.evaluate(instances, predictor);

		return predictor;
	}

	private static void evaluateAndSavePredictions(Predictor predictor,
			List<Instance> instances, String predictions_file)
			throws IOException {
		PredictionsWriter writer = new PredictionsWriter(predictions_file);
		// TODO Evaluate the model if labels are available.
		Instance example = instances.get(0);
		Label examplelabel = example.getLabel();
		int judge = 3;
		if (examplelabel instanceof ClassificationLabel) {
			ClassificationLabel cl = (ClassificationLabel) examplelabel;
			judge = cl.getLabel();
		}
		if (judge != -1) {
			// which means the data have valid labels
			Evaluator evaluator = new AccuracyEvaluator();
			evaluator.evaluate(instances, predictor);
		}

		for (Instance instance : instances) {
			Label label = predictor.predict(instance);
			writer.writePrediction(label);
		}

		writer.close();

	}

	public static void saveObject(Object object, String file_name) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					new BufferedOutputStream(new FileOutputStream(new File(
							file_name))));
			oos.writeObject(object);
			oos.close();
		} catch (IOException e) {
			System.err
					.println("Exception writing file " + file_name + ": " + e);
		}
	}

	/**
	 * Load a single object from a filename.
	 * 
	 * @param file_name
	 * @return
	 */
	public static Object loadObject(String file_name) {
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new BufferedInputStream(
					new FileInputStream(new File(file_name))));
			Object object = ois.readObject();
			ois.close();
			return object;
		} catch (IOException e) {
			System.err.println("Error loading: " + file_name);
		} catch (ClassNotFoundException e) {
			System.err.println("Error loading: " + file_name);
		}
		return null;
	}

	public static void registerOption(String option_name, String arg_name,
			boolean has_arg, String description) {
		OptionBuilder.withArgName(arg_name);
		OptionBuilder.hasArg(has_arg);
		OptionBuilder.withDescription(description);
		Option option = OptionBuilder.create(option_name);

		Classify.options.add(option);
	}

	private static void createCommandLineOptions() {
		registerOption("data", "String", true, "The data to use.");
		registerOption("mode", "String", true, "Operating mode: train or test.");
		registerOption("predictions_file", "String", true,
				"The predictions file to create.");
		registerOption("algorithm", "String", true,
				"The name of the algorithm for training.");
		registerOption("model_file", "String", true,
				"The name of the model file to create/load.");
		registerOption("max_decision_tree_depth", "int", true,
				"The maximum depth of the decision tree.");
		registerOption("lambda", "double", true,
				"The level of smoothing for Naive Bayes.");
		registerOption("thickness", "double", true,
				"The value of the linear separator thickness.");
		registerOption("online_learning_rate", "double", true,
				"The LTU learning rate.");
		registerOption("online_training_iterations", "int", true,
				"The number of training iterations for LTU.");
		registerOption("polynomial_kernel_exponent", "double", true,
				"The exponent of the polynomial kernel.");
		registerOption("gaussian_kernel_sigma", "double", true,
				"The sigma of the Gaussian kernel.");
		registerOption("gradient_ascent_learning_rate", "double", true,
				"The learning rate for logistic regression.");
		registerOption("gradient_ascent_training_iterations", "int", true,
				"The number of training iterations.");
		registerOption("k_nn", "int", true,
				"The value of K for KNN regression.");
		registerOption("k_ensemble", "double", true,
				"The number of classifiers in the ensemble.");
		registerOption("ensemble_learning_rate", "double", true,
				"The ensemble learning rate.");
		registerOption("ensemble_training_iterations", "int", true,
				"The number of ensemble training iterations.");
		registerOption("cluster_lambda", "double", true,
				"The value of lambda in lambda-means.");
		registerOption("clustering_training_iterations", "int", true,
				"The number of lambda-means EM iterations.");
		// Other options will be added here.
	}
}
