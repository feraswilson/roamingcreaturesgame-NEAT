package com.swehorison.neuralnetwork.genetic;

import com.swehorison.neuralnetwork.neuralnetwork.NeuralNetwork;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class NeuralNetworkGeneticImageRecognitionWithCNNExample {

    NeuralNetwork neuralNetwork = new NeuralNetwork(676, 1, 20, 2);

    GANN gann = new GANN(neuralNetwork, 800, 0.5, 0.2);

    public NeuralNetworkGeneticImageRecognitionWithCNNExample() {
        ClassLoader classLoader = getClass().getClassLoader();

        BufferedImage carImage = readImage(classLoader.getResource("car.jpg").getFile());
        double[][] carDoubles = convertToArray(carImage);

        BufferedImage carImage2 = readImage(classLoader.getResource("car2.jpg").getFile());
        double[][] carDoubles2 = convertToArray(carImage2);


        BufferedImage cityImage = readImage(classLoader.getResource("city.jpg").getFile());
        double[][] cityDoubles = convertToArray(cityImage);

        BufferedImage cityImage2 = readImage(classLoader.getResource("city2.jpg").getFile());
        double[][] cityDoubles2 = convertToArray(cityImage2);


        // Convoulation
        double[][] convCar = convolution(carDoubles);
        double[] carArray = convertToNeuralNetworkArray(convCar);

        double[][] convCar2 = convolution(carDoubles2);
        double[] carArray2 = convertToNeuralNetworkArray(convCar2);


        double[][] convCity = convolution(cityDoubles);
        double[] cityArray = convertToNeuralNetworkArray(convCity);

        double[][] convCity2 = convolution(cityDoubles2);
        double[] cityArray2 = convertToNeuralNetworkArray(convCity2);


        gann.initiatePopulation(30, individual -> {
            double fitness = 0;
            double[] output = neuralNetwork.compute(individual.getGenes(), carArray);
            fitness += output[0];
            fitness += 1 - output[1];

            output = neuralNetwork.compute(individual.getGenes(), carArray2);
            fitness += output[0];
            fitness += 1 - output[1];

            output = neuralNetwork.compute(individual.getGenes(), cityArray);
            fitness += output[1];
            fitness += 1 - output[0];

            output = neuralNetwork.compute(individual.getGenes(), cityArray2);
            fitness += output[1];
            fitness += 1 - output[0];

            return fitness;
        });

        gann.train();


        double[] genes = gann.getCurrentPopulation().getFittest().getGenes();
        System.out.println("new double[] {");
        for (int i = 0; i < genes.length; i++) {
            System.out.print(genes[i] + ",");
        }
        System.out.print("}");

        System.out.println();

        double[] output = gann.run(genes, carArray);
        System.out.println("Car: " + output[0] + " City: " + output[1]);
        output = gann.run(genes, cityArray);
        System.out.println("Car: " + output[0] + " City: " + output[1]);


        BufferedImage mercedesImage = readImage(classLoader.getResource("mercedes.jpg").getFile());
        double[] mercedesOutput = runImageRecognition(mercedesImage);
        System.out.println("Car: " + mercedesOutput[0] + " City: " + mercedesOutput[1]);

        BufferedImage cityTestImage = readImage(classLoader.getResource("citytest.jpg").getFile());
        double[] citytest = runImageRecognition(cityTestImage);
        System.out.println("Car: " + citytest[0] + " City: " + citytest[1]);

    }

    public static void main(String[] args) {
        new NeuralNetworkGeneticImageRecognitionWithCNNExample();
    }

    private static double[] convertToNeuralNetworkArray(BufferedImage image) {
        //get image width and height
        int width = image.getWidth();
        int height = image.getHeight();

        double[] array = new double[676];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x, y);
                array[y * width + x] = (p & 0xFF) / 256.0;
            }
        }

        return array;
    }

    private static double[] convertToNeuralNetworkArray(double[][] conv) {
        double[] array = new double[676];
        for (int y = 0; y < conv.length; y++) {
            for (int x = 0; x < conv[y].length; x++) {
                array[y * conv[y].length + x] = conv[y][x];
            }
        }

        return array;
    }

    private static double[][] convertToArray(BufferedImage image) {
        //get image width and height
        int width = image.getWidth();
        int height = image.getHeight();

        double[][] array = new double[26][26];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x, y);
                array[y][x] = (p & 0xFF) / 256.0;
            }
        }

        return array;
    }

    private static BufferedImage readImage(String url) {
        BufferedImage img = null;
        File f = null;

        //read image
        try {
            f = new File(url);
            img = ImageIO.read(f);
        } catch (IOException e) {
            System.out.println(e);
        }

        //get image width and height
        int width = img.getWidth();
        int height = img.getHeight();

        //convert to grayscale
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = img.getRGB(x, y);

                int a = (p >> 24) & 0xff;
                int r = (p >> 16) & 0xff;
                int g = (p >> 8) & 0xff;
                int b = p & 0xff;

                //calculate average
                int avg = (r + g + b) / 3;

                //replace RGB value with avg
                p = (a << 24) | (avg << 16) | (avg << 8) | avg;

                img.setRGB(x, y, p);
            }
        }

        BufferedImage newImage = getScaledImage(img, 26, 26);

        return newImage;
    }

    private static BufferedImage getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TRANSLUCENT);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }

    /**
     * Run an image through the classifier
     *
     * @param image
     * @return
     */
    private double[] runImageRecognition(BufferedImage image) {
        double[][] imageArray = convertToArray(image);
        double[][] arrayConv = convolution(imageArray);
        return neuralNetwork.compute(gann.getCurrentPopulation().getFittest().getGenes(), convertToNeuralNetworkArray(arrayConv));
    }

    private double[][] convolution(double[][] carDoubles) {
        double[][] conv = new double[28][28];
        for (int i = 0; i < conv.length; i++) {
            for (int j = 0; j < conv[i].length; j++) {
                if (i == 0) {
                    conv[i][j] = 0;
                } else if (i == 27) {
                    conv[i][j] = 0;
                } else if (j == 0 || j == 27) {
                    conv[i][j] = 0;
                } else {
                    conv[i][j] = carDoubles[i - 1][j - 1];
                }
            }
        }

        // Filter 3x3
        double[][] filter = new double[3][3];
        filter[0][0] = 0;
        filter[0][1] = -1;
        filter[0][2] = -1;
        filter[1][0] = 0;
        filter[1][1] = 0;
        filter[1][2] = -1;
        filter[2][0] = 0;
        filter[2][1] = 1;
        filter[2][2] = 1;

        double[][] newconv = new double[26][26];
        for (int i = 0; i < conv.length - 2; i++) {
            for (int j = 0; j < conv[i].length - 2; j++) {
                double filterResult = conv[i][j] * filter[0][0]
                        + conv[i][j + 1] * filter[0][1]
                        + conv[i][j + 2] * filter[0][2]
                        + conv[i + 1][j] * filter[1][0]
                        + conv[i + 1][j + 1] * filter[1][1]
                        + conv[i + 1][j + 2] * filter[1][2]
                        + conv[i + 2][j] * filter[2][0]
                        + conv[i + 2][j + 1] * filter[2][1]
                        + conv[i + 2][j + 2] * filter[2][2];

                newconv[i][j] = filterResult;
            }
        }

        return newconv;
    }
}
