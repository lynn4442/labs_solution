import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class TcpClient {
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 5000;

    public static void main(String[] args) {
        String host = (args.length >= 1) ? args[0] : DEFAULT_HOST;
        int port = (args.length >= 2) ? Integer.parseInt(args[1]) : DEFAULT_PORT;

        System.out.println("Connecting to " + host + ":" + port + " ...");

        try (
            Socket socket = new Socket(host, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))
        ) {
            int i = 1;
            while (true) {
                String msg = "ping " + i + " from client @ " + LocalDateTime.now();
                System.out.println("-> sending: " + msg);
                out.write(msg);
                out.newLine();
                out.flush();

                String reply = in.readLine();
                if (reply == null) {
                    System.out.println("Server closed connection.");
                    break;
                }
                System.out.println("<- received: " + reply);
                i++;
                Thread.sleep(700);
            }
        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}