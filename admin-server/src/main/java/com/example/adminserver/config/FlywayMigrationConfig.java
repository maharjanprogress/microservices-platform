package com.example.adminserver.config;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayMigrationConfig {

    private static final Logger log = LoggerFactory.getLogger(FlywayMigrationConfig.class);

    @Bean(initMethod = "migrate")
    public Flyway flyway(
            DataSource dataSource,
            @Value("${spring.flyway.default-schema:admin_server}") String defaultSchema,
            @Value("${spring.flyway.schemas:admin_server}") String[] schemas,
            @Value("${spring.flyway.locations:classpath:db/migration}") String[] locations,
            @Value("${spring.flyway.baseline-on-migrate:true}") boolean baselineOnMigrate) {
        log.info(
                "Running Flyway migrations for admin-server using default schema {}, schemas {}, locations {}",
                defaultSchema,
                schemas,
                locations);
        return Flyway.configure()
                .dataSource(dataSource)
                .defaultSchema(defaultSchema)
                .schemas(schemas)
                .locations(locations)
                .baselineOnMigrate(baselineOnMigrate)
                .load();
    }
}
