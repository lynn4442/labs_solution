import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BetterNode {
    private final String nodeId;
    private final Map<String, Integer> contributions = new HashMap<>();
    private final List<BetterNode> peers = new ArrayList<>();

    public BetterNode(String nodeId) {
        this.nodeId = nodeId;
        contributions.put(nodeId, 0);
    }

    public void addPeer(BetterNode peer) {
        if (peer != null && peer != this && !peers.contains(peer)) {
            peers.add(peer);
        }
    }

    public void increment() {
        contributions.put(nodeId, contributions.getOrDefault(nodeId, 0) + 1);
        System.out.println(nodeId + " incremented its local contribution");
    }

    public int getTotal() {
        int sum = 0;
        for (int value : contributions.values()) {
            sum += value;
        }
        return sum;
    }

    public void replicate() {
        for (BetterNode peer : peers) {
            peer.receiveReplica(new HashMap<>(contributions), nodeId);
        }
    }

    public void receiveReplica(Map<String, Integer> remoteMap, String senderId) {
        System.out.println(nodeId + " received state from " + senderId);
        for (Map.Entry<String, Integer> entry : remoteMap.entrySet()) {
            String key = entry.getKey();
            int remoteValue = entry.getValue();
            int localValue = contributions.getOrDefault(key, 0);
            contributions.put(key, Math.max(localValue, remoteValue));
        }
    }

    public void printState() {
        System.out.println(nodeId + " -> " + contributions + " total=" + getTotal());
    }
}