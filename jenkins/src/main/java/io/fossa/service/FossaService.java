package io.fossa.service;

import io.fossa.service.auth.TokenAuthScheme;
import io.fossa.service.auth.TokenCredentials;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URLEncoder;

public class FossaService {
    private String token;
    private String baseUrl;
    private PrintStream logger;
    private int retries;
    private int timeout;
    private int sleep;

    static {
        AuthPolicy.registerAuthScheme(TokenAuthScheme.NAME, TokenAuthScheme.class);
    }

    public FossaService(String baseUrl, String token, int timeout, int sleep, int retries) {
        this.baseUrl = baseUrl;
        this.token = token;
        this.retries = retries;
        this.timeout = timeout;
        this.sleep = sleep;
    }

    private void log(String msg) {
        if (logger != null) logger.println(msg);
    }

    private HttpClient getClient() {
        HttpClient client = new HttpClient();
        client.getState().setCredentials(AuthScope.ANY, new TokenCredentials(token));

        return client;
    }

    private JSONObject get(String url) throws IOException, ParseException {
        log("GET: " + url);

        GetMethod method = new GetMethod(url);
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler(retries, false));
        method.getHostAuthState().setPreemptive();
        method.getHostAuthState().setAuthScheme(new TokenAuthScheme());

        byte[] responseBody;
        try {
            int statusCode = getClient().executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                throw new RuntimeException("Method failed: " + method.getStatusLine());
            }

            responseBody = method.getResponseBody();
        } finally {
            // Release the connection.
            method.releaseConnection();
        }

        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(new String(responseBody));
    }

    private JSONObject put(String url) throws IOException, ParseException {
        log("PUT: " + url);

        PutMethod method = new PutMethod(url);
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler(retries, false));
        method.getHostAuthState().setPreemptive();
        method.getHostAuthState().setAuthScheme(new TokenAuthScheme());

        byte[] responseBody;
        try {
            int statusCode = getClient().executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                throw new RuntimeException("Method failed: " + method.getStatusLine());
            }

            responseBody = method.getResponseBody();
        } finally {
            // Release the connection.
            method.releaseConnection();
        }

        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(new String(responseBody));
    }

    public Build analyze(Locator locator) throws IOException, ParseException {
        String url = String.format("%s/api/revisions/%s/build", baseUrl, URLEncoder.encode(locator.toPackageString(), "UTF-8"));

        Build.Builder buildBuilder = new Build.Builder();
        buildBuilder.fromJSON(put(url));
        return buildBuilder.build();
    }

    public Build waitUntilBuildIsFinished(Build obuild) throws IOException, ParseException, InterruptedException {
        Build build = obuild;

        int retries = this.timeout / this.sleep + 1;
        while(retries-- > 0) {
            build = getBuild(obuild.getId());
            if (build.isFinished()) {
                break;
            }
            Thread.sleep(this.sleep);
        }

        return build;
    }

    public Build analyzeAndWaitUntilBuildIsFinished(Locator locator) throws IOException, ParseException, InterruptedException {
        return this.waitUntilBuildIsFinished(this.analyze(locator));
    }

    public Revision waitUntilScanIsFinished(Revision orevision) throws IOException, ParseException, InterruptedException {
        Revision revision = orevision;

        int retries = this.timeout / this.sleep + 1;
        while(retries-- > 0) {
            revision = getRevision(revision.getLocator());
            if (revision.isFinishedScanning()) {
                break;
            }
            Thread.sleep(this.sleep);
        }

        return revision;
    }

    public Build getBuild(long id) throws IOException, ParseException {
        String url = String.format("%s/api/builds/%d", baseUrl, id);

        Build.Builder buildBuilder = new Build.Builder();
        buildBuilder.fromJSON(get(url));
        return buildBuilder.build();
    }

    public Revision getRevision(Locator locator) throws IOException, ParseException {
        String url = String.format("%s/api/revisions/%s", baseUrl, URLEncoder.encode(locator.toString(), "UTF-8"));

        Revision.Builder revisionBuilder = new Revision.Builder();
        revisionBuilder.fromJSON(get(url));
        return revisionBuilder.build();
    }

    public void setLogger(PrintStream logger) {
        this.logger = logger;
    }
}
