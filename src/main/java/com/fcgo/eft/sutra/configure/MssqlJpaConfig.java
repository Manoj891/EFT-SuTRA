package com.fcgo.eft.sutra.configure;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.fcgo.eft.sutra.repository.mssql",
        entityManagerFactoryRef = "mssqlEMF",
        transactionManagerRef = "mssqlTM"
)
public class MssqlJpaConfig {
}