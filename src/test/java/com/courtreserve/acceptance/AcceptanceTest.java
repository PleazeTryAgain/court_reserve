package com.courtreserve.acceptance;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AcceptanceTest {
    @LocalServerPort
    private int port;

    @Test
    void acceptanceScenarios() {
        System.setProperty("courtreserve.test.baseUrl", "http://localhost:" + port);
        try {
            Results results = Runner.path("classpath:karate/acceptance.feature").parallel(1);
            Assertions.assertEquals(0, results.getFailCount(), results.getErrorMessages());
        } finally {
            System.clearProperty("courtreserve.test.baseUrl");
        }
    }
}
