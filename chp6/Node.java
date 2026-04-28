import java.util.ArrayList;
import java.util.List;

public class Node {
    private final String nodeId;
    private int counter;
    private final List<Node> peers = new ArrayList<>();

    public Node(String nodeId, int initialCounter) {
        this.nodeId = nodeId;
        this.counter = initialCounter;
    }

    public String getNodeId() { return nodeId; }
    public int getCounter() { return counter; }

    public void addPeer(Node peer) {
        if (peer != null && peer != this && !peers.contains(peer)) {
            peers.add(peer);
        }
    }

    public void increment() {
        counter++;
        System.out.println(nodeId + " incremented its counter to " + counter);
    }

    public void replicate() {
        System.out.println(nodeId + " replicates value " + counter + " to peers");
        for (Node peer : peers) {
            peer.receiveReplica(counter, nodeId);
        }
    }

    public void receiveReplica(int remoteCounter, String senderId) {
        System.out.println(nodeId + " received " + remoteCounter + " from " + senderId);
        int oldValue = counter;
        counter = Math.max(counter, remoteCounter);
        if (counter != oldValue) {
            System.out.println(nodeId + " updated its counter from " + oldValue + " to " + counter);
        } else {
            System.out.println(nodeId + " kept its counter at " + counter);
        }
    }

    public void printState() {
        System.out.println(nodeId + " -> counter = " + counter);
    }
}