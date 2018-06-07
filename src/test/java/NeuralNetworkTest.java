import com.swehorison.neuralnetwork.neuralnetwork.NeuralNetwork;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class NeuralNetworkTest {
    @Test
    public void testSpecificXor() {
        NeuralNetwork network = new NeuralNetwork(2, 1, 3, 1);
        double[] weights = new double[]{9.080264, -6.1803293, -4.3031797, 1.4238648, -6.7579184, -6.1845775, 2.9604888, -4.316061, -6.7640543, -7.090659, 15.150352, -5.6909485, -11.044768};

        System.out.println("0 xor 0 = " + Arrays.toString(network.compute(weights, new double[]{0, 0})));
        System.out.println("0 xor 1 = " + Arrays.toString(network.compute(weights, new double[]{0, 1})));
        System.out.println("1 xor 0 = " + Arrays.toString(network.compute(weights, new double[]{1, 0})));
        System.out.println("1 xor 1 = " + Arrays.toString(network.compute(weights, new double[]{1, 1})));

        double allowedError = 0.02;
        assertTrue(Math.abs(network.compute(weights, new double[]{0, 0})[0] - 0) < allowedError);
        assertTrue(Math.abs(network.compute(weights, new double[]{1, 0})[0] - 1) < allowedError);
        assertTrue(Math.abs(network.compute(weights, new double[]{0, 1})[0] - 1) < allowedError);
        assertTrue(Math.abs(network.compute(weights, new double[]{1, 1})[0] - 0) < allowedError);
    }

}
