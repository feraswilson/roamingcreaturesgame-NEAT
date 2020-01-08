package com.swehorison.neuralnetwork.neuralnetwork;

public class ActivationFunctions {
    public static double sigmoidRough(double input) {
        double x = Math.abs(input);
        double x2 = x * x;
        double e = 1d + x + x2 * 0.555d + x2 * x2 * 0.143d;
        return 1d / (1d + (input > 0 ? 1d / e : e));
    }

    public static double relu(double input) {
        return input > 0 ? input : 0;
    }

    public static double elu(double input) {
        return input > 0 ? input : Math.exp(input) - 1;
    }

    public static void softmax(NeuralNetwork.Layer layers) {
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < layers.getNeurons().length; i++) {
            if (layers.getNeurons()[i] > max) {
                max = layers.getNeurons()[i];
            }
        }

        double sum = 0.0;
        for (int i = 0; i < layers.getNeurons().length; i++) {
            double out = Math.exp(layers.getNeurons()[i] - max);
            layers.getNeurons()[i] = out;
            sum += out;
        }

        for (int i = 0; i < layers.getNeurons().length; i++) {
            layers.getNeurons()[i] /= sum;
        }
    }

}
