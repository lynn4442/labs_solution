import java.io.*;
import java.net.*;
import java.util.*;

public class Leader {
    private int counter = 0;

    // 4 followers -> 5 total nodes -> majority = 3 (leader + 2 followers minimum)
    private static final String[] HOSTS    = {"localhost", "localhost", "localhost", "localhost"};
    private static final int[]    PORTS    = {5001, 5002, 5003, 5004};
    private static final int      MAJORITY = 3;

    public void processRequest() {
        counter++;
        System.out.println("\nTentative value = " + counter);

        int ackCount = 1; // leader counts itself
        List<Integer> ackedFollowers = new ArrayList<>();

        for (int i = 0; i < PORTS.length; i++) {
            try {
                Socket         socket = new Socket(HOSTS[i], PORTS[i]);
                PrintWriter    out    = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in     = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println("REPLICATE " + counter);
                String reply = in.readLine();

                if ("ACK".equals(reply)) {
                    ackCount++;
                    ackedFollowers.add(i);
                    System.out.println("ACK from follower " + (i + 1));
                }

                socket.close();
            } catch (Exception e) {
                System.out.println("Follower " + (i + 1) + " unavailable: " + e.getMessage());
            }
        }

        System.out.println("ACK count = " + ackCount + "/" + (PORTS.length + 1));

        if (ackCount >= MAJORITY) {
            System.out.println("COMMIT SUCCESS");
        } else {
            counter--;
            for (int i : ackedFollowers) {
                try {
                    Socket      socket = new Socket(HOSTS[i], PORTS[i]);
                    PrintWriter out    = new PrintWriter(socket.getOutputStream(), true);
                    out.println("ROLLBACK");
                    socket.close();
                } catch (Exception e) {
                    System.out.println("Follower " + (i + 1) + " unavailable for rollback");
                }
            }
            System.out.println("COMMIT FAILED (no majority) — rolled back");
        }
    }

    public static void main(String[] args) throws Exception {
        Leader       leader = new Leader();
        ServerSocket server = new ServerSocket(6000);
        System.out.println("Leader listening on port 6000");

        while (true) {
            Socket         socket = server.accept();
            BufferedReader in     = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String req = in.readLine();

            if ("increment".equals(req))
                leader.processRequest();

            socket.close();
        }
    }
}
