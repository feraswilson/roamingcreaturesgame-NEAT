package genetic;

public class Population {
    private final Individual[] individuals;
    private final FitnessFunction fitnessCalculation;
    private final int geneLength;

    public Population(int populationSize, boolean initialise, FitnessFunction fitnessCalculation, int geneLength) {
        this(populationSize, initialise, fitnessCalculation, geneLength, null);
    }

    public Population(int populationSize, boolean initialise, FitnessFunction fitnessCalculation, int geneLength, double[] genes) {
        individuals = new Individual[populationSize];
        this.fitnessCalculation = fitnessCalculation;
        this.geneLength = geneLength;

        if (initialise) {

            if (genes == null) {
                for (int i = 0; i < individuals.length; i++) {
                    individuals[i] = Individual.getRandomizedIndividual(fitnessCalculation, geneLength);
                }
            } else {
                if (geneLength != genes.length) {
                    throw new IllegalArgumentException("The given genes size needs to be the same as the provided geneLength");
                }
                individuals[0] = new Individual(genes, fitnessCalculation);

                // Give the rest of the half some randoom stuff
                for (int i = individuals.length / 2; i < individuals.length; i++) {
                    double[] currentGenes = new double[geneLength];
                    System.arraycopy(genes, 0, currentGenes, 0, genes.length);
                    for (int j = 0; j < currentGenes.length; j++) {
                        if (currentGenes[i] == 20) {
                            currentGenes[i] -= Algorithm.random.nextDouble() * 4;
                            ;
                        } else if (currentGenes[i] == -20) {
                            currentGenes[i] += Algorithm.random.nextDouble() * 4;
                        } else if (currentGenes[i] > -17 && currentGenes[i] < 17) {
                            currentGenes[i] += Algorithm.random.nextDouble() * 6 - 6;
                        }
                    }
                    individuals[i] = new Individual(genes, fitnessCalculation);
                }
                for (int i = 1; i < individuals.length / 2; i++) {
                    individuals[i] = Individual.getRandomizedIndividual(fitnessCalculation, geneLength);
                }
            }
        }
    }

    public Individual getFittest() {
        Individual fittest = individuals[0];

        for (int i = 1; i < individuals.length; i++) {
            Individual individual = individuals[i];

            if (individual.getFitness() > fittest.getFitness()) {
                fittest = individual;
            }
        }

        return fittest;
    }

    public Individual getIndividual(int index) {
        return individuals[index];
    }

    public int size() {
        return individuals.length;
    }

    public void saveIndividual(int index, Individual individual) {
        individuals[index] = individual;
    }

    public FitnessFunction getFitnessFunction() {
        return fitnessCalculation;
    }

    public int getGeneLength() {
        return geneLength;
    }
}