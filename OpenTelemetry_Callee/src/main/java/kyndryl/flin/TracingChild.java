package kyndryl.flin;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapPropagator;
//import jakarta.ws.rs.core.HttpHeaders;
//import jakarta.ws.rs.core.MultivaluedMap;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * @Author: Fission Lin
 * @Date: 2023/1/30
 * @Time: 5:28 PM
 * @Description: kyndryl.flin
 */
@RestController
public class TracingChild {

    private static final Tracer TRACER = GlobalOpenTelemetry.getTracer(TracingChild.class.getName());

    //用HttpServletRequest做為carrier
    private TextMapGetter<HttpServletRequest> getter = new TextMapGetter<HttpServletRequest>() {
        @Override
        public Iterable<String> keys(HttpServletRequest request) {
            List<String> keys = new ArrayList<>();
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    keys.add(headerNames.nextElement());
                }
            }
            return keys;
        }

        public String get(HttpServletRequest request, String s) {
            assert request.getHeader(s) != null;
            return request.getHeader(s);
        }
    };

    @GetMapping("/endtracing")
    public String endtracing(@RequestHeader Map<String, String> headers, HttpServletRequest request) {
        //print all headers
        headers.forEach((key, value) -> {
            System.out.println(String.format("Header '%s' = %s", key, value));
        });

        /* 測試getHeaderNames(),將所有header names列印出來
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                System.out.println(headerNames.nextElement());
            }
        }
        */
        ContextPropagators propagators = GlobalOpenTelemetry.getPropagators();
        TextMapPropagator textMapPropagator = propagators.getTextMapPropagator();

        // Extract and store the propagated span's SpanContext and other available concerns
        // in the specified Context.

        Context extractedContext = textMapPropagator.extract(Context.current(), request, getter);

        try (Scope scope = extractedContext.makeCurrent()) {
            // Automatically use the extracted SpanContext as parent.
            Span serverSpan = TRACER.spanBuilder("endtracing")
                    .setSpanKind(SpanKind.SERVER)
                    .startSpan();

            try {
                System.out.println("do some work");
                return "Greetings from Kyndryl!";
            } finally {
                serverSpan.end();
            }
        }
    }

}
