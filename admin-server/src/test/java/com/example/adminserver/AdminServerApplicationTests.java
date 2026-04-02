package com.example.adminserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "eureka.client.enabled=false")
class AdminServerApplicationTests {

    @Test
    void contextLoads() {
    }

}
