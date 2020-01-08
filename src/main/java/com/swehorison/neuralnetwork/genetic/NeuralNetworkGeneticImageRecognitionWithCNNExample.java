package com.swehorison.neuralnetwork.genetic;

import com.swehorison.neuralnetwork.neuralnetwork.NeuralNetwork;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class NeuralNetworkGeneticImageRecognitionWithCNNExample {

    NeuralNetwork neuralNetwork = new NeuralNetwork(36, 5, 5, 3);

    GANN gann = new GANN(neuralNetwork, 1000, 0.5, 0.02);
    ClassLoader classLoader = getClass().getClassLoader();
    private double[][] accordion = new double[55][676];
    private double[][] camera = new double[50][676];
    private double[][] chair = new double[62][676];

    public NeuralNetworkGeneticImageRecognitionWithCNNExample() throws IOException, ClassNotFoundException {

       /* InputStream file = new FileInputStream("data3.ser");
        InputStream buffer = new BufferedInputStream(file);
        ObjectInput input = new ObjectInputStream(buffer);

        double[] readNeuralWeights = (double[]) input.readObject();*/

        for (int i = 0; i < accordion.length; i++) {
            BufferedImage image;
            if (i < 9) {
                image = readImage(classLoader.getResource("accordion/image_000" + (i + 1) + ".jpg").getFile());
            } else {
                image = readImage(classLoader.getResource("accordion/image_00" + (i + 1) + ".jpg").getFile());
            }
            double[][] doubles = convertToArray(image);

            // Convolution
            double[][] conv = convolutionAndPooling(doubles);
            double[] neuralNetworkArray = convertToNeuralNetworkArray(conv);

            accordion[i] = neuralNetworkArray;
        }

        for (int i = 0; i < camera.length; i++) {
            BufferedImage image;
            if (i < 9) {
                image = readImage(classLoader.getResource("camera/image_000" + (i + 1) + ".jpg").getFile());
            } else {
                image = readImage(classLoader.getResource("camera/image_00" + (i + 1) + ".jpg").getFile());
            }
            double[][] doubles = convertToArray(image);

            // Convolution
            double[][] conv = convolutionAndPooling(doubles);
            double[] neuralNetworkArray = convertToNeuralNetworkArray(conv);

            camera[i] = neuralNetworkArray;
        }

        for (int i = 0; i < chair.length; i++) {
            BufferedImage image;
            if (i < 9) {
                image = readImage(classLoader.getResource("chair/image_000" + (i + 1) + ".jpg").getFile());
            } else {
                image = readImage(classLoader.getResource("chair/image_00" + (i + 1) + ".jpg").getFile());
            }
            double[][] doubles = convertToArray(image);

            // Convolution
            double[][] conv = convolutionAndPooling(doubles);
            double[] neuralNetworkArray = convertToNeuralNetworkArray(conv);

            chair[i] = neuralNetworkArray;
        }



/*        neuralNetwork.getBestWeightsForAFunction(new TraningFunction() {
            @Override
            public double[][][] getExpectedInputOutputs() {

                double[][][] inputOutputs =
                return new double[0][][];
            }
        })*/

        gann.initiatePopulation(500, individual -> {
            double fitness = 0;
            double[] output;

            int counter = 0;

            for (int i = 0; i < accordion.length; i++) {
                output = neuralNetwork.compute(individual.getGenes(), accordion[i]);
                fitness += output[0];
                fitness += 1 - output[1];
                fitness += 1 - output[2];
            }

            for (int i = 0; i < camera.length; i++) {
                output = neuralNetwork.compute(individual.getGenes(), camera[i]);
                fitness += 1 - output[0];
                fitness += output[1];
                fitness += 1 - output[2];
            }

            for (int i = 0; i < chair.length; i++) {
                output = neuralNetwork.compute(individual.getGenes(), chair[i]);
                fitness += 1 - output[0];
                fitness += 1 - output[1];
                fitness += output[2];
            }


            return fitness;
        });

        new Thread(() -> {
            try {

                int counter = 0;
                int adjustment = 0;
                double lastGenerationFitness = 0;
                while (gann.getCurrentGeneration() < gann.getMaxGenerations() - 1) {
                    System.out.println("Current Generation: " + gann.getCurrentGeneration() + " Current fitness: " + gann.getCurrentPopulation().getFittest().getFitness());
                    lastGenerationFitness = gann.getCurrentPopulation().getFittest().getFitness();
                    Thread.sleep(2000);


                    if (gann.getCurrentGeneration() % 2000 < 250) {
                        FileOutputStream fileOutputStream = new FileOutputStream("data2.ser");
                        ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
                        out.writeObject(gann.getCurrentPopulation().getFittest().getGenes());
                        out.close();

                        System.out.println("Saving dataset.");
                    }
                    if (gann.getCurrentPopulation().getFittest().getFitness() == lastGenerationFitness) {
                        counter++;

                        if (counter == 3) {
                            if (adjustment < 8) {
                                Algorithm.crossoverRate = Algorithm.crossoverRate + 0.05;
                                Algorithm.mutationRate = Algorithm.mutationRate + 0.03;
                                System.out.println("Tweaking system for more mutation. Mutation: " + Algorithm.mutationRate + " Crossover: " + Algorithm.crossoverRate);

                                adjustment++;

                            }

                            // Reset
                            counter = 0;
                        }

                    }

                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }).start();

        gann.train();


        double[] genes = gann.getCurrentPopulation().getFittest().getGenes();
        System.out.println("new double[] {");
        for (int i = 0; i < genes.length; i++) {
            System.out.print(genes[i] + ",");
        }
        System.out.print("}");

        System.out.println();

        BufferedImage mercedesImage = readImage(classLoader.getResource("mercedes.jpg").getFile());
        double[] mercedesOutput = runImageRecognition(mercedesImage);
        System.out.println("Accordion: " + mercedesOutput[0] + " Camera: " + mercedesOutput[1] + " Chair: " + mercedesOutput[2]);

        BufferedImage cityTestImage = readImage(classLoader.getResource("citytest.jpg").getFile());
        double[] citytest = runImageRecognition(cityTestImage);
        System.out.println("Accordion: " + citytest[0] + " Camera: " + citytest[1] + " Chair: " + citytest[2]);


        BufferedImage accordionTestImage = readImage(classLoader.getResource("accordiontest.jpg").getFile());
        double[] accordiontest = runImageRecognition(accordionTestImage);
        System.out.println("Accordion Test - Accordion: " + accordiontest[0] + " Camera: " + accordiontest[1] + " Chair: " + accordiontest[2]);


        BufferedImage cameraTestImage = readImage(classLoader.getResource("cameratest.jpeg").getFile());
        double[] cameratest = runImageRecognition(cameraTestImage);
        System.out.println("Camera Test - Accordion: " + cameratest[0] + " Camera: " + cameratest[1] + " Chair: " + cameratest[2]);


        BufferedImage sameAccordionTestImage = readImage(classLoader.getResource("accordion/image_0001.jpg").getFile());
        double[] sameAccordiontest = runImageRecognition(sameAccordionTestImage);
        System.out.println("Accordion Same image Test - Accordion: " + sameAccordiontest[0] + " Camera: " + sameAccordiontest[1] + " Chair: " + sameAccordiontest[2]);


        FileOutputStream fileOutputStream = new FileOutputStream("data3.ser");
        ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
        out.writeObject(gann.getCurrentPopulation().getFittest().getGenes());
        out.close();


    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
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
        double[][] arrayConv = convolutionAndPooling(imageArray);
        return neuralNetwork.compute(gann.getCurrentPopulation().getFittest().getGenes(), convertToNeuralNetworkArray(arrayConv));
    }

    private double[][] convolutionAndPooling(double[][] carDoubles) {
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
        filter[0][0] = -1;
        filter[0][1] = -1;
        filter[0][2] = -1;
        filter[1][0] = -1;
        filter[1][1] = 0;
        filter[1][2] = -1;
        filter[2][0] = -1;
        filter[2][1] = -1;
        filter[2][2] = -1;

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


        // Max pooling
        double[][] maxPooled = new double[13][13];
        for (int i = 0; i < maxPooled.length; i++) {
            for (int j = 0; j < maxPooled[i].length; j++) {
                maxPooled[i][j] = Math.max(Math.max(newconv[i * 2][j * 2], newconv[i * 2][j * 2 + 1]), Math.max(newconv[i * 2 + 1][j], newconv[i * 2 + 1][j * 2 + 1]));
            }
        }


        double[][] conv2 = new double[13][13];
        for (int i = 0; i < conv2.length; i++) {
            for (int j = 0; j < conv2[i].length; j++) {
                if (i == 0) {
                    conv2[i][j] = 0;
                } else if (i == 27) {
                    conv2[i][j] = 0;
                } else if (j == 0 || j == 27) {
                    conv2[i][j] = 0;
                } else {
                    conv2[i][j] = maxPooled[i - 1][j - 1];
                }
            }
        }


        // Filter 3x3
        double[][] filter2 = new double[3][3];
        filter2[0][0] = -1;
        filter2[0][1] = -1;
        filter2[0][2] = -1;
        filter2[1][0] = -1;
        filter2[1][1] = 8;
        filter2[1][2] = -1;
        filter2[2][0] = -1;
        filter2[2][1] = -1;
        filter2[2][2] = -1;

        double[][] newconv2 = new double[13][13];
        for (int i = 0; i < conv2.length - 2; i++) {
            for (int j = 0; j < conv2[i].length - 2; j++) {
                double filterResult = conv2[i][j] * filter2[0][0]
                        + conv2[i][j + 1] * filter2[0][1]
                        + conv2[i][j + 2] * filter2[0][2]
                        + conv2[i + 1][j] * filter2[1][0]
                        + conv2[i + 1][j + 1] * filter2[1][1]
                        + conv2[i + 1][j + 2] * filter2[1][2]
                        + conv2[i + 2][j] * filter2[2][0]
                        + conv2[i + 2][j + 1] * filter2[2][1]
                        + conv2[i + 2][j + 2] * filter2[2][2];

                newconv2[i][j] = filterResult;
            }
        }


        double[][] maxPooled2 = new double[6][6];
        for (int i = 0; i < maxPooled2.length; i++) {
            for (int j = 0; j < maxPooled2[i].length; j++) {
                maxPooled2[i][j] = Math.max(Math.max(newconv2[i * 2][j * 2], newconv2[i * 2][j * 2 + 1]), Math.max(newconv2[i * 2 + 1][j], newconv2[i * 2 + 1][j * 2 + 1]));
            }
        }


        return newconv2;
    }
}
