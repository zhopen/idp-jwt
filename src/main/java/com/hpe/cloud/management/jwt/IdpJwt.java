package com.hpe.cloud.management.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.auth0.jwt.algorithms.Algorithm.HMAC256;

public class IdpJwt {
    public static long accessTokenValidDuration = 0;
    public static long refreshTokenValidDuration = 0;
    public static Algorithm algorithm;
    public static JWTVerifier verifier;
    static IdpJwt jwt = null;
    static String hmacSecret;
    private IdpJwt() {
    }

    public static IdpJwt getInstance(long accessTokenValidDuration, long refreshTokenValidDuration, String hmacSecret, String issuer) {
        if (jwt != null)
            return jwt;

        jwt = new IdpJwt();
        jwt.accessTokenValidDuration = accessTokenValidDuration;
        jwt.refreshTokenValidDuration = refreshTokenValidDuration;
        jwt.hmacSecret = hmacSecret;

        try {
            algorithm = HMAC256(hmacSecret);
            verifier = JWT.require(algorithm).withIssuer(issuer).build(); //Reusable verifier instance

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return jwt;
    }

    public String getValueFromToken(String token, String key) throws JWTDecodeException {
        DecodedJWT decodedJWT = JWT.decode(token);
        Claim claim = decodedJWT.getClaim(key);
        return claim.asString();
    }

    public String generateToken(String username, String scope, String type) {
        if (scope == null) scope = "";
        if (type == null) type = "";
        long currTime = System.currentTimeMillis();
        String token = JWT.create()
            .withIssuer("hpe-idp")
            .withIssuedAt(new Date(currTime))
            .withExpiresAt(new Date(currTime + jwt.accessTokenValidDuration * 1000))
            .withClaim("username", username)
            .withClaim("scope", scope)
            .withClaim("type", type)
            .sign(jwt.algorithm);
        return token;
    }

    public String generateTokenJson(String username, String scope, String refreshToken) throws JsonProcessingException {
        Map data = new HashMap();

        long currTime = System.currentTimeMillis();
        String accessToken = JWT.create()
            .withIssuer("hpe-idp")
            .withIssuedAt(new Date(currTime))
            .withExpiresAt(new Date(currTime + jwt.accessTokenValidDuration * 1000))
            .withClaim("username", username)
            .withClaim("scope", scope)
            .withClaim("type", "access_token")
            .sign(jwt.algorithm);

        data.put("access_token", accessToken);


        if (refreshToken == null || refreshToken.equals("")) {
            refreshToken = JWT.create()
                .withIssuer("hpe-idp")
                .withIssuedAt(new Date(currTime))
                .withExpiresAt(new Date(currTime + jwt.refreshTokenValidDuration * 1000))
                .withClaim("username", username)
                .withClaim("type", "refresh_token")
                .sign(jwt.algorithm);
        }
        data.put("refresh_token", refreshToken);


        data.put("expires_in", jwt.accessTokenValidDuration);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(data);
        return json;
    }


}
