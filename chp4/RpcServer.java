import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

public class RpcServer {
    private final int port;
    private final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

    public RpcServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("[Server] Listening on " + port);
            while (true) {
                try (Socket client = server.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
                     BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8))) {

                    String line;
                    while ((line = in.readLine()) != null) {
                        out.write(dispatch(line.trim()));
                        out.newLine();
                        out.flush();
                    }
                }
            }
        }
    }

    private String dispatch(String line) {
        if (line.isEmpty()) return "ERR empty_request";

        String[] parts = line.split(" ", 3);
        String cmd = parts[0].toUpperCase();

        switch (cmd) {
            case "PING":
                return "OK pong";
            case "PUT":
                if (parts.length < 3) return "ERR usage: PUT key value";
                map.put(parts[1], parts[2]);
                return "OK";
            case "GET":
                if (parts.length < 2) return "ERR usage: GET key";
                // Uncomment below to simulate delay for timeout testing (Part 3+4)
                // try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                String v = map.get(parts[1]);
                return (v == null) ? "OK (null)" : "OK " + v;
            default:
                return "ERR unknown_command";
        }
    }

    public static void main(String[] args) throws Exception {
        new RpcServer(5000).start();
    }
}