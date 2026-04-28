Notes to keep in mind
1. Run mvn compile before anything

The .proto file is not Java. Maven runs the protobuf compiler to generate the stub classes. If you skip this step, CounterServiceGrpc, IncrementRequest, etc. don't exist yet and nothing compiles.

2. Never edit generated files

Everything in target/generated-sources is auto-generated. Every mvn compile overwrites them. Put your logic only in CounterServer.java and CounterClient.java.

3. AtomicInteger not int

Two clients hitting the server at the same time means two threads incrementing the counter simultaneously. Plain int or even Integer will give wrong results. AtomicInteger is thread-safe.

4. blockingStub vs asyncStub

blockingStub — client waits for the full response. Good for unary calls.
asyncStub — client gets callbacks via StreamObserver. Required for streaming because responses come one by one, not all at once.

5. CountDownLatch for streaming

Streaming is async — the main thread would finish and exit before all stream responses arrive. CountDownLatch(1) makes the main thread wait until onCompleted() or onError() fires the latch.

6. How the deadline works

withDeadlineAfter(700, TimeUnit.MILLISECONDS) means the entire stream must complete within 700ms. The server sends values every 300ms — so after ~2 values the deadline hits and onError() fires with DEADLINE_EXCEEDED. This is gRPC's version of a timeout.

7. Port 50051

gRPC convention. Not enforced but expected. Don't use 5000 — that's the Lab 4 TCP server port.

8. usePlaintext()

Disables TLS. Fine for lab/local testing. In production you'd use SSL certificates.