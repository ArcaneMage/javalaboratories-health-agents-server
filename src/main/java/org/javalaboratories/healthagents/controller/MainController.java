package org.javalaboratories.healthagents.controller;

import org.javalaboratories.healthagents.model.Response;
import org.javalaboratories.core.util.Arguments;
import org.javalaboratories.healthagents.probes.*;
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
import org.springframework.web.bind.annotation.RequestParam;
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
@SuppressWarnings("unused")
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private CommandRendererFactory factory;
    @Autowired
    private ServiceHealthProbe serviceHealthProbe;

    /**
     * Handles {@code GET /api/agents/ftp/health} endpoint.
     *
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
     * Handles {@code GET /api/agents/requests/health} endpoint.
     *
     * @param request Incoming GET request: note security has already authorised
     *                this request.
     * @return Response object encapsulating with status of the VPN service.
     */
    @GetMapping("/requests/health")
    public ResponseEntity<Response> getRequestsHealth(final HttpServletRequest request,
                                                      @RequestParam(name="alertTTL",required=false) Integer alertTTL) {
        alertTTL = alertTTL == null ? LogHealthProbe.DEFAULT_ALERT_TTL_MINUTES : alertTTL;
        HealthProbe probe = new RequestsHealthProbe(alertTTL);
        return handleRequest(probe,request,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles {@code GET /api/agents/secure-traffic/health} endpoint.
     *
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
     * Handles {@code GET /api/agents/service/health} endpoint.
     *
     * @param request Incoming GET request: note security has already authorised
     *                this request.
     * @return Response object encapsulating with status of the VPN service.
     */
    @GetMapping("/service/health")
    public ResponseEntity<Response> getServiceHealth(final HttpServletRequest request) {
        HealthProbe probe = serviceHealthProbe;
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
        return handleRequest(probe,request,HttpStatus.NOT_IMPLEMENTED);
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
     * @param responseCode custom response code for errors.
     * @return resultant response encapsulates service detection.
     */
    protected final ResponseEntity<Response> handleRequest(final HealthProbe probe, final HttpServletRequest request,
                                                           final HttpStatus responseCode) {
        Function<HealthProbe,ResponseEntity<Response>> function = timeRequest(p -> {
            Response response = detect(p, responseCode);
            logger.info("Responding with '{}' to monitor",response);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json; charset=utf-8");
            return new ResponseEntity<>(response,headers,HttpStatus.valueOf(response.getStatus()));
        },request.getRequestURI());

        return function.apply(probe);
    }

    private Response detect(final HealthProbe probe, HttpStatus responseCode) {
        Response response;
        ZonedDateTime timestamp = ZonedDateTime.now();
        boolean detected = probe.detect();
        response = detected
            ? Response.builder()
                .zonedTimestamp(timestamp)
                .status(HttpStatus.OK.value())
                .meaning(HttpStatus.OK.getReasonPhrase())
                .agent(probe.getName())
                .build()
            : Response.builder()
                .zonedTimestamp(timestamp)
                .status(responseCode.value())
                .meaning(responseCode.getReasonPhrase())
                .agent(probe.getName())
                .message(String.format("Probed service with '%s' and it appears to be unstable or down", probe.getName()))
                .build();
        return response;
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