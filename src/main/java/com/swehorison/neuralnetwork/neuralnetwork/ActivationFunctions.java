package com.swehorison.neuralnetwork.neuralnetwork;

public class ActivationFunctions {
    public static double sigmoidRough(double input) {
        double x = Math.abs(input);
        double x2 = x * x;
        double e = 1d + x + x2 * 0.555d + x2 * x2 * 0.143d;
        return 1d / (1d + (input > 0 ? 1d / e : e));
    }
}
