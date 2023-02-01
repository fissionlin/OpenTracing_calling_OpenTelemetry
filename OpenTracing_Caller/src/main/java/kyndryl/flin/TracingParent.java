package kyndryl.flin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @Author: Fission Lin
 * @Date: 2023/1/30
 * @Time: 4:51 PM
 * @Description: kyndryl.flin
 */
@RestController
public class TracingParent {

    private static final Logger log = LoggerFactory.getLogger(TracingParent.class);
    @Autowired
    private RestTemplate restTemplate;

    @Value("${server.port}")
    private int port;

    @Value("${url}")
    private String url;

    @RequestMapping("/begintracing")
    public String begintracing() throws InterruptedException {
        ResponseEntity<String> response =
                restTemplate.getForEntity(url, String.class);
        Thread.sleep(200);
        return "OpenTelemetry Callee Returns: " + response.getBody();
    }
}
