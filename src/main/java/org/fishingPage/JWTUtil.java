package org.fishingPage;


import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;

import java.security.Key;

public class JWTUtil {

    @Getter
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

}
