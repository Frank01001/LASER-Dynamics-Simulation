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