/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intervaldetector;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Drew
 */
public class IntervalDetector {

    public enum Interval {
        UNISON(Math.pow(2, 0.0 / 12.0), "Unison"), MIN2(Math.pow(2, 1.0 / 12.0), "Minor 2nd"),
        MAJ2(Math.pow(2, 2.0 / 12.0), "Major 2nd"), MIN3(Math.pow(2, 3.0 / 12.0), "Minor 3rd"),
        MAJ3(Math.pow(2, 4.0 / 12.0), "Major 3rd"), P4(Math.pow(2, 5.0 / 12.0), "Perfect 4th"),
        AUG4(Math.pow(2, 6.0 / 12.0), "Tritone"), P5(Math.pow(2, 7.0 / 12.0), "Perfect 5th"),
        MIN6(Math.pow(2, 8.0 / 12.0), "Minor 6th"), MAJ6(Math.pow(2, 9.0 / 12.0), "Major 6th"),
        MIN7(Math.pow(2, 10.0 / 12.0), "Minor 7th"), MAJ7(Math.pow(2, 11.0 / 12.0), "Major 7th"),
        P8(Math.pow(2, 12.0 / 12.0), "Perfect Octave"), MIN9(Math.pow(2, 13.0 / 12.0), "Minor 9th"),
        MAJ9(Math.pow(2, 14.0 / 12.0), "Major 9th"), MIN10(Math.pow(2, 15.0 / 12.0), "Minor 10th"),
        MAJ10(Math.pow(2, 16.0 / 12.0), "Major 10th"), P11(Math.pow(2, 17.0 / 12.0), "Perfect 11th"),
        AUG11(Math.pow(2, 18.0 / 12.0), "Compound Tritone"), P12(Math.pow(2, 19.0 / 12.0), "Perfect 12th"),
        MIN13(Math.pow(2, 20.0 / 12.0), "Minor 13th"), MAJ13(Math.pow(2, 21.0 / 12.0), "Major 13th"),
        MIN14(Math.pow(2, 22.0 / 12.0), "Minor 14th"), MAJ14(Math.pow(2, 23.0 / 12.0), "Major 14th"),
        P15(Math.pow(2, 24 / 12.0), "Double Octave");

        private final double ratio;
        private final String name;

        Interval(double ratio, String name) {
            this.ratio = ratio;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public double getRatio() {
            return ratio;
        }
    }

    boolean debug = false;
    boolean manualMode = false;
    Scanner scan = new Scanner(System.in);

    public void detectInterval() throws IOException, InterruptedException {
        System.out.println("Welcome to Interval Detector!");
        FrequencyInput fg = new FrequencyInput();
        DecimalFormat df = new DecimalFormat("0.000");
        char input = 'a';

        System.out.println("\nEnter 'q' to quit or 'm' for menu or anything else to start detecting...");
        input = scan.next().toLowerCase().charAt(0);
        while (input != 'q') {

            if (input == 'm') {
                menuLoop();
            }

            fg.start();
            double freq1 = 0, freq2 = 0;
            ArrayList<Double> freqs1 = null, freqs2 = null;

            System.out.println("Please sing first pitch:");
            if (!manualMode) {
                freqs1 = fg.detectFrequencies(3, debug);
                System.out.println("Found first pitch.");
            } else {
                freq1 = scan.nextDouble();
                System.out.println("You entered: " + df.format(freq1));
            }

            System.out.println("Please sing second pitch:");
            if (!manualMode) {
                freqs2 = fg.detectFrequencies(3, debug);
                System.out.println("Found second pitch.");
            } else {
                freq2 = scan.nextDouble();
                System.out.println("You entered: " + df.format(freq2));
            }

            if (!manualMode) {
                removeOutliers(freqs1);
                removeOutliers(freqs2);

                double total = 0;
                for (double d : freqs1) {
                    total += d;
                }
                freq1 = total / freqs1.size();
                total = 0;
                for (double d : freqs2) {
                    total += d;
                }
                freq2 = total / freqs2.size();

            }

            System.out.println();
            fg.stop();

            double largerFreq = Math.max(freq2, freq1);
            double smallerFreq = Math.min(freq2, freq1);

            System.out.println("Larger frequency: " + df.format(largerFreq));
            System.out.println("Smaller frequency: " + df.format(smallerFreq));

            double ratio = largerFreq / smallerFreq;
            System.out.println("Ratio: " + df.format(ratio));

            Interval i = findClosestInterval(ratio);

            System.out.print("\nIt seems the closest interval is a " + i.getName());
            System.out.println(" with a ratio of " + df.format(i.getRatio()));

            System.out.println("\nEnter 'q' to quit or 'm' for menu or anything else to detect again...");
            input = scan.next().toLowerCase().charAt(0);

        }

        System.out.println("Thank you! Goodbye.");
    }

    private void menuLoop() {
        char input = 'm';
        while (input != 'c') {
            System.out.println("~~Menu~~");
            System.out.println("Debug mode: " + debug + " (Enter 'd' to toggle)");
            System.out.println("Enter manually: " + manualMode + " (Enter 'e' to toggle)");
            System.out.println("Enter 'c' to close menu...");
            input = scan.next().toLowerCase().charAt(0);
            if (input == 'd') {
                debug = !debug;
            } else if (input == 'e') {
                manualMode = !manualMode;
            }
        }
    }

    private ArrayList<Double> removeOutliers(ArrayList<Double> freqs) {
        double stdDev = 0;
        double total = 0;
        double avg = 0;
        for (double i : freqs) {
            total += i;
        }
        avg = total / freqs.size();
        if (debug) {
            System.out.print("Average: " + avg);
        }
        for (double i : freqs) {
            total = Math.pow(i - avg, 2);
        }
        stdDev = total / freqs.size();
        stdDev = Math.sqrt(stdDev);
        if (debug) {
            System.out.print("Standard Deviation: " + stdDev);
        }
        for (int i = 0; i < freqs.size(); i++) {
            if (freqs.get(i) > avg + stdDev || freqs.get(i) < avg - stdDev) {
                freqs.remove(i);
            }
        }

        return freqs;
    }

    public Interval findClosestInterval(double inputRatio) {
        Interval closest = null;
        double difference = Double.MAX_VALUE;

        for (Interval i : Interval.values()) {
            double tempDifference = Math.abs(i.getRatio() - inputRatio);
            if (tempDifference < difference) {
                difference = tempDifference;
                closest = i;
            }
        }

        return closest;
    }
}
