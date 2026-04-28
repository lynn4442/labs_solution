import java.io.*;
import java.net.*;
import java.util.*;

public class Follower {
    private final List<Integer> log = new ArrayList<>();

    public Follower(int port) throws Exception {
        ServerSocket server = new ServerSocket(port);
        System.out.println("Follower started on port " + port);

        while (true) {
            Socket         socket = server.accept();
            BufferedReader in     = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter    out    = new PrintWriter(socket.getOutputStream(), true);

            String msg = in.readLine();
            if (msg != null && msg.startsWith("REPLICATE")) {
                int value = Integer.parseInt(msg.split(" ")[1]);
                log.add(value);
                System.out.println("[Port " + port + "] Log: " + log);

                // Uncomment to simulate a slow node
                // Thread.sleep(3000);

                out.println("ACK");
            } else if (msg != null && msg.equals("ROLLBACK")) {
                if (!log.isEmpty()) log.remove(log.size() - 1);
                System.out.println("[Port " + port + "] Rolled back. Log: " + log);
            }

            socket.close();
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) { System.err.println("Usage: java Follower <port>"); System.exit(1); }
        new Follower(Integer.parseInt(args[0]));
    }
}
