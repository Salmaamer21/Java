import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;




/*
  This class is written to aid the processing of
  the headers that appear in WAV files.

  With the exception of 'junk'
  (which indicates whether or not there's a JUNK chunk)
  the instance fields of this class all correspond to
  information that appears in the headers of WAV files.

  There are factory methods which...
   - make headers for mono, CD-quality WAV files;
   - make headers for stereo, CD-quality WAV files;
   - read headers from pre-existing WAV files.

  There's a convenient 'toString' method which
  displays all relevant information stored by a header.

  There are 'getHeaderSize' and 'getDataSize' methods,
  and there is a convenient 'getBytes' method.
*/




public class WAVHeader {
    /*
      With the exception of 'junk'
      (which indicates whether or not there's a JUNK chunk)
      the instance fields of this class all correspond to
      information that appears in the headers of WAV files.

      A WAV file belongs to a more general class of files
      called RIFF files. The header of a WAV file indicates
      that it's a RIFF file and that it's a WAV file. It
      provides information about the formatting of the audio
      data, and finally, it indicates how much data there is.


      ENDIANNESS

        All shorts and ints are encoding using little endian.


      RIFF CHUNK

        To indicate that the file is a RIFF file, there are
        4 bytes storing the characters 'R', 'I', 'F', 'F'.

        The next 4 bytes encode an int which says how many
        bytes are stored in the file after this int. This is
        really saying that everything belongs to this chunk.


      WAVE HEADER

        To indicate that the file is a WAV file, there are
        4 bytes storing the characters 'W', 'A', 'V', 'E'.


      INFO CHUNK

        The specification for RIFF files includes the
        definition of an INFO chunk, but the chunk was not
        referenced in the formal specification of a WAV file.
        This has led to many readers processing INFO chunks
        incorrectly, which in turn has led to many WAV files
        not bothering with an INFO chunk. In this class,
        we'll pretend that INFO chunks have never existed.


      JUNK CHUNK

        Sometimes it can be important to align RIFF chunks to
        certain boundaries, for example, 2048 bytes for CD-ROMs.
        For this reason, some WAV files include a JUNK chunk.

        We have an instance field called 'junk' to store whether
        or not there's a JUNK chunk. When there is a JUNK chunk,
        it has a header which uses 4 bytes to store the characters
        'J', 'U', 'N', 'K'. The next 4 bytes encode an int which
        says how many bytes of junk there are. After that, there's
        the actual junk which we do not bother to store: its bytes
        are normally random.


      FORMAT CHUNK

        The format chunk contains information about the formatting
        of the audio data, one of the most important chunks!

        It starts with 4 bytes that store the characters
        'f', 'm', 't', ' '.

        Next, the int 16 is stored. This value records the fact that
        the format information will require 16 bytes to store. Those
        16 bytes consist of...

          2 bytes - the "audio format" (normally 1 or 3)
          2 bytes - the number of channels: 1 for mono, 2 for stereo
          4 bytes - the number of samples per second per channel of audio
          4 bytes - the number of bytes per second of audio
          2 bytes - the number of channels * the number of bytes per sample
          2 bytes - the number of bits per sample


      DATA CHUNK

        The data chunk contains the audio data,
        the most important chunk!

        It starts with 4 bytes that store the characters
        'd', 'a', 't', 'a'.

        Next, an int stores how many bytes of audio data there are.

        After that, there will be audio data.
        This class does not store this information.


      OTHER CHUNKS

        After the audio data, there could be other chunks.
        This class does not consider these chunks.
    */
    private char[]  riff_header;
    private int     riff_size;

    private char[]  wave_header;

    private boolean junk;
    private char[]  junk_header;
    private int     junk_size;

    private char[]  fmt_header;
    private int     fmt_size;
    private short   audio_fmt;
    private short   channels;
    private int     sample_rate_per_chan;
    private int     bytes_per_sec;
    private short   block_size_in_bytes;
    private short   bit_depth_of_sample;

    private char[]  data_header;
    private int     data_size;




    /*
      It is convenient to use an initializer block to initialize
      the fields which will often have the same value.

      'junk' may not always be false and 'junk_size' may not always
      be 0, but our constructors can edit these values if necessary.
    */
    {
        riff_header = new char[] { 'R', 'I', 'F', 'F' };
        wave_header = new char[] { 'W', 'A', 'V', 'E' };
        junk_header = new char[] { 'J', 'U', 'N', 'K' };
         fmt_header = new char[] { 'f', 'm', 't', ' ' };
        data_header = new char[] { 'd', 'a', 't', 'a' };

        junk      = false;
        junk_size = 0;
        fmt_size = 16;
    }




