package httpServer;

import java.io.*;
import java.net.*;

class WebServer {

    // 
    public static void main(String args[]) {
        if (args.length != 1) {
            System.out.println("Usage: WebServer <port>");
            System.exit(1);
        }
        
        WebServer server = new WebServer(Integer.parseInt(args[0]));
    }

    public WebServer(int port) {
        
        ServerSocket    server = null;
        Socket          sock = null;
        InputStream     in = null;
        OutputStream    out = null;

        //*** Open the server socket on the specified port
        //*** Loop forever accepting socket requests
        //***   Get the response bytes from createResponse
        //***   Write the bytes to the socket's output stream
        //***   close streams and socket appropriatels
    }


    public byte[] createResponse(InputStream inStream) {

        byte[] response = null;
        BufferedReader in = null;

        try {

            // Read from socket's input stream.  Must use an
            // InputStreamReader to bridge from streams to a reader
            in = new BufferedReader(
                        new InputStreamReader(inStream, "UTF-8"));

            // Get header and save the filename from the GET line:
            //    example GET format: GET /index.html HTTP/1.1

            String filename = null;

            boolean done = false;
            while (!done) {
                String line = in.readLine();

System.out.println("Received: " + line);
                if (line == null || line.equals(""))
                    done = true;

                else if (line.startsWith("GET")) {
                    int firstSpace = line.indexOf(" ");
                    int secondSpace = line.indexOf(" ", firstSpace+1);

                    // skipt the leading / (our docroot is the current dir)
                    filename = line.substring(firstSpace+2, secondSpace);
                }

            }
System.out.println("FINISHED\n");


            // Generate an appropriate response to the user
            if (filename == null) {
                response =
                    "<html>Illegal request: no GET</html>".getBytes();
            } else {
            
                File file = new File(filename);
                if (!file.exists()) {
                    response = ("<html>File not found: " +
                                filename + "</html>").getBytes();
                } else {
                    response = readFileInBytes(file);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            response = ("<html>ERROR: " +
                        e.getMessage() + "</html>").getBytes();
        }

        return response;
    }

    /** Read bytes from a file and return them in the byte array.
        We read in blocks of 512 bytes for efficiency.
    */
    public static byte[] readFileInBytes(File f)
        throws IOException {

        FileInputStream file = new FileInputStream(f);
        ByteArrayOutputStream data = new ByteArrayOutputStream(file.available());

        byte buffer[] = new byte[512];
        int numRead = file.read(buffer);
        while (numRead > 0) {
            data.write(buffer, 0, numRead);
            numRead = file.read(buffer);
        }
        file.close();

        byte[] result =  data.toByteArray();
        data.close();

        return result;
    }
}
