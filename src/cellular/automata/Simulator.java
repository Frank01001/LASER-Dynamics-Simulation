package cellular.automata;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Simulator {
	
	private final static int TIME_STEPS = 1000;
	private final static int LATTICE_WIDTH = 100;
	private final static int LATTICE_HEIGHT = 100;
	private final static int PHOTON_SATURATION = 20;
	
	static Cell[][] previousAutomaton;
	
	public static void main(String[] args) {
		
		Cell[][] automaton = new Cell[LATTICE_WIDTH][LATTICE_HEIGHT];
		previousAutomaton = new Cell[LATTICE_WIDTH][LATTICE_HEIGHT];
		for (int i = 0; i < LATTICE_WIDTH; i++) {
			for (int j = 0; j < LATTICE_HEIGHT; j++) {
				int[] photonLifeTimes = new int[PHOTON_SATURATION];
				int[] prevPhotonLifeTimes = new int[PHOTON_SATURATION];
				for (int phot = 0; phot < PHOTON_SATURATION; phot++) {
					photonLifeTimes[phot] = 0;
					prevPhotonLifeTimes[phot] = 0;
				}
				automaton[i][j] = new Cell(false, 0, photonLifeTimes, 0);
				previousAutomaton[i][j] = new Cell(false, 0, prevPhotonLifeTimes, 0);
			}
		}
		
		//int numberOfTests = 10; // number of pumping thresholds to test until the correct one is found
		
		
		// input data parameters for calculating the threshold with fixed photon life time
		int[] electronLifeTimeSpace = new int[] {10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150};
		int photonLifeTime = 3;
		// logarithmic space of probability
		//double[] pumpingProbabilitySpace = new double[] {-4, -3.7, -3.3, -3, -2.8, -2.5, -2.3, -2, -1.5, -1}; // 10 values
		// parameters used for this alternative set of lambdas: noise = 0.005, photonLife = 3, saturation = 20
		double[] pumpingProbabilitySpace = linearSpace(0.00187, 0.00188, 15);
		
		/*// input data parameters for calculating the threshold with fixed electron life time
		int electronLifeTime = 20;
		int[] photonLifeTimeSpace = new int[] {2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
		// logarithmic space of probability
		double[] pumpingProbabilitySpace = linearSpace(0.0131, 0.0255, 15);
		 */
		
		double noiseProbability = 0.005;
		int stimulatedEmissionThreshold = 1;
		
		// iteration on the possible values for the pumping threshold
		for (int electronLifeTime : electronLifeTimeSpace) {
			for (int p = 0; p < pumpingProbabilitySpace.length; p++) {
				//checks if this pumping probability is a valid threshold value
				//double pumpingProbability = Math.pow(10, pumpingProbabilitySpace[p]); // logspace
				double pumpingProbability = pumpingProbabilitySpace[p]; //linspace
				
				// if the electron life time is varying
				int timeSteps = electronLifeTime * 10;
				if (timeSteps < 200) timeSteps = 200;
				else if (timeSteps > 1000) timeSteps = 1000;
				
				// if the photon life time is varying
				//int timeSteps = 200;
				
				// counters initialization for the populations and the counters
				int[] populationCounter = new int[timeSteps];
				int[] photonCounter = new int[timeSteps];
				for (int phot = 0; phot < timeSteps; phot++) {
					populationCounter[phot] = 0;
					photonCounter[phot] = 0;
				}
				
				int[] noisePhotons = new int[timeSteps]; // number of noise photons over time
				for (int phot = 0; phot < timeSteps; phot++) {
					noisePhotons[phot] = 0;
				}
				
				//iteration over time
				for (int t = 0; t < timeSteps; t++) {
					double[][] noiseProbabilityMatrix = new double[LATTICE_WIDTH][LATTICE_HEIGHT];
					for (int i = 0; i < LATTICE_WIDTH; i++) {
						for (int j = 0; j < LATTICE_HEIGHT; j++) {
							noiseProbabilityMatrix[i][j] = ThreadLocalRandom.current().nextDouble(0.0, 1.0);
						}
					}
					
					int populationSum = 0;
					int photonSum = 0;
					
					for (int row = 0; row < LATTICE_WIDTH; row++) {
						for (int col = 0; col < LATTICE_HEIGHT; col++) {
							
							//Stimulated Emission Rule
							if (automaton[row][col].isHigh() && automaton[row][col].getPhotonCount() < PHOTON_SATURATION &&
									mooreNeighborhood(row, col) >= stimulatedEmissionThreshold) {
								for (int sat = 0; sat < PHOTON_SATURATION; sat++) {
									if (automaton[row][col].getPhotonLifeTimes(sat) == 0) {
										automaton[row][col].setPhotonLifeTimes(photonLifeTime, sat);
										automaton[row][col].setPhotonCount(automaton[row][col].getPhotonCount() + 1);
										break;
									}
								}
								automaton[row][col].setElectronLevel(false);
								automaton[row][col].setElectronLifeTime(0);
							}
							
							//Photon Decay
							for (int sat = 0; sat < PHOTON_SATURATION; sat++) {
								if (automaton[row][col].getPhotonLifeTimes(sat) > 0) {
									automaton[row][col].setPhotonLifeTimes(automaton[row][col].getPhotonLifeTimes(sat) - 1, sat);
									if (automaton[row][col].getPhotonLifeTimes(sat) == 0) {
										automaton[row][col].setPhotonCount(automaton[row][col].getPhotonCount() - 1);
									}
								}
							}
							
							//Electron Decay
							if (automaton[row][col].isHigh() && automaton[row][col].getElectronLifeTime() > 0) {
								automaton[row][col].setElectronLifeTime(automaton[row][col].getElectronLifeTime() - 1);
								if (automaton[row][col].getElectronLifeTime() == 0) {
									automaton[row][col].setElectronLevel(false);
								}
							}
							
							//Pumping Rule
							if (!automaton[row][col].isHigh() && noiseProbabilityMatrix[row][col] < pumpingProbability) {
								automaton[row][col].setElectronLevel(true);
								automaton[row][col].setElectronLifeTime(electronLifeTime);
							}
							
							//Noise Photon
							if (noiseProbabilityMatrix[row][col] < noiseProbability) {
								for (int sat = 0; sat < PHOTON_SATURATION; sat++) {
									if (automaton[row][col].getPhotonLifeTimes(sat) == 0) {
										automaton[row][col].setPhotonLifeTimes(photonLifeTime, sat);
										automaton[row][col].setPhotonCount(automaton[row][col].getPhotonCount() + 1);
										
										noisePhotons[t] += 1;
										break;
									}
								}
							}
							
							populationSum += (automaton[row][col].isHigh() ? 1 : 0);
							photonSum += automaton[row][col].getPhotonCount();
						}
					}
					
					// updating counters over time
					populationCounter[t] = populationSum;
					photonCounter[t] = photonSum;
					
					
					// previousAutomaton = automaton;
					for (int i = 0; i < LATTICE_WIDTH; i++) {
						for (int j = 0; j < LATTICE_HEIGHT; j++) {
							int[] photonLifeTimes = new int[PHOTON_SATURATION];
							for (int phot = 0; phot < PHOTON_SATURATION; phot++) {
								photonLifeTimes[phot] = automaton[i][j].getPhotonLifeTimes(phot);
							}
							previousAutomaton[i][j] = new Cell(automaton[i][j].isHigh(),
									automaton[i][j].getElectronLifeTime(), photonLifeTimes, automaton[i][j].getPhotonCount());
						}
					}
					
				}
				
				double n_np = (double) Arrays.stream(noisePhotons).sum() / timeSteps * photonLifeTime;
				double averagePhotons = (double) Arrays.stream(photonCounter).sum() / timeSteps;
				double averagePopulation = (double) Arrays.stream(populationCounter).sum() / timeSteps;
				
				System.out.println("Ended simulation n." + (p + 1) + " after " + timeSteps + " time steps. Parameters: photon life = "
						+ photonLifeTime + "; n_np = " + n_np + "; average photons = " + averagePhotons +	"; average population = "
						+ averagePopulation + "; lambda tested = " + pumpingProbability + ".");
				
				if (averagePhotons > 1.25 * n_np) {
					System.out.println(" ->  Found threshold with lambda = " + pumpingProbability);
					break;
				}
			}
			
		}
		
	}
	
	//Returns the number of photons in the adjacent cells using Moore's neighborhood rule
	private static int mooreNeighborhood(int centerX, int centerY) {
		int count = 0;
		int minX = Math.max(centerX - 1, 0);
		int maxX = Math.min(centerX + 1, LATTICE_WIDTH - 1);
		int minY = Math.max(centerY - 1, 0);
		int maxY = Math.min(centerY + 1, LATTICE_HEIGHT - 1);
		
		for (int i = minX; i <= maxX; i++) {
			for (int j = minY; j <= maxY; j++) {
				count += previousAutomaton[i][j].getPhotonCount();
			}
		}
		return count;
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
}
