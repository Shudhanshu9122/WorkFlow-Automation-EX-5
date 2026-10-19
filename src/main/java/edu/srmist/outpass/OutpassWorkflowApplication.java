package edu.srmist.outpass;

import io.camunda.client.annotation.Deployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main Spring Boot Application for SRMIST Workflow Automation (Lab EX-5).
 * Automatically deploys BPMN diagrams and Camunda forms to Camunda 8 SaaS on startup.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"edu.srmist"})
@Deployment(resources = {
        "classpath:bpmn/hostel-outpass.bpmn",
        "classpath:bpmn/warden-approval.form",
        "classpath:bpmn/student-leave.bpmn",
        "classpath:bpmn/fa-approval.form"
})
public class OutpassWorkflowApplication {

    private static final Logger log = LoggerFactory.getLogger(OutpassWorkflowApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(OutpassWorkflowApplication.class, args);
    }

    @Bean
    public CommandLineRunner initRunner() {
        return args -> {
            log.info("==========================================================================");
            log.info(" SRMIST Workflow Automation Application (EX-5) Started Successfully!      ");
            log.info(" Deployed Models:                                                        ");
            log.info("   1. Hostel Out-Pass Request Workflow  (Process ID: hostel-outpass)      ");
            log.info("   2. Student Leave Application Workflow (Process ID: student-leave)      ");
            log.info(" Active Job Workers:                                                     ");
            log.info("   - check-outpass                                                       ");
            log.info("   - issue-outpass                                                       ");
            log.info("   - validate-leave                                                      ");
            log.info("   - notify-student                                                      ");
            log.info(" REST Endpoints:                                                         ");
            log.info("   - POST http://localhost:8080/outpass                                  ");
            log.info("   - POST http://localhost:8080/leave                                    ");
            log.info("==========================================================================");
        };
    }
}
