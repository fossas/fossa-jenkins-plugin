package io.fossa.service.auth;

import org.apache.commons.httpclient.Credentials;

public class TokenCredentials implements Credentials {
    private String token;

    public TokenCredentials(String token) {
        this.token = token;
    }

    public String toString() {
        return token;
    }
}