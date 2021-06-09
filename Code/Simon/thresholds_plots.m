clear;
close all;

%% photon lifetime varies with fixed electron life time
figure("Name", "Dependence of the threshold on the lifetime of a photon");
tau_photon = [2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15];
lambdas = [0.0255, 0.0193, 0.01753, 0.01576, 0.01576, 0.01487, 0.01487, 0.01487,...
    0.01399, 0.01399 , 0.01399, 0.01399, 0.01399, 0.0135];
semilogy(tau_photon, lambdas, "-o", "MarkerFaceColor", "b");
title("Parameters used: \tau electron = 20; noise = 0.05");
grid on;
xlabel("Photon Life Time");
ylabel("Pumping Thresholds found");
xlim([1, 16]);
ylim([0.012, 0.027]);

%% electron life time varies with fixed photon life time
figure("Name", "Dependence of the threshold on the lifetime of an electron");
tau_electron = [10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150];
lambdas = [0.01879, 0.01874, 0.01876, 0.01874, 0.01872, 0.01870, 0.01876, 0.01870,...
    0.01872, 0.01876, 0.01874, 0.01872, 0.01874, 0.01870, 0.01872];
loglog(tau_electron, lambdas, "-o", "MarkerFaceColor", "b");
title("Parameters used: \tau photon = 3; noise = 0.05");
grid on;
xlabel("Electron Life Time");
ylabel("Pumping Thresholds found");
xlim([9, 170]);
ylim([0.01868, 0.01882]);

%% photons over pumping

figure("Name", "Average number of photons over pumping probability");
averagePhotons = [1126.2166666666667, 1134.56, 1145.3766666666668, 1166.15, 1180.5766666666666, 1216.9333333333334, 1238.8133333333333, 1285.9333333333334, ...
1343.9166666666667, 1464.66, 1592.2933333333333, 1750.65, 2008.8066666666666, 2273.79, 2695.09, ...
3280.653333333333, 4144.663333333333, 5367.776666666667, 7016.13, 9315.393333333333, 12458.876666666667, 16700.046666666665, ...
22547.396666666667, 30537.82, 41285.86, 55178.986666666664, 71518.17666666667, 86699.72666666667, 96082.90666666666, ...
99280.99];

pumping = logspace(-4, 0, 30);
loglog(pumping, averagePhotons);
title("Parameters: noise = 0.005, \tau electron = 30, \tau photon = 10, 300 time steps");
xlabel("Pumping Probability");
ylabel("Average number of photons");
grid on;

%% test
clear;
close all;

noiseThresholds = [8.858667904100833e-7, 8.858667904100833e-7, 8.858667904100833e-7, 8.858667904100833e-7, 1.8329807108324375e-6, 1.8329807108324375e-6, 3.7926901907322535e-6, 3.7926901907322535e-6, 3.7926901907322535e-6, 7.847599703514622e-6, 1.6237767391887242e-5, 1.6237767391887242e-5, 1.6237767391887242e-5, 3.359818286283788e-5, 3.359818286283788e-5, 6.951927961775619e-5, 6.951927961775619e-5, 6.951927961775619e-5, 1.438449888287666e-4, 1.438449888287666e-4, 2.9763514416313253e-4, 2.9763514416313253e-4, 6.158482110660279e-4, 6.158482110660279e-4, 0.0012742749857031373, 0.0012742749857031373, 0.0012742749857031373, 0.002636650898730366, 0.002636650898730366, 0.005455594781168536];

figure(1);
noise = logspace(-6, -2, 30);
loglog(noise, noiseThresholds);
grid on;
title("Pumping Probability Threshold over Noise Probability");
legend('Pumping Probability Threshold');
xlabel('Noise Probability');
ylabel('Pumping Threshold');

