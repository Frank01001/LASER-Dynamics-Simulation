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
xlabel("electron Life Time");
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

figure(1);
stimemTh = [1, 2, 3, 4];
thresholds = [3.7926901907322575e-4, 0.0042813323987194, 0.048329302385717574, 0.08858667904100832];
semilogy(stimemTh, thresholds, '--o', "MarkerFaceColor", "b", "MarkerSize", 8);
title("Pumping Probability Threshold over Stimulated emission Threshold");
grid on;
legend('Pumping Threshold');
xlabel('Strimulated emission Threshold');
ylabel('Pumping Threshold');
xlim([0.5, 4.5]);
ylim([1e-4, 0.1]);

%% plotting noise thresholds with the noise probability
clear;
noiseThresholds = [5.963623316594661e-7, 5.963623316594661e-7, 1.1246578221198265e-6, 1.7166979066078743e-6, 2.6203985288583757e-6, 3.2374575428176797e-6, 3.2374575428176797e-6, 3.2374575428176797e-6, 6.105402296585414e-6, 4.941713361323899e-6, 6.105402296585414e-6, 4.941713361323899e-6, 7.543120063354731e-6, 7.543120063354731e-6, 1.151395399326467e-5, 1.4225293134853952e-5, 1.4225293134853952e-5, 1.7575106248548253e-5, 2.1713743029375617e-5, 2.6826957952797823e-5, 2.6826957952797823e-5, 3.3144247494664994e-5, 4.0949150623805194e-5, 5.059197488435946e-5, 5.059197488435946e-5, 6.25055192527413e-5, 7.722449945836459e-5, 9.540954763500197e-5, 1.1787686347936191e-4, 1.1787686347936191e-4, 1.7992936232916012e-4, 1.7992936232916012e-4, 2.2229964825262548e-4, 2.746474114816126e-4, 2.746474114816126e-4, 3.39322177189542e-4, 4.19226743523703e-4, 5.179474679231351e-4, 5.179474679231351e-4, 6.399152336349437e-4, 7.906043210907912e-4, 9.767781100895152e-4, 0.001206792640639361, 0.001206792640639361, 0.0014909716571841042, 0.0018420699693267653, 0.002275845926074849, 0.002275845926074849, 0.0028117686979743056, 0.003473892112083208 ];
noiseProbabilitySpace = logspace(-5.5, -2, 50);
pumpingSpace = logspace(-6.5, -2, 50);
figure("Name", "Noise Over Pumping Thresholds");
loglog(noiseProbabilitySpace, noiseThresholds);
grid on;
legend('Pumping Thresholds');
xlabel("Noise Probability");
ylabel("Pumping Threshold");
