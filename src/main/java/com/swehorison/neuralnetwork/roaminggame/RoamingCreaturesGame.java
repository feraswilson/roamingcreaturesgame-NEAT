package com.swehorison.neuralnetwork.roaminggame;

import com.swehorison.neuralnetwork.genetic.GANN;
import com.swehorison.neuralnetwork.neuralnetwork.NeuralNetwork;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.SplittableRandom;

public class RoamingCreaturesGame extends JPanel {
    private static final long serialVersionUID = 1L;

   static int slowDownInGeneration = 20;
    private static final int CREATURE_SIZE = 20;
    private static final int NUM_CREATURES =200;

    private static final int NUM_CREATURES_AFTER_RENDER = 10;
    private static int NUM_FOOD = 100;
    private final NeuralNetwork neuralNetwork;
    private final GANN gann;

    private ArrayList<Creature> creatures;
    private ArrayList<Point> food;
    private int TIMER = 500;

    private SplittableRandom random = new SplittableRandom();


    public RoamingCreaturesGame() {
        setPreferredSize(new Dimension(1920, 1080));
        setBackground(Color.WHITE);


        neuralNetwork = new NeuralNetwork(3, 2, 2, 3);
        gann = new GANN(neuralNetwork, 3000000, 0.9, 0.05);
        gann.initiatePopulation(Math.max(NUM_CREATURES, NUM_CREATURES_AFTER_RENDER), individual -> 0.0);

        createNewCreaturesAndFood(NUM_CREATURES);
    }

    private void createNewCreaturesAndFood(int amount) {
        creatures = new ArrayList<>();
        food = new ArrayList<>();

        for(int i = 0; i < gann.getCurrentPopulation().size(); i++) {
            //gann.getCurrentPopulation().getIndividual(i).setFitness(0.0);
        }

        Random random = new Random();
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


        g.setColor(Color.RED);

        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Generation: " + gann.getCurrentGeneration(), 10, 30);

        g.setFont(new Font("Arial", Font.PLAIN, 14));
        for (Creature c : creatures) {
            c.draw(g);
        }

        g.setColor(Color.GREEN);
        for (Point p : food) {
            g.fillOval(p.x, p.y, CREATURE_SIZE, CREATURE_SIZE);
        }
    }

