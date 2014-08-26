

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

import cs475.indexDuo;

public class MRFImageProcessor {

	private double eta;
	private double beta;
	private double omega;
	private int num_iterations;
	private int num_K;
	private boolean use_second_level;

	public MRFImageProcessor(double eta, double beta, double omega,
			int num_iterations, int num_K, boolean use_second_level) {
		this.eta = eta;
		this.beta = beta;
		this.omega = omega;
		this.num_iterations = num_iterations;
		this.num_K = num_K;
		this.use_second_level = use_second_level;
	}

	public LinkedList<indexDuo> getClique(int ii, int jj, int[][] hidden_nodes) {
		LinkedList<indexDuo> cliquelist = new LinkedList<indexDuo>();
		if (ii - 1 >= 0 && jj - 1 >= 0 && ii + 1 < hidden_nodes.length
				&& jj + 1 < hidden_nodes[0].length) {
			cliquelist.add(new indexDuo(ii - 1, jj));
			cliquelist.add(new indexDuo(ii, jj - 1));
			cliquelist.add(new indexDuo(ii + 1, jj));
			cliquelist.add(new indexDuo(ii, jj + 1));
		}

		if (ii - 1 < 0 && jj - 1 >= 0 && ii + 1 < hidden_nodes.length
				&& jj + 1 < hidden_nodes[0].length) {
			cliquelist.add(new indexDuo(ii, jj - 1));
			cliquelist.add(new indexDuo(ii + 1, jj));
			cliquelist.add(new indexDuo(ii, jj + 1));
		}
		if (ii - 1 >= 0 && jj - 1 < 0 && ii + 1 < hidden_nodes.length
				&& jj + 1 < hidden_nodes[0].length) {
			cliquelist.add(new indexDuo(ii - 1, jj));
			cliquelist.add(new indexDuo(ii + 1, jj));
			cliquelist.add(new indexDuo(ii, jj + 1));
		}
		if (ii - 1 >= 0 && jj - 1 >= 0 && ii + 1 >= hidden_nodes.length
				&& jj + 1 < hidden_nodes[0].length) {
			cliquelist.add(new indexDuo(ii - 1, jj));
			cliquelist.add(new indexDuo(ii, jj - 1));
			cliquelist.add(new indexDuo(ii, jj + 1));
		}
		if (ii - 1 >= 0 && jj - 1 >= 0 && ii + 1 < hidden_nodes.length
				&& jj + 1 >= hidden_nodes[0].length) {
			cliquelist.add(new indexDuo(ii - 1, jj));
			cliquelist.add(new indexDuo(ii, jj - 1));
			cliquelist.add(new indexDuo(ii + 1, jj));
		}
		if (ii - 1 < 0 && jj - 1 < 0 && ii + 1 < hidden_nodes.length
				&& jj + 1 < hidden_nodes[0].length) {
			cliquelist.add(new indexDuo(ii + 1, jj));
			cliquelist.add(new indexDuo(ii, jj + 1));
		}
		if (ii - 1 < 0 && jj - 1 >= 0 && ii + 1 >= hidden_nodes.length
				&& jj + 1 < hidden_nodes[0].length) {
			cliquelist.add(new indexDuo(ii, jj - 1));
			cliquelist.add(new indexDuo(ii, jj + 1));
		}
		if (ii - 1 < 0 && jj - 1 >= 0 && ii + 1 < hidden_nodes.length
				&& jj + 1 >= hidden_nodes[0].length) {
			cliquelist.add(new indexDuo(ii, jj - 1));
			cliquelist.add(new indexDuo(ii + 1, jj));
		}
		if (ii - 1 >= 0 && jj - 1 < 0 && ii + 1 >= hidden_nodes.length
				&& jj + 1 < hidden_nodes[0].length) {
			cliquelist.add(new indexDuo(ii - 1, jj));
			cliquelist.add(new indexDuo(ii, jj + 1));
		}
		if (ii - 1 >= 0 && jj - 1 < 0 && ii + 1 < hidden_nodes.length
				&& jj + 1 >= hidden_nodes[0].length) {
			cliquelist.add(new indexDuo(ii - 1, jj));
			cliquelist.add(new indexDuo(ii + 1, jj));
		}
		if (ii - 1 >= 0 && jj - 1 >= 0 && ii + 1 >= hidden_nodes.length
				&& jj + 1 >= hidden_nodes[0].length) {
			cliquelist.add(new indexDuo(ii - 1, jj));
			cliquelist.add(new indexDuo(ii, jj - 1));
		}
		if (ii - 1 >= 0 && jj - 1 < 0 && ii + 1 >= hidden_nodes.length
				&& jj + 1 >= hidden_nodes[0].length) {
			cliquelist.add(new indexDuo(ii - 1, jj));
		}
		if (ii - 1 < 0 && jj - 1 >= 0 && ii + 1 >= hidden_nodes.length
				&& jj + 1 >= hidden_nodes[0].length) {
			cliquelist.add(new indexDuo(ii, jj - 1));
		}
		if (ii - 1 < 0 && jj - 1 < 0 && ii + 1 < hidden_nodes.length
				&& jj + 1 >= hidden_nodes[0].length) {
			cliquelist.add(new indexDuo(ii + 1, jj));
		}
		if (ii - 1 < 0 && jj - 1 < 0 && ii + 1 >= hidden_nodes.length
				&& jj + 1 < hidden_nodes[0].length) {
			cliquelist.add(new indexDuo(ii, jj + 1));
		}
		return cliquelist;
	}

