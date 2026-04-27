import java.util.concurrent.BlockingQueue;

public class Subscriber implements Runnable {
    private final String name;
    private final BlockingQueue<Message> inbox;

    public Subscriber(String name, BlockingQueue<Message> inbox) {
        this.name = name;
        this.inbox = inbox;
    }

    public void run() {
        while (true) {
            try {
                Message msg = inbox.take();
                System.out.println("[" + name + "] Received: " + msg.payload);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}