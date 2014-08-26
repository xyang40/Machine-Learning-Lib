
public class SumProductTester {

	public static void main(String[] args) {
		if(args.length == 0) {
			System.out.println("provide some MRF potential data files");
			return;
		}
		for(String data_file : args) {
			System.out.println("[main] testing potentials for data file: " + data_file);
			findPotentials(data_file);
		}
	}

	public static void findPotentials(String data_file) {
		ChainMRFPotentials p = new ChainMRFPotentials(data_file);
		SumProduct sp = new SumProduct(p);
		for(int i=1; i<=p.chainLength(); i++) {
			double[] marginal = sp.marginalProbability(i);
			if(marginal.length-1 != p.numXValues())		// take off 1 for 0 index which is not used
				throw new RuntimeException("length of probability distribution is incorrect: " + marginal.length);
			System.out.println("marginal probability distribution for node " + i + " is:");
			double sum = 0.0;
			for(int k=1; k<=p.numXValues(); k++) {
				if(marginal[k] < 0.0 || marginal[k] > 1.0)
					throw new RuntimeException("illegal probability for x_" + i);
				System.out.println("\tP(x = " + k + ") = " + marginal[k]);
				sum += marginal[k];
			}
			double err = 1e-5;
			if(sum < 1.0-err || sum > 1.0+err)
				throw new RuntimeException("marginal probability distribution for x_" + i + " doesn't sum to 1");
		}
	}
}

