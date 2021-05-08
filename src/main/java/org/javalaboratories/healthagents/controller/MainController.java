package org.javalaboratories.healthagents.controller;

import org.javalaboratories.healthagents.model.Response;
import org.javalaboratories.healthagents.probes.CommandRendererFactory;
import org.javalaboratories.healthagents.probes.HealthProbe;
import org.javalaboratories.healthagents.probes.OpenVpnHealthProbe;
import org.javalaboratories.healthagents.probes.PureFtpHealthProbe;
import org.javalaboratories.core.util.Arguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
import java.util.function.Function;

/**
 * Main controller to handle all health-agent requests.
 * <p>
 * All {@code ../health} endpoints require ROLE_MONITOR privileged access for
 * successful responses. This server has several health probes that have the
 * ability to interact with the underlying operating system or otherwise to
 * determine the current state of a service (whether it is functioning
 * correctly).
 */
@RestController
@PreAuthorize("hasRole('MONITOR')")
@RequestMapping("/api/agents")
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private CommandRendererFactory factory;

    /**
     * Handles {@code GET /api/agents/ftp/health} endpoint.
     * @param request Incoming GET request: note security has already authorised
     *                this request.
     * @return Response object encapsulating with status of the FTP service.
     */
    @GetMapping("/ftp/health")
    public ResponseEntity<Response> getFtpHealth(final HttpServletRequest request) {
        HealthProbe probe = new PureFtpHealthProbe(factory);
        return handleRequest(probe,request);
    }

    /**
     * Handles {@code GET /api/agents/secure-traffic/health} endpoint.
     * @param request Incoming GET request: note security has already authorised
     *                this request.
     * @return Response object encapsulating with status of the VPN service.
     */
    @GetMapping("/secure-traffic/health")
    public ResponseEntity<Response> getVpnHealth(final HttpServletRequest request) {
        HealthProbe probe = new OpenVpnHealthProbe(factory);
        return handleRequest(probe,request);
    }

    /**
     * Handles all incoming requests uniformly.
     * <p>
     * This method will probe the service to detect whether it is functioning
     * correctly. Not only the service will be probe but this process will be
     * timed/measured and reported.
     *
     * @param probe health probe to interrogate service detection.
     * @param request incoming HTTP request.
     * @return resultant response encapsulates service detection.
     */
    protected final ResponseEntity<Response> handleRequest(final HealthProbe probe, final HttpServletRequest request) {
        Function<HealthProbe,ResponseEntity<Response>> function = timeRequest(p -> {
            boolean detected = p.detect();
            Response response;
            ZonedDateTime timestamp = ZonedDateTime.now();
            if (detected) {
                response = Response.builder()
                        .zonedTimestamp(timestamp)
                        .status(HttpStatus.OK.value())
                        .meaning(HttpStatus.OK.getReasonPhrase())
                        .agent(p.getName())
                        .build();
            } else {
                response = Response.builder()
                        .zonedTimestamp(timestamp)
                        .status(HttpStatus.NOT_IMPLEMENTED.value())
                        .meaning(HttpStatus.NOT_IMPLEMENTED.getReasonPhrase())
                        .agent(p.getName())
                        .message(String.format("Probed service with '%s' and it appears to be down", p.getName()))
                        .build();
            }
            logger.info("Responding with '{}' to monitor",response);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json; charset=utf-8");
            return new ResponseEntity<>(response,headers,HttpStatus.valueOf(response.getStatus()));
        },request.getRequestURI());

        return function.apply(probe);
    }

    private <T,R> Function<T,R> timeRequest(final Function<? super T, ? extends R> action, final String uri) {
        Arguments.requireNonNull("Requires both action and URI objects",action,uri);
        return value -> {
            StopWatch watch = new StopWatch();
            R result;
            try {
                watch.start();
                result = action.apply(value);
            } finally {
                watch.stop();
            }
            logger.info("Response elapsed time for REST endpoint '{}' was {}ms", uri, watch.getTotalTimeMillis());
            return result;
        };
    }
}