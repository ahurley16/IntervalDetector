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

    private FrequencyInput fg = new FrequencyInput();

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
