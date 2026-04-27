import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class UdpServer {
    private static final int PORT = 6000;

    public static void main(String[] args) throws Exception {
        int port = (args.length >= 1) ? Integer.parseInt(args[0]) : PORT;
        int messagesReceived = 0;
        byte[] buf = new byte[2048];

        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("UDP server listening on port " + port);
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                messagesReceived++;
                String msg = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8).trim();
                System.out.println("[" + LocalDateTime.now() + "] #" + messagesReceived + " from " + packet.getAddress() + ":" + packet.getPort() + " -> " + msg);
            }
        }
    }
}