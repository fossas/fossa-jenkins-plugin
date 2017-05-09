package io.fossa.service.auth;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.auth.AuthenticationException;
import org.apache.commons.httpclient.auth.MalformedChallengeException;

public class TokenAuthScheme implements AuthScheme {
    public static String NAME = "Token";

    @Override
    public void processChallenge(String challenge) throws MalformedChallengeException {}

    @Override
    public String getSchemeName() {
        return NAME;
    }

    @Override
    public String getParameter(String param) {
        return null;
    }

    @Override
    public String getRealm() {
        return null;
    }

    @Override
    public String getID() {
        return NAME;
    }

    @Override
    public boolean isConnectionBased() {
        return false;
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public String authenticate(Credentials credentials, String method, String uri) throws AuthenticationException {
        StringBuilder buf = new StringBuilder();
        buf.append("token ");
        buf.append(credentials.toString());
        return buf.toString();
    }

    @Override
    public String authenticate(Credentials credentials, HttpMethod httpMethod) throws AuthenticationException {
        return authenticate(credentials, null, null);
    }
}