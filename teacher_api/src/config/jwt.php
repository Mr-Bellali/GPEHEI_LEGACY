<?php

class JWT
{
    private static string $secret = 'testing-jwt-signature';

    public static function generate(array $payload): string
    {
        $header  = self::base64url(json_encode(['alg' => 'HS256', 'typ' => 'JWT']));
        $payload = self::base64url(json_encode($payload));
        $signature = self::base64url(hash_hmac('sha256', "$header.$payload", self::$secret, true));

        return "$header.$payload.$signature";
    }

    public static function verify(string $token): ?array
    {
        $parts = explode('.', $token);

        if (count($parts) !== 3) return null;

        [$header, $payload, $signature] = $parts;

        $expected = self::base64url(hash_hmac('sha256', "$header.$payload", self::$secret, true));

        if (!hash_equals($expected, $signature)) return null;

        $data = json_decode(base64_decode($payload), true);

        // Check expiration
        if (isset($data['exp']) && $data['exp'] < time()) return null;

        return $data;
    }

    private static function base64url(string $data): string
    {
        return rtrim(strtr(base64_encode($data), '+/', '-_'), '=');
    }
}