    /**
     * This constructor performs some relevant calculations
     * that Michael Andrews does not wish for every PIC 20A student
     * to have to think about carefully. Michael is happy to talk
     * about these calculations at length. Audio Processing and
     * Digital Signal Processing are some of his favorite things!
     * 
     * @param audio_fmt             1 for integer samples, 3 for floating point samples
     * @param channels              1 for mono, 2 for stereo
     * @param sample_rate_per_chan  often 44_100 or 48_000
     * @param bit_depth_of_sample   16, 24, or 32
     * @param samples_per_channel   depends on the audio file length
    */
    public WAVHeader(int audio_fmt,
                     int channels,
                     int sample_rate_per_chan,
                     int bit_depth_of_sample,
                     int samples_per_channel) {

        this.audio_fmt            = (short) audio_fmt;
        this.channels             = (short) channels;
        this.sample_rate_per_chan = sample_rate_per_chan;
        this.bit_depth_of_sample  = (short) bit_depth_of_sample;
        this.block_size_in_bytes  = (short) (channels * (bit_depth_of_sample / 8));

        this.bytes_per_sec        = this.block_size_in_bytes * sample_rate_per_chan;
        this.data_size            = this.block_size_in_bytes * samples_per_channel;
        this.riff_size            = this.getHeaderSize() - 8 + this.getDataSize();
    }




    /**
     * This factory method creates a WAVHeader
     * for compact disk quality mono audio
     * using a single array of shorts.
     * 
     * @param mono an array of shorts containing audio data
     * @return a WAVHeader compatible with the audio data provided
    */
    public static WAVHeader makeWAVHeaderForCompactDiskQualityAudio(short[] mono) {
        return new WAVHeader(1, 1, 44100, 16, mono.length);
    }

    /**
     * This factory method creates a WAVHeader
     * for compact disk quality stereo audio
     * using two arrays of shorts.
     * 
     * @param left an array of shorts containing audio data
     * @param right an array of shorts containing audio data
     * @return a WAVHeader compatible with the audio data provided
    */
    public static WAVHeader makeWAVHeaderForCompactDiskQualityAudio(short[] left, short[] right) {
        if (left.length != right.length) {
            throw new WAVStereoException();
        }
        return new WAVHeader(1, 2, 44100, 16, left.length);
    }




