import java.io.*;

public class ChainMRFPotentials {

	private double[][] potentials1;
	private double[][][] potentials2;
	private int n, k;

	public ChainMRFPotentials(String data_file) {
		int line_num = 1;
		try {
			BufferedReader br = new BufferedReader(new FileReader(data_file));
			String line = br.readLine();
			String[] ar = line.split("\\s+");
			n = Integer.parseInt(ar[0]);
			k = Integer.parseInt(ar[1]);
			potentials1 = new double[n+1][k+1];
			potentials2 = new double[2*n][k+1][k+1];
			for(int i=1; i<=n; i++) { 
				for(int a=1; a<=k; a++) { 
					potentials1[i][a] = -1.0;
				}
			}
			for(int i=n+1; i<2*n; i++) { 
				for(int a=1; a<=k; a++) { 
					for(int b=1; b<=k; b++) {
						potentials2[i][a][b] = -1.0;
					}
				}
			}
			line_num++;

			while((line = br.readLine()) != null) {
				ar = line.split("\\s+");

				if(ar.length == 3) {
					int i = Integer.parseInt(ar[0]);
					int a = Integer.parseInt(ar[1]);
					if(i < 1 || i > n)
						throw new Exception("given n=" + n + ", illegal value for i: " + i);
					if(a < 1 || a > k)
						throw new Exception("given k=" + k + ", illegal value for a=" + a);
					if(potentials1[i][a] >= 0.0)
						throw new Exception("ill-formed energy file: duplicate keys: " + line);
					potentials1[i][a] = Double.parseDouble(ar[2]);
				}
				else if(ar.length == 4) {
					int i = Integer.parseInt(ar[0]);
					int a = Integer.parseInt(ar[1]);
					int b = Integer.parseInt(ar[2]);
					if(i < n+1 || i > 2*n-1)
						throw new Exception("given n=" + n + ", illegal value for i: " + i);
					if(a < 1 || a > k || b < 1 || b > k)
						throw new Exception("given k=" + k + ", illegal value for a=" + a + " or b=" + b);
					if(potentials2[i][a][b] >= 0.0)
						throw new Exception("ill-formed energy file: duplicate keys: " + line);
					potentials2[i][a][b] = Double.parseDouble(ar[3]);
				}
				else {
					continue;
				}

				line_num++;
			}
		} catch(Exception e) {
			throw new RuntimeException("error while reading energy file: " + data_file
				+ " on line " + line_num + " [" + e.getMessage() + "]");
		}

		// check that all of the needed potentials were provided
		for(int i=1; i<=n; i++) {
			for(int a=1; a<=k; a++) {
				if(potentials1[i][a] < 0.0)
					throw new RuntimeException("no potential provided for i=" + i + ", a=" + a);
			}
		}
		for(int i=n+1; i<2*n; i++) {
			for(int a=1; a<=k; a++) {
				for(int b=1; b<=k; b++) {
					if(potentials2[i][a][b] < 0.0)
						throw new RuntimeException("no potential provided for i=" + i + ", a=" + a + ", b=" + b);
				}
			}
		}
	}

	public int chainLength() { return n; }

	public int numXValues() { return k; }

	public double potential(int i, int a) {
		if(i < 1 || i > n)
			throw new RuntimeException("given n=" + n + ", illegal value for i: " + i);
		if(a < 1 || a > k)
			throw new RuntimeException("given k=" + k + ", illegal value for a=" + a);
		return potentials1[i][a];
	}

	public double potential(int i, int a, int b) {
		if(i < n+1 || i > 2*n-1)
			throw new RuntimeException("given n=" + n + ", illegal value for i: " + i);
		if(a < 1 || a > k || b < 1 || b > k)
			throw new RuntimeException("given k=" + k + ", illegal value for a=" + a + " or b=" + b);
		return potentials2[i][a][b];
	}
}

