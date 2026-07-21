package imperator.adapters.out.postgresql;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

final class PostgresFlatMetadataJson {
    private PostgresFlatMetadataJson() {
    }

    static String toJson(Map<String, String> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return "{}";
        }

        StringBuilder builder = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> entry : metadata.entrySet()) {
            if (!first) {
                builder.append(',');
            }
            builder.append('"').append(escapeJson(entry.getKey())).append('"');
            builder.append(':');
            builder.append('"').append(escapeJson(entry.getValue())).append('"');
            first = false;
        }
        return builder.append('}').toString();
    }

    static Map<String, String> fromJson(String json) {
        if (json == null || json.isBlank() || "{}".equals(json.trim())) {
            return Map.of();
        }
        return new Parser(json).parse();
    }

    private static String escapeJson(String value) {
        Objects.requireNonNull(value, "Metadata value is required");

        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            if (character == '"' || character == '\\') {
                builder.append('\\').append(character);
            } else if (character == '\n') {
                builder.append("\\n");
            } else if (character == '\r') {
                builder.append("\\r");
            } else if (character == '\t') {
                builder.append("\\t");
            } else if (character == '\b') {
                builder.append("\\b");
            } else if (character == '\f') {
                builder.append("\\f");
            } else if (character < 0x20) {
                builder.append(String.format("\\u%04x", (int) character));
            } else {
                builder.append(character);
            }
        }
        return builder.toString();
    }

    private static final class Parser {
        private final String json;
        private int position;

        private Parser(String json) {
            this.json = Objects.requireNonNull(json, "Metadata JSON is required").trim();
        }

        private Map<String, String> parse() {
            expect('{');
            Map<String, String> values = new LinkedHashMap<>();
            skipWhitespace();
            if (peek('}')) {
                position++;
                ensureFinished();
                return Map.copyOf(values);
            }
            while (position < json.length()) {
                String key = parseString();
                skipWhitespace();
                expect(':');
                String value = parseString();
                values.put(key, value);
                skipWhitespace();
                if (peek(',')) {
                    position++;
                    continue;
                }
                expect('}');
                ensureFinished();
                return Map.copyOf(values);
            }
            throw invalidMetadata();
        }

        private String parseString() {
            skipWhitespace();
            expect('"');

            StringBuilder builder = new StringBuilder();
            while (position < json.length()) {
                char character = json.charAt(position++);
                if (character == '"') {
                    return builder.toString();
                }
                if (character == '\\') {
                    builder.append(parseEscapedCharacter());
                } else {
                    builder.append(character);
                }
            }
            throw invalidMetadata();
        }

        private char parseEscapedCharacter() {
            if (position >= json.length()) {
                throw invalidMetadata();
            }
            char escaped = json.charAt(position++);
            return switch (escaped) {
                case '"', '\\', '/' -> escaped;
                case 'n' -> '\n';
                case 'r' -> '\r';
                case 't' -> '\t';
                case 'b' -> '\b';
                case 'f' -> '\f';
                case 'u' -> parseUnicodeCharacter();
                default -> throw invalidMetadata();
            };
        }

        private char parseUnicodeCharacter() {
            if (position + 4 > json.length()) {
                throw invalidMetadata();
            }
            String value = json.substring(position, position + 4);
            position += 4;
            try {
                return (char) Integer.parseInt(value, 16);
            } catch (NumberFormatException exception) {
                throw invalidMetadata();
            }
        }

        private void expect(char expected) {
            skipWhitespace();
            if (position >= json.length() || json.charAt(position) != expected) {
                throw invalidMetadata();
            }
            position++;
        }

        private boolean peek(char expected) {
            skipWhitespace();
            return position < json.length() && json.charAt(position) == expected;
        }

        private void skipWhitespace() {
            while (position < json.length() && Character.isWhitespace(json.charAt(position))) {
                position++;
            }
        }

        private void ensureFinished() {
            skipWhitespace();
            if (position != json.length()) {
                throw invalidMetadata();
            }
        }

        private IllegalStateException invalidMetadata() {
            return new IllegalStateException("Persisted metadata must be a flat string map");
        }
    }
}
