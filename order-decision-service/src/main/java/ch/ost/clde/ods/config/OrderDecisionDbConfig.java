package ch.ost.clde.ods.config;

import jakarta.persistence.EntityManagerFactory;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "ch.ost.clde.ods.repository.orderdecision",
        entityManagerFactoryRef = "orderDecisionEntityManagerFactory",
        transactionManagerRef = "orderDecisionTransactionManager"
)
public class OrderDecisionDbConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.orderdecision")
    public DataSourceProperties orderDecisionDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource orderDecisionDataSource() {
        return orderDecisionDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "orderDecisionEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean orderDecisionEntityManagerFactory(
            @Qualifier("orderDecisionDataSource") DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("ch.ost.clde.ods.entity.orderDecision");
        emf.setPersistenceUnitName("orderDecisionPU");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.hbm2ddl.auto", "update");
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        emf.setJpaPropertyMap(props);

        return emf;
    }

    @Bean
    public PlatformTransactionManager orderDecisionTransactionManager(
            @Qualifier("orderDecisionEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    public Flyway orderDecisionFlyway(@Qualifier("orderDecisionDataSource") DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .schemas("order_decision")   // schema managed by Flyway
                .defaultSchema("order_decision")
                .locations("classpath:db/migration") // put migrations there
                .baselineOnMigrate(true)
                .load();
    }

    @Bean
    public FlywayMigrationInitializer flywayInitializer(Flyway orderDecisionFlyway) {
        return new FlywayMigrationInitializer(orderDecisionFlyway);
    }
}