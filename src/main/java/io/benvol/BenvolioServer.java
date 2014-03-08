package io.benvol;

import io.benvol.config.LoggingConfig;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.elasticsearch.common.base.Throwables;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class BenvolioServer extends Thread {

    private static final long STARTUP_TIME_MILLIS = System.currentTimeMillis();
    private static final DateTime STARTUP_DATE_TIME = new DateTime();
    
    private static Logger LOG;
    
    private final BenvolioSettings _settings;
    
    private final Server _jetty;
    
    public BenvolioServer(BenvolioSettings settings) {
        _settings = settings;
        _jetty = new Server(_settings.getServerPort());
    }
    
    public static void main(String[] args) throws Throwable {
        
        // Load the server configuration settings 
        BenvolioSettings settings = loadSettings();

        // Configure the LOG4J system
        LoggingConfig.configure(settings.getEnvironment());
        LOG = Logger.getLogger(BenvolioServer.class);
        LOG.info("STARTUP");
        
        // Create the server and start it running.
        BenvolioServer server = new BenvolioServer(settings);
        server.run();
    }

    private static BenvolioSettings loadSettings() {
        BenvolioSettings settings = null;
        try {
            InputStream stream = BenvolioServer.class.getResourceAsStream("/benvolio.config.json");
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode config = (ObjectNode) objectMapper.readTree(stream);
            settings = new BenvolioSettings(config);
        } catch (Throwable t) {
            Throwables.propagate(t);
        }
        return settings;
    }

    @Override
    public void run() {

        try {
            // Add a ShutdownSequence, so that the application
            // can perform cleanup work before terminating.
            ShutdownSequence shutdownSequence = new ShutdownSequence();
            Runtime.getRuntime().addShutdownHook(shutdownSequence);
            
            // Configure the servlet context & handlers
            ServletContextHandler context = new ServletContextHandler(_jetty, "/");
            ServletHolder servletHolder = new ServletHolder(new BenvolioServlet(_settings));
            servletHolder.setAsyncSupported(true);
            context.addServlet(servletHolder, "/");

            // Start the HTTP server
            LOG.info("starting up jetty http server");
            _jetty.start();
            _jetty.join();

        } catch (Throwable t) {
            LOG.fatal("Uncaught exception in IstanbulServer.run() method", t);
        }
    }

    public static long getStartupTimeMillis() {
        return STARTUP_TIME_MILLIS;
    }

    public static DateTime getStartupDateTime() {
        return STARTUP_DATE_TIME;
    }

    private class ShutdownSequence extends Thread {

        private ShutdownSequence() {
            super("SHUTDOWN");
        }

        public void run() {
            try {
                LOG.info("shutting down jetty http server");
                _jetty.stop();
            } catch (Exception e) {
                LOG.error("Uncaught exception during shutdown sequence", e);
            }
            System.exit(0);
        }

    }

}