    /**
     * This factory method reads a header from a pre-existing WAV file.
     * 
     * @param name the path to the WAV file to read
     * @return a WAVHeader storing the header read from 'name'
     *
    */
    public static WAVHeader readWAVHeaderFromFile(String name) throws IOException {
        InputStream in = null;
        byte[]   bytes = new byte[4096]; // A WAVHeader should always
                                         // be fewer than 4096 bytes.
        try {
            in = new FileInputStream(name);
            in.read(bytes);
        }
        catch (FileNotFoundException e) {
            System.out.println("File called " + name + " not found");
            throw e; // We won't have the necessary information to create a WAVHeader.
        }
        catch (IOException e) {
            System.out.println("Reading from file called " + name + " failed");
            throw e; // We won't have the necessary information to create a WAVHeader.
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e) {
                    System.out.println("Closing stream for file called " + name + " failed");
                }
            }
        }

        /*
          We return a new instance of WAVHeader
          constructed from an array of bytes which
          stores the bytes at the start of the file
          called 'name'.
        */
        return new WAVHeader(bytes);
    }




    /**
     * @return a String including all the non-junk information
     *         that would be written to a WAV file
    */
    public String toString() {
        String s = "";


        s += "riff_header          " + riff_header[0] + riff_header[1] + riff_header[2] + riff_header[3] + "\n";
        s += "riff_size            " + riff_size                                                         + "\n";

        s += "wave_header          " + wave_header[0] + wave_header[1] + wave_header[2] + wave_header[3] + "\n";


        if (junk) {
            s += "junk_header          " + junk_header[0] + junk_header[1] + junk_header[2] + junk_header[3] + "\n";
            s += "junk_size            " + junk_size                                                         + "\n";
        }


        s += "fmt_header           " +  fmt_header[0] +  fmt_header[1] +  fmt_header[2] +  fmt_header[3] + "\n";
        s += "fmt_size             " + fmt_size                                                          + "\n";
        s += "audio_fmt            " + audio_fmt                                                         + "\n";
        s += "channels             " + channels                                                          + "\n";
        s += "sample_rate_per_chan " + sample_rate_per_chan                                              + "\n";
        s += "bytes_per_sec        " + bytes_per_sec                                                     + "\n";
        s += "block_size_in_bytes  " + block_size_in_bytes                                               + "\n";
        s += "bit_depth_of_sample  " + bit_depth_of_sample                                               + "\n";

        s += "data_header          " + data_header[0] + data_header[1] + data_header[2] + data_header[3] + "\n";
        s += "data_size            " + data_size                                                         + "\n";


        return s;
    }




    /**
     * @return the size of 'this' in bytes when it is written to a WAV file
    */
    public int getHeaderSize() {
        if (!junk) {
            return 44;
        }
        return 52 + junk_size;
    }

    /**
     * @return the size of the audio data in bytes when it is written to a WAV file
    */
    public int getDataSize() {
        return data_size;
    }




    /**
     * This constructor constructs a WAVHeader using
     * the bytes from the start of a WAV file.
     * 
     * @param b an array of bytes that were read
     *          from the start of a WAV file
    */
    private WAVHeader(byte[] b) {

    ByteBuffer bytes = ByteBuffer.wrap(b);
    bytes.order(ByteOrder.LITTLE_ENDIAN);

        riff_header = new char[] { (char) bytes.get(), (char) bytes.get(), (char) bytes.get(), (char) bytes.get() };
        riff_size = bytes.getInt(); 
        wave_header = new char[] { (char) bytes.get(), (char) bytes.get(), (char) bytes.get(), (char) bytes.get() }; 

        if(((char)b[12] == 'J') && ((char)b[13] == 'U') && ((char)b[14] == 'N') && ((char)b[15] == 'K')) { 
            junk = true; 
            junk_header = new char[] { (char) bytes.get(), (char) bytes.get(), (char) bytes.get(), (char) bytes.get() };
            junk_size = bytes.getInt();
            for( int i = 0; i < junk_size; ++i){
                  bytes.get();
            }

        }

            fmt_header = new char[] { (char) bytes.get(), (char) bytes.get(), (char) bytes.get(), (char) bytes.get() };
            fmt_size = bytes.getInt();
            audio_fmt = bytes.getShort();
            channels= bytes.getShort();
            sample_rate_per_chan = bytes.getInt();
            bytes_per_sec = bytes.getInt();
            block_size_in_bytes = bytes.getShort();
            bit_depth_of_sample = bytes.getShort();
            data_header = new char[] { (char) bytes.get(), (char) bytes.get(), (char) bytes.get(), (char) bytes.get() };
            data_size = bytes.getInt(); 

    }

    /**
     * This method returns an array of bytes
     * that is suitable for writing to a file
     * using a FileOutputStream.
     * 
     * @return bytes suitable for writing
     *         at the start of a WAV file
    */


    public byte[] getBytes() {

        int c = getHeaderSize();
        byte[] b =  new byte[c]; 
        ByteBuffer bytes = ByteBuffer.wrap(b);
        bytes.order(ByteOrder.LITTLE_ENDIAN);
        bytes.put((byte)riff_header[0]).put((byte)riff_header[1]).put((byte)riff_header[2]).put((byte)riff_header[3]);
        bytes.putInt(riff_size);
        bytes.put((byte)wave_header[0]).put((byte)wave_header[1]).put((byte)wave_header[2]).put((byte)wave_header[3]);

        if (c>44){ //could also say if junk = true i guess
            bytes.put((byte)junk_header[0]).put((byte)junk_header[1]).put((byte)junk_header[2]).put((byte)junk_header[3]);
            bytes.putInt(junk_size);
            for(int i=0; i<junk_size ;++i){
                bytes.put((byte)0);
            }
        }

        bytes.put((byte)fmt_header[0]).put((byte)fmt_header[1]).put((byte)fmt_header[2]).put((byte)fmt_header[3]);
        bytes.putInt(fmt_size);
        bytes.putShort(audio_fmt);
        bytes.putShort(channels);
        bytes.putInt(sample_rate_per_chan);
        bytes.putInt(bytes_per_sec);
        bytes.putShort(block_size_in_bytes);
        bytes.putShort(bit_depth_of_sample);
        bytes.put((byte)data_header[0]).put((byte)data_header[1]).put((byte)data_header[2]).put((byte)data_header[3]);
        bytes.putInt(data_size);

        // test: 

        System.out.println(riff_header);
        System.out.println(riff_size);
        System.out.println(wave_header);

        if(c>44){
        System.out.println(junk_header);
        System.out.println(junk_size);
        }
        System.out.println(fmt_header);
        System.out.println(fmt_size);
        System.out.println(audio_fmt);
        System.out.println(channels);
        System.out.println(sample_rate_per_chan);
        System.out.println(bytes_per_sec);
        System.out.println(block_size_in_bytes);
        System.out.println(bit_depth_of_sample);
        System.out.println(data_header);
        System.out.println(data_size);

        return b;
    }
}


















///