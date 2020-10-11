package socket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IO {
  /**
   * Socket read for an unknown receive size
   * @param in
   * @return byte array of the received.
   * @throws IOException
   * @throws InterruptedException
   */
  public static byte[] read(InputStream in) throws IOException, InterruptedException {
    int count;
    byte[] buffer = new byte[1];
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      // startWait will allow this to block until the first packet is received
      boolean startWait = true;
      // while first packet or a packet is available, process
      while ((startWait || in.available() > 0) && 
          (count = in.read(buffer, 0, buffer.length)) != -1) { // were there bytes to read?
        // if so, write them out
        outputStream.write(buffer, 0, count);
        // not first packet anymore
        startWait = false;
      }

      return outputStream.toByteArray();
    }
  }
}
