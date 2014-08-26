
public class SumProduct {

	private ChainMRFPotentials potentials;

	// add whatever data structures needed
	public SumProduct(ChainMRFPotentials p) {
		this.potentials = p;
	}

	public double[] marginalProbability(int x_i) {

		int n = potentials.chainLength();
		int k = potentials.numXValues();
		double[] result = new double[k + 1];

		double[][] f2x = new double[n + 1][k + 1];//
		double[][] x2g_f = new double[n + 1][k + 1];//
		double[][] x2g_b = new double[n + 1][k + 1];//
		double[][] g2x_f = new double[n + 1][k + 1];//
		double[][] g2x_b = new double[n + 1][k + 1];//

		for (int i = 0; i < f2x.length; i++) {
			for (int j = 0; j < f2x[0].length; j++) {
				f2x[i][j] = 1;
				x2g_f[i][j] = 1;
				x2g_b[i][j] = 1;
				g2x_f[i][j] = 1;
				g2x_b[i][j] = 1;
			}
		}

		for (int value = 1; value <= k; value++) {
			f2x[1][value] = potentials.potential(1, value);
			x2g_f[1][value] = f2x[1][value] * g2x_f[1][value];
		}

		for (int i = 2; i <= n; i++) {
			for (int value = 1; value <= k; value++) {
				f2x[i][value] = potentials.potential(i, value);
				g2x_f[i][value]=0;
				for (int val = 1; val <= k; val++) {
					g2x_f[i][value] += (potentials.potential(i + n - 1, val,
							value) * x2g_f[i - 1][val]);
				}
				x2g_f[i][value] = f2x[i][value] * g2x_f[i][value];
			}
		}

		for (int value = 1; value <= k; value++) {
			x2g_b[n][value] = g2x_b[n][value] * f2x[n][value];
		}

		for (int i = n - 1; i >= 1; i--) {
			for (int value = 1; value <= k; value++) {
				g2x_b[i][value]=0;
				for (int val = 1; val <= k; val++) {
					g2x_b[i][value] += (potentials.potential(i + n, value, val) * x2g_b[i + 1][val]);
				}
				x2g_b[i][value] = g2x_b[i][value] * f2x[i][value];
			}
		}

		for (int value = 1; value <= k; value++) {
			result[value] = f2x[x_i][value] * g2x_f[x_i][value]
					* g2x_b[x_i][value];
		}

		double sum = 0.0;
		for (int i = 1; i <= k; i++) {
			sum += result[i];
		}
		for (int i = 1; i <= k; i++) {
			result[i] /= sum;
		}

		return result;
	}
}
