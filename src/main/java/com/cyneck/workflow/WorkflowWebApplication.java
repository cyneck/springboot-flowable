package com.cyneck.workflow;

import com.cyneck.workflow.config.database.DatabaseConfiguration;
import com.cyneck.workflow.config.flowable.AppDispatcherServletConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Import({
        AppDispatcherServletConfiguration.class
})
@ServletComponentScan
@EnableTransactionManagement
@SpringBootApplication
@MapperScan(basePackages = {"com.cyneck.workflow.mapper"})
public class WorkflowWebApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfiguration.class);

    public static void main(String[] args) {
        SpringApplication.run(WorkflowWebApplication.class, args);
        LOGGER.info("Started App!");
    }

}
