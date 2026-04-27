public class RunClient {
    public static void main(String[] args) throws Exception {
        // Use 500ms to trigger timeouts, 2000ms for normal runs
        int timeoutMs = 2000;

        try (RpcClient client = new RpcClient("127.0.0.1", 5000, timeoutMs)) {

            // Part 1+2 — basic commands
            System.out.println("PING => " + client.call("PING"));
            System.out.println("PUT name Sandy => " + client.call("PUT name Sandy"));
            System.out.println("GET name => " + client.call("GET name"));
            System.out.println("PUT note Sandy is a CS student => " + client.call("PUT note Sandy is a CS student"));
            System.out.println("GET note => " + client.call("GET note"));
            System.out.println("GET unknown => " + client.call("GET unknown"));
            System.out.println("BAD COMMAND => " + client.call("HELLO"));

            // Part 3+4 — timeout and retry
            System.out.println("PUT name Aline => " + client.callWithOneRetry("PUT name Aline"));
            System.out.println("GET name => " + client.callWithOneRetry("GET name"));
            System.out.println("PUT note ... => " + client.callWithOneRetry("PUT note This is a longer value with spaces"));
            System.out.println("GET note => " + client.callWithOneRetry("GET note"));
        }
    }
}