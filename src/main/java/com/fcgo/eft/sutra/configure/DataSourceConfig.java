//package com.fcgo.eft.sutra.configure;
//
//import com.zaxxer.hikari.HikariDataSource;
//import jakarta.persistence.EntityManagerFactory;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
//import org.springframework.transaction.PlatformTransactionManager;
//
//import javax.sql.DataSource;
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//public class DataSourceConfig {
//
//    // -------------------- Oracle --------------------
//    @Primary
//    @Bean(name = "oracleDataSource")
//    @ConfigurationProperties(prefix = "spring.datasource.oracle")
//    public DataSource oracleDataSource() {
//        return DataSourceBuilder.create().type(HikariDataSource.class).build();
//    }
//
//    @Bean(name = "oracleEMF")
//    @Primary
//    public LocalContainerEntityManagerFactoryBean oracleEntityManagerFactory(
//            @Qualifier("oracleDataSource") DataSource dataSource) {
//
//        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
//        emf.setDataSource(dataSource);
//        emf.setPackagesToScan("com.fcgo.eft.sutra.entity.oracle");
//        emf.setPersistenceUnitName("oraclePU");
//
//        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        emf.setJpaVendorAdapter(vendorAdapter);
//
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("hibernate.dialect", "org.hibernate.dialect.OracleDialect");
//        properties.put("hibernate.hbm2ddl.auto", "update");
//        properties.put("hibernate.show_sql", false);
//        properties.put("hibernate.format_sql", false);
//        properties.put("hibernate.default_schema", "EFT_SUTRA");
//        properties.put("hibernate.jdbc.fetch_size", 100);
//        properties.put("hibernate.jdbc.batch_size", 50);
//        properties.put("hibernate.order_inserts", true);
//        properties.put("hibernate.order_updates", true);
//        properties.put("hibernate.connection.isolation", 2);
//        emf.setJpaPropertyMap(properties);
//
//        return emf;
//    }
//
//    @Primary
//    @Bean(name = "oracleTM")
//    public PlatformTransactionManager oracleTransactionManager(
//            @Qualifier("oracleEMF") EntityManagerFactory emf) {
//        return new JpaTransactionManager(emf);
//    }
//
//    // -------------------- SQL Server --------------------
//    @Bean(name = "mssqlDataSource")
//    @ConfigurationProperties(prefix = "spring.datasource.mssql")
//    public DataSource mssqlDataSource() {
//        return DataSourceBuilder.create().type(HikariDataSource.class).build();
//    }
//
//    @Bean(name = "mssqlEMF")
//    public LocalContainerEntityManagerFactoryBean mssqlEntityManagerFactory(
//            @Qualifier("mssqlDataSource") DataSource dataSource) {
//
//        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
//        emf.setDataSource(dataSource);
//        emf.setPackagesToScan("com.fcgo.eft.sutra.entity.mssql");
//        emf.setPersistenceUnitName("mssqlPU");
//
//        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        emf.setJpaVendorAdapter(vendorAdapter);
//
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
//        properties.put("hibernate.show_sql", false);
//        properties.put("hibernate.format_sql", false);
//        properties.put("hibernate.default_schema", "dbo");
//
//        emf.setJpaPropertyMap(properties);
//
//        return emf;
//    }
//
//    @Bean(name = "mssqlTM")
//    public PlatformTransactionManager mssqlTransactionManager(
//            @Qualifier("mssqlEMF") EntityManagerFactory emf) {
//        return new JpaTransactionManager(emf);
//    }
//}