    public void play() {

        int timer = TIMER;

        while (true) {
            boolean dead = true;
            int x = 0;

            for (Creature c : creatures) {

                if(c.hp <= 0) {
                    c.setVisible(false);
                }
                else {
                    c.move(gann.getCurrentPopulation().getIndividual(x).getGenes(), x);

                    for (int i = 0; i < food.size(); i++) {
                        Point p = food.get(i);
                        if (c.x <= p.x + CREATURE_SIZE && c.x + CREATURE_SIZE >= p.x &&
                                c.y <= p.y + CREATURE_SIZE && c.y + CREATURE_SIZE >= p.y) {
                            c.incrementPoints();
                            //gann.getCurrentPopulation().getIndividual(x).setFitness(gann.getCurrentPopulation().getIndividual(x).getFitness() + 1);

                            if(c.hp < 400) {
                                c.hp += 150;
                            }
                            else {
                                c.hp = 400;
                            }

                            food.remove(i);

                            int foodX = random.nextInt(1920);
                            int foodY = random.nextInt(1080);
                            food.add(new Point(foodX, foodY));
                            i--;
                        }
                    }
                }
                c.hp--;
                x++;


            }

            for(int i = 0; i < creatures.size(); i++) {
                 if(creatures.get(i).hp > 0) {
                    dead = false;
                    break;
                }
            }

            if(food.isEmpty() || dead) {



                int i = 0;
                for (Creature c : creatures) {
                    gann.getCurrentPopulation().getIndividual(i).setFitness(c.points + 0.0 + c.extraPoints);
                    i++;
                }
                gann.trainOneGeneration();



                if(gann.getCurrentGeneration() > slowDownInGeneration) {
                    createNewCreaturesAndFood(NUM_CREATURES_AFTER_RENDER);
                }
                else {
                    createNewCreaturesAndFood(NUM_CREATURES);
                }
                timer = 160;

             //   if(gann.getCurrentGeneration() % 10 == 0) {
                    System.out.println("Current generation: " + gann.getCurrentGeneration() + " " + gann.getBestFitness());
              //  }

                dead = true;


                //System.out.println();
            }

            timer--;

            if(gann.getCurrentGeneration() > slowDownInGeneration) {
                repaint();
            }
            try {
                if(gann.getCurrentGeneration() > slowDownInGeneration) {
                    Thread.sleep(50);
                }else {
                 //   Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Roaming Creatures Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        RoamingCreaturesGame game = new RoamingCreaturesGame();
        frame.add(game);
        frame.pack();
        frame.setVisible(true);

        game.play();
    }

    class Creature {
        public int hp = 400;
        private int x, y, points;
        private boolean leftSensor, rightSensor;
        private double angle, orientation, extraPoints = 0.0;
        private boolean visible =true;

        public Creature(int x, int y) {
            this.x = x;
            this.y = y;
            Random random = new Random();
            int dx = random.nextInt(3) - 1;
            int dy = random.nextInt(3) - 1;
            this.x += dx * CREATURE_SIZE;
            this.y += dy * CREATURE_SIZE;
            this.points = 0;
            this.angle = random.nextDouble() * 360;
            this.orientation = (random.nextDouble() * 6.28) - 3.14;
        }

        public void draw(Graphics g) {
            if(visible) {
                int sensorLength = CREATURE_SIZE / 2;
                int leftSensorTipX = (int) (x + CREATURE_SIZE / 2 + Math.cos(orientation - Math.PI / 4) * sensorLength);
                int leftSensorTipY = (int) (y + CREATURE_SIZE / 2 + Math.sin(orientation - Math.PI / 4) * sensorLength);
                int rightSensorTipX = (int) (x + CREATURE_SIZE / 2 + Math.cos(orientation + Math.PI / 4) * sensorLength);
                int rightSensorTipY = (int) (y + CREATURE_SIZE / 2 + Math.sin(orientation + Math.PI / 4) * sensorLength);

                g.setColor(Color.BLUE);
                g.fillOval(x, y, CREATURE_SIZE, CREATURE_SIZE);
                g.setColor(Color.RED);
                g.fillOval(x + CREATURE_SIZE / 4, y + CREATURE_SIZE / 4, CREATURE_SIZE / 2, CREATURE_SIZE / 2);
                g.drawLine(x + CREATURE_SIZE / 2, y + CREATURE_SIZE / 2, leftSensorTipX, leftSensorTipY);
                g.drawLine(x + CREATURE_SIZE / 2, y + CREATURE_SIZE / 2, rightSensorTipX, rightSensorTipY);
                // Add angle number
                g.drawString(String.format("%.0f", Math.toDegrees(orientation) % 360), x + 30, y + 30);

                if (leftSensor) {
                    g.setColor(Color.YELLOW);
                    g.fillOval(leftSensorTipX - CREATURE_SIZE / 8, leftSensorTipY - CREATURE_SIZE / 8, CREATURE_SIZE / 4, CREATURE_SIZE / 4);
                }
                else {
                    g.setColor(Color.BLUE);
                    g.fillOval(leftSensorTipX - CREATURE_SIZE / 8, leftSensorTipY - CREATURE_SIZE / 8, CREATURE_SIZE / 4, CREATURE_SIZE / 4);
                }
                if (rightSensor) {
                    g.setColor(Color.YELLOW);
                    g.fillOval(rightSensorTipX - CREATURE_SIZE / 8, rightSensorTipY - CREATURE_SIZE / 8, CREATURE_SIZE / 4, CREATURE_SIZE / 4);
                }
                else {
                    g.setColor(Color.BLUE);
                    g.fillOval(rightSensorTipX - CREATURE_SIZE / 8, rightSensorTipY - CREATURE_SIZE / 8, CREATURE_SIZE / 4, CREATURE_SIZE / 4);
                }

                g.setColor(Color.RED);
                g.drawString("Points: " + points, x, y + 40);
                g.drawString("Left: " + (leftSensor ? "active" : "inactive"), x, y - 20);
                g.drawString("Right: " + (rightSensor ? "active" : "inactive"), x, y - 10);
            }
        }

        public void move(double[] weights, int positionInGeneration) {
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
            // closestFoodDistance, currentX, currentY, angle, leftSensor, rightSensor
            if (closestFood != null) {
                int foodX = closestFood.x + CREATURE_SIZE / 2;
                int foodY = closestFood.y + CREATURE_SIZE / 2;
                int creatureX = x + CREATURE_SIZE / 2;
                int creatureY = y + CREATURE_SIZE / 2;

                double normalizedOrientation = ((orientation + Math.PI) / 6.28);
                //  double normalizedOrientation = ((orientation / 360) * 6.28) - 3.14;


                angle = Math.atan2(foodY - creatureY, foodX - creatureX) - orientation;


                double normalizedAngle = (angle + Math.PI) / (2 * Math.PI);;

                // System.out.println(orientation + " " + normalizedOrientation);
                double normalizedPosX = (x) / (1000.0);
                double normalizedPosY = (y) / (1000.0);
                double normalizedFoodDistance = (closestFoodDistance) / (1000.0);

                double degrees = Math.toDegrees(angle);
                if(degrees < -360) {
                    degrees += 360;
                }
                else if(degrees > 360) {
                    degrees =- 360;
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

                double[] output = gann.run(weights, new double[]{normalizedFoodDistance, rightSensor ? 1.0 : 0.0, leftSensor ? 1.0 : 0.0});

                if(output[0] == output[1]) {
                    // Do nothing
                }
                else if(output[0] < output[1]) {
                    if(orientation >= Math.PI) {
                        orientation = -Math.PI + (0.017 * output[1]*4);
                    }
                    else {
                        orientation += 0.017  * output[1]*4;
                    }
                }
                else {
                    if(orientation <= -Math.PI) {
                        orientation = Math.PI - (0.017 * output[0]*4);
                    }
                    else {
                        orientation -= 0.017 * output[0]*4;
                    }
                }

              //  System.out.println(positionInGeneration + " HP: " + hp/200.0 + " Food: " + food.size() / (NUM_FOOD + 0.0));

                extraPoints += (1.0 - normalizedFoodDistance)/500.0;
               // gann.getCurrentPopulation().getIndividual(positionInGeneration).setFitness(gann.getCurrentPopulation().getIndividual(positionInGeneration).getFitness() + (1.0 - normalizedFoodDistance));
               // gann.getCurrentPopulation().getIndividual(positionInGeneration).setFitness(gann.getCurrentPopulation().getIndividual(positionInGeneration).getFitness() + (1.0 - normalizedAngle) * 20);
                int dx = (int) (CREATURE_SIZE/10.0 * Math.cos(orientation));
                int dy = (int) (CREATURE_SIZE/10.0 * Math.sin(orientation));
                if(output[2] > 0.9) {
                    x += dx * 2;
                    y += dy * 2;
                } else if(output[2] > 0.5 && output[2] < 0.9) {
                    x += dx;
                    y += dy;
                }


                //System.out.println("angle-orient: " + degrees + " angle: " + Math.atan2(foodY - creatureY, foodX - creatureX) + "  Orient: " + orientation + " Dist: " + normalizedFoodDistance + " Lsens:" + leftSensor + " Rsens: " + (rightSensor));

            }


           // x = Math.max(2, x);
            x = Math.min(getWidth() - CREATURE_SIZE - 2, x);
           // y = Math.max(2, y);
            y = Math.min(getHeight() - CREATURE_SIZE - 2, y);
        }

        public java.awt.Rectangle getBounds() {
            return new java.awt.Rectangle(x, y, CREATURE_SIZE, CREATURE_SIZE);
        }


        public void incrementPoints() {
            points++;
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
    }
}