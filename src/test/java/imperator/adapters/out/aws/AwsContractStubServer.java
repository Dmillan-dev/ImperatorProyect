package imperator.adapters.out.aws;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

final class AwsContractStubServer implements AutoCloseable {
    enum Mode {
        HAPPY,
        ACCOUNT_MISMATCH,
        ESTIMATED_COST,
        MISSING_OWNER,
        MISSING_METRICS,
        UNSUPPORTED_RESOURCE,
        PAGINATION_LOOP,
        NON_EUR_COST,
        THROTTLED_TAG,
        FORBIDDEN_TAG
    }

    record RequestSnapshot(
            String target,
            String authorization,
            String contentType,
            String accept,
            String smithyProtocol,
            String queryMode,
            String body
    ) {
    }

    private static final String ACCOUNT = "123456789012";
    private static final String LAMBDA_ARN = "arn:aws:lambda:eu-west-1:" + ACCOUNT
            + ":function:onboarding-assistant";

    private final Mode mode;
    private final HttpServer server;
    private final List<RequestSnapshot> requests = new CopyOnWriteArrayList<>();

    AwsContractStubServer(Mode mode) throws IOException {
        this.mode = mode;
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", this::handle);
        server.start();
    }

    URI endpoint() {
        return URI.create("http://127.0.0.1:" + server.getAddress().getPort());
    }

    List<RequestSnapshot> requests() {
        return List.copyOf(requests);
    }

    private void handle(HttpExchange exchange) throws IOException {
        byte[] requestBytes = exchange.getRequestBody().readAllBytes();
        String body = new String(requestBytes, StandardCharsets.ISO_8859_1);
        String target = exchange.getRequestHeaders().getFirst("X-Amz-Target");
        if (target == null) {
            target = formAction(body);
        }
        if (target.isEmpty()
                && "application/cbor".equals(exchange.getRequestHeaders().getFirst("Content-Type"))) {
            target = "GetMetricData";
        }
        requests.add(new RequestSnapshot(
                target,
                exchange.getRequestHeaders().getFirst("Authorization"),
                exchange.getRequestHeaders().getFirst("Content-Type"),
                exchange.getRequestHeaders().getFirst("Accept"),
                exchange.getRequestHeaders().getFirst("smithy-protocol"),
                exchange.getRequestHeaders().getFirst("x-amzn-query-mode"),
                body
        ));
        if ("GetCallerIdentity".equals(target)) {
            xml(exchange, sts());
            return;
        }
        if (target != null && target.endsWith(".GetResources")) {
            if (mode == Mode.THROTTLED_TAG) {
                awsError(exchange, "ThrottlingException");
                return;
            }
            if (mode == Mode.FORBIDDEN_TAG) {
                awsError(exchange, "AccessDeniedException");
                return;
            }
            json(exchange, resources(body));
            return;
        }
        if (target != null && target.endsWith(".GetCostAndUsage")) {
            json(exchange, cost());
            return;
        }
        if ("GetMetricData".equals(target)) {
            cbor(exchange, metrics());
            return;
        }
        json(exchange, 400, "{\"__type\":\"UnknownOperationException\"}");
    }

    private String sts() {
        String account = mode == Mode.ACCOUNT_MISMATCH ? "999999999999" : ACCOUNT;
        return """
                <GetCallerIdentityResponse xmlns="https://sts.amazonaws.com/doc/2011-06-15/">
                  <GetCallerIdentityResult>
                    <Arn>arn:aws:iam::%s:role/dummy-test-role</Arn>
                    <UserId>DUMMYTEST</UserId>
                    <Account>%s</Account>
                  </GetCallerIdentityResult>
                  <ResponseMetadata><RequestId>stub-sts-request</RequestId></ResponseMetadata>
                </GetCallerIdentityResponse>
                """.formatted(account, account);
    }

    private String resources(String requestBody) {
        String token = "";
        if (mode == Mode.PAGINATION_LOOP) {
            token = "loop-token";
        }
        if (mode == Mode.PAGINATION_LOOP && requestBody.contains("loop-token")) {
            return "{\"PaginationToken\":\"loop-token\",\"ResourceTagMappingList\":[]}";
        }
        String arn = mode == Mode.UNSUPPORTED_RESOURCE
                ? "arn:aws:s3:::onboarding-assistant-prod"
                : LAMBDA_ARN;
        String owner = mode == Mode.MISSING_OWNER
                ? ""
                : ",{\"Key\":\"owner\",\"Value\":\"platform-team\"}";
        return """
                {
                  "PaginationToken":"%s",
                  "ResourceTagMappingList":[{
                    "ResourceARN":"%s",
                    "Tags":[
                      {"Key":"project","Value":"customer-onboarding"},
                      {"Key":"jira_ticket","Value":"IMP-214"},
                      {"Key":"resource_group","Value":"onboarding-assistant-prod"}%s
                    ]
                  }]
                }
                """.formatted(token, arn, owner);
    }

