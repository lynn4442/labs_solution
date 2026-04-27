import java.net.*;
import java.nio.charset.StandardCharsets;

public class B {
    static int LC = 0;

    static void receive(DatagramSocket s) throws Exception {
        byte[] buf = new byte[256];
        DatagramPacket p = new DatagramPacket(buf, buf.length);
        s.receive(p);
        String msg = new String(p.getData(), 0, p.getLength(), StandardCharsets.UTF_8);
        String[] parts = msg.split("\\|");
        String sender = parts[0];
        int msgTs = Integer.parseInt(parts[1]);
        String type = parts[2];
        LC = Math.max(LC, msgTs) + 1;
        System.out.println("B recv " + type + " from " + sender + " msgTs=" + msgTs + " -> LC=" + LC);
    }

    static void send(DatagramSocket s, String host, int port, String type) throws Exception {
        LC++;
        String msg = "B|" + LC + "|" + type;
        byte[] data = msg.getBytes(StandardCharsets.UTF_8);
        s.send(new DatagramPacket(data, data.length, InetAddress.getByName(host), port));
        System.out.println("B send " + type + " ts=" + LC);
    }

    public static void main(String[] args) throws Exception {
        try (DatagramSocket s = new DatagramSocket(6001)) {
            receive(s); // REQ1
            receive(s); // REQ2
            send(s, "127.0.0.1", 6000, "ACK");
        }
    }
}