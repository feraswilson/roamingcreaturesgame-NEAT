package com.swehorison.neuralnetwork.genetic;

import com.swehorison.neuralnetwork.neuralnetwork.NeuralNetwork;

public class GANN {
    private int maxGenerations;
    private volatile Population population;
    private volatile int currentGeneration;
    private volatile double currentFitness;
    private NeuralNetwork neuralNetwork;

    public GANN(NeuralNetwork neuralNetwork, int maxGenerations, double crossoverRate, double mutationRate) {
        this.neuralNetwork = neuralNetwork;
        this.maxGenerations = maxGenerations;
        Algorithm.mutationRate = mutationRate;
        Algorithm.crossoverRate = crossoverRate;
        this.currentGeneration = -1;
    }

    public void initiatePopulation(int populationSize, FitnessFunction fitnessFunction) {
        population = new Population(populationSize, true, fitnessFunction, this.neuralNetwork.getWeightSize());
    }

    /**
     * @param populationSize
     * @param fitnessFunction
     */
    public void initiatePopulation(int populationSize, double[] headStartGenes, FitnessFunction fitnessFunction) {
        population = new Population(populationSize, true, fitnessFunction, this.neuralNetwork.getWeightSize(), headStartGenes);
    }

    public void train() {
        for (int i = 0; i < this.maxGenerations; i++) {
            population = Algorithm.evolvePopulation(population);
            currentGeneration++;
            currentFitness = population.getFittest().getFitness();
        }
        // currentGeneration = -1;
        //  currentFitness = -1;
    }

    /**
     * Trains only to the next generation
     */
    public void trainOneGeneration() {
        population = Algorithm.evolvePopulation(population);
        currentGeneration++;
        currentFitness = population.getFittest().getFitness();
    }

    public int getCurrentGeneration() {
        return this.currentGeneration;
    }

    public double getBestFitness() {
        return this.currentFitness;
    }

    public Population getCurrentPopulation() {
        return this.population;
    }

    public double[] run(double[] weights, double[] inputs) {
        return neuralNetwork.compute(weights, inputs);
    }

    public int getMaxGenerations() {
        return maxGenerations;
    }
}