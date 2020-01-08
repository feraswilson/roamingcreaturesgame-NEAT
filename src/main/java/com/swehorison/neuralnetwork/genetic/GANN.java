package com.swehorison.neuralnetwork.genetic;

import com.swehorison.neuralnetwork.neuralnetwork.NeuralNetwork;

import java.util.HashMap;

public class GANN {
    private int maxGenerations;
    private volatile Population population;
    private volatile int currentGeneration;
    private volatile double currentFitness;
    private NeuralNetwork neuralNetwork;
    private boolean outputInfo;
    private Thread thread;
    private Runnable onCompleteListener;
    private EvolutionListener onGenerationEvolution;
    private boolean isRunning;

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
        train(new HashMap<>(), true);
    }

    public void train(boolean blocking) {
        train(new HashMap<>(), blocking);
    }

    /**
     * Train with changing mutation rates.
     *
     * @param mutationRates First is generation, second is mutation rate.
     *                      Example: new double[][] {
     *                      [1, 0.2], [100, 0.4]
     *                      }
     */
    public void train(HashMap<Integer, Double> mutationRates, boolean blocking) {

        isRunning = true;

        Runnable runnable = () -> {
            for (int i = 0; i < this.maxGenerations; i++) {
                population = Algorithm.evolvePopulation(population);
                if (mutationRates.containsKey(currentGeneration)) {
                    Algorithm.mutationRate = mutationRates.get(currentGeneration);
                }
                currentGeneration++;
                currentFitness = population.getFittest().getFitness();

                if (outputInfo) {
                    System.out.println("Current Generation: " + currentGeneration + ", Mutation Rate: " + Algorithm.mutationRate);
                }

                if (onGenerationEvolution != null) {
                    onGenerationEvolution.onComplete(population);
                }

                if (!isRunning) {
                    break;
                }
            }

            // Run something at the end
            if (onCompleteListener != null) {
                onCompleteListener.run();
            }


        };

        if (blocking) {
            runnable.run();
        } else {
            thread = new Thread(runnable);
            thread.start();
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

    public boolean isOutputInfo() {
        return outputInfo;
    }

    public void setOutputInfo(boolean outputInfo) {
        this.outputInfo = outputInfo;
    }

    public double[] getBestFitnessGenes() {
        return this.getCurrentPopulation().getFittest().getGenes();
    }

    public void setOnCompleteListener(Runnable onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    public void setOnGenerationEvolution(EvolutionListener onGenerationEvolution) {
        this.onGenerationEvolution = onGenerationEvolution;
    }

    public void end() {
        isRunning = false;
    }
}