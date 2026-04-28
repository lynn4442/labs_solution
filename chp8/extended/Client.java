import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws Exception {
        Socket      socket = new Socket("localhost", 6000);
        PrintWriter out    = new PrintWriter(socket.getOutputStream(), true);
        out.println("increment");
        socket.close();
        System.out.println("Sent: increment");
    }
}
