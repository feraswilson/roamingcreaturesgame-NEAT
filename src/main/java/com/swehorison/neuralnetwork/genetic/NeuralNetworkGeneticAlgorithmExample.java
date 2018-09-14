package com.swehorison.neuralnetwork.genetic;

import com.swehorison.neuralnetwork.neuralnetwork.NeuralNetwork;

public class NeuralNetworkGeneticAlgorithmExample {
    public static void main(String[] args) {

        NeuralNetwork neuralNetwork = new NeuralNetwork(2, 1, 2, 1);

        GANN gann = new GANN(neuralNetwork, 100, 0.5, 0.1);


        gann.initiatePopulation(10, new FitnessFunction() {
            @Override
            public double calculate(Individual individual) {

                double fitness = 0;
                double[] output = neuralNetwork.compute(individual.getGenes(), new double[]{0, 0});
                fitness += 1 - output[0];

                output = neuralNetwork.compute(individual.getGenes(), new double[]{0, 1});
                fitness += output[0];

                output = neuralNetwork.compute(individual.getGenes(), new double[]{1, 0});
                fitness += output[0];

                output = neuralNetwork.compute(individual.getGenes(), new double[]{1, 1});
                fitness += output[0];

                return fitness;
            }
        });

        gann.train();


        double[] genes = gann.getCurrentPopulation().getFittest().getGenes();
        System.out.println("new double[] {");
        for (int i = 0; i < genes.length; i++) {
            System.out.print(genes[i] + ",");
        }
        System.out.print("}");

        System.out.println();

        System.out.println(gann.run(genes, new double[]{0, 0})[0]);
        System.out.println(gann.run(genes, new double[]{0, 1})[0]);
        System.out.println(gann.run(genes, new double[]{1, 0})[0]);
        System.out.println(gann.run(genes, new double[]{1, 1})[0]);
    }
}
