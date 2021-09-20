package net.ddns.mrtiptap.servermetrics.metricsserver;

import com.sun.net.httpserver.HttpServer;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class PrometheusMetricsServer implements AutoCloseable {
    private final PrometheusMeterRegistry registry;
    private final Logger log;
    private final String endpoint;
    private final int port;

    private HttpServer server;

    public PrometheusMetricsServer(CompositeMeterRegistry compositeMeterRegistry, Logger logger, String endpoint, int port) {
        registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        compositeMeterRegistry.add(registry);

        log = logger;
        this.endpoint = endpoint;
        this.port = port;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(endpoint, httpExchange -> {
            String response = registry.scrape();
            httpExchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });

        new Thread(server::start).start();
        log.info("Prometheus metrics available: http://localhost:" + port + endpoint);
    }

    @Override
    public void close() {
        server.stop(0);
    }
}
