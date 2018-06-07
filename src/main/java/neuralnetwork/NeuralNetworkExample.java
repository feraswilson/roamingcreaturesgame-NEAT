package neuralnetwork;

public class NeuralNetworkExample {
    public static void main(String[] args) {
        NeuralNetwork neuralNetwork = new NeuralNetwork(2, 2, 4, 1);

//        System.out.println(neuralNetwork.compute(neuralNetwork.getRandomizedWeights(37), new double[]{1, 1})[0]);

        double[] bestWeights = neuralNetwork.getBestWeights(new double[]{1, 1}, new double[]{0, 0});
        // System.out.println(neuralNetwork.compute(bestWeights, new double[]{1, 1})[0]);

        double[] bestWeights2 = neuralNetwork.getBestWeightsForAFunction(() -> {

            double[][][] expected = new double[][][]{
                    {new double[]{0, 0}, new double[]{0}},
                    {new double[]{0, 1}, new double[]{1}},
                    {new double[]{1, 0}, new double[]{1}},
                    {new double[]{1, 1}, new double[]{1}}
            };

            return expected;
        });


        System.out.println(neuralNetwork.compute(bestWeights2, new double[]{0, 0})[0]);
        System.out.println(neuralNetwork.compute(bestWeights2, new double[]{0, 1})[0]);
        System.out.println(neuralNetwork.compute(bestWeights2, new double[]{1, 0})[0]);
        System.out.println(neuralNetwork.compute(bestWeights2, new double[]{1, 1})[0]);
    }
}
