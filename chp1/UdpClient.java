import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class UdpClient {
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 6000;

    public static void main(String[] args) throws Exception {
        String host = (args.length >= 1) ? args[0] : DEFAULT_HOST;
        int port = (args.length >= 2) ? Integer.parseInt(args[1]) : DEFAULT_PORT;

        InetAddress address = InetAddress.getByName(host);
        DatagramSocket socket = new DatagramSocket();

        System.out.println("UDP client sending to " + host + ":" + port);

        int i = 1;
        while (true) {
            String msg = "ping " + i + " from UDP client @ " + LocalDateTime.now();
            byte[] data = msg.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            socket.send(packet);
            System.out.println("-> sent: " + msg);
            i++;
            Thread.sleep(700);
        }
    }
}