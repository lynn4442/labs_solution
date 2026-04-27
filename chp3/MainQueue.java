public class MainQueue {
    public static void main(String[] args) {
        QueueBroker broker = new QueueBroker();

        Thread p1 = new Thread(new QueueProducer("Producer 1", broker));
        Thread p2 = new Thread(new QueueProducer("Producer 2", broker));

        Thread c1 = new Thread(new QueueConsumer("Consumer 1", broker));
        Thread c2 = new Thread(new QueueConsumer("Consumer 2", broker));

        c1.start();
        c2.start();
        p1.start();
        p2.start();
    }
}