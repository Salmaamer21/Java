/*
  An instance of this class will be thrown
  if someone attempts to make a WAVHeader for a stereo WAV file
  but provides a left and right channel with different lengths.
*/
public class WAVStereoException extends RuntimeException {
    public WAVStereoException() {
        super("left and right channels do not have the same number of samples");
    }
}