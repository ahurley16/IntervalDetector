/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intervaldetector;

import be.tarsos.dsp.io.TarsosDSPAudioFloatConverter;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.pitch.DynamicWavelet;
import java.io.IOException;
import java.util.ArrayList;
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

    public static final int SAMPLE_RATE = 32000;
    public static final int BITS_PER_SAMPLE = 8;
    public static final int CHANNELS = 1;
    public static final boolean SIGNED = true;
    public static final boolean BIG_ENDIAN = true;

    public static final int HERTZ = 20;
    public static final int DATA_READ = SAMPLE_RATE / HERTZ;

    TargetDataLine line;
    AudioFormat format;
    DataLine.Info info;
    TarsosDSPAudioFloatConverter tdsp;
    DynamicWavelet dw;
    byte[] data;
    float[] dataFloat;

    boolean isOpen;

    public FrequencyInput() {
        line = null;
        format = new AudioFormat(SAMPLE_RATE, BITS_PER_SAMPLE, CHANNELS, SIGNED, BIG_ENDIAN);
        info = new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
            System.err.println("Line not supported");

        }
// Obtain and open the line.
        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
        } catch (LineUnavailableException ex) {
            System.err.println("Line not available");
        }

        data = new byte[DATA_READ];
        dataFloat = new float[data.length];
        dw = new DynamicWavelet(SAMPLE_RATE, line.getBufferSize());
        tdsp = TarsosDSPAudioFloatConverter.getConverter(new TarsosDSPAudioFormat(SAMPLE_RATE, BITS_PER_SAMPLE, CHANNELS, SIGNED, BIG_ENDIAN));
        isOpen = false;
    }

    public void start() {
        line.start();

        isOpen = true;
    }

    public void stop() {
        line.stop();

        isOpen = false;

    }

    public ArrayList<Double> detectFrequencies(int minSamples, boolean debug) throws IOException {
        ArrayList<Double> freqs = new ArrayList<>();
        if (!isOpen) {
            throw new IOException();
        }
        line.read(data, 0, data.length);
        tdsp.toFloatArray(data, dataFloat);
        double baseline = dw.getPitch(dataFloat).getPitch();
        double total = 0;
        int counter = 1;
        int spaceCounter = 0;
        int newFrqCounter = 0;
        boolean ready = false;

        while (true) {
            line.read(data, 0, data.length);
            tdsp.toFloatArray(data, dataFloat);
            double tmpFrq = dw.getPitch(dataFloat).getPitch();
//            System.out.println("Base\t| Count\t| Total\t| Freq");
//            System.out.printf("%4.2f\t| %4d\t| %4.1f\t| %4.2f\n", baseline, counter, total, tmpFrq);
//            System.out.println("+------------------------------+");

            if (tmpFrq == -1.0) {
                spaceCounter++;
                if (spaceCounter > 10 && ready) {
                    return freqs;
                }
            } else if (tmpFrq < baseline * 1.03 && tmpFrq > baseline * 0.97) {
                newFrqCounter = 0;
                spaceCounter = 0;
                freqs.add(tmpFrq);
                if (counter <= minSamples) {
                    total += tmpFrq;
                    baseline = total / counter;
                    //System.out.println(baseline);
                    counter++;

                } else {
                    freqs.add(tmpFrq);
                    ready = true;
                }
            } else {
                newFrqCounter++;
                if (ready) {
                    return freqs;
                }
                freqs.clear();
                baseline = tmpFrq;
                total = 0;
                counter = 1;
            }
            if (debug) {
                System.out.println("Average\t| Count\t| Freq");
                System.out.printf("%4.2f\t| %4d\t| %4.2f\n", baseline, counter, tmpFrq);
                System.out.println("+-----------------------+");
            }
        }

        //   return total / minSamples;
    }

}
