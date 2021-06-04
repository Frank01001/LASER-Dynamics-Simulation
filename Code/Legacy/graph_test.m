%Final calculations
%Output results
figure("Name", "First graph");
grid on;
hold on;
title("Population inversion over time");
time = linspace(1, TIME_STEPS, TIME_STEPS);
plot(time, populationCounter);
plot(time, photonCounter);
legend('Population Inversion', 'Photon Count');
xlabel('Time Step');
ylabel('Population');
xlim([1 800]);
hold off;
