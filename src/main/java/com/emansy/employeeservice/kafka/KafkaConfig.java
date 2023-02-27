package com.emansy.employeeservice.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic allEmployeeRequestTopic() {
        return TopicBuilder.name("all_employee_request").build();
    }

    @Bean
    public NewTopic allEmployeeSendTopic() {
        return TopicBuilder.name("all_employee_send").build();
    }
}
