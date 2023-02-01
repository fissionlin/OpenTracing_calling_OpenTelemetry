package kyndryl.flin;

import io.opentelemetry.sdk.OpenTelemetrySdk;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: Fission Lin
 * @Date: 2023/1/30
 * @Time: 5:29 PM
 * @Description: kyndryl.flin
 */
@SpringBootApplication
public class TracingChildApplication {
    public static void main(String[] args) {
        //The first step is to get a handle to an instance of the OpenTelemetry interface.
        //
        //If you are an application developer, you need to configure an instance of the OpenTelemetrySdk
        //as early as possible in your application. This can be done using the
        //OpenTelemetrySdk.builder() method.

        OpenTelemetrySdk openTelemetrySdk = OpenTelemetryConfig.configureGlobal("opentelemetry_callee");
        SpringApplication.run(TracingChildApplication.class, args);
    }
}
