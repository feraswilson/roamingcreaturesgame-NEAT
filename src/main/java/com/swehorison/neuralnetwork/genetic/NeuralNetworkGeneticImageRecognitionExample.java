package com.swehorison.neuralnetwork.genetic;

import com.swehorison.neuralnetwork.neuralnetwork.NeuralNetwork;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class NeuralNetworkGeneticImageRecognitionExample {
    public NeuralNetworkGeneticImageRecognitionExample() {
        ClassLoader classLoader = getClass().getClassLoader();

        BufferedImage carImage = readImage(classLoader.getResource("car.jpg").getFile());
        double[] carDoubles = convertToNeuralNetworkArray(carImage);

        BufferedImage carImage2 = readImage(classLoader.getResource("car2.jpg").getFile());
        double[] carDoubles2 = convertToNeuralNetworkArray(carImage2);


        BufferedImage cityImage = readImage(classLoader.getResource("city.jpg").getFile());
        double[] cityDoubles = convertToNeuralNetworkArray(cityImage);


        BufferedImage cityImage2 = readImage(classLoader.getResource("city2.jpg").getFile());
        double[] cityDoubles2 = convertToNeuralNetworkArray(cityImage2);

        NeuralNetwork neuralNetwork = new NeuralNetwork(676, 1, 20, 2);

        GANN gann = new GANN(neuralNetwork, 800, 0.5, 0.2);


        gann.initiatePopulation(30, individual -> {
            double fitness = 0;
            double[] output = neuralNetwork.compute(individual.getGenes(), carDoubles);
            fitness += output[0];
            fitness += 1 - output[1];

            output = neuralNetwork.compute(individual.getGenes(), carDoubles2);
            fitness += output[0];
            fitness += 1 - output[1];

            output = neuralNetwork.compute(individual.getGenes(), cityDoubles);
            fitness += output[1];
            fitness += 1 - output[0];

            neuralNetwork.compute(individual.getGenes(), cityDoubles2);
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

        double[] output = gann.run(genes, carDoubles);
        System.out.println("Car: " + output[0] + " City: " + output[1]);
        output = gann.run(genes, cityDoubles);
        System.out.println("Car: " + output[0] + " City: " + output[1]);


        BufferedImage mercedesImage = readImage(classLoader.getResource("mercedes.jpg").getFile());
        double[] mercedesDoubles = convertToNeuralNetworkArray(mercedesImage);

        System.out.println("Not trained image (Mercedes):");
        output = gann.run(genes, mercedesDoubles);
        System.out.println("Car: " + output[0] + " City: " + output[1]);

        BufferedImage cityTestImage = readImage(classLoader.getResource("citytest.jpg").getFile());
        double[] cityTestDoubles = convertToNeuralNetworkArray(cityTestImage);

        System.out.println("Not trained image (City):");
        output = gann.run(genes, cityTestDoubles);
        System.out.println("Car: " + output[0] + " City: " + output[1]);
    }

    public static void main(String[] args) {
        new NeuralNetworkGeneticImageRecognitionExample();
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
}