	public double getBWEnergy(int[][] hidden_nodes,
			int[][] encoded_image_array, int ii, int jj, int newvalue) {

		double energy = 0.0;

		LinkedList<indexDuo> list = getClique(ii, jj, hidden_nodes);

		for (indexDuo id : list) {
			if (newvalue == hidden_nodes[id.getFirstDimensionIndex()][id
					.getSecondDimensionIndex()]) {
				energy -= this.beta;
			} else {
				energy += this.beta;
			}
		}

		if (newvalue == encoded_image_array[ii][jj]) {
			energy -= this.eta;
		} else {
			energy += this.eta;
		}

		return energy;
	}

	public double getBWEnergy_z(int[][] z_temp, int[][] hidden_nodes,
			int[][] encoded_image_array, int ii, int jj, int newvalue) {

		double energy = 0.0;

		LinkedList<indexDuo> list = getClique(ii, jj, hidden_nodes);

		for (indexDuo id : list) {
			if (newvalue == hidden_nodes[id.getFirstDimensionIndex()][id
					.getSecondDimensionIndex()]) {
				energy -= this.beta;
			} else {
				energy += this.beta;
			}
		}

		if (newvalue == encoded_image_array[ii][jj]) {
			energy -= this.eta;
		} else {
			energy += this.eta;
		}

		if (newvalue == z_temp[(int) (Math.floor(ii / (double) num_K))][(int) (Math
				.floor(jj / (double) num_K))]) {
			energy -= this.omega;
		} else {
			energy += this.omega;
		}

		return energy;
	}

	public double getGSEnergy(int[][] hidden_nodes,
			int[][] encoded_image_array, int ii, int jj, int newvalue) {

		double energy = 0.0;

		LinkedList<indexDuo> list = getClique(ii, jj, hidden_nodes);

		for (indexDuo id : list) {
			energy += (Math.log(Math.abs(newvalue
					- hidden_nodes[id.getFirstDimensionIndex()][id
							.getSecondDimensionIndex()]) + 1)
					/ Math.log(2) - 1)
					* this.beta;
		}

		energy += (Math
				.log(Math.abs(newvalue - encoded_image_array[ii][jj]) + 1)
				/ Math.log(2) - 1) * this.eta;
		return energy;
	}

