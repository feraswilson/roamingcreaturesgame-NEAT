package com.swehorison.neuralnetwork.genetic;

import com.swehorison.neuralnetwork.neuralnetwork.NeuralNetwork;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.SplittableRandom;

public class OrGateExample {
    public OrGateExample() {
        ClassLoader classLoader = getClass().getClassLoader();

        NeuralNetwork neuralNetwork = new NeuralNetwork(2, 1, 2, 1);

        GANN gann = new GANN(neuralNetwork, 5000, 0.4, 0.1);

        SplittableRandom random = new SplittableRandom();

        gann.initiatePopulation(100, individual -> {

            double fitness = 0;
            double[] output = neuralNetwork.compute(individual.getGenes(), new double[]{0, 0});
            fitness += 1 - output[0];

            output = neuralNetwork.compute(individual.getGenes(), new double[]{1, 0});
            fitness += output[0];

            output = neuralNetwork.compute(individual.getGenes(), new double[]{0, 1});
            fitness += output[0];

            neuralNetwork.compute(individual.getGenes(), new double[]{1, 1});
            fitness += output[0];
            return fitness;


        });

        gann.train();


        System.out.println(neuralNetwork.compute(gann.getCurrentPopulation().getFittest().getGenes(), new double[]{0, 0})[0]);
        System.out.println(neuralNetwork.compute(gann.getCurrentPopulation().getFittest().getGenes(), new double[]{0, 1})[0]);
        System.out.println(neuralNetwork.compute(gann.getCurrentPopulation().getFittest().getGenes(), new double[]{1, 0})[0]);
        System.out.println(neuralNetwork.compute(gann.getCurrentPopulation().getFittest().getGenes(), new double[]{1, 1})[0]);

    }

    public static void main(String[] args) {
        new OrGateExample();
    }


    private static double[] convertToNeuralNetworkArray(BufferedImage image) {
        //get image width and height
        int width = image.getWidth();
        int height = image.getHeight();

        double[] array = new double[169];
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

        BufferedImage newImage = getScaledImage(img, 13, 13);

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
