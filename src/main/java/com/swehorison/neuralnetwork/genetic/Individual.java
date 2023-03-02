package com.swehorison.neuralnetwork.genetic;

public class Individual {
    private double[] genes;
    private Double fitness;
    private FitnessFunction fitnessCalculation;

    public Individual(double[] genes, FitnessFunction fitnessCalculation) {
        this.genes = genes;
        this.fitnessCalculation = fitnessCalculation;
    }

    public static Individual getBlankIndividual(FitnessFunction fitnessCalculation, int geneLength) {
        return new Individual(new double[geneLength], fitnessCalculation);
    }

    public static Individual getRandomizedIndividual(FitnessFunction fitnessCalculation, int geneLength) {
        double[] genes = new double[geneLength];

        for (int i = 0; i < genes.length; i++) {
            genes[i] = Algorithm.random.nextDouble() * 40 - 20;
        }

        return new Individual(genes, fitnessCalculation);
    }

    public static Individual getCrossoverIndividual(Individual individual1, Individual individual2, FitnessFunction fitnessCalculation) {
        int length = individual1.getGenes().length;
        double[] genes = new double[length];
        int splice = (int) (0.5 * length);
        System.arraycopy(individual1.getGenes(), 0, genes, 0, splice);
        System.arraycopy(individual2.getGenes(), splice, genes, splice, length - splice);
        return new Individual(genes, fitnessCalculation);
    }

    public double getGene(int index) {
        return genes[index];
    }

    public void setGene(int index, double value) {
        genes[index] = value;
        fitness = null;
    }

    public int size() {
        return genes.length;
    }

    public double getFitness() {
        if (fitness == null) {
            fitness = fitnessCalculation.calculate(this);
        }
        return fitness;
    }

    /**
     * Set fitness manually. Should only be used when traning is done using the a real simulation.
     *
     * @param fitness value to set
     */
    public void setFitness(Double fitness) {
        this.fitness = fitness;
    }

    public double[] getGenes() {
        return genes;
    }

    @Override
    public String toString() {
        String geneString = "";
        for (int i = 0; i < size(); i++) {
            geneString += getGene(i) + " ";
        }
        return geneString;
    }
}