	public double getGSEnergy_z(int[][] z_temp, int[][] hidden_nodes,
			int[][] encoded_image_array, int ii, int jj, int newvalue) {

		double energy = 0.0;

		LinkedList<indexDuo> list = getClique(ii, jj, hidden_nodes);

		for (indexDuo id : list) {
			energy += (Math.log(Math.abs(newvalue
					- hidden_nodes[id.getFirstDimensionIndex()][id
							.getSecondDimensionIndex()]) + 1)
					/ Math.log(2) - 1)
					* this.beta;
		}

		energy += (Math
				.log(Math.abs(newvalue - encoded_image_array[ii][jj]) + 1)
				/ Math.log(2) - 1) * this.eta;

		energy += (Math.log(Math.abs(newvalue
				- z_temp[(int) (Math.floor(ii / (double) num_K))][(int) (Math
						.floor(jj / (double) num_K))]) + 1)
				/ Math.log(2) - 1)
				* this.omega;

		return energy;
	}

	public int[][] denoisifyImage(int[][] encoded_image_array,
			int[][] encoded_image_array2) {

		int[][] hidden_nodes = new int[encoded_image_array.length][];
		for (int i = 0; i < encoded_image_array.length; i++) {
			hidden_nodes[i] = encoded_image_array[i].clone();
		}
		int[][] up_nodes = new int[hidden_nodes.length][];
		for (int i = 0; i < hidden_nodes.length; i++) {
			up_nodes[i] = hidden_nodes[i].clone();
		}

		HashSet<Integer> colorset = new HashSet<Integer>();
		for (int ii = 0; ii < hidden_nodes.length; ii++) {
			for (int jj = 0; jj < hidden_nodes[ii].length; jj++) {
				colorset.add(new Integer(encoded_image_array[ii][jj]));
			}
		}
		int[] colorarray = new int[colorset.size()];
		int num = 0;
		for (Integer i : colorset) {
			colorarray[num] = i.intValue();
			num++;
		}
		Arrays.sort(colorarray);

		if (this.use_second_level == false) {
			if (ImageUtils.countColors(encoded_image_array, false) == 2) {

				int iteration = 1;
				while (iteration <= this.num_iterations) {
					for (int ii = 0; ii < hidden_nodes.length; ii++) {
						for (int jj = 0; jj < hidden_nodes[ii].length; jj++) {
							double leastenergy = Double.MAX_VALUE;
							for (int colorindex = 0; colorindex < colorarray.length; colorindex++) {
								double currentenergy = getBWEnergy(
										hidden_nodes, encoded_image_array, ii,
										jj, colorarray[colorindex]);
								if (currentenergy < leastenergy) {
									leastenergy = currentenergy;
									up_nodes[ii][jj] = colorarray[colorindex];
								}
							}
						}
					}
					for (int ii = 0; ii < hidden_nodes.length; ii++) {
						for (int jj = 0; jj < hidden_nodes[ii].length; jj++) {
							hidden_nodes[ii][jj] = up_nodes[ii][jj];
						}
					}
					iteration++;
				}
			} else {
				int iteration = 1;
				while (iteration <= this.num_iterations) {
					for (int ii = 0; ii < hidden_nodes.length; ii++) {
						for (int jj = 0; jj < hidden_nodes[ii].length; jj++) {
							double leastenergy = Double.MAX_VALUE;
							for (int colorindex = 0; colorindex < colorarray.length; colorindex++) {
								double currentenergy = getGSEnergy(
										hidden_nodes, encoded_image_array, ii,
										jj, colorarray[colorindex]);
								if (currentenergy < leastenergy) {
									leastenergy = currentenergy;
									up_nodes[ii][jj] = colorarray[colorindex];
								}
							}
						}
					}
					for (int ii = 0; ii < hidden_nodes.length; ii++) {
						for (int jj = 0; jj < hidden_nodes[ii].length; jj++) {
							hidden_nodes[ii][jj] = up_nodes[ii][jj];
						}
					}
					iteration++;
				}
			}
		}

		else {
			int[][] z = new int[(int) (Math.ceil((encoded_image_array.length)
					/ (double) num_K))][(int) (Math
					.ceil((encoded_image_array[0].length) / (double) num_K))];

			if (ImageUtils.countColors(encoded_image_array, false) == 2) {

				double meancolor = (colorarray[0] + colorarray[1]) / 2.0;
				int iteration = 1;
				while (iteration <= this.num_iterations) {
					for (int a = 0; a < z.length; a++) {
						for (int b = 0; b < z[a].length; b++) {
							int counter = 0;
							for (int i = 0; i < hidden_nodes.length; i++) {
								for (int j = 0; j < hidden_nodes[i].length; j++) {
									if ((int) (Math.floor(i / (double) num_K)) == a
											&& (int) (Math.floor(j
													/ (double) num_K)) == b) {
										counter++;
										z[a][b] += hidden_nodes[i][j];
									}
								}
							}
							z[a][b] /= counter;
							if (z[a][b] > meancolor) {
								z[a][b] = colorarray[1];
							} else {
								z[a][b] = colorarray[0];
							}
						}
					}

					for (int ii = 0; ii < hidden_nodes.length; ii++) {
						for (int jj = 0; jj < hidden_nodes[ii].length; jj++) {
							double leastenergy = Double.MAX_VALUE;
							for (int colorindex = 0; colorindex < colorarray.length; colorindex++) {
								double currentenergy = getBWEnergy_z(z,
										hidden_nodes, encoded_image_array, ii,
										jj, colorarray[colorindex]);
								if (currentenergy < leastenergy) {
									leastenergy = currentenergy;
									up_nodes[ii][jj] = colorarray[colorindex];
								}
							}
						}
					}
					for (int ii = 0; ii < hidden_nodes.length; ii++) {
						for (int jj = 0; jj < hidden_nodes[ii].length; jj++) {
							hidden_nodes[ii][jj] = up_nodes[ii][jj];
						}
					}
					iteration++;
				}
			} else {
				int iteration = 1;
				while (iteration <= this.num_iterations) {
					for (int a = 0; a < z.length; a++) {
						for (int b = 0; b < z[a].length; b++) {
							int counter = 0;
							for (int i = 0; i < hidden_nodes.length; i++) {
								for (int j = 0; j < hidden_nodes[i].length; j++) {
									if ((int) Math.floor(i / num_K) == a
											&& (int) Math.floor(j / num_K) == b) {
										counter++;
										z[a][b] += hidden_nodes[i][j];
									}
								}
							}
							z[a][b] /= counter;
						}
					}

					for (int ii = 0; ii < hidden_nodes.length; ii++) {
						for (int jj = 0; jj < hidden_nodes[ii].length; jj++) {
							double leastenergy = Double.MAX_VALUE;
							for (int colorindex = 0; colorindex < colorarray.length; colorindex++) {
								double currentenergy = getGSEnergy_z(z,
										hidden_nodes, encoded_image_array, ii,
										jj, colorarray[colorindex]);
								if (currentenergy < leastenergy) {
									leastenergy = currentenergy;
									up_nodes[ii][jj] = colorarray[colorindex];
								}
							}
						}
					}
					for (int ii = 0; ii < hidden_nodes.length; ii++) {
						for (int jj = 0; jj < hidden_nodes[ii].length; jj++) {
							hidden_nodes[ii][jj] = up_nodes[ii][jj];
						}
					}
					iteration++;
				}
			}
		}
		return hidden_nodes;
	}

	public double getEta() {
		return eta;
	}

	public void setEta(double eta) {
		this.eta = eta;
	}

	public double getBeta() {
		return beta;
	}

	public void setBeta(double beta) {
		this.beta = beta;
	}

	public double getOmega() {
		return omega;
	}

	public void setOmega(double omega) {
		this.omega = omega;
	}

	public int getNum_iterations() {
		return num_iterations;
	}

	public void setNum_iterations(int num_iterations) {
		this.num_iterations = num_iterations;
	}

	public int getNum_K() {
		return num_K;
	}

	public void setNum_K(int num_K) {
		this.num_K = num_K;
	}

	public boolean getUse_second_level() {
		return use_second_level;
	}

	public void setUse_second_level(boolean use_second_level) {
		this.use_second_level = use_second_level;
	}

}
