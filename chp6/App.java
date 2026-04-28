public class App {
    public static void main(String[] args) throws Exception {

        // ── Basic version ──────────────────────────────────────────
        System.out.println("========== BASIC NODE VERSION ==========");

        Node nodeA = new Node("Node A", 42);
        Node nodeB = new Node("Node B", 42);
        Node nodeC = new Node("Node C", 42);

        // Full mesh — every node knows every other node
        nodeA.addPeer(nodeB); nodeA.addPeer(nodeC);
        nodeB.addPeer(nodeA); nodeB.addPeer(nodeC);
        nodeC.addPeer(nodeA); nodeC.addPeer(nodeB);

        System.out.println("\n=== Initial state ===");
        printAll(nodeA, nodeB, nodeC);

        System.out.println("\n=== Step 1: Node A increments (stale read on B and C) ===");
        nodeA.increment();
        printAll(nodeA, nodeB, nodeC);

        System.out.println("\n=== Step 2: Replication from Node A (convergence) ===");
        nodeA.replicate();
        printAll(nodeA, nodeB, nodeC);

        System.out.println("\n=== Step 3: Concurrent updates from A and C (divergence) ===");
        nodeA.increment(); // A = 44
        nodeC.increment(); // C = 44
        printAll(nodeA, nodeB, nodeC);

        System.out.println("\n=== Step 4: Partial replication from Node A ===");
        nodeA.replicate();
        printAll(nodeA, nodeB, nodeC);

        System.out.println("\n=== Step 5: Reconciliation from Node C (lost update visible) ===");
        nodeC.replicate();
        printAll(nodeA, nodeB, nodeC);

        // ── Better version ─────────────────────────────────────────
        System.out.println("\n========== BETTER NODE VERSION ==========");

        BetterNode bA = new BetterNode("Node A");
        BetterNode bB = new BetterNode("Node B");
        BetterNode bC = new BetterNode("Node C");

        bA.addPeer(bB); bA.addPeer(bC);
        bB.addPeer(bA); bB.addPeer(bC);
        bC.addPeer(bA); bC.addPeer(bB);

        System.out.println("\n=== Initial state ===");
        printAllBetter(bA, bB, bC);

        System.out.println("\n=== Step 1: Node A increments ===");
        bA.increment();
        printAllBetter(bA, bB, bC);

        System.out.println("\n=== Step 2: Replication from Node A ===");
        bA.replicate();
        printAllBetter(bA, bB, bC);

        System.out.println("\n=== Step 3: Concurrent updates from A and C ===");
        bA.increment();
        bC.increment();
        printAllBetter(bA, bB, bC);

        System.out.println("\n=== Step 4: Replication from Node A ===");
        bA.replicate();
        printAllBetter(bA, bB, bC);

        System.out.println("\n=== Step 5: Replication from Node C (no lost update) ===");
        bC.replicate();
        printAllBetter(bA, bB, bC);
    }

    private static void printAll(Node... nodes) {
        for (Node n : nodes) n.printState();
    }

    private static void printAllBetter(BetterNode... nodes) {
        for (BetterNode n : nodes) n.printState();
    }
}