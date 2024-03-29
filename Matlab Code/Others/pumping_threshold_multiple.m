clear; % clear the workspace
close all; % closes the windows

%Constants
TIME_STEPS = 200;
LATTICE_WIDTH = 50;
LATTICE_HEIGHT = 50;
PHOTON_SATURATION = 20;

%Initialize System
cell.electron = 0;
cell.electronLife = 0;
cell.photonCount = 0;
cell.lifeTimes = zeros(1, PHOTON_SATURATION);

currAutomaton = repmat(cell, LATTICE_WIDTH, LATTICE_HEIGHT);
prevAutomaton = currAutomaton;

% Oscillatory behavior input data
electronLifeTimeSpace = logspace(1, 2.3, 10);
photonLifeTime = 20;
pumpingProbabilitySpace = logspace(-3, -1, 10);
noiseProbability = 5e-4;
stimulatedEmissionThreshold = 1;

%output data
populationCounter = zeros(1, TIME_STEPS);
photonCounter = zeros(1, TIME_STEPS);
thresholds = zeros(1, length(electronLifeTimeSpace));

for h = 1:length(electronLifeTimeSpace)
    electronLifeTime = electronLifeTimeSpace(h);
    for p = 1:length(pumpingProbabilitySpace)
        pumpingProbability = pumpingProbabilitySpace(p);
        noisePhotons = zeros(1, TIME_STEPS);

        %Time iteration
        for t = 1:TIME_STEPS
            %Noise probability matrix
            noise = rand(LATTICE_WIDTH, LATTICE_HEIGHT);

            populationSum = 0;
            photonSum = 0;

            %For each cell
            for i = 1:LATTICE_WIDTH
                for j = 1:LATTICE_HEIGHT

                    %Apply stimulated emission rule
                    if currAutomaton(i, j).electron == 1 && currAutomaton(i, j).photonCount < PHOTON_SATURATION && ...
                            mooreNeighborhood(prevAutomaton, i, j, LATTICE_WIDTH, LATTICE_HEIGHT) >= stimulatedEmissionThreshold
                        for index = 1:PHOTON_SATURATION
                            if currAutomaton(i, j).lifeTimes(index) == 0
                                currAutomaton(i, j).lifeTimes(index) = photonLifeTime;
                                currAutomaton(i, j).photonCount = currAutomaton(i, j).photonCount + 1; 
                                break;
                            end
                        end
                        currAutomaton(i, j).electron = 0;
                        currAutomaton(i, j).electronLife = 0;
                    end

                    %Apply photon decay
                    for index = 1:PHOTON_SATURATION
                        if currAutomaton(i, j).lifeTimes(index) > 0
                            currAutomaton(i, j).lifeTimes(index) = currAutomaton(i, j).lifeTimes(index) - 1; 
                            if currAutomaton(i, j).lifeTimes(index) == 0
                                currAutomaton(i, j).photonCount = currAutomaton(i, j).photonCount - 1; 
                            end
                        end
                    end

                    %Apply electron decay
                    if currAutomaton(i, j).electron == 1 && currAutomaton(i, j).electronLife > 0
                        currAutomaton(i, j).electronLife = currAutomaton(i, j).electronLife - 1; 
                        if currAutomaton(i, j).electronLife == 0
                            currAutomaton(i, j).electron = 0;
                        end
                    end

                    %Apply pumping rule
                    if currAutomaton(i, j).electron == 0 && rand < pumpingProbability
                        currAutomaton(i, j).electron = 1;
                        currAutomaton(i, j).electronLife = electronLifeTime;
                    end

                    %Apply noise photon
                    if noise(i, j) < noiseProbability
                        for index = 1:PHOTON_SATURATION
                            if currAutomaton(i, j).lifeTimes(index) == 0
                                currAutomaton(i, j).lifeTimes(index) = photonLifeTime;
                                currAutomaton(i, j).photonCount = currAutomaton(i, j).photonCount + 1;
                                noisePhotons(t) = noisePhotons(t) + 1;
                                break;
                            end
                        end
                    end

                    %Partial sum of upper state electrons
                    populationSum = populationSum + currAutomaton(i, j).electron;
                    %Partial sum of total photons in the lattice
                    photonSum = photonSum + currAutomaton(i, j).photonCount;
                end
            end

            %Calculate populations after this time step
            populationCounter(t) = populationSum;
            photonCounter(t) = photonSum;

            %Update state
            prevAutomaton = currAutomaton;
        end

        n_np = mean(noisePhotons) * photonLifeTime;
        
        averagePop = mean(populationCounter);
        averagePhotons = mean(photonCounter);

        fprintf("Ended simulation n.%d with: n_np = %d; lambda = %f; averagePop = %f; averagePhot = %f\n", ...
            p, n_np, pumpingProbability, averagePop, averagePhotons);
        
        if(averagePhotons > 1.25 * n_np)
            fprintf("Found threshold at iteration %d with lambda = %f\n", p, pumpingProbability);
            thresholds(h) = pumpingProbability;
            break;
        end

        
    end
end

%Final calculations
%Output results
figure(1);
grid on;
hold on;
title("Pumping Threshold by electron life time");
loglog(electronLifeTimeSpace, thresholds, "o", "MarkerFaceColor", "b");
legend('Pumping Thresholds');
xlabel('Electron life time');
ylabel('Pumping theshold');
hold off;


%Returns the number of photons in the adjacent cells using moore's
%neighborhood rule
function count = mooreNeighborhood(automaton, center_x, center_y, lattice_width, lattice_height)
    count = 0;
    
    min_x = max(center_x - 1, 1);
    max_x = min(center_x + 1, lattice_width);
    min_y = max(center_y - 1, 1);
    max_y = min(center_y + 1, lattice_height);
    
    for i = min_x:max_x
        for j = min_y:max_y
            count = count + automaton(i, j).photonCount;
        end
    end
end