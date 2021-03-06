/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intervaldetector;

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
