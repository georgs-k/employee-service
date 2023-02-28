package com.emansy.employeeservice.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic invitedEmployeesRequestTopic() {
        return TopicBuilder.name("invited_employees_request").build();
    }

    @Bean
    public NewTopic invitedEmployeesSendTopic() {
        return TopicBuilder.name("invited_employees_send").build();
    }

    @Bean
    public NewTopic uninvitedEmployeesRequestTopic() {
        return TopicBuilder.name("uninvited_employees_request").build();
    }

    @Bean
    public NewTopic uninvitedEmployeesSendTopic() {
        return TopicBuilder.name("uninvited_employees_send").build();
    }
}
