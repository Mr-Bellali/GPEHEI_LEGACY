package utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET =
            "GPEHEI_LAGACY_SECRET_KEY_2026_ADMIN_SYSTEM_123456";

    private static final SecretKey KEY =
            Keys.hmacShaKeyFor(
                    SECRET.getBytes(StandardCharsets.UTF_8)
            );

    private static final long EXPIRATION =
            1000 * 60 * 60 * 24; // 24h

    private JwtUtil(){}

    public static String generateToken(
            int adminId,
            String email,
            String role,
            String first_name
    ) {

        return Jwts.builder()
                .subject(email)
                .claim("name",first_name)
                .claim("adminId", adminId)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + EXPIRATION
                        )
                )
                .signWith(KEY)
                .compact();
    }

    public static Claims extractClaims(
            String token
    ) {

        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static boolean isValid(
            String token
    ) {

        try {

            extractClaims(token);

            return true;

        } catch (Exception e) {

            return false;
        }
    }
}

/*
to use jwt download 3 lib and add all libere find in folder lib
like if want to download :
1-https://repo1.maven.org/maven2/io/jsonwebtoken/jjwt-api/0.12.6/jjwt-api-0.12.6.jar
2-https://repo1.maven.org/maven2/io/jsonwebtoken/jjwt-impl/0.12.6/jjwt-impl-0.12.6.jar
3-https://repo1.maven.org/maven2/io/jsonwebtoken/jjwt-jackson/0.12.6/jjwt-jackson-0.12.6.jar
also need 3 bib for serialisation
1-https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.17.0/jackson-databind-2.17.0.jar
2-https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.17.0/jackson-core-2.17.0.jar
3-https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.17.0/jackson-annotations-2.17.0.jar
 */