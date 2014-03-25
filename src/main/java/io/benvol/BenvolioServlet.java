package io.benvol;

import io.benvol.elastic.client.ElasticRestClient;
import io.benvol.model.ElasticHttpRequest;
import io.benvol.model.ElasticRequestFactory;
import io.benvol.model.HttpKind;
import io.benvol.model.auth.AuthDirective;
import io.benvol.model.auth.AuthUser;
import io.benvol.model.auth.remote.UserRemoteSchema;
import io.benvol.model.policy.Policy;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
class BenvolioServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(BenvolioServlet.class);

    private static final String UNSUPPORTED_HTTP_KIND = "Unsupported HTTP method kind: %s";

    private final ExecutorService _threadPool;
    private final UserRemoteSchema _userRemoteSchema;
    private final ElasticRestClient _elasticRestClient;
    private final ElasticRequestFactory _elasticQueryFactory;

    public BenvolioServlet(BenvolioSettings settings) {
        _threadPool = Executors.newFixedThreadPool(settings.getThreadPoolSize());;
        _userRemoteSchema = settings.getUserRemoteSchema();
        _elasticRestClient = new ElasticRestClient(settings);
        _elasticQueryFactory = new ElasticRequestFactory(settings);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        process(HttpKind.GET, request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        process(HttpKind.POST, request, response);
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        process(HttpKind.PUT, request, response);
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        process(HttpKind.DELETE, request, response);
    }

    @Override
    public void doHead(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, String.format(UNSUPPORTED_HTTP_KIND, HttpKind.HEAD));
    }

    @Override
    public void doTrace(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, String.format(UNSUPPORTED_HTTP_KIND, HttpKind.TRACE));
    }

    @Override
    public void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, String.format(UNSUPPORTED_HTTP_KIND, HttpKind.OPTIONS));
    }

    private void process(
        final HttpKind httpKind,
        final HttpServletRequest request,
        final HttpServletResponse response
    ) throws IOException, ServletException {

        // Executing GET requests in a browser usually results in a bunch of favicon requests
        // hitting the API. Prevent these (as well as robots.txt requests) from hitting any
        // of the API code, which won't be able to make sense of them.
        final String path = request.getServletPath();
        if (path.endsWith("favicon.ico") || path.endsWith("robots.txt")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "");
            return;
        }

        // Create an asynchronous context and enqueue this request into the thread pool
        final AsyncContext ctx = request.startAsync();
        _threadPool.execute(new Runnable() {
            public void run() {

                // Interpret this query as an ElasticSearch operation with a corresponding authentication directive.
                ElasticHttpRequest elasticHttpRequest = new ElasticHttpRequest(httpKind, request);
                AuthDirective authDirective = elasticHttpRequest.getAuthDirective();

                if (authDirective.isAnonymous()) {
                    // TODO: implement anonymous requests
                    try {
                        ((HttpServletResponse) ctx.getResponse()).sendError(
                            HttpServletResponse.SC_NOT_IMPLEMENTED,
                            "Anonymous request are not yet supported"
                        );
                    } catch (IOException e) {/* IGNORE */}
                } else {

                    // Authenticate the user
                    AuthUser authUser = authDirective.authenticate(
                        _elasticRestClient, _userRemoteSchema, _elasticQueryFactory
                    );

                    // Load policies that apply to this user and this request
                    List<Policy> policies = _elasticRestClient.findPoliciesFor(authUser, elasticHttpRequest);

                }

                ctx.getResponse().setContentType("application/json");
                try (final PrintWriter w = ctx.getResponse().getWriter()) {
                    w.printf("HttpKind: %s, path: %s, query: %s", httpKind, path, request.getQueryString()); // TODO
                } catch (IOException e) {
                    LOG.error(e);
                }
                ctx.complete();
            }
        });
    }
}