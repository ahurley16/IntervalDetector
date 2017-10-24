/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intervaldetector;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Scanner;

/**
 *
 * @author Drew
 */
public class UserInterface {
    
    private DecimalFormat df = new DecimalFormat("0.000");
    private Scanner scan = new Scanner(System.in);

    public void start() throws IOException, InterruptedException {
        boolean debug = false;
        boolean manualMode = false;
        System.out.println("Welcome to Interval Detector!");
        
        
        char input = 'a';

        System.out.println("\nEnter 'q' to quit or 'm' for menu or anything else to start detecting...");
        input = scan.next().toLowerCase().charAt(0);
        while (input != 'q') {

            if (input == 'm') {
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

            fg.start();
            double freq1, freq2;

            System.out.println("Please sing first pitch:");
            if (!manualMode) {
                freq1 = fg.detectFrequency(3, debug);
            } else {
                freq1 = scan.nextDouble();
            }
            System.out.println("You sang: " + df.format(freq1));

            System.out.println("Please sing second pitch:");
            if (!manualMode) {
                freq2 = fg.detectFrequency(3, debug);
            } else {
                freq2 = scan.nextDouble();
            }
            System.out.println("You sang: " + df.format(freq2));

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
}
