package com.example.mychat.security.jwt;

import com.example.mychat.exception.JwtServiceException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
public class JwtService {

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;

    public JwtService() {
        try {
            this.privateKey = loadPrivateKey("keys/private.pem");
            this.publicKey = loadPublicKey("keys/public.pem");
        } catch (Exception e) {
            throw new JwtServiceException("Error loading keys");
        }
    }

    public String getToken(UserDetails user) {
        return getToken(new HashMap<>(), user);
    }

    private String getToken(Map<String, Object> extraClaims, UserDetails user) {
        try {
            JWSSigner signer = new RSASSASigner(privateKey);

            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                    .subject(user.getUsername())
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24));

            extraClaims.forEach(builder::claim);

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.RS256),
                    builder.build()
            );

            signedJWT.sign(signer);

            return signedJWT.serialize();

        } catch (Exception e) {
            throw new JwtServiceException("Error generating JWT");
        }
    }

    public String getUsernameFromToken(String token) {
        return getClaim(token, JWTClaimsSet::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public <T> T getClaim(String token, java.util.function.Function<JWTClaimsSet, T> resolver) {
        try {
            JWTClaimsSet claims = getAllClaims(token);
            return resolver.apply(claims);
        } catch (Exception e) {
            throw new JwtServiceException("Error extracting claim");
        }
    }

    private JWTClaimsSet getAllClaims(String token) throws Exception {
        SignedJWT signedJWT = SignedJWT.parse(token);

        JWSVerifier verifier = new RSASSAVerifier(publicKey);

        if (!signedJWT.verify(verifier)) {
            throw new JwtServiceException("Invalid token");
        }

        return signedJWT.getJWTClaimsSet();
    }

    private Date getExpiration(String token) {
        return getClaim(token, JWTClaimsSet::getExpirationTime);
    }

    private boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }

    private RSAPrivateKey loadPrivateKey(String path) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String key = new String(Files.readAllBytes(new ClassPathResource(path).getFile().toPath()));
        key = key.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory factory = KeyFactory.getInstance("RSA");

        return (RSAPrivateKey) factory.generatePrivate(spec);
    }

    private RSAPublicKey loadPublicKey(String path) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String key = new String(Files.readAllBytes(new ClassPathResource(path).getFile().toPath()));
        key = key.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);

        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory factory = KeyFactory.getInstance("RSA");

        return (RSAPublicKey) factory.generatePublic(spec);
    }
}
