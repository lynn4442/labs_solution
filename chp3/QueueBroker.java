import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class QueueBroker {
    private final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();

    public void publish(Message msg) {
        InterfaceContract.validate(msg);
        queue.add(msg);
        System.out.println("[Broker] Message queued: " + msg.payload);
    }

    public Message consume() throws InterruptedException {
        return queue.take(); // blocks until message is available
    }
}