%Final calculations
%Output results
figure("Name", "First graph");
grid on;
hold on;
title("Population inversion over time");
time = linspace(1, TIME_STEPS, TIME_STEPS);
plot(time, populationCounter / 1e3);
plot(time, photonCounter / 1e4);
legend('Population Inversion', 'Photon Count');
xlabel('Time Step');
ylabel('Population');
hold off;
