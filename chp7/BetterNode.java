import java.util.*;

// Step 5: versioned value
class VersionedValue {
    int value;
    int version;

    VersionedValue(int value, int version) {
        this.value = value;
        this.version = version;
    }

    public String toString() {
        return value + "(v" + version + ")";
    }
}

// Step 6: node status
enum NodeStatus { ALIVE, SUSPECTED }

// Step 1: message abstractions
abstract class Message {
    String from;
    String to;
    Message(String from, String to) {
        this.from = from;
        this.to = to;
    }
}

class GossipDigestMessage extends Message {
    Map<String, Integer> versions; // key → version only
    GossipDigestMessage(String from, String to, Map<String, Integer> versions) {
        super(from, to);
        this.versions = versions;
    }
}

class GossipRequestMessage extends Message {
    Set<String> missingKeys;
    GossipRequestMessage(String from, String to, Set<String> missingKeys) {
        super(from, to);
        this.missingKeys = missingKeys;
    }
}

class GossipSyncMessage extends Message {
    Map<String, VersionedValue> values;
    GossipSyncMessage(String from, String to, Map<String, VersionedValue> values) {
        super(from, to);
        this.values = values;
    }
}

class HeartbeatMessage extends Message {
    HeartbeatMessage(String from, String to) {
        super(from, to);
    }
}

// Step 2: unreliable network
class Network {
    static final double LOSS_PROBABILITY = 0.2;
    static final int MAX_DELAY_MS = 500;
    static Map<String, BetterNode> nodeRegistry = new HashMap<>();

    static void register(BetterNode node) {
        nodeRegistry.put(node.id, node);
    }

    static void send(Message msg) {
        // Simulate message loss
        if (Math.random() < LOSS_PROBABILITY) {
            System.out.println("[Network] Message from " + msg.from + " to " + msg.to + " DROPPED");
            return;
        }
        // Simulate delay
        long delay = (long)(Math.random() * MAX_DELAY_MS);
        BetterNode target = nodeRegistry.get(msg.to);
        if (target != null && target.running) {
            new Timer().schedule(new TimerTask() {
                public void run() {
                    target.deliver(msg);
                }
            }, delay);
        }
    }
}

class BetterNode implements Runnable {
    String id;
    Map<String, VersionedValue> data = new HashMap<>();
    Map<String, Long> lastSeen = new HashMap<>();
    Map<String, NodeStatus> peerStatus = new HashMap<>();
    List<String> peerIds = new ArrayList<>();
    Queue<Message> inbox = new LinkedList<>();
    boolean running = true;

    // Step 7: fanout — how many peers to gossip to per round
    int fanout = 2;

    public BetterNode(String id) {
        this.id = id;
    }

    public void setPeers(List<BetterNode> allNodes) {
        for (BetterNode n : allNodes) {
            if (!n.id.equals(this.id)) {
                peerIds.add(n.id);
                lastSeen.put(n.id, System.currentTimeMillis());
                peerStatus.put(n.id, NodeStatus.ALIVE);
            }
        }
    }

    // Step 5: update a value with version increment
    public void updateValue(String key, int value) {
        int currentVersion = data.containsKey(key) ? data.get(key).version : 0;
        data.put(key, new VersionedValue(value, currentVersion + 1));
        System.out.println(id + " set " + key + " = " + data.get(key));
    }

    // Step 3: async inbox delivery
    public synchronized void deliver(Message msg) {
        inbox.add(msg);
    }

    synchronized void processInbox() {
        while (!inbox.isEmpty()) {
            Message msg = inbox.poll();
            if (msg instanceof HeartbeatMessage) {
                lastSeen.put(msg.from, System.currentTimeMillis());

            } else if (msg instanceof GossipDigestMessage) {
                // Compare digest, request missing/outdated keys
                GossipDigestMessage digest = (GossipDigestMessage) msg;
                Set<String> needed = new HashSet<>();
                for (String key : digest.versions.keySet()) {
                    int remoteVersion = digest.versions.get(key);
                    if (!data.containsKey(key) || data.get(key).version < remoteVersion) {
                        needed.add(key);
                    }
                }
                if (!needed.isEmpty()) {
                    Network.send(new GossipRequestMessage(this.id, msg.from, needed));
                }

            } else if (msg instanceof GossipRequestMessage) {
                // Send only requested keys
                GossipRequestMessage req = (GossipRequestMessage) msg;
                Map<String, VersionedValue> toSend = new HashMap<>();
                for (String key : req.missingKeys) {
                    if (data.containsKey(key)) toSend.put(key, data.get(key));
                }
                Network.send(new GossipSyncMessage(this.id, msg.from, toSend));

            } else if (msg instanceof GossipSyncMessage) {
                // Merge received values, keep highest version
                GossipSyncMessage sync = (GossipSyncMessage) msg;
                for (Map.Entry<String, VersionedValue> entry : sync.values.entrySet()) {
                    String key = entry.getKey();
                    VersionedValue remote = entry.getValue();
                    if (!data.containsKey(key) || data.get(key).version < remote.version) {
                        data.put(key, remote);
                        System.out.println(id + " synced " + key + " = " + remote);
                    }
                }
            }
        }
    }

    // Step 4: digest → request → sync gossip
    void gossip() {
        List<String> targets = getRandomPeers(fanout);
        for (String peerId : targets) {
            Map<String, Integer> digest = new HashMap<>();
            for (Map.Entry<String, VersionedValue> e : data.entrySet()) {
                digest.put(e.getKey(), e.getValue().version);
            }
            Network.send(new GossipDigestMessage(this.id, peerId, digest));
        }
    }

    // Step 7: fanout — pick multiple peers
    List<String> getRandomPeers(int count) {
        List<String> shuffled = new ArrayList<>(peerIds);
        Collections.shuffle(shuffled);
        return shuffled.subList(0, Math.min(count, shuffled.size()));
    }

    void sendHeartbeats() {
        for (String peerId : peerIds) {
            Network.send(new HeartbeatMessage(this.id, peerId));
        }
    }

    // Step 6: status transitions
    void checkFailures() {
        long now = System.currentTimeMillis();
        for (String peerId : peerIds) {
            long last = lastSeen.getOrDefault(peerId, now);
            NodeStatus current = peerStatus.get(peerId);
            if (now - last > 3000) {
                if (current == NodeStatus.ALIVE) {
                    peerStatus.put(peerId, NodeStatus.SUSPECTED);
                    System.out.println(id + " → " + peerId + " status: ALIVE → SUSPECTED");
                }
            } else {
                if (current == NodeStatus.SUSPECTED) {
                    peerStatus.put(peerId, NodeStatus.ALIVE);
                    System.out.println(id + " → " + peerId + " status: SUSPECTED → ALIVE");
                }
            }
        }
    }

    public void printState() {
        System.out.println(id + " state: " + data);
    }

    public void run() {
        while (running) {
            gossip();
            sendHeartbeats();
            checkFailures();
            processInbox();
            sleep(1000);
        }
        System.out.println(id + " stopped.");
    }

    void sleep(long ms) {
        try { Thread.sleep(ms); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}