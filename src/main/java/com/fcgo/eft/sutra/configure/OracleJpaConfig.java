package com.fcgo.eft.sutra.configure;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.fcgo.eft.sutra.repository.oracle",
        entityManagerFactoryRef = "oracleEMF",
        transactionManagerRef = "oracleTM"
)
public class OracleJpaConfig {
}
