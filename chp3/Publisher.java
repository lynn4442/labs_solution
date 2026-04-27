public class Publisher implements Runnable {
    private final String name;
    private final PubSubBroker broker;

    public Publisher(String name, PubSubBroker broker) {
        this.name = name;
        this.broker = broker;
    }

    public void run() {
        for (int i = 1; i <= 5; i++) {
            Message msg = new Message("Event", name + " - Event " + i);
            broker.publish(msg);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}