package com.task.platform

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration
class ApplicationIntegrationConfiguration {

    @Bean
    PostgreSQLContainer<?> postgresContainer() {
        def postgresContainer = new PostgreSQLContainer(DockerImageName.parse("postgres:17.4"))
                .withDatabaseName("mydb")
                .withUsername("myuser")
                .withPassword("mypassword")
        postgresContainer.start()
        return postgresContainer
    }
}
