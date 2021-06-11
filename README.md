# LASER Dynamics Simulation - Politecnico di Milano

The included script is a simplified LASER dynamics simulation written in Matlab as an assignment for the course of _"Dispositivi per la Trasmissione dell'Informazione"_ at Politecnico di Milano.

The algorithm is based on the Cellular Automata Model, and was heavily inspired by _"Laser Dynamics Modelling and Simulation: An Application of Dynamic Load Balancing of Parallel Cellular Automata"_, a paper available at https://www.researchgate.net/publication/226637251_Laser_Dynamics_Modelling_and_Simulation_An_Application_of_Dynamic_Load_Balancing_of_Parallel_Cellular_Automata.

The repository contains both a Matlab implementation and a Java implementation. The latter was created later in the project development in order to increase the performance of the numerical computations.

### How to navigate the repository

This repository contains several folders:
* __Matlab Code__: contains the simulations scripts alongside the graphs generation scripts
    - *base_project*: script for running a single simulation with the same rules and descriptors mentioned in the reference paper.
    - *perfected_project*: includes the perfected version of the cellular automaton (different from the reference paper, this is our addition) and user input parameters.
* __Java Code__: contains an IntelliJ Idea project with different methods for running different numerical simulations. This code runs much faster than the Matlab simulations.
* __Presentations__: contains the Powerpoint Presentation, the Word essay and some notes taken during the development.
* __Simulations__: contains various screenshots of some of the simulations we run. It also contains text files with the descriptions of the parameters used.

Authors of this project:
- Francesco Panebianco
- [Simone Giampà](https://github.com/SimonGiampy)
