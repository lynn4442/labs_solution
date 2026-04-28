package lab.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class CounterServer {

    private final int port;
    private Server server;

    public CounterServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new CounterServiceImpl())
                .build()
                .start();
        System.out.println("[Server] Running on port " + port);
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) server.awaitTermination();
    }

    static class CounterServiceImpl extends CounterServiceGrpc.CounterServiceImplBase {

        private final AtomicInteger counter = new AtomicInteger(0);

        // Part 3 — Unary RPC
        @Override
        public void increment(IncrementRequest request, StreamObserver<IncrementResponse> responseObserver) {
            int newValue = counter.addAndGet(request.getValue());
            System.out.println("[Server] Increment by " + request.getValue() + " → counter = " + newValue);

            IncrementResponse response = IncrementResponse.newBuilder()
                    .setNewValue(newValue)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        // Part 3 — Server streaming RPC
        @Override
        public void streamValues(StreamRequest request, StreamObserver<StreamResponse> responseObserver) {
            int count = request.getCount();
            int delayMs = request.getDelayMs();

            System.out.println("[Server] Streaming " + count + " values with " + delayMs + "ms delay");

            for (int i = 0; i < count; i++) {
                int value = counter.incrementAndGet();

                StreamResponse response = StreamResponse.newBuilder()
                        .setCounterValue(value)
                        .build();

                responseObserver.onNext(response);

                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException e) {
                    responseObserver.onError(e);
                    return;
                }
            }
            responseObserver.onCompleted();
        }
    }

    public static void main(String[] args) throws Exception {
        CounterServer server = new CounterServer(50051);
        server.start();
        server.blockUntilShutdown();
    }
}