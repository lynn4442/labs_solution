import java.util.*;

public class App {
    public static void main(String[] args) {
        Node A = new Node("A");
        Node B = new Node("B");
        Node C = new Node("C");

        List<Node> nodes = List.of(A, B, C);

        A.setPeers(nodes);
        B.setPeers(nodes);
        C.setPeers(nodes);

        // Initial divergence — nodes start with different values
        A.data.put("Y", 4);
        B.data.put("Y", 2);
        C.data.put("Y", 4);

        System.out.println("=== Starting nodes ===");
        new Thread(A).start();
        new Thread(B).start();
        new Thread(C).start();

        // Stop Node B after 8 seconds to simulate failure
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("\n--- Stopping Node B (simulated failure) ---\n");
                B.running = false;
            }
        }, 8000);
    }
}