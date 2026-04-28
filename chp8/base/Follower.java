import java.io.*;
import java.net.*;

public class Follower {
    private int counter = 0;

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
                counter = value;
                System.out.println("[Port " + port + "] Updated counter = " + counter);

                // uncomment the line below to simulate a slow node
                // Thread.sleep(3000);

                out.println("ACK");
            } else if (msg != null && msg.equals("ROLLBACK")) {
                counter--;
                System.out.println("[Port " + port + "] Rolled back. Counter = " + counter);
            }

            socket.close();
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: java Follower <port>");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        new Follower(port);
    }
}
