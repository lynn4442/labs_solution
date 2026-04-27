import java.net.*;
import java.nio.charset.StandardCharsets;

public class C {
    static int LC = 0;

    static void send(DatagramSocket s, String host, int port, String type) throws Exception {
        LC++;
        String msg = "C|" + LC + "|" + type;
        byte[] data = msg.getBytes(StandardCharsets.UTF_8);
        s.send(new DatagramPacket(data, data.length, InetAddress.getByName(host), port));
        System.out.println("C send " + type + " ts=" + LC);
    }

    public static void main(String[] args) throws Exception {
        try (DatagramSocket s = new DatagramSocket()) {
            send(s, "127.0.0.1", 6000, "NOTIFY");
        }
    }
}