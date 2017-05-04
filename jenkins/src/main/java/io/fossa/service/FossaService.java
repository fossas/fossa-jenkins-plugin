package io.fossa.service;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URLEncoder;

public class FossaService {
    private String baseUrl;
    private PrintStream logger;

    public FossaService(String baseUrl, PrintStream logger) {
        this.baseUrl = baseUrl;
        this.logger = logger;
    }

    private JSONObject get(String url) throws IOException, ParseException {
        logger.println("Fetching: " + url);

        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler(3, false));
        byte[] responseBody;
        try {
            int statusCode = client.executeMethod(method);

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
        JSONObject buildObject = get(baseUrl + "/hooks/web/" + URLEncoder.encode(locator.toString(), "UTF-8"));

        Build.Builder buildBuilder = new Build.Builder();
        buildBuilder.fromJSON(buildObject);
        return buildBuilder.build();
    }

    public Build waitUntilBuildIsFinished(Build obuild, int wait, int retries) throws IOException, ParseException, InterruptedException {
        Build build = obuild;

        while(retries-- > 0) {
            Build.Builder buildBuilder = new Build.Builder();
            buildBuilder.fromJSON(get(baseUrl + "/api/builds/" + obuild.getId()));
            build = buildBuilder.build();
            if (build.isFinished()) {
                break;
            }
            Thread.sleep(wait);
        }

        return build;
    }

    public Build analyzeAndWaitUntilIsFinished(Locator locator, int wait, int retries) throws IOException, ParseException, InterruptedException {
        return this.waitUntilBuildIsFinished(this.analyze(locator), wait, retries);
    }

    public Revision getRevision(Locator locator) throws IOException, ParseException {
        Revision.Builder revisionBuilder = new Revision.Builder();
        revisionBuilder.fromJSON(get(baseUrl + "/api/revisions/" + URLEncoder.encode(locator.toString(), "UTF-8")));
        return revisionBuilder.build();
    }

    public Revision waitUntilScanIsFinished(Revision orevision, int wait, int retries) throws IOException, ParseException, InterruptedException {
        Revision revision = orevision;

        while(retries-- > 0) {
            Revision.Builder revisionBuilder = new Revision.Builder();
            revisionBuilder.fromJSON(get(baseUrl + "/api/revisions/" + URLEncoder.encode(revision.getLocator().toString(), "UTF-8")));
            revision = revisionBuilder.build();
            if (revision.isFinishedScanning()) {
                break;
            }
            Thread.sleep(wait);
        }

        return revision;
    }
}
