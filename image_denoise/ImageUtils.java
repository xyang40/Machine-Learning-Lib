package cs475.image_denoise;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import javax.imageio.ImageIO;

public abstract class ImageUtils {
	public static int[][] decodeImageArrayUsingColorMap(
			HashMap<Integer, Integer> color_map, int[][] image_array) {
		int[][] decoded_image = new int[image_array.length][image_array[0].length];
		
		HashMap<Integer, Integer> reverse_color_map = new HashMap<Integer, Integer>();
		
		for (int key : color_map.keySet()) {
			int value = color_map.get(key);
			reverse_color_map.put(value, key);
		}
		for (int ii = 0; ii < image_array.length; ii++) {
			for (int jj = 0; jj < image_array[ii].length; jj++) {
				int color = image_array[ii][jj];
				int decoded_color = reverse_color_map.get(color);
				decoded_image[ii][jj] = decoded_color;
			}
		}
		return decoded_image;
	}

	public static int[][] encodeImageArrayUsingColorMap(
			HashMap<Integer, Integer> color_map, int[][] image_array) {
		int[][] encoded_image = new int[image_array.length][image_array[0].length];
		
		for (int ii = 0; ii < image_array.length; ii++) {
			for (int jj = 0; jj < image_array[ii].length; jj++) {
				int color = image_array[ii][jj];
				int encoded_color = color_map.get(color);
				encoded_image[ii][jj] = encoded_color;
			}
		}
		return encoded_image;
	}

	public static HashMap<Integer, Integer> createColorMap(int[][] image_array) {
		HashSet<Integer> colors = new HashSet<Integer>(); 
		for (int ii = 0; ii < image_array.length; ii++) {
			for (int jj = 0; jj < image_array[ii].length; jj++) {
				int color = image_array[ii][jj];
				colors.add(color);
			}
		}
		
		// Sort the colors by their greyscale values.
		// In greyscale, every field R/G/B is the same value.
		ColorModel color_model = ColorModel.getRGBdefault();
		ArrayList<Pair> colors_list = new ArrayList<Pair>(); 
		for (int key : colors) {
			int red = color_model.getRed(key);
			colors_list.add(new Pair(red, key));
		}
		
		Collections.sort(colors_list);
		
		HashMap<Integer, Integer> color_map = new HashMap<Integer, Integer>();
		
		int color_value = 0;
		for (Pair pair : colors_list) {
			color_map.put(pair.second(), color_value);
			color_value += 1;
		}
		
		return color_map;
	}

	public static int countColors(int[][] image_array, boolean printColors) {
		HashMap<Integer, Integer> color_count = new HashMap<Integer, Integer>(); 
		for (int ii = 0; ii < image_array.length; ii++) {
			for (int jj = 0; jj < image_array[ii].length; jj++) {
				int color = image_array[ii][jj];
				if (!color_count.containsKey(color)) {
					color_count.put(color, 0);
				}
				int new_count = color_count.get(color) + 1;
				color_count.put(color, new_count);
			}
		}
		
		if (printColors) {
			ColorModel color_model = ColorModel.getRGBdefault();
			for (int key : color_count.keySet()) {
				int count = color_count.get(key);
				
				int red = color_model.getRed(key);
				int green = color_model.getGreen(key);
				int blue = color_model.getBlue(key);
				
				System.out.println(key + "\t" + count + "\t" + red + "\t" + green + "\t" + blue);
			}
		}
		return color_count.size();
	}

	public static void reduceToTwoColors(int[][] image_array) { 
		for (int ii = 0; ii < image_array.length; ii++) {
			for (int jj = 0; jj < image_array[ii].length; jj++) {
				int color = image_array[ii][jj];
				if (color != -1) {
					image_array[ii][jj] = Color.black.getRGB();
				}
			}
		}
	}
	public static BufferedImage convertIntArrayToImage(int[][] image_array) {
		BufferedImage image = new BufferedImage(image_array[0].length, image_array.length, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < image.getHeight(); i++) {  
			for(int j = 0; j < image.getWidth(); j++) {
				image.setRGB(j, i, image_array[i][j]);
			}  
		}
		
		return image;
	}

	public static int[][] convertImageToIntArray(BufferedImage image) {
		int[][] image_array = new int[image.getHeight()][image.getWidth()];
		for(int i = 0; i < image.getHeight(); i++) {  
			for(int j = 0; j < image.getWidth(); j++) {
				image_array[i][j] = image.getRGB(j, i);
			}  
		}
		return image_array;
	}

	public static int[][] insertNoise(int[][] image_array, double noise_level) {
		Random random = new Random(System.currentTimeMillis());
		// How many colors are there?
		int num_colors = countColors(image_array, false);
		
		int[][] noisy_image = new int[image_array.length][image_array[0].length];
		
		for(int ii = 0; ii < image_array.length; ii++) {  
			for(int jj = 0; jj < image_array[ii].length; jj++) {
				if (random.nextDouble() < noise_level) {
					int current_color = image_array[ii][jj];
					int new_color = current_color;
					while (new_color == current_color) {
						new_color = random.nextInt(num_colors);
					}
					noisy_image[ii][jj] = new_color; 
				} else {
					noisy_image[ii][jj] = image_array[ii][jj];
				}
			}  
		}  
		return noisy_image;
	}

	public static void saveImage(BufferedImage img, String ref) {  
		try {
			String format = null;
			if (ref.endsWith(".png")) {
				format = "png";
			} else if (ref.endsWith(".gif")) {
				format = "gif";
			} else if (ref.endsWith(".jpg")) {
				format = "jpg";
			} else if (ref.endsWith(".bmp")) {
				format = "bmp";
			} else {
				throw new IllegalArgumentException("Unknown image formation for " + ref);
			}
			
			//String format "png", "jpg", "gif"  
			System.out.println("Saving " + format);
			ImageIO.write(img, format, new File(ref));  
		} catch (IOException e) {  
			e.printStackTrace();  
		}  
	}  

	public static BufferedImage loadImage(String ref) {  
		BufferedImage bimg = null;  
		try {  
			bimg = ImageIO.read(new File(ref));  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
		return bimg;  
	} 
	
	public static void compareImages(int[][] image_array_a,
			int[][] image_array_b) {
		
		int num_colors = Math.max(ImageUtils.countColors(image_array_a, false), ImageUtils.countColors(image_array_b, false));
		int total = image_array_a.length * image_array_a[0].length;
		
		if (num_colors <= 2) {
			int matched = 0;	
			for (int ii = 0; ii < image_array_a.length; ii++) {
				for (int jj = 0; jj < image_array_a[ii].length; jj++) {
					if (image_array_a[ii][jj] == image_array_b[ii][jj]) {
						matched++;
					}
				}
			}
			double matched_percentage = (double)matched / (double)total;

			System.out.println("Accuracy: " + matched_percentage);
		} else {
			double distance = 0;
			
			for (int ii = 0; ii < image_array_a.length; ii++) {
				for (int jj = 0; jj < image_array_a[ii].length; jj++) {
					distance += Math.pow(Math.log(Math.abs(image_array_a[ii][jj] - image_array_b[ii][jj]) + 1), 2);
				}
			}
			double average_distance = distance / (double)total;

			System.out.println("Distance: " + average_distance);
			
		}
	}

	public static int maxColor(int[][] image) {
		int max_color = 0;
		for (int ii = 0; ii < image.length; ii++) {
			for (int jj = 0; jj < image[ii].length; jj++) {
				int color = image[ii][jj];
				max_color = Math.max(color, max_color);
			}
		}
		return max_color;		
	}
}
