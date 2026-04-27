import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class PubSubBroker {
    private final List<BlockingQueue<Message>> subscriberQueues = new CopyOnWriteArrayList<>();

    public BlockingQueue<Message> subscribe() {
        BlockingQueue<Message> q = new LinkedBlockingQueue<>();
        subscriberQueues.add(q);
        System.out.println("[Broker] New subscriber registered. Total = " + subscriberQueues.size());
        return q;
    }

    public void publish(Message msg) {
        InterfaceContract.validate(msg);
        for (BlockingQueue<Message> q : subscriberQueues) {
            q.add(msg);
        }
        System.out.println("[Broker] Published to " + subscriberQueues.size() + " subscribers: " + msg.payload);
    }
}