package lab.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CounterClient {

    private final ManagedChannel channel;
    private final CounterServiceGrpc.CounterServiceBlockingStub blockingStub;
    private final CounterServiceGrpc.CounterServiceStub asyncStub;

    public CounterClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        blockingStub = CounterServiceGrpc.newBlockingStub(channel);
        asyncStub = CounterServiceGrpc.newStub(channel);
        System.out.println("[Client] Connected to " + host + ":" + port);
    }

    // Part 4 — Unary call
    public void increment(int value) {
        IncrementRequest request = IncrementRequest.newBuilder()
                .setValue(value)
                .build();
        try {
            IncrementResponse response = blockingStub.increment(request);
            System.out.println("[Client] Increment(" + value + ") → newValue = " + response.getNewValue());
        } catch (StatusRuntimeException e) {
            System.err.println("[Client] RPC failed: " + e.getStatus());
        }
    }

    // Part 4 — Server streaming call
    public void streamValues(int count, int delayMs) throws InterruptedException {
        StreamRequest request = StreamRequest.newBuilder()
                .setCount(count)
                .setDelayMs(delayMs)
                .build();

        CountDownLatch latch = new CountDownLatch(1);

        asyncStub.streamValues(request, new StreamObserver<StreamResponse>() {
            @Override
            public void onNext(StreamResponse value) {
                System.out.println("[Client] Stream received: " + value.getCounterValue());
            }
            @Override
            public void onError(Throwable t) {
                System.err.println("[Client] Stream error: " + t.getMessage());
                latch.countDown();
            }
            @Override
            public void onCompleted() {
                System.out.println("[Client] Stream completed.");
                latch.countDown();
            }
        });

        latch.await();
    }

    // Part 6 — Streaming with deadline
    public void streamValuesWithDeadline(int count, int delayMs, long deadlineMs) throws InterruptedException {
        StreamRequest request = StreamRequest.newBuilder()
                .setCount(count)
                .setDelayMs(delayMs)
                .build();

        CountDownLatch latch = new CountDownLatch(1);

        asyncStub.withDeadlineAfter(deadlineMs, TimeUnit.MILLISECONDS)
                .streamValues(request, new StreamObserver<StreamResponse>() {
                    @Override
                    public void onNext(StreamResponse value) {
                        System.out.println("[Client] Stream received: " + value.getCounterValue());
                    }
                    @Override
                    public void onError(Throwable t) {
                        System.err.println("[Client] Deadline exceeded or error: " + t.getMessage());
                        latch.countDown();
                    }
                    @Override
                    public void onCompleted() {
                        System.out.println("[Client] Stream completed.");
                        latch.countDown();
                    }
                });

        latch.await();
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws Exception {
        CounterClient client = new CounterClient("localhost", 50051);

        // Part 4 — basic calls
        client.increment(5);
        client.increment(3);

        // Part 4 — server streaming
        client.streamValues(5, 200);

        // Part 6 — streaming with 700ms deadline
        System.out.println("\n--- Testing deadline (700ms) ---");
        client.streamValuesWithDeadline(10, 300, 700);

        client.shutdown();
    }
}