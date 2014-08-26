
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import cs475.CommandLineUtilities;

public class RemoveImageNoise {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LinkedList<org.apache.commons.cli.Option> options = 
			createCommandLineOptions();
		String[] manditory_args = { "input_image", "output_image", "eta", "beta", "num_iterations"};
		CommandLineUtilities.initCommandLineParameters(args, options, manditory_args);
		
		String input_image = CommandLineUtilities.getOptionValue("input_image");
		
		String input_image2 = null;
		if (CommandLineUtilities.hasArg("input_image2"))
			input_image2 = CommandLineUtilities.getOptionValue("input_image2");
		
		String output_image = CommandLineUtilities.getOptionValue("output_image");
		double eta = CommandLineUtilities.getOptionValueAsFloat("eta");
		double beta = CommandLineUtilities.getOptionValueAsFloat("beta");
		int num_iterations = CommandLineUtilities.getOptionValueAsInt("num_iterations");
		int num_K = CommandLineUtilities.getOptionValueAsInt("num_K");
		
		double omega = -1;
		if (CommandLineUtilities.hasArg("omega")) {
			omega = CommandLineUtilities.getOptionValueAsFloat("omega");
		}

		boolean use_second_level = CommandLineUtilities.hasArg("use_second_level");
		
		// Load the image.
		BufferedImage buffered_image = ImageUtils.loadImage(input_image);
		
		int[][] image_array = ImageUtils.convertImageToIntArray(buffered_image);
		
		int num_colors = ImageUtils.countColors(image_array, false);
		System.out.println("Input has " + num_colors + " colors.");
		
		// Turn the colors into simple integers.
		HashMap<Integer, Integer> color_map = ImageUtils.createColorMap(image_array);
		
		int[][] encoded_image_array = ImageUtils.encodeImageArrayUsingColorMap(color_map, image_array);
		
		
		int[][] encoded_image_array2 = null;
		if (input_image2 != null) {
			BufferedImage buffered_image2 = ImageUtils.loadImage(input_image2);
			int[][] image_array2 = ImageUtils.convertImageToIntArray(buffered_image2);
			
			int num_colors2 = ImageUtils.countColors(image_array2, false);
			System.out.println("Input 2 has " + num_colors2 + " colors.");
			
			// Turn the colors into simple integers.
			encoded_image_array2 = ImageUtils.encodeImageArrayUsingColorMap(color_map, image_array2);			
		}
		
		
		MRFImageProcessor mrf = new MRFImageProcessor(eta, beta, omega, num_iterations, num_K, use_second_level);
		int[][] denosified_image_array = mrf.denoisifyImage(encoded_image_array, encoded_image_array2);

		
		int[][] decoded_image_array = ImageUtils.decodeImageArrayUsingColorMap(color_map, denosified_image_array);


		BufferedImage new_image = ImageUtils.convertIntArrayToImage(decoded_image_array);
		
		ImageUtils.saveImage(new_image, output_image);
	}
	
	private static LinkedList<Option> createCommandLineOptions() {
		LinkedList<Option> options = new LinkedList<Option>();
		Option option = null;
		
		OptionBuilder.withArgName("Path");
		OptionBuilder.hasArg();
		OptionBuilder
		.withDescription("The image to use as input.");
		option = OptionBuilder.create("input_image");
		
		options.add(option);
		
		OptionBuilder.withArgName("Path");
		OptionBuilder.hasArg();
		OptionBuilder
		.withDescription("The second image to use as input.");
		option = OptionBuilder.create("input_image2");
		
		options.add(option);
		
		OptionBuilder.withArgName("Path");
		OptionBuilder.hasArg();
		OptionBuilder
		.withDescription("The new image to create as output.");
		option = OptionBuilder.create("output_image");
		
		options.add(option);
		
		OptionBuilder.withArgName("double");
		OptionBuilder.hasArg();
		OptionBuilder
		.withDescription("The parameter eta.");
		option = OptionBuilder.create("eta");
		
		options.add(option);
		
		OptionBuilder.withArgName("double");
		OptionBuilder.hasArg();
		OptionBuilder
		.withDescription("The parameter beta.");
		option = OptionBuilder.create("beta");
		
		options.add(option);
		
		OptionBuilder.withArgName("double");
		OptionBuilder.hasArg();
		OptionBuilder
		.withDescription("The parameter omega.");
		option = OptionBuilder.create("omega");
		
		options.add(option);
		
		OptionBuilder.withArgName("int");
		OptionBuilder.hasArg();
		OptionBuilder
		.withDescription("The size K of the window for second level variables.");
		option = OptionBuilder.create("num_K");
		
		options.add(option);
		
		OptionBuilder.withArgName("int");
		OptionBuilder.hasArg();
		OptionBuilder
		.withDescription("The number of iterations to run.");
		option = OptionBuilder.create("num_iterations");
		
		options.add(option);
		
		OptionBuilder.hasArg(false);
		OptionBuilder
		.withDescription("Use a second level of hidden nodes.");
		option = OptionBuilder.create("use_second_level");
		
		options.add(option);
		
		
		return options;
	}
}
