package net.ddns.mrtiptap.servermetrics.metricsserver;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import java.io.IOException;

@RequiredArgsConstructor
public class ScrapingHandler extends AbstractHandler {
    private final String endpoint;
    private final PrometheusMeterRegistry prometheusMeterRegistry;

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (target.equalsIgnoreCase(endpoint)) {
            response.setContentType(TextFormat.CONTENT_TYPE_004);

            prometheusMeterRegistry.scrape(response.getWriter(), TextFormat.CONTENT_TYPE_004);
            response.setStatus(200);

            baseRequest.setHandled(true);
        } else {
            response.sendError(404);
        }
    }
}
