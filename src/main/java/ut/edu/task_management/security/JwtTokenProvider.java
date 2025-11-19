package ut.edu.task_management.security;

import org.springframework.stereotype.Component; 
import ut.edu.task_management.constants.AppConstants;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Lightweight JWT helper using HMAC-SHA256 without external dependencies.
 * NOTE: For production use, prefer a vetted library and store secrets securely.
 */
<<<<<<< HEAD
public class    JwtTokenProvider {
=======
@Component // <-- NEW
public class JwtTokenProvider {
>>>>>>> e1071296e8548a17cd4f310dbc454ff030755efb

    private static final String HMAC_ALGO = "HmacSHA256";

    // Simple creation of JWT token (header.payload.signature)
    public String generateToken(String username) {
        try {
            String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
            long now = Instant.now().toEpochMilli();
            long exp = now + AppConstants.JWT_EXPIRATION_MS;
            String payloadJson = String.format("{\"sub\":\"%s\",\"iat\":%d,\"exp\":%d}", username, now/1000, exp/1000);

            String header = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));
            String payload = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));
            String unsigned = header + "." + payload;
            String signature = sign(unsigned, AppConstants.JWT_SECRET);
            return unsigned + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate token", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            if (token == null || token.isBlank()) return false;
            String[] parts = token.split(Pattern.quote("."));
            if (parts.length != 3) return false;
            String unsigned = parts[0] + "." + parts[1];
            String signature = parts[2];
            String expected = sign(unsigned, AppConstants.JWT_SECRET);
            if (!constantTimeEquals(expected, signature)) return false;
            // validate exp
            String payloadJson = new String(base64UrlDecode(parts[1]), StandardCharsets.UTF_8);
            // crude parse for exp
            long exp = parseLongFromJson(payloadJson, "exp");
            long now = Instant.now().getEpochSecond();
            return exp == 0 || exp > now;
        } catch (Exception ex) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        if (token == null) return null;
        String[] parts = token.split(Pattern.quote("."));
        if (parts.length != 3) return null;
        String payloadJson = new String(base64UrlDecode(parts[1]), StandardCharsets.UTF_8);
        return parseStringFromJson(payloadJson, "sub");
    }

    // --- helpers ---
    private static String sign(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance(HMAC_ALGO);
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGO));
        byte[] sig = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return base64UrlEncode(sig);
    }

    private static String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static byte[] base64UrlDecode(String str) { return Base64.getUrlDecoder().decode(str); }

    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) result |= a.charAt(i) ^ b.charAt(i);
        return result == 0;
    }

    private static long parseLongFromJson(String json, String key) {
        String val = parseStringFromJson(json, key);
        try { return val == null ? 0L : Long.parseLong(val); } catch (Exception e) { return 0L; }
    }

    private static String parseStringFromJson(String json, String key) {
        // Very small JSON parsing helper (assumes no nested quotes)
        String pattern = "\"" + key + "\"\s*:\s*";
        int idx = json.indexOf(pattern);
        if (idx == -1) return null;
        int start = idx + pattern.length();
        char c = json.charAt(start);
        if (c == '\"') {
            int end = json.indexOf('"', start + 1);
            return json.substring(start + 1, end);
        } else {
            // numeric
            int end = start;
            while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end)=='-')) end++;
            return json.substring(start, end);
        }
    }
}
