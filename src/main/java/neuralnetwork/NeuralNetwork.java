package neuralnetwork;

import java.util.SplittableRandom;

public class NeuralNetwork {

    private Layer inputLayer;
    private Layer[] hiddenLayers;
    private int hiddenLayerNeuronSize;
    private Layer outputLayer;
    private int amountOfWeights;


    /**
     * Initialize neural network.
     *
     * @param inputLayerSize     defines the amount of inputs.
     * @param hiddenLayerSize    defines the amount of hidden layers.
     * @param hiddenLayerNeurons defines the amount of neurons in each hidden layer.
     * @param outputLayerSize    defines the amount of output neurons.
     */
    public NeuralNetwork(int inputLayerSize, int hiddenLayerSize, int hiddenLayerNeurons, int outputLayerSize) {
        inputLayer = new Layer(inputLayerSize);
        hiddenLayers = new Layer[hiddenLayerSize];
        this.hiddenLayerNeuronSize = hiddenLayerNeurons;
        for (int i = 0; i < hiddenLayerSize; i++) {
            hiddenLayers[i] = new Layer(hiddenLayerNeurons);
        }
        outputLayer = new Layer(outputLayerSize);

        this.amountOfWeights = hiddenLayerNeuronSize * (inputLayerSize + 1) + (hiddenLayers.length - 1) * hiddenLayerNeuronSize * (hiddenLayerNeuronSize + 1) + outputLayerSize * (hiddenLayerNeuronSize + 1);
    }

    /**
     * Compute output based on weights and input.
     *
     * @param weights to fill the biases and weights for each neuron.
     * @param inputs  to be evaluated.
     * @return a double with the outputs.
     */
    public double[] compute(double[] weights, double[] inputs) {
        int weightPosition = 0;

        inputLayer.setNeurons(inputs);

        for (int hln = 0; hln < hiddenLayers[0].getNeurons().length; hln++) {
            double bias = weights[weightPosition++];

            double totalWeights = 0;
            for (int j = 0; j < inputLayer.getNeurons().length; j++) {
                totalWeights += inputLayer.getNeurons()[j] * weights[weightPosition++];
            }

            hiddenLayers[0].getNeurons()[hln] = ActivationFunctions.sigmoidRough(bias + totalWeights);
        }

        for (int i = 1; i < hiddenLayers.length; i++) {
            for (int hln = 0; hln < hiddenLayers[i].getNeurons().length; hln++) {
                double bias = weights[weightPosition++];

                double totalWeights = 0;
                for (int j = 0; j < hiddenLayers[i - 1].getNeurons().length; j++) {
                    totalWeights += hiddenLayers[i - 1].getNeurons()[j] * weights[weightPosition++];
                }


                hiddenLayers[i].getNeurons()[hln] = ActivationFunctions.sigmoidRough(bias + totalWeights);
            }
        }

        for (int i = 0; i < outputLayer.getNeurons().length; i++) {
            double bias = weights[weightPosition++];

            double totalWeights = 0;
            for (int j = 0; j < hiddenLayers[hiddenLayers.length - 1].getNeurons().length; j++) {
                totalWeights += hiddenLayers[hiddenLayers.length - 1].getNeurons()[j] * weights[weightPosition++];
            }

            outputLayer.getNeurons()[i] = ActivationFunctions.sigmoidRough(bias + totalWeights);
        }

        return outputLayer.getNeurons();
    }


    public double[] getRandomizedWeights(int weightSize) {
        SplittableRandom random = new SplittableRandom();

        double[] weights = new double[weightSize];

        for (int i = 0; i < weightSize; i++) {
            weights[i] = random.nextDouble(-20, 20);
        }

        return weights;
    }

    public double[] getBestWeights(double[] inputs, double[] outputs) {
        int amountOfWeights = hiddenLayerNeuronSize * (inputs.length + 1) + (hiddenLayers.length - 1) * hiddenLayerNeuronSize * (hiddenLayerNeuronSize + 1) + outputs.length * (hiddenLayerNeuronSize + 1);
        double bestError = Double.MAX_VALUE;
        double[] bestWeights = new double[0];

        for (int i = 0; i < 1000000; i++) {
            double[] randomizedWeights = getRandomizedWeights(amountOfWeights);
            double error = 0;

            double[] result = this.compute(randomizedWeights, inputs);
            for (int j = 0; j < result.length; j++) {
                error += result[j] - outputs[j];
            }

            if (error < bestError) {
                bestError = error;
                bestWeights = randomizedWeights;
            }

        }

        return bestWeights;
    }

    public double[] getBestWeightsForAFunction(TraningFunction traningFunction) {
        int amountOfWeights = hiddenLayerNeuronSize * (traningFunction.getExpectedInputOutputs()[0][0].length + 1) + (hiddenLayers.length - 1) * hiddenLayerNeuronSize * (hiddenLayerNeuronSize + 1) + traningFunction.getExpectedInputOutputs()[0][1].length * (hiddenLayerNeuronSize + 1);
        double bestError = Double.MAX_VALUE;
        double[] bestWeights = new double[0];

        for (int i = 0; i < 1000000; i++) {
            double[] randomizedWeights = getRandomizedWeights(amountOfWeights);
            double error = 0;

            for (int m = 0; m < traningFunction.getExpectedInputOutputs().length; m++) {
                double[] result = this.compute(randomizedWeights, traningFunction.getExpectedInputOutputs()[m][0]);
                for (int j = 0; j < result.length; j++) {
                    error += Math.pow(result[j] - traningFunction.getExpectedInputOutputs()[m][1][j], 2);
                }
            }

            if (error < bestError) {
                bestError = error;
                bestWeights = randomizedWeights;
            }
        }

        return bestWeights;
    }

    public int getWeightSize() {
        return this.amountOfWeights;
    }


    public class Layer {
        private final double[] neurons;

        public Layer(int neuronsSize) {
            neurons = new double[neuronsSize];
        }

        public double[] getNeurons() {
            return neurons;
        }

        public void setNeurons(double[] neurons) {
            for (int i = 0; i < this.neurons.length; i++) {
                this.neurons[i] = neurons[i];
            }
        }
    }
}
