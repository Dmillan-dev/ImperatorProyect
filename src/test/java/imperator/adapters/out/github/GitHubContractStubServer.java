package imperator.adapters.out.github;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

final class GitHubContractStubServer implements AutoCloseable {
    enum Mode {
        HAPPY,
        NO_MATCH,
        AMBIGUOUS,
        PARTIAL,
        PAGINATED_RATE_LIMIT,
        TRANSIENT_RETRY,
        PAGE_LIMIT,
        RESPONSE_TOO_LARGE,
        NOT_FOUND,
        CROSS_ORIGIN_REDIRECT
    }

    record RequestSnapshot(
            String method,
            String path,
            String query,
            String accept,
            String apiVersion,
            String userAgent,
            String authorization
    ) {
    }

    private final Mode mode;
    private final HttpServer server;
    private final List<RequestSnapshot> requests = new CopyOnWriteArrayList<>();
    private final AtomicInteger repositoryCalls = new AtomicInteger();

    GitHubContractStubServer(Mode mode) throws IOException {
        this.mode = mode;
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", this::handle);
        server.start();
    }

    URI baseUri() {
        return URI.create("http://127.0.0.1:" + server.getAddress().getPort());
    }

    List<RequestSnapshot> requests() {
        return List.copyOf(requests);
    }

    private void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getRawQuery();
        requests.add(new RequestSnapshot(
                exchange.getRequestMethod(),
                path,
                query == null ? "" : query,
                exchange.getRequestHeaders().getFirst("Accept"),
                exchange.getRequestHeaders().getFirst("X-GitHub-Api-Version"),
                exchange.getRequestHeaders().getFirst("User-Agent"),
                exchange.getRequestHeaders().getFirst("Authorization")
        ));

        if ("/repos/acme/imperator-demo".equals(path)) {
            repository(exchange);
            return;
        }
        if ("/repos/acme/imperator-demo/pulls".equals(path)) {
            pulls(exchange, query == null ? "" : query);
            return;
        }
        if ("/repos/acme/imperator-demo/pulls/184".equals(path)) {
            json(exchange, 200, pull(184, true));
            return;
        }
        if ("/repos/acme/imperator-demo/pulls/184/commits".equals(path)) {
            json(exchange, 200, "[{\"sha\":\"head-sha-184\"}]");
            return;
        }
        if ("/repos/acme/imperator-demo/pulls/184/reviews".equals(path)) {
            json(exchange, 200, mode == Mode.PARTIAL ? "[]" : review());
            return;
        }
        if ("/repos/acme/imperator-demo/deployments".equals(path)) {
            json(exchange, 200, mode == Mode.PARTIAL ? "[]" : deployment());
            return;
        }
        if ("/repos/acme/imperator-demo/deployments/601/statuses".equals(path)) {
            json(exchange, 200, deploymentStatus());
            return;
        }
        json(exchange, 404, "{\"message\":\"not found\"}");
    }

    private void repository(HttpExchange exchange) throws IOException {
        if (mode == Mode.NOT_FOUND) {
            json(exchange, 404, "{\"message\":\"not found\"}");
            return;
        }
        if (mode == Mode.CROSS_ORIGIN_REDIRECT) {
            exchange.getResponseHeaders().add("Location", "https://example.com/provider");
            exchange.sendResponseHeaders(302, -1);
            exchange.close();
            return;
        }
        if (mode == Mode.PAGINATED_RATE_LIMIT && repositoryCalls.getAndIncrement() == 0) {
            exchange.getResponseHeaders().add("Retry-After", "1");
            json(exchange, 429, "{\"message\":\"secondary rate limit\"}");
            return;
        }
        if (mode == Mode.TRANSIENT_RETRY && repositoryCalls.getAndIncrement() < 2) {
            json(exchange, 503, "{\"message\":\"temporarily unavailable\"}");
            return;
        }
        if (mode == Mode.RESPONSE_TOO_LARGE) {
            json(exchange, 200, "x".repeat(GitHubHttpClient.MAX_RESPONSE_BYTES + 1));
            return;
        }
        json(exchange, 200, """
                {
                  "name":"imperator-demo",
                  "default_branch":"main",
                  "owner":{"login":"acme","type":"Organization"}
                }
                """);
    }

    private void pulls(HttpExchange exchange, String query) throws IOException {
        if (mode == Mode.NO_MATCH) {
            json(exchange, 200, "[" + pull(184, false) + "]");
            return;
        }
        if (mode == Mode.AMBIGUOUS) {
            json(exchange, 200, "[" + pull(184, true) + "," + pull(185, true) + "]");
            return;
        }
        if (mode == Mode.PAGINATED_RATE_LIMIT && !query.contains("page=2")) {
            exchange.getResponseHeaders().add(
                    "Link",
                    "<" + baseUri() + "/repos/acme/imperator-demo/pulls?state=closed"
                            + "&base=main&sort=updated&direction=desc&per_page=100&page=2>; rel=\"next\""
            );
            json(exchange, 200, "[]");
            return;
        }
        if (mode == Mode.PAGE_LIMIT) {
            int currentPage = page(query);
            exchange.getResponseHeaders().add(
                    "Link",
                    "<" + baseUri() + "/repos/acme/imperator-demo/pulls?state=closed"
                            + "&base=main&sort=updated&direction=desc&per_page=100&page="
                            + (currentPage + 1) + ">; rel=\"next\""
            );
            json(exchange, 200, "[]");
            return;
        }
        json(exchange, 200, "[" + pull(184, true) + "]");
    }

    private static int page(String query) {
        for (String parameter : query.split("&")) {
            if (parameter.startsWith("page=")) {
                return Integer.parseInt(parameter.substring("page=".length()));
            }
        }
        return 1;
    }

    private static String pull(long number, boolean matching) {
        String issue = matching ? "IMP-214" : "IMP-2140";
        return """
                {
                  "number":%d,
                  "node_id":"PR_node_%d",
                  "state":"closed",
                  "title":"%s raw-title-must-not-persist",
                  "body":"raw-body-must-not-persist %s",
                  "merged_at":"2026-04-02T12:00:00Z",
                  "merge_commit_sha":"merge-sha-%d",
                  "user":{"login":"author"},
                  "merged_by":{"login":"platform-engineer"},
                  "head":{"ref":"feature/%s-onboarding"},
                  "base":{
                    "ref":"main",
                    "repo":{"full_name":"acme/imperator-demo"}
                  }
                }
                """.formatted(number, number, issue, issue, number, issue);
    }

    private static String review() {
        return """
                [{
                  "id":501,
                  "state":"APPROVED",
                  "submitted_at":"2026-04-02T11:00:00Z",
                  "commit_id":"head-sha-184",
                  "body":"review-body-must-not-persist",
                  "user":{"login":"reviewer"}
                }]
                """;
    }

    private static String deployment() {
        return """
                [{"id":601,"sha":"merge-sha-184","environment":"production"}]
                """;
    }

    private static String deploymentStatus() {
        return """
                [{
                  "id":701,
                  "state":"success",
                  "created_at":"2026-04-05T08:00:00Z",
                  "creator":{"login":"deployment-bot"}
                }]
                """;
    }

    private static void json(HttpExchange exchange, int status, String body) throws IOException {
        byte[] payload = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, payload.length);
        exchange.getResponseBody().write(payload);
        exchange.close();
    }

    @Override
    public void close() {
        server.stop(0);
    }
}
