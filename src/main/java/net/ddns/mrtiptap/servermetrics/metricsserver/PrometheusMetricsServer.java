package net.ddns.mrtiptap.servermetrics.metricsserver;

import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.slf4j.Logger;

public class PrometheusMetricsServer implements AutoCloseable {
    private final Logger log;
    private final String endpoint;
    private final int port;

    private final Server server;

    public PrometheusMetricsServer(CompositeMeterRegistry compositeMeterRegistry, Logger logger, String endpoint, int port) {
        this.endpoint = endpoint;
        this.port = port;
        log = logger;

        final PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        compositeMeterRegistry.add(registry);

        final GzipHandler gzipHandler = new GzipHandler();
        gzipHandler.setHandler(new ScrapingHandler(endpoint, registry));

        this.server = new Server(port);
        server.setHandler(gzipHandler);
    }

    public void start() throws Exception {
        server.start();
        log.info("Prometheus metrics available: http://localhost:{}{}", port, endpoint);
    }

    @Override
    public void close() throws Exception {
        if (server.isRunning()) {
            server.stop();
        }
    }
}
