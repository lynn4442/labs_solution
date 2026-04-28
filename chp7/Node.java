import java.util.*;

class Node implements Runnable {
    String id;
    Map<String, Integer> data = new HashMap<>();
    Map<String, Long> lastSeen = new HashMap<>();
    List<Node> peers;
    boolean running = true;

    public Node(String id) {
        this.id = id;
    }

    public void setPeers(List<Node> allNodes) {
        peers = new ArrayList<>();
        for (Node n : allNodes) {
            if (!n.id.equals(this.id)) {
                peers.add(n);
                lastSeen.put(n.id, System.currentTimeMillis());
            }
        }
    }

    public void run() {
        while (running) {
            gossip();
            sendHeartbeats();
            checkFailures();
            sleep(1000);
        }
        System.out.println(id + " has stopped.");
    }

    // ── Gossip ──────────────────────────────────────────────────────
    void gossip() {
        Node peer = getRandomPeer();
        if (peer == null) return;

        // Simulate 20% message loss
        if (Math.random() < 0.2) {
            System.out.println(id + " gossip to " + peer.id + " DROPPED");
            return;
        }

        // Simulate random delay up to 500ms
        sleep((int)(Math.random() * 500));
        peer.receiveGossip(this.data);
    }

    Node getRandomPeer() {
        if (peers.isEmpty()) return null;
        return peers.get((int)(Math.random() * peers.size()));
    }

    // Anti-entropy: only update if value is higher (newer)
    void receiveGossip(Map<String, Integer> otherData) {
        for (String key : otherData.keySet()) {
            int otherValue = otherData.get(key);
            if (!data.containsKey(key) || data.get(key) < otherValue) {
                data.put(key, otherValue);
                System.out.println(id + " updated " + key + " to " + otherValue);
            }
        }
    }

    // ── Heartbeats ──────────────────────────────────────────────────
    void sendHeartbeats() {
        for (Node peer : peers) {
            peer.receiveHeartbeat(this.id);
        }
    }

    void receiveHeartbeat(String from) {
        lastSeen.put(from, System.currentTimeMillis());
    }

    // ── Failure Detection ────────────────────────────────────────────
    void checkFailures() {
        long now = System.currentTimeMillis();
        for (String peer : lastSeen.keySet()) {
            long last = lastSeen.get(peer);
            if (now - last > 3000) {
                System.out.println(id + " SUSPECTS " + peer + " has failed");
            }
        }
    }

    void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}