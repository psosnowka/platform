package com.task.platform

import io.restassured.RestAssured
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import spock.lang.Specification

@Testcontainers
@ContextConfiguration
@ActiveProfiles(value = "integration")
@SpringBootTest(classes = PlatformApplication, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaseApplicationIntegrationSpec extends Specification {


    @LocalServerPort
    protected int port

    def setup() {
        RestAssured.port = port
    }

    @Container
    static PostgreSQLContainer postgreSQLContainer =
            new PostgreSQLContainer("postgres:17")
                    .withDatabaseName("mydb")
                    .withUsername("myuser")
                    .withPassword("mypassword")

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl)
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername)
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword)
    }

    static {
        postgreSQLContainer.start()
    }

}
