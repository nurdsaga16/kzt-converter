import com.sun.net.httpserver.HttpServer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Plain test runner, no JUnit. Starts the real server on a free port (0)
 * and calls it over HTTP. Each check can fail on its own.
 */
public class ServerTests {
    static int total = 0, passed = 0;
    static HttpClient client = HttpClient.newHttpClient();
    static int port;

    static void check(String name, boolean ok) {
        total++;
        if (ok) { passed++; System.out.println("PASS " + name); }
        else System.out.println("FAIL " + name);
    }

    static HttpResponse<String> get(String path) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path)).build();
        return client.send(req, HttpResponse.BodyHandlers.ofString());
    }

    public static void main(String[] args) throws Exception {
        HttpServer server = Server.makeServer(0);
        server.start();
        port = server.getAddress().getPort();

        try {
            HttpResponse<String> root = get("/");
            check("root answers 200", root.statusCode() == 200);

            HttpResponse<String> health = get("/healthz");
            check("healthz is 200 and non-empty", health.statusCode() == 200 && !health.body().isBlank());

            HttpResponse<String> rates = get("/rates");
            check("rates lists USD", rates.statusCode() == 200 && rates.body().contains("USD 500"));
        } finally {
            server.stop(0);
        }

        System.out.println("TESTS: " + passed + "/" + total);
        System.exit(passed == total ? 0 : 1);
    }
}
