package com.swehorison.neuralnetwork.roaminggame;

import com.swehorison.neuralnetwork.genetic.Algorithm;
import com.swehorison.neuralnetwork.genetic.GANN;
import com.swehorison.neuralnetwork.neuralnetwork.NeuralNetwork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.SplittableRandom;

public class RoamingCreaturesGame extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final int CREATURE_SIZE = 50;
    private static final int NUM_CREATURES = 30;
    private static final int NUM_CREATURES_AFTER_RENDER = 1;
    private static final int CROSSOVER_RATE_INCREASE_KEY = KeyEvent.VK_NUMPAD7;
    private static final int CROSSOVER_RATE_DECREASE_KEY = KeyEvent.VK_NUMPAD4;
    private final NeuralNetwork neuralNetwork;
    private final GANN gann;

    private ArrayList<Creature> creatures;
    private ArrayList<Point> food;
    private static final int MUTATION_RATE_INCREASE_KEY = KeyEvent.VK_NUMPAD9;
    private static final int MUTATION_RATE_DECREASE_KEY = KeyEvent.VK_NUMPAD6;

    private SplittableRandom random = new SplittableRandom();
    static int slowDownInGeneration = 1000;
    private static int NUM_FOOD = 20;
    private ArrayList<Point> animals;
    private int TIMER = 70000;
    private int pauseTime = 50;

    public RoamingCreaturesGame() throws IOException, ClassNotFoundException {
        setPreferredSize(new Dimension(2560, 1440));
        setBackground(Color.WHITE);


        neuralNetwork = new NeuralNetwork(5, 2, 6, 3);

        gann = new GANN(neuralNetwork, 3000000, 0.8, 0.01);

        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(
                    "java2sObject22_4inputs.dat"));
            double[] ia = (double[]) (ois.readObject());
            gann.initiatePopulation(Math.max(NUM_CREATURES, NUM_CREATURES_AFTER_RENDER), ia, individual -> 0.0);
        } catch (IOException e) {
            gann.initiatePopulation(Math.max(NUM_CREATURES, NUM_CREATURES_AFTER_RENDER), individual -> 0.0);
        }


        createNewCreaturesAndFood(NUM_CREATURES);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        JFrame frame = new JFrame("Roaming Creatures Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        RoamingCreaturesGame game = new RoamingCreaturesGame();

        game.setFocusable(true);
        frame.add(game);
        frame.pack();

        frame.setVisible(true);

        game.play();
    }

    @Override
    public void processKeyEvent(KeyEvent e) {
        switch (e.getKeyCode()) {
            case CROSSOVER_RATE_INCREASE_KEY:
                Algorithm.crossoverRate += 0.01;
                System.out.println("Crossover rate increased to " + Algorithm.crossoverRate);
                break;
            case CROSSOVER_RATE_DECREASE_KEY:
                Algorithm.crossoverRate -= 0.01;
                System.out.println("Crossover rate decreased to " + Algorithm.crossoverRate);
                break;
            case MUTATION_RATE_INCREASE_KEY:
                Algorithm.mutationRate += 0.01;
                System.out.println("Mutation rate increased to " + Algorithm.mutationRate);
                break;
            case MUTATION_RATE_DECREASE_KEY:
                Algorithm.mutationRate -= 0.01;
                System.out.println("Mutation rate decreased to " + Algorithm.mutationRate);
                break;
            case KeyEvent.VK_P:

                pauseTime = pauseTime == 15000 ? 100 : 15000;
                break;
        }
        try {
            Thread.sleep(50);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void createNewCreaturesAndFood(int amount) {
        creatures = new ArrayList<>();
        food = new ArrayList<>();
        animals = new ArrayList<>();

        Random random = new Random();
        for (int i = 0; i < 12; i++) {
            int x = random.nextInt(1920 - CREATURE_SIZE);
            int y = random.nextInt(1080 - CREATURE_SIZE);
            animals.add(new Point(x, y));
        }

        for (int i = 0; i < amount; i++) {
            int x = random.nextInt(1920 - CREATURE_SIZE);
            int y = random.nextInt(1080 - CREATURE_SIZE);
            creatures.add(new Creature(x, y));
        }

        for (int i = 0; i < NUM_FOOD; i++) {
            int x = random.nextInt(1920);
            int y = random.nextInt(1080);
            food.add(new Point(x, y));
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        RenderingHints hints = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(hints);

        g2d.setColor(Color.RED);

        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Generation: " + gann.getCurrentGeneration(), 10, 30);

        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        for (Creature c : creatures) {
            c.draw(g2d);
        }

        g2d.setColor(Color.GREEN);
        for (Point p : food) {
            g2d.fillOval(p.x, p.y, CREATURE_SIZE, CREATURE_SIZE);
        }

        g2d.setColor(Color.MAGENTA);
        for (Point p : animals) {
            g2d.fillOval(p.x, p.y, CREATURE_SIZE, CREATURE_SIZE);
        }
    }

    public void play() throws IOException {

        int timer = TIMER;

        while (true) {
            boolean dead = true;
            int x = 0;

            for (Creature c : creatures) {

                if (c.hp <= 0) {
                    c.setVisible(false);
                } else {
                    c.move(gann.getCurrentPopulation().getIndividual(x).getGenes(), x);

                    for (int i = 0; i < food.size(); i++) {
                        Point p = food.get(i);
                        if (c.x <= p.x + CREATURE_SIZE && c.x + CREATURE_SIZE >= p.x &&
                                c.y <= p.y + CREATURE_SIZE && c.y + CREATURE_SIZE >= p.y) {
                            c.incrementPoints();
                            //gann.getCurrentPopulation().getIndividual(x).setFitness(gann.getCurrentPopulation().getIndividual(x).getFitness() + 1);

                            if (c.hp < 180) {
                                c.hp += 25;
                            } else {
                                c.hp = 180;
                            }

                            food.remove(i);

                            int foodX = random.nextInt(1920);
                            int foodY = random.nextInt(1080);
                            food.add(new Point(foodX, foodY));
                            i--;
                        }
                    }

                    for (int i = 0; i < animals.size(); i++) {
                        Point p = animals.get(i);
                        if (c.x <= p.x + CREATURE_SIZE && c.x + CREATURE_SIZE >= p.x &&
                                c.y <= p.y + CREATURE_SIZE && c.y + CREATURE_SIZE >= p.y) {
                            c.incrementAnimalPoints();
                            //gann.getCurrentPopulation().getIndividual(x).setFitness(gann.getCurrentPopulation().getIndividual(x).getFitness() + 1);

                            if (c.hp < 180) {
                                c.hp += 100;
                            } else {
                                c.hp = 180;
                            }

                            animals.remove(i);

                            int animalX = random.nextInt(1920);
                            int animalY = random.nextInt(1080);
                            animals.add(new Point(animalX, animalY));
                            i--;
                        }
                    }
                }
                c.hp--;
                x++;


            }

            for (int i = 0; i < creatures.size(); i++) {
                if (creatures.get(i).hp > 0) {
                    dead = false;
                    break;
                }
            }

            if ((food.isEmpty() && animals.isEmpty()) || timer == 0 || dead) {


                int i = 0;
                for (Creature c : creatures) {
                    gann.getCurrentPopulation().getIndividual(i).setFitness(c.pointsFood + (c.pointsAnimal * 4) + 0.0 + c.extraPoints);
                    i++;
                }
                gann.trainOneGeneration();


                if (gann.getCurrentGeneration() > slowDownInGeneration) {
                    createNewCreaturesAndFood(NUM_CREATURES_AFTER_RENDER);
                } else {
                    createNewCreaturesAndFood(NUM_CREATURES);
                }
                timer = TIMER;

                if (gann.getCurrentGeneration() % 100 == 0) {
                    System.out.println("Current generation: " + gann.getCurrentGeneration() + " " + gann.getBestFitness());
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
                            "java2sObject22_4inputs.dat"));
                    oos.writeObject(gann.getBestFitnessGenes());
                    oos.close();
                }

                dead = true;


                //System.out.println();
            }

            timer--;

            if (gann.getCurrentGeneration() > slowDownInGeneration) {
                repaint();
            }
            try {
                if (gann.getCurrentGeneration() > slowDownInGeneration) {
                    Thread.sleep(pauseTime);
                } else {
                    //   Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class Creature {
        public int hp = 180;
        private int x, y, pointsFood, pointsAnimal;
        private boolean leftSensor, rightSensor;
        private double foodAngle, orientation, extraPoints = 0.0;
        private boolean visible = true;
        private double animalAngle;
        private boolean animalRightSensor, animalLeftSensor;

        public Creature(int x, int y) {
            this.x = x;
            this.y = y;
            Random random = new Random();
            int dx = random.nextInt(3) - 1;
            int dy = random.nextInt(3) - 1;
            this.x += dx * CREATURE_SIZE;
            this.y += dy * CREATURE_SIZE;
            this.pointsFood = 0;
            this.pointsAnimal = 0;
            this.animalAngle = random.nextDouble() * 360;
            this.foodAngle = random.nextDouble() * 360;
            this.orientation = (random.nextDouble() * 6.28) - 3.14;
        }

        public void draw(Graphics g) {
            if (visible) {
                Graphics2D g2d = (Graphics2D) g.create();
                RenderingHints hints = new RenderingHints(
                        RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHints(hints);

                int sensorLength = CREATURE_SIZE / 2;
                int leftSensorTipX = (int) (x + CREATURE_SIZE / 2 + Math.cos(orientation - Math.PI / 4) * sensorLength);
                int leftSensorTipY = (int) (y + CREATURE_SIZE / 2 + Math.sin(orientation - Math.PI / 4) * sensorLength);
                int rightSensorTipX = (int) (x + CREATURE_SIZE / 2 + Math.cos(orientation + Math.PI / 4) * sensorLength);
                int rightSensorTipY = (int) (y + CREATURE_SIZE / 2 + Math.sin(orientation + Math.PI / 4) * sensorLength);

                g2d.setColor(Color.BLUE);
                g2d.fillOval(x, y, CREATURE_SIZE, CREATURE_SIZE);
                g2d.setColor(Color.RED);
                g2d.fillOval(x + CREATURE_SIZE / 4, y + CREATURE_SIZE / 4, CREATURE_SIZE / 2, CREATURE_SIZE / 2);
                g2d.drawLine(x + CREATURE_SIZE / 2, y + CREATURE_SIZE / 2, leftSensorTipX, leftSensorTipY);
                g2d.drawLine(x + CREATURE_SIZE / 2, y + CREATURE_SIZE / 2, rightSensorTipX, rightSensorTipY);
                // Add angle number
                g2d.drawString(String.format("%.0f", Math.toDegrees(orientation) % 360), x + 30, y + 30);

                if (leftSensor) {
                    g2d.setColor(Color.YELLOW);
                    g2d.fillOval(leftSensorTipX - CREATURE_SIZE / 8, leftSensorTipY - CREATURE_SIZE / 8, CREATURE_SIZE / 4, CREATURE_SIZE / 4);
                } else {
                    g2d.setColor(Color.BLUE);
                    g2d.fillOval(leftSensorTipX - CREATURE_SIZE / 8, leftSensorTipY - CREATURE_SIZE / 8, CREATURE_SIZE / 4, CREATURE_SIZE / 4);
                }
                if (rightSensor) {
                    g2d.setColor(Color.YELLOW);
                    g2d.fillOval(rightSensorTipX - CREATURE_SIZE / 8, rightSensorTipY - CREATURE_SIZE / 8, CREATURE_SIZE / 4, CREATURE_SIZE / 4);
                } else {
                    g2d.setColor(Color.BLUE);
                    g2d.fillOval(rightSensorTipX - CREATURE_SIZE / 8, rightSensorTipY - CREATURE_SIZE / 8, CREATURE_SIZE / 4, CREATURE_SIZE / 4);
                }

                g2d.setColor(Color.RED);
                g2d.drawString("Points: " + pointsFood + " Animal: " + pointsAnimal, x, y + 40);
                g2d.drawString("Left: " + (leftSensor ? "active" : "inactive"), x, y - 20);
                g2d.drawString("Right: " + (rightSensor ? "active" : "inactive"), x, y - 10);

                g2d.drawString("Anim. Left: " + (animalLeftSensor ? "active" : "inactive"), x, y - 40);
                g2d.drawString("Anim. Right: " + (animalRightSensor ? "active" : "inactive"), x, y - 30);
            }
        }

        public void move(double[] weights, int positionInGeneration) {
            if (visible) {
                leftSensor = false;
                rightSensor = false;

                Point closestFood = null;
                int closestFoodDistance = Integer.MAX_VALUE;
                for (Point p : food) {
                    int distance = (int) Math.sqrt(Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2));
                    if (distance < closestFoodDistance) {
                        closestFood = p;
                        closestFoodDistance = distance;
                    }
                }

                Point closestAnimal = null;
                int closestAnimalDistance = Integer.MAX_VALUE;
                for (Point p : animals) {
                    int distance = (int) Math.sqrt(Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2));
                    if (distance < closestAnimalDistance) {
                        closestAnimal = p;
                        closestAnimalDistance = distance;
                    }
                }
                // closestFoodDistance, currentX, currentY, angle, leftSensor, rightSensor
                int animalX = 0;
                int animalY = 0;
                int foodX = 0;
                int foodY = 0;
                if (closestFood != null || closestAnimal != null) {
                    animalRightSensor = false;
                    animalLeftSensor = false;

                    if (closestFood != null) {
                        foodX = closestFood.x + CREATURE_SIZE / 2;
                        foodY = closestFood.y + CREATURE_SIZE / 2;
                    }

                    if (closestAnimal != null) {
                        animalX = closestAnimal.x + CREATURE_SIZE / 2;
                        animalY = closestAnimal.y + CREATURE_SIZE / 2;
                    }

                    int creatureX = x + CREATURE_SIZE / 2;
                    int creatureY = y + CREATURE_SIZE / 2;

                    double normalizedOrientation = ((orientation + Math.PI) / 6.28);
                    //  double normalizedOrientation = ((orientation / 360) * 6.28) - 3.14;


                    animalAngle = Math.atan2(animalY - creatureY, animalX - creatureX) - orientation;
                    foodAngle = Math.atan2(foodY - creatureY, foodX - creatureX) - orientation;


                    double normalizedFoodDistance = (closestFoodDistance) / (1000.0);
                    double normalizedAnimalDistance = (closestAnimalDistance) / (1000.0);

                    double degrees = Math.toDegrees(foodAngle);
                    if (closestFood == null) {
                        degrees = 0;
                        normalizedFoodDistance = 0;
                    }
                    if (degrees < -360) {
                        degrees += 360;
                    } else if (degrees > 360) {
                        degrees = -360;
                    }
                    if (degrees > 10 && degrees < 180 || degrees < -180 && degrees > -350) {
                        rightSensor = true;
                    } else if (degrees < -10 && degrees > -180 || degrees > 180 && degrees < 350) {
                        leftSensor = true;
                    } else {
                        // System.out.println(degrees);
                        leftSensor = true;
                        rightSensor = true;
                    }

                    double degreesAnimal = Math.toDegrees(animalAngle);
                    if (closestAnimal == null) {
                        degreesAnimal = 0;
                        normalizedAnimalDistance = 0;
                    }
                    if (degreesAnimal < -360) {
                        degreesAnimal += 360;
                    } else if (degreesAnimal > 360) {
                        degreesAnimal = -360;
                    }

                    if (degreesAnimal > 10 && degreesAnimal < 180 || degreesAnimal < -180 && degreesAnimal > -350) {
                        animalRightSensor = true;
                    } else if (degreesAnimal < -10 && degreesAnimal > -180 || degreesAnimal > 180 && degreesAnimal < 350) {
                        animalLeftSensor = true;
                    } else {
                        // System.out.println(degrees);
                        animalLeftSensor = true;
                        animalRightSensor = true;
                    }

                    // System.out.println("Closest animal: " + animalX + " " + animalY + " AnimalAngle: " + degreesAnimal+ " Orientation: " + Math.toDegrees(orientation) + " Sensors Left: "+ animalLeftSensor + " Sensors Right: " + animalRightSensor);

                    // System.out.println("normalizedAnimalDistance: " + normalizedAnimalDistance  + " FoodPoints: " + pointsFood/500.0 + " AnimalPoints: " + pointsAnimal/500.0);
                    double animalSensor = -1;
                    if (animalLeftSensor && animalRightSensor) {
                        animalSensor = 0.5;
                    } else if (animalLeftSensor) {
                        animalSensor = 0;
                    } else {
                        animalSensor = 1;
                    }

                    double foodSensor = -1;
                    if (leftSensor && rightSensor) {
                        foodSensor = 0.5;
                    } else if (leftSensor) {
                        foodSensor = 0;
                    } else {
                        foodSensor = 1;
                    }
                    double[] output = gann.run(weights, new double[]{normalizedFoodDistance, normalizedAnimalDistance, foodSensor, animalSensor, hp / 180.0});

                    if (output[0] == output[1]) {
                        // Do nothing
                    } else if (output[0] < output[1]) {
                        if (orientation >= Math.PI) {
                            orientation = -Math.PI + (0.017 * output[1] * 4);
                        } else {
                            orientation += 0.017 * output[1] * 4;
                        }
                    } else {
                        if (orientation <= -Math.PI) {
                            orientation = Math.PI - (0.017 * output[0] * 4);
                        } else {
                            orientation -= 0.017 * output[0] * 4;
                        }
                    }


                 /*   if (closestFood != null) {
                        extraPoints += (1.0 - normalizedFoodDistance) / 500.0;
                    }
                    if (closestAnimal != null) {
                        extraPoints += (1.0 - normalizedAnimalDistance) / 500.0;
                    }*/
                    int dx = (int) (CREATURE_SIZE / 10.0 * Math.cos(orientation));
                    int dy = (int) (CREATURE_SIZE / 10.0 * Math.sin(orientation));

                    x += dx * output[2] * 2;
                    y += dy * output[2] * 2;


                    //System.out.println("angle-orient: " + degrees + " angle: " + Math.atan2(foodY - creatureY, foodX - creatureX) + "  Orient: " + orientation + " Dist: " + normalizedFoodDistance + " Lsens:" + leftSensor + " Rsens: " + (rightSensor));

                }


                x = Math.max(4, x);
                x = Math.min(getWidth() - CREATURE_SIZE - 2, x);
                y = Math.max(4, y);
                y = Math.min(getHeight() - CREATURE_SIZE - 2, y);
            }
        }

        public java.awt.Rectangle getBounds() {
            return new java.awt.Rectangle(x, y, CREATURE_SIZE, CREATURE_SIZE);
        }


        public void incrementPoints() {
            pointsFood++;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }

        public double getExtraPoints() {
            return extraPoints;
        }

        public void setExtraPoints(double extraPoints) {
            this.extraPoints = extraPoints;
        }

        public void incrementAnimalPoints() {
            pointsAnimal++;
        }
    }
}