
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import cs475.CommandLineUtilities;

public class CompareImages {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LinkedList<org.apache.commons.cli.Option> options = 
			createCommandLineOptions();
		String[] manditory_args = { "image_a", "image_b" };
		CommandLineUtilities.initCommandLineParameters(args, options, manditory_args);
		
		String input_image_a = CommandLineUtilities.getOptionValue("image_a");
		String input_image_b = CommandLineUtilities.getOptionValue("image_b");
		
		BufferedImage buffered_image_a = ImageUtils.loadImage(input_image_a);
		BufferedImage buffered_image_b = ImageUtils.loadImage(input_image_b);
		
		int[][] image_array_a = ImageUtils.convertImageToIntArray(buffered_image_a);
		int[][] image_array_b = ImageUtils.convertImageToIntArray(buffered_image_b);
		
		ImageUtils.compareImages(image_array_a, image_array_b);
	}

	private static LinkedList<Option> createCommandLineOptions() {
		LinkedList<Option> options = new LinkedList<Option>();
		Option option = null;
		
		OptionBuilder.withArgName("Path");
		OptionBuilder.hasArg();
		OptionBuilder
		.withDescription("The first image to compare.");
		option = OptionBuilder.create("image_a");
		
		options.add(option);
		
		OptionBuilder.withArgName("Path");
		OptionBuilder.hasArg();
		OptionBuilder
		.withDescription("The second image to compare.");
		option = OptionBuilder.create("image_b");
		
		options.add(option);
		
		return options;
	}
}
