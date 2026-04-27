import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpServer {
    private static final int PORT = 5000;
    private static final int THREADS = 50;

    public static void main(String[] args) {
        int port = (args.length >= 1) ? Integer.parseInt(args[0]) : PORT;
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        System.out.println("Starting TCP server on port " + port + " ...");

        try (ServerSocket server = new ServerSocket(port)) {
            server.setReuseAddress(true);
            while (true) {
                Socket client = server.accept();
                pool.submit(new ClientHandler(client));
            }
        } catch (IOException e) {
            System.err.println("Server crashed: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket socket;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            String clientId = socket.getRemoteSocketAddress().toString();
            System.out.println("[" + LocalDateTime.now() + "] Connected: " + clientId);

            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))
            ) {
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("[" + LocalDateTime.now() + "] From " + clientId + ": " + line);
                    Thread.sleep(1000); // simulate delay
                    String reply = "ACK from server @ " + LocalDateTime.now() + " | received: " + line;
                    out.write(reply);
                    out.newLine();
                    out.flush();
                }
                System.out.println("[" + LocalDateTime.now() + "] Client closed: " + clientId);

            } catch (SocketException se) {
                System.out.println("[" + LocalDateTime.now() + "] Abrupt disconnect: " + clientId + " (" + se.getMessage() + ")");
            } catch (Exception e) {
                System.out.println("[" + LocalDateTime.now() + "] Error with " + clientId + ": " + e.getMessage());
            } finally {
                try { socket.close(); } catch (IOException ignored) {}
            }
        }
    }
}