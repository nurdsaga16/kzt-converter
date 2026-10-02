import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class Server {

    public static HttpServer makeServer(int port) throws IOException {
        // 0.0.0.0, not 127.0.0.1 — so it is reachable from outside a container (week 5)
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

        server.createContext("/healthz", ex -> send(ex, 200, "ok"));

        server.createContext("/", ex -> {
            if (ex.getRequestURI().getPath().equals("/")) {
                send(ex, 200, "KZT converter. Try /rates or /convert?from=USD&to=KZT&amount=10");
            } else {
                send(ex, 404, "not found");
            }
        });

        return server;
    }

    static void send(HttpExchange ex, int code, String body) {
        try {
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
            ex.sendResponseHeaders(code, bytes.length);
            try (OutputStream os = ex.getResponseBody()) {
                os.write(bytes);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        // whoever runs the service decides the port, not the code
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        makeServer(port).start();
        System.out.println("Listening on port " + port);
    }
}
