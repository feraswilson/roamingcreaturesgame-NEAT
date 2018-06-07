package com.swehorison.neuralnetwork.genetic;

/**
 * A bunch of sigmoid function approximations.
 * <p>
 * atan(pi*x/2)*2/pi
 * atan(x)
 * 1/(1+exp(-x))
 * 1/sqrt(1+x^2)
 * erf(sqrt(pi)*x/2)
 * erf2(sqrt(pi)*x/2)
 * tanh(x)
 * x/(1+|x|)
 * rough
 * feras
 */
public final class Sigmoids {
    private static final double sqrtPi = Math.sqrt(Math.PI);

    public static double sigmoidAtanpix22pi(double input) {
        return Math.atan(Math.PI * input / 2) * 2 / Math.PI;
    }

    public static double sigmoidAtanx(double input) {
        return Math.atan(input);
    }

    public static double sigmoid11xpx(double input) {
        return 1 / (1 + Math.exp(-input));
    }

    public static double sigmoid1sqrt1x2(double input) {
        return 1 / Math.sqrt(1 + input * input);
    }

    public static double sigmoiderfsqrtpix2(double input) {
        return erf(sqrtPi * input / 2);
    }

    public static double sigmoiderf2sqrtpix2(double input) {
        return erf2(sqrtPi * input / 2);
    }

    public static double sigmoidtanh(double input) {
        return Math.tanh(input);
    }

    public static double sigmoidx1x(double input) {
        return input / (1 + Math.abs(input));
    }

    public static double sigmoidRough(double input) {
        double x = Math.abs(input);
        double x2 = x * x;
        double e = 1d + x + x2 * 0.555d + x2 * x2 * 0.143d;
        return 1d / (1d + (input > 0 ? 1d / e : e));
    }

    public static double relu(double input) {
        return input > 0 ? (input > 1 ? 1 : input) : 0;
    }

    // fractional error in math formula less than 1.2 * 10 ^ -7.
    // although subject to catastrophic cancellation when z in very close to 0
    // from Chebyshev fitting formula for erf(z) from Numerical Recipes, 6.2
    private static double erf(double z) {
        double t = 1.0 / (1.0 + 0.5 * Math.abs(z));

        // use Horner's method
        double ans = 1 - t * Math.exp(-z * z - 1.26551223 + t * (1.00002368 + t * (0.37409196 + t * (0.09678418 + t * (-0.18628806 + t * (0.27886807 + t * (-1.13520398 + t * (1.48851587 + t * (-0.82215223 + t * (0.17087277))))))))));
        if (z >= 0) return ans;
        else return -ans;
    }

    // fractional error less than x.xx * 10 ^ -4.
    // Algorithm 26.2.17 in Abromowitz and Stegun, Handbook of Mathematical.
    private static double erf2(double z) {
        double t = 1.0 / (1.0 + 0.47047 * Math.abs(z));
        double poly = t * (0.3480242 + t * (-0.0958798 + t * (0.7478556)));
        double ans = 1.0 - poly * Math.exp(-z * z);
        if (z >= 0) return ans;
        else return -ans;
    }

    public static double sigmoidFeras(double x) {
        x = -x;
        x = 1d + x / 256d;
        x *= x;
        x *= x;
        x *= x;
        x *= x;
        x *= x;
        x *= x;
        x *= x;
        x *= x;
        return (1 / (1 + x));
    }
}
