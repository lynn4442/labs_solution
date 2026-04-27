import java.util.concurrent.BlockingQueue;

public class MainPubSub {
    public static void main(String[] args) {
        PubSubBroker broker = new PubSubBroker();

        BlockingQueue<Message> s1Inbox = broker.subscribe();
        BlockingQueue<Message> s2Inbox = broker.subscribe();
        BlockingQueue<Message> s3Inbox = broker.subscribe();

        Thread s1 = new Thread(new Subscriber("Subscriber A", s1Inbox));
        Thread s2 = new Thread(new Subscriber("Subscriber B", s2Inbox));
        Thread s3 = new Thread(new Subscriber("Subscriber C", s3Inbox));

        s1.start();
        s2.start();
        s3.start();

        Thread p1 = new Thread(new Publisher("Publisher 1", broker));
        Thread p2 = new Thread(new Publisher("Publisher 2", broker));

        p1.start();
        p2.start();
    }
}