    private String cost() {
        String amount = mode == Mode.NON_EUR_COST ? "99.95" : "410.000";
        String unit = mode == Mode.NON_EUR_COST ? "USD" : "EUR";
        return """
                {
                  "ResultsByTime":[{
                    "TimePeriod":{"Start":"2026-06-01","End":"2026-07-01"},
                    "Total":{"UnblendedCost":{"Amount":"%s","Unit":"%s"}},
                    "Groups":[],
                    "Estimated":%s
                  }],
                  "DimensionValueAttributes":[]
                }
                """.formatted(amount, unit, mode == Mode.ESTIMATED_COST);
    }

    private List<MetricResult> metrics() {
        if (mode == Mode.MISSING_METRICS) {
            return List.of(new MetricResult("l00i", 42.0));
        }
        return List.of(
                new MetricResult("l00i", 42.0),
                new MetricResult("l00e", 2.0),
                new MetricResult("l00d", 1234.5678)
        );
    }

    private static String formAction(String body) {
        for (String parameter : body.split("&")) {
            if (parameter.startsWith("Action=")) {
                return parameter.substring("Action=".length());
            }
        }
        return "";
    }

    private static void json(HttpExchange exchange, String body) throws IOException {
        json(exchange, 200, body);
    }

    private static void json(HttpExchange exchange, int status, String body) throws IOException {
        respond(exchange, status, "application/x-amz-json-1.1", body);
    }

    private static void awsError(HttpExchange exchange, String type) throws IOException {
        exchange.getResponseHeaders().add("x-amzn-errortype", type);
        json(exchange, 400, "{\"__type\":\"" + type + "\",\"message\":\"redacted\"}");
    }

    private static void xml(HttpExchange exchange, String body) throws IOException {
        respond(exchange, 200, "text/xml", body);
    }

    private static void cbor(HttpExchange exchange, List<MetricResult> results) throws IOException {
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        cborMap(body, 1);
        cborText(body, "MetricDataResults");
        cborArray(body, results.size());
        for (MetricResult result : results) {
            cborMap(body, 4);
            cborText(body, "Id");
            cborText(body, result.id());
            cborText(body, "Label");
            cborText(body, result.id());
            cborText(body, "Values");
            cborArray(body, 1);
            cborDouble(body, result.value());
            cborText(body, "StatusCode");
            cborText(body, "Complete");
        }
        byte[] payload = body.toByteArray();
        exchange.getResponseHeaders().add("Content-Type", "application/cbor");
        exchange.getResponseHeaders().add("smithy-protocol", "rpc-v2-cbor");
        exchange.getResponseHeaders().add("x-amzn-RequestId", "dummy-request-id");
        exchange.sendResponseHeaders(200, payload.length);
        exchange.getResponseBody().write(payload);
        exchange.close();
    }

    private static void cborMap(ByteArrayOutputStream output, int size) {
        cborLength(output, 5, size);
    }

    private static void cborArray(ByteArrayOutputStream output, int size) {
        cborLength(output, 4, size);
    }

    private static void cborText(ByteArrayOutputStream output, String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        cborLength(output, 3, bytes.length);
        output.writeBytes(bytes);
    }

    private static void cborDouble(ByteArrayOutputStream output, double value) {
        output.write(0xfb);
        long bits = Double.doubleToLongBits(value);
        for (int shift = 56; shift >= 0; shift -= 8) {
            output.write((int) (bits >>> shift) & 0xff);
        }
    }

    private static void cborLength(ByteArrayOutputStream output, int majorType, int length) {
        if (length < 24) {
            output.write((majorType << 5) | length);
            return;
        }
        if (length < 256) {
            output.write((majorType << 5) | 24);
            output.write(length);
            return;
        }
        throw new IllegalArgumentException("Test CBOR value is too large");
    }

    private static void respond(
            HttpExchange exchange,
            int status,
            String contentType,
            String body
    ) throws IOException {
        byte[] payload = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", contentType);
        exchange.getResponseHeaders().add("x-amzn-RequestId", "dummy-request-id");
        exchange.sendResponseHeaders(status, payload.length);
        exchange.getResponseBody().write(payload);
        exchange.close();
    }

    @Override
    public void close() {
        server.stop(0);
    }

    private record MetricResult(String id, double value) {
    }
}
