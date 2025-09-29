package ch.ost.clde.ods.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
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
        basePackages = "ch.ost.clde.ods.repository.marketdata",
        entityManagerFactoryRef = "marketDataEntityManagerFactory",
        transactionManagerRef = "marketDataTransactionManager"
)
public class MarketDataDbConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.marketdata")
    public DataSourceProperties marketDataDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource marketDataDataSource() {
        return marketDataDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "marketDataEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean marketDataEntityManagerFactory(
            @Qualifier("marketDataDataSource") DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("ch.ost.clde.ods.entity.marketdata");
        emf.setPersistenceUnitName("marketDataPU");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.hbm2ddl.auto", "update");
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        emf.setJpaPropertyMap(props);

        return emf;
    }


    @Bean
    public PlatformTransactionManager marketDataTransactionManager(
            @Qualifier("marketDataEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}