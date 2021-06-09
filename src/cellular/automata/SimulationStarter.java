public class SimulationStarter {
	
	static Simulator simulator;
	
	public static void main(String[] args) {
		simulator = new Simulator();
		simThresholdOverNoise();
	}

	public static void simStimEmTh() {
		int electronLifeTime = 30;
		int photonLifeTime = 10;
		double noiseProbability = 0.0009;
		int timeSteps = 1000;

		int[] stimulatedEmissionThresholdSpace = {1, 2, 3, 4};
		double[] pumpingProbabilitySpace = logspace(-5, 0, 20);

		double[] thresholds = new double[stimulatedEmissionThresholdSpace.length];

		for(int n = 0; n < stimulatedEmissionThresholdSpace.length; n++)
		{
			double pumpingThr = 0.0f;
			for (int p = 0; p < pumpingProbabilitySpace.length; p++)
			{
				double pumpingProbability = pumpingProbabilitySpace[p]; // logspace
				int stimulatedEmissionThreshold = stimulatedEmissionThresholdSpace[n];

				simulator.stimulatedEmissionThreshold = stimulatedEmissionThreshold;
				simulator.simulate(timeSteps, photonLifeTime, electronLifeTime, pumpingProbability, noiseProbability);
				System.out.println("Simulation n. " + (p + 1));

				if(simulator.averagePhotons > 1.25 * simulator.n_np)
				{
					System.out.println("Threshold found: " + pumpingProbability + "\n");
					pumpingThr = pumpingProbability;
					break;
				}

			}

			thresholds[n] = pumpingThr;
		}

		System.out.print("stimEmThresholds = [");
		for(double th : thresholds)
		{
			System.out.print(th + ", ");
		}

	}

	public static void simThresholdOverNoise() {
		int electronLifeTime = 30;
		int photonLifeTime = 10;
		int timeSteps = 200;

		double[] noiseProbabilitySpace = linearSpace(0.0001, 0.001, 30);
		double[] pumpingProbabilitySpace = logspace(-8, -1, 50);

		double[] thresholds = new double[noiseProbabilitySpace.length];

		for(int n = 0; n < noiseProbabilitySpace.length; n++)
		{
			double pumpingThr = 0.0f;
			for (int p = 0; p < pumpingProbabilitySpace.length; p++)
			{
				double pumpingProbability = pumpingProbabilitySpace[p]; // logspace
				double noiseProbability = noiseProbabilitySpace[n];

				simulator.simulate(timeSteps, photonLifeTime, electronLifeTime, pumpingProbability, noiseProbability);
				System.out.println("Simulation n. " + (p + 1));

				if(simulator.averagePhotons > 1.25 * simulator.n_np)
				{
					System.out.println("Threshold found: " + pumpingProbability + "\n");
					pumpingThr = pumpingProbability;
					break;
				}

			}

			thresholds[n] = pumpingThr;
		}

		ThresholdChart chart = new ThresholdChart(noiseProbabilitySpace, thresholds);
		chart.pack();
		chart.setVisible(true);

		System.out.print("noiseThresholds = [");
		for(double th : thresholds)
		{
			System.out.print(th + ", ");
		}

	}
	
	public static void simulationKVersusNNp() {
		
		// input data parameters for calculating the threshold with fixed electron life time
		int[] electronLifeTimeSpace = new int[] {10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150};
		int photonLifeTime = 3;
		// linear space of probability
		double[] pumpingProbabilitySpace = linearSpace(0.0017, 0.0019, 20);
		
		
		// iteration on the possible values for the pumping threshold
		for (int electronLifeTime : electronLifeTimeSpace) {
			
			boolean check = false;
			for (int p = 0; p < pumpingProbabilitySpace.length; p++) {
				//checks if this pumping probability is a valid threshold value
				double pumpingProbability = pumpingProbabilitySpace[p];
				
				// if the photon life time is varying
				int timeSteps = 200;
				
				simulator.simulate(timeSteps, photonLifeTime, electronLifeTime, pumpingProbability, 0.005);
				
				System.out.println("Ended simulation n." + (p + 1) + " after " + timeSteps + " time steps. Parameters: photon life = "
						+ photonLifeTime + "; n_np = " + simulator.n_np + "; average photons = " + simulator.averagePhotons +	"; average " +
						"population = " + simulator.averagePopulation + "; lambda tested = " + pumpingProbability + ".");
				
				
				double steadyPopulation = 0.0;
				for (int i = timeSteps * 3 / 4, h = 0; i < timeSteps; i++, h++) {
					steadyPopulation += simulator.populationCounter[i];
				}
				steadyPopulation = steadyPopulation / ((double) timeSteps / 4.0);
				double K = 1.0 / (photonLifeTime * steadyPopulation);
				double pumpingRate = 1.0 / (electronLifeTime *  photonLifeTime * K);
				double estimatedPumpingThreshold = pumpingRate / (Simulator.LATTICE_HEIGHT * Simulator.LATTICE_WIDTH);
				System.out.println("K = " + K + "; estimated pumping threshold = " + estimatedPumpingThreshold + "; pumping rate = " + pumpingRate);
				
				if (simulator.averagePhotons > 1.25 * simulator.n_np && !check &&
						(estimatedPumpingThreshold - pumpingProbability) / estimatedPumpingThreshold < 0.05 ) {
					System.out.println(" ->  Found threshold with lambda = " + pumpingProbability);
					check = true;
				}
			}
			System.out.println("");
		}
		
	}
	
	/**
	 * Simulation that resembles a diode characteristic
	 */
	public static void simulationPhotonsOverPumping() {
		double noiseProbability = 0.005;
		int electronLifeTime = 30;
		int photonLifeTime = 10;
		int timeSteps = 300;
		
		double[] pumpingProbabilitySpace = logspace(-4, 0, 30);
		double[] averagePhotons = new double[pumpingProbabilitySpace.length]; // plotting average photons over pumping probabilities
		
		for (int p = 0; p < pumpingProbabilitySpace.length; p++) {
			double pumpingProbability = pumpingProbabilitySpace[p]; // logspace
			
			simulator.simulate(timeSteps, photonLifeTime, electronLifeTime, pumpingProbability, noiseProbability);
			averagePhotons[p] = simulator.averagePhotons;
			
			System.out.println("Ended simulation n." + (p + 1) + " after " + timeSteps + " time steps. Parameters: photon life = "
					+ photonLifeTime + "; n_np = " + simulator.n_np + "; average photons = " + simulator.averagePhotons +	"; average " +
					"population = " + simulator.averagePopulation + "; lambda tested = " + pumpingProbability + ".");
			
		}
		
		System.out.print("averagePhotons = [");
		for (int p = 0; p < pumpingProbabilitySpace.length; p++) {
			System.out.print(averagePhotons[p] + ", ");
			if (p % 7 == 0 && p != 0) System.out.println("...");
		}
		System.out.print("];\n");
		
		// First Simulation: electron = 30, noise = 0.005, photon = 10;
		// averagePhotons = [499.245, 492.73, 497.44, 495.79, 495.78, 498.825, 498.925, 510.63, 532.325,...
		//    563.24, 599.47, 673.615, 782.885, 964.34, 1209.51, 1666.0, 2481.14, 3764.97, 5866.04, 9198.69];
		
		// Second Simulation: electron = 200, noise = 0.005, photon = 10;
		//averagePhotons = [500.2475, 499.816, 499.8005, 502.2585, 503.8795, 511.202, 511.8315, ...
		//    524.5525, 542.017, 569.1235, 615.6455, 687.365, 799.047, 986.334, 1271.011, 1738.694,...
		//    2536.5895, 3881.1025, 6018.5825, 9477.217];
		
		//Final Simulation with 30 pumpings from 10^-4 to 10^0. Parameters used = electron = 30, noise = 0.005, photon = 10; 300 time steps
		//averagePhotons = [1126.2166666666667, 1134.56, 1145.3766666666668, 1166.15, 1180.5766666666666, 1216.9333333333334, 1238.8133333333333, 1285.9333333333334, ...
		//1343.9166666666667, 1464.66, 1592.2933333333333, 1750.65, 2008.8066666666666, 2273.79, 2695.09, ...
		//3280.653333333333, 4144.663333333333, 5367.776666666667, 7016.13, 9315.393333333333, 12458.876666666667, 16700.046666666665, ...
		//22547.396666666667, 30537.82, 41285.86, 55178.986666666664, 71518.17666666667, 86699.72666666667, 96082.90666666666, 99280.99];
	}
	
	public static void simulationVariablePhoton() {
		
		// input data parameters for calculating the threshold with fixed electron life time
		int electronLifeTime = 20;
		int[] photonLifeTimeSpace = new int[] {2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
		// linear space of probability
		double[] pumpingProbabilitySpace = linearSpace(0.0131, 0.0255, 15);
		
		
		// iteration on the possible values for the pumping threshold
		for (int photonLifeTime : photonLifeTimeSpace) {
			for (int p = 0; p < pumpingProbabilitySpace.length; p++) {
				//checks if this pumping probability is a valid threshold value
				double pumpingProbability = pumpingProbabilitySpace[p];
				
				// if the photon life time is varying
				int timeSteps = 200;
				
				simulator.simulate(timeSteps, photonLifeTime, electronLifeTime, pumpingProbability, 0.005);
				
				System.out.println("Ended simulation n." + (p + 1) + " after " + timeSteps + " time steps. Parameters: photon life = "
						+ photonLifeTime + "; n_np = " + simulator.n_np + "; average photons = " + simulator.averagePhotons +	"; average " +
						"population = " + simulator.averagePopulation + "; lambda tested = " + pumpingProbability + ".");
				
				if (simulator.averagePhotons > 1.25 * simulator.n_np) {
					System.out.println(" ->  Found threshold with lambda = " + pumpingProbability);
					break;
				}
				
			}
			
		}
		
	}
	
	public static void simulationVariableElectron() {
		// input data parameters for calculating the threshold with fixed photon life time
		int[] electronLifeTimeSpace = new int[] {10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150};
		int photonLifeTime = 3;
		
		// parameters used for this alternative set of lambdas: noise = 0.005, photonLife = 3, saturation = 20
		double[] pumpingProbabilitySpace = linearSpace(0.001865, 0.00188, 15);
		
		
		// iteration on the possible values for the pumping threshold
		for (int electronLifeTime : electronLifeTimeSpace) {
			for (int p = 0; p < pumpingProbabilitySpace.length; p++) {
				//checks if this pumping probability is a valid threshold value
				double pumpingProbability = pumpingProbabilitySpace[p];
				
				// if the electron life time is varying
				int timeSteps = electronLifeTime * 10;
				if (timeSteps < 200) timeSteps = 200;
				else if (timeSteps > 1000) timeSteps = 1000;
				
				simulator.simulate(timeSteps, photonLifeTime, electronLifeTime, pumpingProbability, 0.005);
				
				System.out.println("Ended simulation n." + (p + 1) + " after " + timeSteps + " time steps. Parameters: electron life = "
						+ electronLifeTime + "; n_np = " + simulator.n_np + "; average photons = " + simulator.averagePhotons +	"; average " +
						"population = " + simulator.averagePopulation + "; lambda tested = " + pumpingProbability + ".");
				
				if (simulator.averagePhotons > 1.25 * simulator.n_np) {
					System.out.println(" ->  Found threshold with lambda = " + pumpingProbability);
					break;
				}
				
			}
			
		}
		
	}
	
	private static double[] linearSpace(double start, double end, int quantity) {
		double[] space = new double[quantity];
		space[0] = start;
		double delta = (end - start) / (quantity - 1);
		for (int i = 1; i < quantity; i++) {
			space[i] = space[i-1] + delta;
		}
		return space;
	}
	
	/**
	 * generates n logarithmically-spaced points between d1 and d2 using 10 as base.
	 * @param start The min value
	 * @param end The max value
	 * @param n The number of points to generated
	 * @return an array of linearly spaced points.
	 */
	public static double[] logspace(double start, double end, int n) {
		double[] space = new double[n];
		double[] p = linearSpace(start, end, n);
		for(int i = 0; i < n - 1; i++) {
			space[i] = Math.pow(10, p[i]);
		}
		space[n - 1] = Math.pow(10, end);
		return space;
	}
	
}
