package cellular.automata;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Simulator {
	
	private final static int LATTICE_WIDTH = 150;
	private final static int LATTICE_HEIGHT = 150;
	private final static int PHOTON_SATURATION = 5;
	
	private Cell[][] previousAutomaton;
	
	//public double noiseProbability;
	public int stimulatedEmissionThreshold = 1;
	
	public double n_np;
	public double averagePopulation;
	public double averagePhotons;
	
	
	public void simulate(int timeSteps, int photonLifeTime, int electronLifeTime, double pumpingProbability, double noiseProbability) {
		
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
		
		n_np = (double) Arrays.stream(noisePhotons).sum() / timeSteps * photonLifeTime;
		averagePhotons = (double) Arrays.stream(photonCounter).sum() / timeSteps;
		averagePopulation = (double) Arrays.stream(populationCounter).sum() / timeSteps;
		
	}
	
	//Returns the number of photons in the adjacent cells using Moore's neighborhood rule
	private int mooreNeighborhood(int centerX, int centerY) {
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
	
	
}
