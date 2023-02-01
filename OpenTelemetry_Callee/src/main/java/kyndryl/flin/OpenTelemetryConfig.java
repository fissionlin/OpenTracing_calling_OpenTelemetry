package kyndryl.flin;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporterBuilder;
import io.opentelemetry.extension.trace.propagation.JaegerPropagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;

import java.util.UUID;

/**
 * @Author: Fission Lin
 * @Date: 2023/1/30
 * @Time: 6:05 PM
 * @Description: kyndryl.flin
 */
public class OpenTelemetryConfig {

    private static String OTLP_HOST="http://localhost:4317";

    public static OpenTelemetrySdk configureGlobal(String serviceName) {
        //ResourceAttributes.SERVICE_NAME 在jaeger console中呈現的名稱, 預設是unknown_service
        Resource resource = Resource.getDefault()
                .merge(Resource.create(
                        Attributes.of(ResourceAttributes.SERVICE_NAME,serviceName)
                ));

        // Configure traces
        //Builder utility for OtlpGrpcSpanExporter which Exports spans using OTLP via gRPC, using OpenTelemetry's protobuf model.
        //OtlpGrpcSpanExporter.builder() Returns a new builder instance for this exporter.
        OtlpGrpcSpanExporterBuilder spanExporterBuilder = OtlpGrpcSpanExporter.builder()
                .setEndpoint(OTLP_HOST);

        /*

        This is the OpenTelemetry exporter, sending span data to Jaeger via gRPC.
        port 14250 used to accept model.proto
        JaegerGrpcSpanExporter exporter = JaegerGrpcSpanExporter.builder().setEndpoint("http://localhost:14250").build();


         */

        //A builder for configuring an OpenTelemetrySdk.
        SdkTracerProviderBuilder sdkTracerProviderBuilder = SdkTracerProvider.builder()
                .setResource(resource)
                .addSpanProcessor(BatchSpanProcessor.builder(spanExporterBuilder.build()).build());

        //Returns a new OpenTelemetrySdkBuilder for configuring an instance of the OpenTelemetry SDK
        return OpenTelemetrySdk.builder()
                .setPropagators(ContextPropagators.create(
                        TextMapPropagator.composite(
                                W3CTraceContextPropagator.getInstance(),
                                JaegerPropagator.getInstance())))
                .setTracerProvider(sdkTracerProviderBuilder.build())
                .buildAndRegisterGlobal();
    }

    private OpenTelemetryConfig() {}
}
