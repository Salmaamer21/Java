import java.io.IOException;


public class Test {
    public static void main(String[] args) throws IOException {
        /*
          Since this is a testing file,
          we're lazy about catching
          the potential IOException.
        */

      WAVHeader head = WAVHeader.readWAVHeaderFromFile("fmt1_chan1_sr48000_bit16.wav"); 
      System.out.println(head);
      head.getBytes(); //make sure getbytes has print statements

        /*
          First, we test our ability to
          read a WAVHeader from a file.
          This tests the constructor

            WAVHeader(byte[])

          that you wrote.
        */
        System.out.println(WAVHeader.readWAVHeaderFromFile("fmt1_chan1_sr44100_bit16.wav")); 
        System.out.println(WAVHeader.readWAVHeaderFromFile("fmt1_chan2_sr44100_bit16.wav"));
        System.out.println(WAVHeader.readWAVHeaderFromFile("fmt1_chan1_sr48000_bit16.wav"));
        System.out.println(WAVHeader.readWAVHeaderFromFile("fmt1_chan2_sr48000_bit16.wav"));
        System.out.println(WAVHeader.readWAVHeaderFromFile("fmt1_chan1_sr44100_bit24.wav"));
        System.out.println(WAVHeader.readWAVHeaderFromFile("fmt1_chan2_sr48000_bit24.wav"));
        System.out.println(WAVHeader.readWAVHeaderFromFile("fmt3_chan1_sr48000_bit32.wav"));
        System.out.println(WAVHeader.readWAVHeaderFromFile("fmt3_chan2_sr44100_bit32.wav"));
        System.out.println(WAVHeader.readWAVHeaderFromFile("euclid.wav"));
        System.out.println(WAVHeader.readWAVHeaderFromFile("not_WAV.wav"));




        /*
          Next, we test our ability to write a WAV file.
          This tests the method
 
            byte[] getBytes()

          that you wrote.

          In order to generate audio data that makes sense,
          one needs to think about digital signal processing.
          However, I've done this for you, so you don't have to.
          Again, I am happy to talk about this at length
          because it's the reason I started to code.
          This HW was essentially my first coding project!
        */

        final int SAMP = 44100; // CD quality sample rate.
        final int N = SAMP * 8; // The number of samples per channel
                                // for 8 seconds of audio.

        short[] left  = new short[N]; // An array for storing audio data.
        short[] right = new short[N]; // An array for storing audio data.

        /*
          We have 'left' store a 220 Hz sine wave.
          We have 'right' store a 440 Hz sine wave.
        */
        final double TAU = 2 * Math.PI; // TAU is often more useful than PI.
        final double  F1 = 220;         // A frequency.
        final double  F2 = 440;         // Another frequency.
        final double AMP = 24576;       // An amplitude.

        for (int i = 0; i < N; ++i) {
            left[i]  = (short) (AMP * Math.sin(TAU * F1 * i / SAMP));
            right[i] = (short) (AMP * Math.sin(TAU * F2 * i / SAMP));
        }

        /*
          We write the "left" data to a mono WAV file.
          We write the left and right data to a stereo file.
        */
        WAV.writeCompactDiskQualityWAVFile(    "220_m.wav", left);
        WAV.writeCompactDiskQualityWAVFile("220_440_s.wav", left, right);
    }
}