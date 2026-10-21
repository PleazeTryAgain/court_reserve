package com.courtreserve.acceptance;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

/**
 * Ejecuta los escenarios Karate de src/test/resources/karate contra la
 * aplicación levantada en un puerto aleatorio con base de datos H2.
 */
@Tag("karate")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AcceptanceTest {

    @LocalServerPort
    private int port;

    @Test
    void escenariosDeAceptacionDeLaApi() {
        Results results = Runner.path("classpath:karate")
                .systemProperty("courtreserve.test.baseUrl", "http://localhost:" + port)
                .outputJunitXml(true)
                .parallel(1);

        Assertions.assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }
}
