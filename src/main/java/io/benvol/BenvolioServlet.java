package io.benvol;

import java.io.IOException;
import java.io.PrintWriter;
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
    
    private static final String UNSUPPORTED_HTTP_METHOD = "Unsupported HTTP method: %s";

    private final ExecutorService _threadPool;

    public BenvolioServlet(BenvolioSettings settings) {
        _threadPool = Executors.newFixedThreadPool(settings.getThreadPoolSize());
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        process("GET", request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        process("POST", request, response);
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        process("PUT", request, response);
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        process("DELETE", request, response);
    }
    
    @Override
    public void doHead(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, String.format(UNSUPPORTED_HTTP_METHOD, "HEAD"));
    }
    
    @Override
    public void doTrace(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, String.format(UNSUPPORTED_HTTP_METHOD, "TRACE"));
    }
    
    @Override
    public void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, String.format(UNSUPPORTED_HTTP_METHOD, "OPTIONS"));
    }

    private void process(final String method, final HttpServletRequest request, final HttpServletResponse response) {
        final AsyncContext ctx = request.startAsync();
        _threadPool.execute(new Runnable() {
            @Override
            public void run() {
                ctx.getResponse().setContentType("application/json");
                try (final PrintWriter w = ctx.getResponse().getWriter()) {
                    w.printf("method: %s, path: %s, query: %s", method, request.getPathInfo(), request.getQueryString()); // TODO
                } catch (IOException e) {
                    LOG.error(e);
                }
                ctx.complete();
            }
        });
    }
}