import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Server {

    public static HttpServer makeServer(int port) throws IOException {
        // 0.0.0.0, not 127.0.0.1 — so it is reachable from outside a container (week 5)
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

        server.createContext("/healthz", ex -> send(ex, 200, "ok"));
        server.createContext("/rates", ex -> send(ex, 200, Converter.ratesText()));

        // GET /convert?from=USD&to=KZT&amount=10  ->  5000.00
        server.createContext("/convert", ex -> {
            Map<String, String> q = query(ex.getRequestURI().getRawQuery());
            String from = q.get("from"), to = q.get("to"), amount = q.get("amount");
            if (from == null || to == null || amount == null) {
                send(ex, 400, "need from, to and amount");
                return;
            }
            if (!Converter.supports(from) || !Converter.supports(to)) {
                send(ex, 400, "unsupported currency");
                return;
            }
            try {
                BigDecimal result = Converter.convert(from, to, new BigDecimal(amount));
                send(ex, 200, result.toPlainString());
            } catch (NumberFormatException e) {
                send(ex, 400, "amount must be a number");
            }
        });

        server.createContext("/", ex -> {
            if (ex.getRequestURI().getPath().equals("/")) {
                send(ex, 200, "KZT converter. Try /rates or /convert?from=USD&to=KZT&amount=10");
            } else {
                send(ex, 404, "not found");
            }
        });

        return server;
    }

    static Map<String, String> query(String raw) {
        Map<String, String> params = new HashMap<>();
        if (raw == null || raw.isEmpty()) return params;
        for (String pair : raw.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                params.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                           URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
            }
        }
        return params;
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
