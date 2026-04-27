import java.net.*;
import java.nio.charset.StandardCharsets;

public class A {
    static int LC = 0;

    static void send(DatagramSocket s, String host, int port, String type) throws Exception {
        LC++;
        String msg = "A|" + LC + "|" + type;
        byte[] data = msg.getBytes(StandardCharsets.UTF_8);
        s.send(new DatagramPacket(data, data.length, InetAddress.getByName(host), port));
        System.out.println("A send " + type + " ts=" + LC);
    }

    static void receive(DatagramSocket s) throws Exception {
        byte[] buf = new byte[256];
        DatagramPacket p = new DatagramPacket(buf, buf.length);
        s.receive(p);
        String msg = new String(p.getData(), 0, p.getLength(), StandardCharsets.UTF_8);
        String[] parts = msg.split("\\|");
        String sender = parts[0];
        int ts = Integer.parseInt(parts[1]);
        String type = parts[2];
        LC = Math.max(LC, ts) + 1;
        System.out.println("A recv " + type + " from " + sender + " msgTs=" + ts + " -> LC=" + LC);
    }

    public static void main(String[] args) throws Exception {
        try (DatagramSocket s = new DatagramSocket(6000)) {
            send(s, "127.0.0.1", 6001, "REQ1");
            send(s, "127.0.0.1", 6001, "REQ2");
            receive(s); // ACK or NOTIFY
            receive(s); // the other one
        }
    }
}