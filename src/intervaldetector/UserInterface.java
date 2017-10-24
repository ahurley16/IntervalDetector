/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intervaldetector;

import java.text.DecimalFormat;
import java.util.Scanner;

/**
 *
 * @author Drew
 */
public class UserInterface {

    private final DecimalFormat df = new DecimalFormat("0.000");
    private final Scanner scan = new Scanner(System.in);

    private final FrequencyInput fi = new FrequencyInput();
    private final IntervalDetector detector = new IntervalDetector();

    private boolean manualMode;
    private boolean debugMode;

    public void menuLoop() {
        System.out.println("Welcome to Interval Detector!");

        char input;

        do {
            System.out.println("+----------------------------------------------+");
            System.out.println("|                     Menu                     |");
            System.out.println("+----------------------------------------------+");
            System.out.println("| (D): Detect - Start interval detection       |");
            System.out.println("| (L): List - List all supported intervals     |");
            System.out.println("| (S): Settings - Settings menu                |");
            System.out.println("| (Q): Quit - Exit application                 |");
            System.out.println("+----------------------------------------------+");
            input = scan.next().toLowerCase().charAt(0);

            switch (input) {
                case 's':
                    settingsLoop();
                    break;
                case 'l':
                    printIntervals();
                    break;
                case 'd':
                    detectInterval();
                    break;
                default:
                    break;
            }

        } while (input != 'q');

        System.out.println("Thank you! Goodbye.");
    }

    private void settingsLoop() {
        char input = 's';
        while (input != 'c') {
            System.out.println("+----------------------------------------------+");
            System.out.println("|                  Settings                    |");
            System.out.println("+----------------------------------------------+");
            if (debugMode) {
                System.out.println("| (D): Debug (True) - Toggle debug mode        |");
            } else {
                System.out.println("| (D): Debug (False) - Toggle debug mode       |");
            }
            if (manualMode) {
                System.out.println("| (M): Manual (True) - Toggle manual mode      |");
            } else {
                System.out.println("| (M): Manual (False) - Toggle manual mode     |");
            }
            System.out.println("| (C): Close - Close settings menu             |");
            System.out.println("+----------------------------------------------+");
            input = scan.next().toLowerCase().charAt(0);
            if (input == 'd') {
                debugMode = !debugMode;
            } else if (input == 'm') {
                manualMode = !manualMode;
            }
        }
    }

    private void detectInterval() {

        double freq1, freq2;

        System.out.println("Please sing first pitch:");
        if (manualMode) {
            freq1 = scan.nextDouble();
        } else {
            freq1 = fi.detectFrequency(3, debugMode);
        }
        System.out.println("You sang: " + df.format(freq1));

        System.out.println("Please sing second pitch:");
        if (manualMode) {
            freq2 = scan.nextDouble();
        } else {
            freq2 = fi.detectFrequency(3, debugMode);
        }
        System.out.println("You sang: " + df.format(freq2));
        System.out.println();

        fi.stop();

        double largerFreq = Math.max(freq2, freq1);
        double smallerFreq = Math.min(freq2, freq1);

        System.out.println("Larger frequency: " + df.format(largerFreq));
        System.out.println("Smaller frequency: " + df.format(smallerFreq));

        double ratio = largerFreq / smallerFreq;
        System.out.println("Ratio: " + df.format(ratio));

        IntervalDetector.Interval i = detector.findClosestInterval(ratio);

        System.out.print("\nIt seems the closest interval is a " + i.getName());
        System.out.println(" with a ratio of " + df.format(i.getRatio()));
    }

    private void printIntervals() {
        IntervalDetector.Interval[] intervals = IntervalDetector.Interval.values();
        int halfSize = (int) Math.floor(intervals.length / 2.0);
        System.out.println(intervals[0].getName());
        for (int i = 1; i <= halfSize; i++) {
            System.out.printf("%-32s", intervals[i].getName());
            System.out.println(intervals[i + halfSize].getName());

        }
    }
}
