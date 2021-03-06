/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intervaldetector;

import be.tarsos.dsp.io.TarsosDSPAudioFloatConverter;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.pitch.DynamicWavelet;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author Drew
 */
public class FrequencyInput {

    public final int SAMPLE_RATE = 32000;
    public final int BITS_PER_SAMPLE = 8;
    public final int CHANNELS = 1;
    public final boolean SIGNED = true;
    public final boolean BIG_ENDIAN = true;

    public final int FRACTION = 20;
    public final int DATA_READ = SAMPLE_RATE / FRACTION;

    TargetDataLine line;
    AudioFormat format;
    DataLine.Info info;
    TarsosDSPAudioFloatConverter tdsp;
    DynamicWavelet dw;
    byte[] data;
    float[] dataFloat;

    public FrequencyInput() {
        line = null;
        format = new AudioFormat(SAMPLE_RATE, BITS_PER_SAMPLE, CHANNELS, SIGNED, BIG_ENDIAN);
        info = new DataLine.Info(TargetDataLine.class, format);

        // Obtain and open the line.
        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
        } catch (LineUnavailableException e) {
            System.err.println("Error getting mic input: " + e.getMessage());
        }
        data = new byte[DATA_READ];
        dataFloat = new float[data.length];
        dw = new DynamicWavelet(SAMPLE_RATE, line.getBufferSize());
        tdsp = TarsosDSPAudioFloatConverter.getConverter(new TarsosDSPAudioFormat(SAMPLE_RATE, BITS_PER_SAMPLE, CHANNELS, SIGNED, BIG_ENDIAN));
    }

    public void start() {
        line.start();
    }

    public void stop() {
        line.stop();
    }

    public double detectFrequency(int samples, boolean debug) {
        line.start();
        double baseline = 0;
        double total = 0;
        int counter = 0;
        int spaceCounter = 0;
        int newFrqCounter = 0;
        boolean ready = false;

        while (true) {
            line.read(data, 0, data.length);
            tdsp.toFloatArray(data, dataFloat);
            double tmpFrq = dw.getPitch(dataFloat).getPitch();

            //If no frequency detected then count up a space. If a frequency is
            //already detected and there are 7 spaces in a row with no pitch
            //in beteween then return.
            if (tmpFrq == -1.0) {
                spaceCounter++;
                if (spaceCounter > 7 && ready) {
                    return total / samples;
                }
                //If the pitch is within 3% of the current baseline, reset the space
                //counter. If we don't have enough samples, then add the current sample
                //to the running average. Once enough samples has been reached this
                //method doesn't do anything but reset space counter and set ready.
            } else if (tmpFrq < baseline * 1.03 && tmpFrq > baseline * 0.97) {
                newFrqCounter = 0;
                spaceCounter = 0;
                if (counter < samples) {
                    total += tmpFrq;
                    counter++;
                    baseline = total / counter;

                } else {
                    ready = true;
                }
                //If the pitch was detected but it wasn't within the 3% margin then 
                //return if it's ready. Otherwise if it hasn't had enough samples,
                //reset the baseline and counter because it will start to find a new pitch.
            } else {
                newFrqCounter++;
                if (newFrqCounter > 1 && ready) {
                    return total / samples;
                } else if (!ready) {
                    baseline = tmpFrq;
                    total = 0;
                    counter = 0;
                }
            }

            //Debugger
            if (debug) {
                System.out.println("Average\t| Count\t| Freq");
                System.out.printf("%4.2f\t| %4d\t| %4.1f\t| %d%n", baseline, counter, tmpFrq, newFrqCounter);
                System.out.println("+-----------------------+");
            }
        }
    }

}
