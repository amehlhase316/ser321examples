package fauxSolution.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class NetworkUtils {
  // https://mkyong.com/java/java-convert-byte-to-int-and-vice-versa/
  public static byte[] intToBytes(final int data) {
    return new byte[] { (byte) ((data >> 24) & 0xff), (byte) ((data >> 16) & 0xff), (byte) ((data >> 8) & 0xff),
        (byte) ((data >> 0) & 0xff), };
  }

// https://mkyong.com/java/java-convert-byte-to-int-and-vice-versa/
  public static int bytesToInt(byte[] bytes) {
    return ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | ((bytes[3] & 0xFF) << 0);
  }

  /* packet (1024 max)
   * [ 
   *   totalPackets(4-byte int), 
   *   currentPacket#(4-byte int), 
   *   payloadLength(4-byte int),
   *   payload(byte[])
   * ]
   */
  public static void Send(DatagramSocket sock, InetAddress addr, int port, byte... bytes) throws IOException {
    int maxBufferLength = 1024 - 12;
    int packetsTotal = bytes.length / maxBufferLength + 1;
    
    int offset = 0;
    int packetNum = 0;
    while (offset < bytes.length) {
      int bytesLeftToSend = bytes.length - offset;
      int length = Math.min(maxBufferLength, bytesLeftToSend);
      
      byte[] totalBytes = NetworkUtils.intToBytes(packetsTotal);
      byte[] currentBytes = NetworkUtils.intToBytes(packetNum);
      byte[] lengthBytes = NetworkUtils.intToBytes(length);
     
      byte[] buffer = new byte[12 + length];
      System.arraycopy(totalBytes, 0, buffer, 0, 4);
      System.arraycopy(currentBytes, 0, buffer, 4, 4);
      System.arraycopy(lengthBytes, 0, buffer, 8, 4);
      System.arraycopy(bytes, offset, buffer, 12, length);
      
      DatagramPacket packet = new DatagramPacket(buffer, buffer.length, addr, port);
      sock.send(packet);
      
      packetNum++;
      offset += length;
    }
  }

  static class Packet {
    /* packet (1024 max)
     * [ 
     *   totalPackets(4-byte int), 
     *   currentPacket#(4-byte int), 
     *   payloadLength(4-byte int),
     *   payload(byte[])
     * ]
     */
    public final DatagramPacket Packet;
    public final int Total;
    public final int Current;
    public final int Length;
    public final byte[] Payload;
    
    public Packet(DatagramPacket packet) {
      Packet = packet;
      
      byte[] totalBytes = new byte[4];
      System.arraycopy(packet.getData(), 0, totalBytes, 0, 4);
      Total = NetworkUtils.bytesToInt(totalBytes);
      
      byte[] currentBytes = new byte[4];
      System.arraycopy(packet.getData(), 4, currentBytes, 0, 4);
      Current = NetworkUtils.bytesToInt(currentBytes);
      
      byte[] lengthBytes = new byte[4];
      System.arraycopy(packet.getData(), 8, lengthBytes, 0, 4);
      Length = NetworkUtils.bytesToInt(lengthBytes);
      
      int payloadLength = packet.getLength() - 12;
      Payload = new byte[payloadLength];
      System.arraycopy(packet.getData(), 12, Payload, 0, payloadLength);
    }
  }
  
  static class Tuple {
    public final InetAddress Address;
    public final int Port;
    public final byte[] Payload;
    
    public Tuple(InetAddress address, int port, byte[] payload) {
      Address = address;
      Port = port;
      Payload = payload;
    }
  }
  
  private static Packet Read(DatagramSocket sock, int length) throws IOException {
    byte[] buff = new byte[length];
    DatagramPacket request = new DatagramPacket(buff, length);
    sock.receive(request);
    return new Packet(request);
  }
 
  // reading in all the packets and adding them to "packets"
  // collecting packets as long as the size of the packets is smaller then the total ones we are supposed to receice
  public static Tuple Receive(DatagramSocket sock) throws IOException {
    ArrayList<Packet> packets = new ArrayList<Packet>();
    do {
      packets.add(Read(sock, 1024));
    } while (packets.size() > 0 && packets.size() < packets.get(0).Total);
    
    packets.sort((p1, p2) -> p1.Current - p2.Current); // sorting the packages by package number
    int totalBufferLength = packets.stream().mapToInt((p)->p.Length).sum(); // summing up how long the payload is
    byte[] buffer = new byte[totalBufferLength]; // creating big enough buffer
    int offset = 0;
    
    // interating through all packets, adding the paylod of each package to the buffer, make sure offset is used to not overwrite things
    for(var p : packets) {
      System.arraycopy(p.Payload, 0, buffer, offset, p.Length);
      offset += p.Length;
    }
    DatagramPacket first = packets.get(0).Packet;
    return new Tuple(first.getAddress(), first.getPort(), buffer);
  }
}
