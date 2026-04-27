import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class RpcClient implements Closeable {
    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;

    // Constructor with timeout (Parts 3 & 4)
    public RpcClient(String host, int port, int timeoutMs) throws IOException {
        socket = new Socket(host, port);
        socket.setSoTimeout(timeoutMs);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        System.out.println("[Client] Connected to " + host + ":" + port + " (timeout=" + timeoutMs + "ms)");
    }

    // Simple call — no retry
    public String call(String request) throws IOException {
        send(request);
        return readResponse();
    }

    // Call with ONE retry on timeout (Part 4)
    public String callWithOneRetry(String request) throws IOException {
        try {
            send(request);
            return readResponse();
        } catch (SocketTimeoutException e) {
            System.out.println("[Client] Timeout. Retrying once: " + request);
            send(request);
            try {
                return readResponse();
            } catch (SocketTimeoutException e2) {
                throw new IOException("Timeout after one retry");
            }
        }
    }

    private void send(String request) throws IOException {
        out.write(request);
        out.newLine();
        out.flush();
    }

    private String readResponse() throws IOException {
        String response = in.readLine();
        if (response == null) throw new IOException("Server closed connection");
        return response;
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}