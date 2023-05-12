import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


/*
  This class allows the writing of mono and stereo CD-quality WAV files.
*/
public class WAV {
    /**
     * This method allows the writing of a mono WAV file.
     * 
     * @param name the path to the WAV file that is written
     * @param mono an array of shorts storing audio data
    */
    public static void writeCompactDiskQualityWAVFile(String name, short[] mono) {
        /*
          We create an appropriate header and store its bytes.
        */
        WAVHeader   header = WAVHeader.makeWAVHeaderForCompactDiskQualityAudio(mono); //what does this do??
        byte[] headerBytes = header.getBytes(); // ok ... 

        /*
          We create an array of bytes for storing the audio data.
          We wrap it in a ByteBuffer so that we can conveniently
          convert shorts to two bytes using little endian.
        */
        byte[]   dataBytes = new byte[header.getDataSize()];
        ByteBuffer    data = ByteBuffer.wrap(dataBytes);
        data.order(ByteOrder.LITTLE_ENDIAN);

        /*
          We add the audio data to the array of bytes
          by using the ByteBuffer.
        */
        for (int i = 0; i < mono.length; ++i) {
            data.putShort(mono[i]);
        }

        /*
          We now have two arrays of bytes.
          We write them to the specified file.
        */
        write(name, headerBytes, dataBytes);
    }


    /**
     * This method allows the writing of a stereo WAV file.
     * 
     * @param name the path to the WAV file that is written
     * @param left an array of shorts storing audio data
     * @param right an array of shorts storing audio data
    */
    public static void writeCompactDiskQualityWAVFile(String name, short[] left, short[] right) {
        /*
          We create an appropriate header and store its bytes.
        */
        WAVHeader   header = WAVHeader.makeWAVHeaderForCompactDiskQualityAudio(left, right);
        byte[] headerBytes = header.getBytes();

        /*
          We create an array of bytes for storing the audio data.
          We wrap it in a ByteBuffer so that we can conveniently
          convert shorts to two bytes using little endian.
        */
        byte[]   dataBytes = new byte[header.getDataSize()];
        ByteBuffer    data = ByteBuffer.wrap(dataBytes);
        data.order(ByteOrder.LITTLE_ENDIAN);

        /*
          We add the audio data to the array of bytes
          by using the ByteBuffer.
        */
        for (int i = 0; i < left.length; ++i) {
            data.putShort(left[i]);
            data.putShort(right[i]);
        }

        /*
          We now have two arrays of bytes.
          We write them to the specified file.
        */
        write(name, headerBytes, dataBytes);
    }


    /**
     * This method writes two arrays of bytes to a specified file.
     * 
     * @param name the path to the file that is written
     * @param header an array of bytes obtained from a WAVHeader
     * @param data an array of bytes storing audio data
    */
    private static void write(String name, byte[] header, byte[] data) {
        FileOutputStream fout = null;

        try {
            fout = new FileOutputStream(name);

            fout.write(header);
            fout.write(data);
        }
        catch (FileNotFoundException e) {
            System.out.println("File cannot be opened");
        }
        catch (IOException e) {
            System.out.println("Writing to file failed");
        }
        finally {
            if (fout != null) {
                try {
                    fout.close();
                }
                catch (IOException e) {
                    System.out.println("Closing file stream failed");
                }
            }
        }
    }
}