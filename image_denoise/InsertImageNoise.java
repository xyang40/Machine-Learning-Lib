

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import cs475.CommandLineUtilities;

public class InsertImageNoise {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LinkedList<org.apache.commons.cli.Option> options = 
			createCommandLineOptions();
		String[] manditory_args = { "input_image", "output_image", "noise_level"};
		CommandLineUtilities.initCommandLineParameters(args, options, manditory_args);
		
		String input_image = CommandLineUtilities.getOptionValue("input_image");
		String output_image = CommandLineUtilities.getOptionValue("output_image");
		double noise_level = CommandLineUtilities.getOptionValueAsFloat("noise_level");
		
		BufferedImage buffered_image = ImageUtils.loadImage(input_image);

		int[][] image_array = ImageUtils.convertImageToIntArray(buffered_image);
		
		int num_colors = ImageUtils.countColors(image_array, false);
		System.out.println("Input has " + num_colors + " colors.");		
		
		HashMap<Integer, Integer> color_map = ImageUtils.createColorMap(image_array);
		int[][] encoded_image_array = ImageUtils.encodeImageArrayUsingColorMap(color_map, image_array);
		
		int[][] noisy_image = ImageUtils.insertNoise(encoded_image_array, noise_level);
		
		int[][] decoded_image_array = ImageUtils.decodeImageArrayUsingColorMap(color_map, noisy_image);
		
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
		.withDescription("The new image to create as output.");
		option = OptionBuilder.create("output_image");
		
		options.add(option);
		
		OptionBuilder.withArgName("double");
		OptionBuilder.hasArg();
		OptionBuilder
		.withDescription("The amount of noise to randomly insert (0-1).");
		option = OptionBuilder.create("noise_level");
		
		options.add(option);
		
		return options;
	}
}
