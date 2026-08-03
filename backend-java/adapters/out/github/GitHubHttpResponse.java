package imperator.adapters.out.github;

import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

record GitHubHttpResponse(int statusCode, HttpHeaders headers, byte[] body) {
    GitHubHttpResponse {
        body = body.clone();
    }

    Optional<String> header(String name) {
        return headers.firstValue(name);
    }

    String bodyAsString() {
        return new String(body, StandardCharsets.UTF_8);
    }

    @Override
    public byte[] body() {
        return body.clone();
    }
}
