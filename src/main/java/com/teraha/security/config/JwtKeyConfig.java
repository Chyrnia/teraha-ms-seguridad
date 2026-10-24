package com.teraha.security.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
public class JwtKeyConfig {

    @Bean
    public KeyPair jwtKeyPair(
            @Value("${security.jwt.private-key-location}") String privatePath,
            @Value("${security.jwt.public-key-location}") String publicPath)
            throws IOException, GeneralSecurityException {

        String privatePem = Files.readString(Path.of(privatePath));
        String publicPem = Files.readString(Path.of(publicPath));

        byte[] privateBytes = decodePem(privatePem);
        byte[] publicBytes = decodePem(publicPem);

        KeyFactory factory = KeyFactory.getInstance("RSA");

        var privateKey = factory.generatePrivate(
            new PKCS8EncodedKeySpec(privateBytes));

        var publicKey = factory.generatePublic(
            new X509EncodedKeySpec(publicBytes));

        return new KeyPair(publicKey, privateKey);
    }

    @Bean
    public JwtEncoder jwtEncoder(KeyPair jwtKeyPair) {
        RSAKey rsaKey = new RSAKey.Builder(
                (RSAPublicKey) jwtKeyPair.getPublic())
            .privateKey((RSAPrivateKey) jwtKeyPair.getPrivate())
            .build();

        JWKSource<SecurityContext> jwkSource =
            new ImmutableJWKSet<>(new JWKSet(rsaKey));

        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder(KeyPair jwtKeyPair) {
        return NimbusJwtDecoder
            .withPublicKey((RSAPublicKey) jwtKeyPair.getPublic())
            .build();
    }

    private static byte[] decodePem(String pem) {
        String content = pem
            .replaceAll("-----BEGIN [^-]+-----", "")
            .replaceAll("-----END [^-]+-----", "")
            .replaceAll("\\s", "");

        return Base64.getDecoder().decode(content);
    }
}

