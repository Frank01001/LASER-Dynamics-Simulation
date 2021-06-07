clear; % clear the workspace
close all; % closes the windows

%Constants
TIME_STEPS = 1000;
LATTICE_WIDTH = 70;
LATTICE_HEIGHT = 70;
PHOTON_SATURATION = 40;

%Initialize System
cell.electron = 0;
cell.electronLife = 0;
cell.photonCount = 0;
cell.lifeTimes = zeros(1, PHOTON_SATURATION);

currAutomaton = repmat(cell, LATTICE_WIDTH, LATTICE_HEIGHT);
prevAutomaton = currAutomaton;

% Oscillatory behavior input data
electronLifeTime = 200; %logspace(1, 2.3, 10);
photonLifeTime = 10;
pumpingProbabilitySpace = logspace(-10, -2, 10); % 15 windows opened, one for each threshold tested
noiseProbability = 5e-3;
stimulatedEmissionThreshold = 1;

%output data
populationCounter = zeros(1, TIME_STEPS);
photonCounter = zeros(1, TIME_STEPS);

% iteration on the possible values for the pumping threshold
for p = 1:length(pumpingProbabilitySpace)
    
    % checks if this pumping probability is a valid threshold value
    pumpingProbability = pumpingProbabilitySpace(p);
    % noise photons over time
    noisePhotons = zeros(1, TIME_STEPS);
    
    %Time iteration
    for t = 1:TIME_STEPS
        %Noise probability matrix
        noise = rand(LATTICE_WIDTH, LATTICE_HEIGHT);

        populationSum = 0;
        photonSum = 0;

        %For each cell in the automaton
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
    
    % number of noise photons for this value of pumping probability
    n_np = mean(noisePhotons) * photonLifeTime;
    % average number of photons emitted for this pumping level
    averagePhotons = mean(photonCounter);
    averagePop = mean(populationCounter);
    
    if(averagePhotons > 1.5 * n_np)
    	fprintf("Threshold found! lambda_threshold = %d\n", pumpingProbability);
    end
    
    fprintf("Ended simulation n.%d : n_np = %d ; averagePhotons = %d; lambda = %d\n", ...
        p, n_np, averagePhotons, pumpingProbability);
    
    %Final calculations
    %Output results
    figure(p);
    grid on;
    hold on;
    title(sprintf("Population inversion over time with pumping probability = %f", pumpingProbability));
    time = linspace(1, TIME_STEPS, TIME_STEPS);
    plot(time, populationCounter);
    plot(time, photonCounter);
    plot(time, noisePhotons);
    legend("Population Inversion", "Photon Count", "Noise Photons");
    xlabel('Time Step');
    ylabel('Population');
    hold off;

end


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