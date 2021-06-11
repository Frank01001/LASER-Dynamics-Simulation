clear; % clear the workspace
close all; % closes the windows

%Constants
TIME_STEPS = 20;
LATTICE_WIDTH = 100;
LATTICE_HEIGHT = 100;
PHOTON_SATURATION = 25;

%Initialize System
cell.electron = 0;
cell.electronLife = 0;
cell.photonCount = 0;
cell.lifeTimes = zeros(1, PHOTON_SATURATION);

currAutomaton = repmat(cell, LATTICE_WIDTH, LATTICE_HEIGHT);
prevAutomaton = currAutomaton;

% Input Data
electronLifeTime = 100;
photonLifeTime = 16;
pumpingProbability = 0.0125;
noiseProbability = 0.0001;
stimulatedEmissionThreshold = 1;

%output data
populationCounter = zeros(1, TIME_STEPS);
photonCounter = zeros(1, TIME_STEPS);
noisePhotons = zeros(1, TIME_STEPS);

wb = waitbar(0, "Simulation Progress");

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
            if prevAutomaton(i, j).electron == 1 && prevAutomaton(i, j).photonCount < PHOTON_SATURATION && ...
                    mooreNeighborhood(prevAutomaton, i, j, LATTICE_WIDTH, LATTICE_HEIGHT) >= stimulatedEmissionThreshold
                for index = 1:PHOTON_SATURATION
                    if prevAutomaton(i, j).lifeTimes(index) == 0
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
            	if prevAutomaton(i, j).lifeTimes(index) > 0
                	currAutomaton(i, j).lifeTimes(index) = currAutomaton(i, j).lifeTimes(index) - 1; 
                    if currAutomaton(i, j).lifeTimes(index) == 0
                        currAutomaton(i, j).photonCount = currAutomaton(i, j).photonCount - 1; 
                    end
            	end
            end
            
            %Apply electron decay
            if prevAutomaton(i, j).electron == 1 && prevAutomaton(i, j).electronLife > 0
                currAutomaton(i, j).electronLife = currAutomaton(i, j).electronLife - 1; 
                if currAutomaton(i, j).electronLife == 0
                	currAutomaton(i, j).electron = 0;
                end
            end
            
            %Apply pumping rule
            if prevAutomaton(i, j).electron == 0 && rand < pumpingProbability
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
    
    progressString = sprintf("Time %d ps | %3.2f%%", t, (t / TIME_STEPS * 100));
    waitbar((t / TIME_STEPS), wb, progressString);
    
    %Update state
    prevAutomaton = currAutomaton;
end

close(wb); % closes wait bar after simulation is completed

%Final calculations
%Output results
figure(1);

hold on;
title("Laser Dynamics over time");
time = linspace(1, TIME_STEPS, TIME_STEPS);
plot(time, populationCounter);
plot(time, photonCounter);
plot(time, noisePhotons);
legend('Population Inversion', 'Photon Count', 'New Noise Photons');
grid on;
xlabel('Time [ps]');
ylabel('Simulation variables');
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