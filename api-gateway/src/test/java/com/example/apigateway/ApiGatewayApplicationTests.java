package com.example.apigateway;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;

import reactor.core.publisher.Flux;

@SpringBootTest(properties = "eureka.client.enabled=false")
class ApiGatewayApplicationTests {

    @Autowired
    private RouteLocator routeLocator;

    @Test
    void contextLoads() {
    }

    @Test
    void shouldLoadConfiguredRoutes() {
        List<String> routeIds = Flux.from(routeLocator.getRoutes())
                .map(Route::getId)
                .collectList()
                .block();

        assertThat(routeIds)
                .contains("user-service", "order-service", "payment-service");
    }

}
