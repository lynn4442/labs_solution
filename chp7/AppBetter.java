import java.util.*;

public class AppBetter {
    public static void main(String[] args) {
        BetterNode A = new BetterNode("A");
        BetterNode B = new BetterNode("B");
        BetterNode C = new BetterNode("C");

        List<BetterNode> nodes = List.of(A, B, C);

        A.setPeers(nodes);
        B.setPeers(nodes);
        C.setPeers(nodes);

        // Register in network so messages can be delivered
        Network.register(A);
        Network.register(B);
        Network.register(C);

        // Initial divergence with versions
        A.updateValue("Y", 4);
        B.updateValue("Y", 2);
        C.updateValue("Y", 3);

        System.out.println("=== Starting BetterNodes ===");
        new Thread(A).start();
        new Thread(B).start();
        new Thread(C).start();

        // Stop Node B after 8 seconds
        new Timer().schedule(new TimerTask() {
            public void run() {
                System.out.println("\n--- Stopping Node B ---\n");
                B.running = false;
            }
        }, 8000);
    }
}