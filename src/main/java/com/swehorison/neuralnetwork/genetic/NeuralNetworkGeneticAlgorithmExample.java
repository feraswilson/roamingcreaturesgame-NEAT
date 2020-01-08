package com.swehorison.neuralnetwork.genetic;

import com.swehorison.neuralnetwork.neuralnetwork.NeuralNetwork;
import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class NeuralNetworkGeneticAlgorithmExample {

    private static XYSeries[] timeSeriesGlobal = new XYSeries[2];


    public static void main(String[] args) {


        timeSeriesGlobal[0] = ChartPanelDemo.createSeries("Static Evolution", new ArrayList<>());
        timeSeriesGlobal[1] = ChartPanelDemo.createSeries("Optimized Evolution", new ArrayList<>());

        NeuralNetwork neuralNetwork = new NeuralNetwork(3, 80, 20, 1);

        Thread n = new Thread(() -> {
            runTraining("Static Evolution", neuralNetwork, new HashMap<>(), 0);

            HashMap<Integer, Double> mutationRates = new HashMap<>();
            mutationRates.put(1, 0.9);
            mutationRates.put(200, 0.9);
            mutationRates.put(300, 0.8);
            mutationRates.put(400, 0.7);


            runTraining("Optimized Evolution", neuralNetwork, mutationRates, 1);
        });

        n.start();


        XYDataset dataset = ChartPanelDemo.createDataset(new ArrayList<>() {{
            add(timeSeriesGlobal[0]);
            add(timeSeriesGlobal[1]);
        }});
        ChartPanel chartPanel = ChartPanelDemo.createChart(dataset);
        ChartPanelDemo demo = new ChartPanelDemo(chartPanel);

        new Thread(() -> {
            for (int i = 0; i < 100000000; i++) {
                XYDataset datasetInside = ChartPanelDemo.createDataset(new ArrayList<>() {{
                    add(timeSeriesGlobal[0]);
                    add(timeSeriesGlobal[1]);
                }});
                ChartPanel chartPanelInside = ChartPanelDemo.createChart(datasetInside);

                demo.reload(chartPanelInside);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private static XYSeries runTraining(String name, NeuralNetwork neuralNetwork, HashMap<Integer, Double> mutationRates, int timeSeriesInput) {
        GANN gann = new GANN(neuralNetwork, 2000000, 0.1, 0.3);
        gann.setOutputInfo(false);

        long timeStart = System.currentTimeMillis();
        gann.initiatePopulation(1000, new FitnessFunction() {
            @Override
            public double calculate(Individual individual) {

                double fitness = 0;
                double[] output = neuralNetwork.compute(individual.getGenes(), new double[]{0, 0, 0});
                fitness += 1 - output[0];

                output = neuralNetwork.compute(individual.getGenes(), new double[]{0, 1, 0});
                fitness += output[0];

                output = neuralNetwork.compute(individual.getGenes(), new double[]{1, 0, 0});
                fitness += output[0];

                output = neuralNetwork.compute(individual.getGenes(), new double[]{1, 1, 0});
                fitness += output[0];


                output = neuralNetwork.compute(individual.getGenes(), new double[]{0, 0, 1});
                fitness += 1 - output[0];

                output = neuralNetwork.compute(individual.getGenes(), new double[]{0, 1, 1});
                fitness += output[0];

                output = neuralNetwork.compute(individual.getGenes(), new double[]{1, 0, 1});
                fitness += output[0];

                output = neuralNetwork.compute(individual.getGenes(), new double[]{1, 1, 1});
                fitness += output[0];

                return fitness;
            }
        });


        long timeEnd = System.currentTimeMillis();
        System.out.println("Difference: " + (timeEnd - timeStart));

        ArrayList<Double> evolutionFitness = new ArrayList<>();

        final XYSeries[] series = {null};
        gann.setOnCompleteListener(() -> {

            gann.getBestFitnessGenes();

            double[] genes = gann.getCurrentPopulation().getFittest().getGenes();
            System.out.println("new double[] {");
            for (int i = 0; i < genes.length; i++) {
                System.out.print(genes[i] + ",");
            }
            System.out.print("}");

            System.out.println();

            System.out.println(gann.run(genes, new double[]{0, 0, 0})[0]);
            System.out.println(gann.run(genes, new double[]{0, 1, 0})[0]);
            System.out.println(gann.run(genes, new double[]{1, 0, 0})[0]);
            System.out.println(gann.run(genes, new double[]{1, 1, 0})[0]);

            System.out.println(Arrays.toString(evolutionFitness.toArray()));
            // Create Graph
            series[0] = ChartPanelDemo.createSeries(name, evolutionFitness);


        });


        gann.setOnGenerationEvolution(population -> {
            if (gann.getBestFitness() > 6.9) {
                Algorithm.crossoverRate = 0.5;
            }
            evolutionFitness.add(gann.getCurrentGeneration(), population.getFittest().getFitness());
            timeSeriesGlobal[timeSeriesInput] = ChartPanelDemo.createSeries(name, evolutionFitness);
            if (population.getFittest().getFitness() > 7.8) {
                gann.end();
            }
            ;
        });
        gann.train(mutationRates, true);

        return series[0];
    }
}
