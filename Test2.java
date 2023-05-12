import java.io.IOException;
import java.io.FileInputStream;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;


//what edit was made? 



public class Test2 {
    public static void main(String[] args) throws IOException {
        // Store euclid.wav's header. This relies on your WAVHeader(byte[]).
        WAVHeader euclid = WAVHeader.readWAVHeaderFromFile("euclid.wav");


        // Store euclid.wav's bytes.
        FileInputStream in = new FileInputStream("euclid.wav");

        byte[]  bytes = new byte[euclid.getHeaderSize() + euclid.getDataSize()];
        in.read(bytes);

        in.close();




        // Open a file for writing.
        OutputStream out = new FileOutputStream("euclid_copy.wav");
        out = new BufferedOutputStream(out);  // This line creates a massive speedup!


        // Copy the header by using your getBytes method.
        // This will almost certainly write the incorrect junk.
        out.write(euclid.getBytes());


        // Copy the bytes after the header.
        for (int i = euclid.getHeaderSize(); i < bytes.length; ++i) {
            out.write(bytes[i]);
        }


        // Close the file.
        out.close();


        // Obtain the header written using your getBytes method.
        WAVHeader euclid_copy = WAVHeader.readWAVHeaderFromFile("euclid_copy.wav");


        // Check that the WAVHeaders agree,
        // i.e. that the headers agree except for the exact junk they store.
        System.out.println(euclid.toString().equals(euclid_copy.toString()));
   }
}