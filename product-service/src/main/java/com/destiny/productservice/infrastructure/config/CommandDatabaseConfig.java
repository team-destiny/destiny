package com.destiny.productservice.infrastructure.config;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.destiny.productservice.infrastructure.repository.command",
    entityManagerFactoryRef = "commandEntityManagerFactory",
    transactionManagerRef = "commandTransactionManager"
)
public class CommandDatabaseConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.command")
    public DataSource commandDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean commandEntityManagerFactory(
        @Qualifier("commandDataSource") DataSource dataSource,
        JpaVendorAdapter jpaVendorAdapter,
        JpaProperties jpaProperties
    ) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("com.destiny.productservice.domain.entity");
        emf.setJpaVendorAdapter(jpaVendorAdapter);
        emf.setJpaPropertyMap(jpaProperties.getProperties());
        emf.setPersistenceUnitName("command");

        return emf;
    }

    @Bean
    public PlatformTransactionManager commandTransactionManager(
        @Qualifier("commandEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
