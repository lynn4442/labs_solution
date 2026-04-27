public class InterfaceContract {
    public static void validate(Message msg) {
        if (msg == null)
            throw new IllegalArgumentException("Message is null");
        if (msg.type == null || msg.type.isBlank())
            throw new IllegalArgumentException("Invalid contract: 'type' is required");
        if (msg.payload == null || msg.payload.isBlank())
            throw new IllegalArgumentException("Invalid contract: 'payload' is required");
    }
}