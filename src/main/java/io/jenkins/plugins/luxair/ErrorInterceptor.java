package io.jenkins.plugins.luxair;

import java.util.logging.Logger;
import kong.unirest.Config;
import kong.unirest.FailedResponse;
import kong.unirest.HttpRequestSummary;
import kong.unirest.HttpResponse;
import kong.unirest.Interceptor;


public class ErrorInterceptor implements Interceptor {
    private static final Logger logger = Logger.getLogger(ImageTag.class.getName());
    @Override
    public HttpResponse onFail(Exception e, HttpRequestSummary request, Config config) {
        logger.severe(e.getMessage());
        return new FailedResponse(e);
    }
}