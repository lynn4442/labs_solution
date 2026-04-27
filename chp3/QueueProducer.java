public class QueueProducer implements Runnable {
    private final String name;
    private final QueueBroker broker;

    public QueueProducer(String name, QueueBroker broker) {
        this.name = name;
        this.broker = broker;
    }

    public void run() {
        for (int i = 1; i <= 5; i++) {
            Message msg = new Message("Task", name + " Task " + i);
            broker.publish(msg);
            System.out.println("[" + name + "] Sent: " + msg.payload);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}