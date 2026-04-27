public class QueueConsumer implements Runnable {
    private final String name;
    private final QueueBroker broker;

    public QueueConsumer(String name, QueueBroker broker) {
        this.name = name;
        this.broker = broker;
    }

    public void run() {
        while (true) {
            try {
                Message msg = broker.consume();
                System.out.println("[" + name + "] Received: " + msg.payload);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}