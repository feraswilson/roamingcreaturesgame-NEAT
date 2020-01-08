package com.swehorison.neuralnetwork.genetic;

import java.util.SplittableRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Algorithm {
    public static volatile double crossoverRate = 0.5;
    public static volatile double mutationRate = 0.01;
    public static SplittableRandom random = new SplittableRandom();
    private static boolean elitism = true;
    private static int tournamentSelectionSize = -1;

    // Evolve a population
    public static Population evolvePopulation(Population population) {
        // Set tournament size if not set
        if (tournamentSelectionSize == -1) {
            tournamentSelectionSize = (int) Math.ceil(population.size() * 0.05) - 1;
        }

        // Init population
        Population newPopulation = new Population(population.size(), false, population.getFitnessFunction(), population.getGeneLength());

        // Keep our best individual
        if (elitism) {
            newPopulation.saveIndividual(0, population.getFittest());
        }

        // Crossover population
        int elitismOffset;
        if (elitism) {
            elitismOffset = 1;
        } else {
            elitismOffset = 0;
        }
        // Loop over the population size and create new individuals with
        // crossover

        ExecutorService executorService = Executors.newWorkStealingPool(8);

        Runnable firstHalf = new Runnable() {
            @Override
            public void run() {
                for (int i = elitismOffset; i < population.size() / 2; i++) {
                    if (Algorithm.random.nextDouble() <= crossoverRate) {
                        Individual indiv1 = tournamentSelection(population);
                        Individual indiv2 = tournamentSelection(population);
                        Individual newIndiv = crossover(indiv1, indiv2, population.getFitnessFunction());
                        newPopulation.saveIndividual(i, newIndiv);
                    } else {
                        newPopulation.saveIndividual(i, Individual.getRandomizedIndividual(newPopulation.getFitnessFunction(), newPopulation.getGeneLength()));
                    }
                }
            }
        };

        Runnable secondHalf = new Runnable() {
            @Override
            public void run() {
                for (int i = elitismOffset + (population.size() / 2) - 1; i < population.size(); i++) {
                    if (Algorithm.random.nextDouble() <= crossoverRate) {
                        Individual indiv1 = tournamentSelection(population);
                        Individual indiv2 = tournamentSelection(population);
                        Individual newIndiv = crossover(indiv1, indiv2, population.getFitnessFunction());
                        newPopulation.saveIndividual(i, newIndiv);
                    } else {
                        newPopulation.saveIndividual(i, Individual.getRandomizedIndividual(newPopulation.getFitnessFunction(), newPopulation.getGeneLength()));
                    }
                }
            }
        };

        executorService.execute(firstHalf);
        executorService.execute(secondHalf);

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(20, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
            ;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Mutate population
        for (int i = elitismOffset; i < newPopulation.size(); i++) {
            mutate(newPopulation.getIndividual(i));
        }

        return newPopulation;
    }

    // Crossover individuals
    private static Individual crossover(Individual individual1, Individual individual2, FitnessFunction fitnessCalculation) {
        return Individual.getCrossoverIndividual(individual1, individual2, fitnessCalculation, random.nextDouble());
    }

    // Mutate an individual
    private static void mutate(Individual indiv) {
        // Loop through genes
        //      for (int i = 0; i < indiv.size(); i++) {

        int i = (int) (indiv.size() * Algorithm.random.nextDouble());
        if (Algorithm.random.nextDouble() <= mutationRate) {
            // Create random gene
            double gene = (double) Algorithm.random.nextDouble() + Algorithm.random.nextInt(40) - 20;
            indiv.setGene(i, gene);

        }
        // }
    }

    // Select the best among a few random individuals
    private static Individual tournamentSelection(Population population) {
        Individual fittest = population.getIndividual(random.nextInt(population.size()));

        for (int i = 0; i < tournamentSelectionSize; i++) {
            Individual competitor = population.getIndividual(random.nextInt(population.size()));

            if (competitor.getFitness() > fittest.getFitness()) {
                fittest = competitor;
            }
        }

        return fittest;
    }
}